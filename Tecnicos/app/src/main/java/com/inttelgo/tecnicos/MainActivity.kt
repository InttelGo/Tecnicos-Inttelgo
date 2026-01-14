package com.inttelgo.tecnicos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.inttelgo.tecnicos.logic.persistence.LocationService
import com.inttelgo.tecnicos.navigation.AppNavigation
import com.inttelgo.tecnicos.network.RetrofitClient
import com.inttelgo.tecnicos.ui.theme.TecnicosTheme

class MainActivity : ComponentActivity() {

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar RetrofitClient con el contexto
        RetrofitClient.initialize(this)

        // Verificar y solicitar permisos de ubicación
        requestPermissionsIfNeeded()

        setContent {
            TecnicosTheme (darkTheme = false) {
                AppNavigation(this)
            }
        }
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                startLocationService()
            } else {
                Toast.makeText(this, "Permisos denegados. Algunas funciones no estarán disponibles.", Toast.LENGTH_LONG).show()
            }
        }

    private fun requestPermissionsIfNeeded() {
        val permissionsToRequest = mutableListOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // Si el dispositivo es Android 13+, agregamos el permiso de notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        if (!checkPermissions(permissionsToRequest)) {
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            startLocationService()
        }
    }

    private fun checkPermissions(permissions: List<String>): Boolean {
        return permissions.all { checkSelfPermission(it) == android.content.pm.PackageManager.PERMISSION_GRANTED }
    }

    @SuppressLint("NewApi")
    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        startForegroundService(serviceIntent) // Usar startForegroundService para servicios en segundo plano en Android 8.0+
    }
}