package com.inttelgo.tecnicos.logic.persistence

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

class Localizacion {

    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun getUserLocation(context: Context): Location?{
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //Validacion de que tenga algun tipo de proveedor de GPS
        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if(!isGPSEnabled){
            return null
        }
        return suspendCancellableCoroutine { con -> //lanza la corrutina para que siempre se este ejecutando
            fusedLocationProviderClient.lastLocation.apply {
                if(isComplete){
                    if(isSuccessful){
                        con.resume(result){}//Retorna la ubicacion si tiene proveedor de ubicacion
                    }else{
                        con.resume(null){} //Retorna un vacio poruqe no ha encontrado nafa
                    }
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener{
                    con.resume(it){} //Escuchador constante
                }
                addOnFailureListener{
                    con.resume(null){} //Falla en algun momento
                }
                addOnCanceledListener {
                    con.resume(null){} //Si se cancela el servicio
                }
            }
        }
    }
}
