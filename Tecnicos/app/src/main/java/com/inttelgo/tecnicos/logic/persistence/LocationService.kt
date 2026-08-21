package com.inttelgo.tecnicos.logic.persistence

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.inttelgo.tecnicos.logic.Model.Request.UbicationRequest
import com.inttelgo.tecnicos.logic.repository.UsuarioRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LocationService : Service() {

    private val tag = "LocationService"
    private val repository = UsuarioRepository()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private val workHoursHandler = Handler(Looper.getMainLooper())
    private val workHoursCheck = object : Runnable {
        override fun run() {
            if (!WorkSchedule.isWithinWorkHours()) {
                Log.d(tag, "Fuera de jornada laboral. Deteniendo tracking.")
                stopTrackingAndSelf()
                return
            }
            workHoursHandler.postDelayed(this, WORK_HOURS_CHECK_MS)
        }
    }

    private var isTracking = false

    private fun checkPermissions(): Boolean {
        val fineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION
        return checkSelfPermission(fineLocation) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(coarseLocation) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "Servicio de ubicación creado")

        createNotificationChannel()

        // Android exige startForeground poco después de startForegroundService.
        // Usamos una notificación silenciosa/mínima (no se puede omitir del todo).
        promoteToForeground()

        if (!checkPermissions()) {
            Log.w(tag, "Permisos de ubicación no concedidos. Deteniendo el servicio.")
            stopSelf()
            return
        }

        if (!WorkSchedule.isWithinWorkHours()) {
            Log.d(tag, "Fuera de jornada (8am-7pm Bogotá). No se inicia tracking.")
            scheduleNextWorkStart(this)
            stopSelf()
            return
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (!WorkSchedule.isWithinWorkHours()) {
                    stopTrackingAndSelf()
                    return
                }
                for (location in locationResult.locations) {
                    sendLocationToServer(location)
                }
            }
        }

        startLocationUpdates()
        scheduleWorkEndAlarm(this)
        workHoursHandler.postDelayed(workHoursCheck, WORK_HOURS_CHECK_MS)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopTrackingAndSelf()
                return START_NOT_STICKY
            }
            ACTION_START_IF_WORK_HOURS -> {
                if (!WorkSchedule.isWithinWorkHours()) {
                    scheduleNextWorkStart(this)
                    stopSelf()
                    return START_NOT_STICKY
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "Servicio de ubicación detenido")
        workHoursHandler.removeCallbacks(workHoursCheck)
        stopLocationUpdates()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun promoteToForeground() {
        val notification = createSilentNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (isTracking) return
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            mainLooper
        )
        isTracking = true
        Log.d(tag, "Tracking de ubicación iniciado (jornada laboral)")
    }

    private fun stopLocationUpdates() {
        if (!isTracking) return
        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        } catch (_: Exception) {
            // Cliente aún no inicializado
        }
        isTracking = false
    }

    private fun stopTrackingAndSelf() {
        stopLocationUpdates()
        workHoursHandler.removeCallbacks(workHoursCheck)
        scheduleNextWorkStart(this)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    /**
     * Notificación mínima requerida por Android para FGS.
     * IMPORTANCE_MIN + silent evita sonido/banner; el sistema igual puede
     * mostrar un indicador discreto en la barra de estado.
     */
    private fun createSilentNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tecnicos")
            .setContentText("Servicio activo")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setSilent(true)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Servicio en segundo plano",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Canal interno del servicio"
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
                setSound(null, null)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun sendLocationToServer(location: Location) {
        if (!WorkSchedule.isWithinWorkHours()) return

        val user = UserPreferences(this).getUser()
        Log.d(tag, "Ubicación: Lat=${location.latitude}, Lng=${location.longitude}")
        if (user == null) return

        serviceScope.launch {
            try {
                val result = repository.ubication(
                    UbicationRequest(location.latitude, location.longitude)
                )
                Log.d(tag, "Ubicación enviada: ${result.body()}")
            } catch (e: Exception) {
                Log.e(tag, "Error al enviar ubicación: ${e.message}", e)
            }
        }
    }

    companion object {
        const val ACTION_START_IF_WORK_HOURS = "com.inttelgo.tecnicos.action.START_LOCATION_IF_WORK_HOURS"
        const val ACTION_STOP = "com.inttelgo.tecnicos.action.STOP_LOCATION"
        private const val CHANNEL_ID = "location_channel_min"
        private const val NOTIFICATION_ID = 1
        private const val WORK_HOURS_CHECK_MS = 60_000L
        private const val REQUEST_START = 1001
        private const val REQUEST_STOP = 1002

        fun startIfWorkHours(context: Context) {
            if (!WorkSchedule.isWithinWorkHours()) {
                scheduleNextWorkStart(context)
                return
            }
            val intent = Intent(context, LocationService::class.java).apply {
                action = ACTION_START_IF_WORK_HOURS
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, LocationService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

        fun scheduleNextWorkStart(context: Context) {
            val triggerAt = WorkSchedule.nextWorkStartMillis()
            val intent = Intent(context, LocationService::class.java).apply {
                action = ACTION_START_IF_WORK_HOURS
            }
            val pending = PendingIntent.getService(
                context,
                REQUEST_START,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAt,
                        pending
                    )
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pending)
                }
                Log.d("LocationService", "Programado reinicio de tracking a las 8:00 Bogotá")
            } catch (e: SecurityException) {
                val delay = (triggerAt - System.currentTimeMillis()).coerceAtLeast(0L)
                alarmManager.set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + delay,
                    pending
                )
                Log.w("LocationService", "Exact alarm no disponible, usando alarm inexacta: ${e.message}")
            }
        }

        fun scheduleWorkEndAlarm(context: Context) {
            val triggerAt = WorkSchedule.nextWorkEndMillis()
            val intent = Intent(context, LocationService::class.java).apply {
                action = ACTION_STOP
            }
            val pending = PendingIntent.getService(
                context,
                REQUEST_STOP,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAt,
                        pending
                    )
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pending)
                }
                Log.d("LocationService", "Programada detención a las 19:00 Bogotá")
            } catch (e: SecurityException) {
                val delay = (triggerAt - System.currentTimeMillis()).coerceAtLeast(0L)
                alarmManager.set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + delay,
                    pending
                )
                Log.w("LocationService", "Exact alarm no disponible para stop: ${e.message}")
            }
        }
    }
}
