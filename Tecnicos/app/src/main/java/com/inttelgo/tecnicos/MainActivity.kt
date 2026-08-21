package com.inttelgo.tecnicos

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.inttelgo.tecnicos.logic.notifications.NotificationHelper
import com.inttelgo.tecnicos.logic.persistence.LocationService
import com.inttelgo.tecnicos.navigation.AppNavigation
import com.inttelgo.tecnicos.network.RetrofitClient
import com.inttelgo.tecnicos.ui.theme.TecnicosTheme

class MainActivity : FragmentActivity() {

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar RetrofitClient con el contexto
        RetrofitClient.initialize(this)
        NotificationHelper.createChannels(this)

        // Verificar y solicitar permisos de ubicación / notificaciones
        requestPermissionsIfNeeded()

        setContent {
            TecnicosTheme (darkTheme = false) {
                AppNavigation(this)
            }
        }
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val locationGranted = (permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                    (permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true)
            if (locationGranted) {
                startLocationServiceIfNeeded()
            } else {
                Toast.makeText(this, "Permisos denegados. Algunas funciones no estarán disponibles.", Toast.LENGTH_LONG).show()
            }
        }

    private fun requestPermissionsIfNeeded() {
        val permissionsToRequest = mutableListOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        if (!checkPermissions(permissionsToRequest)) {
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            startLocationServiceIfNeeded()
        }
    }

    private fun checkPermissions(permissions: List<String>): Boolean {
        return permissions.all { checkSelfPermission(it) == android.content.pm.PackageManager.PERMISSION_GRANTED }
    }

    private fun startLocationServiceIfNeeded() {
        // Solo trackea en jornada laboral 8am-7pm (Bogotá).
        // Fuera de horario no se inicia el servicio (ni su notificación).
        LocationService.startIfWorkHours(this)
    }
}
