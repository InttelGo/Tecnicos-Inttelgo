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

class MainActivity : ComponentActivity() {

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Verificar y solicitar permisos de ubicación
        requestPermissionsIfNeeded()

        setContent {
            AppNavigation(this)
        }
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                startLocationService()
            } else {
                Toast.makeText(this, "Permisos denegados. La ubicación no estará disponible.", Toast.LENGTH_LONG).show()
            }
        }

    private fun requestPermissionsIfNeeded() {
        if (!checkPermissions()) {
            requestPermissionsLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            startLocationService()
        }
    }

    private fun checkPermissions(): Boolean {
        return checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("NewApi")
    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        startForegroundService(serviceIntent) // Usar startForegroundService para servicios en segundo plano en Android 8.0+
    }
}
