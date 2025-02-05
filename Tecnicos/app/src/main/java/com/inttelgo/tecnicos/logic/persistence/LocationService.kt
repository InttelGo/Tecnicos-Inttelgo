package com.inttelgo.tecnicos.logic.persistence

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.inttelgo.tecnicos.logic.Model.RetroFitService
import okhttp3.*
import java.io.IOException

class LocationService : Service() {
    private val TAG = "LocationService"

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private fun checkPermissions(): Boolean {
        val fineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION

        return checkSelfPermission(fineLocation) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(coarseLocation) == PackageManager.PERMISSION_GRANTED
    }


    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Servicio de ubicación iniciado")

        // Verificar permisos
        if (!checkPermissions()) {
            stopSelf() // Detener el servicio si los permisos no están concedidos
            println("Permisos de ubicación no concedidos. Deteniendo el servicio.")
            return
        }

        // Crear el canal de notificación
        createNotificationChannel()

        // Iniciar el servicio en primer plano
        val notification = createNotification("Escuchando ubicación...")
        startForeground(1, notification)

        // Configurar cliente de ubicación
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000).build()

        // Callback de ubicación
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    sendLocationToServer(location)
                }
            }
        }

        // Iniciar actualizaciones de ubicación
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Servicio de ubicación detenido")
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(message: String): Notification {
        return NotificationCompat.Builder(this, "location_channel")
            .setContentTitle("Servicio de Ubicación")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location_channel",
                "Ubicación en segundo plano",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "Este canal es para notificaciones de ubicación"

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }


    private fun sendLocationToServer(location: Location) {
        val userid = UserPreferences(this).getId()
        val color = UserPreferences(this).getColor()
        Log.d(TAG, "Ubicación obtenida: Lat=${location.latitude}, Lng=${location.longitude}, idUser=${userid}")
        if(userid!=""){
            val client = OkHttpClient()
            val url = "https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/ubication.php")}"

            val formBody = FormBody.Builder()
                .add("latitud", location.latitude.toString())
                .add("altitud", location.longitude.toString())
                .add("id_usuario", userid.toString())
                .add("color", color.toString())
                .build()

            val request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()
            Log.d(TAG, request.toString())
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }
                override fun onResponse(call: Call, response: Response) {
                    println("Respuesta del servidor: ${response.body?.string()}")
                }
            })
        }
    }
}
