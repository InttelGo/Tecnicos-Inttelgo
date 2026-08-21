package com.inttelgo.tecnicos.logic.notifications

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.inttelgo.tecnicos.logic.Model.Request.FcmTokenRequest
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import com.inttelgo.tecnicos.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object FcmTokenManager {
    private const val TAG = "FcmTokenManager"

    fun registerCurrentToken(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                saveAndSyncToken(context, token, forceSync = true)
            } catch (e: Exception) {
                Log.e(TAG, "No se pudo obtener el token FCM: ${e.message}")
            }
        }
    }

    suspend fun saveAndSyncToken(
        context: Context,
        token: String,
        forceSync: Boolean = false
    ) {
        val preferences = UserPreferences(context)
        val previous = preferences.getFcmToken()
        preferences.saveFcmToken(token)

        Log.d(TAG, "FCM token: $token")

        if (preferences.getToken().isNullOrBlank()) {
            Log.d(TAG, "Usuario sin sesión; token guardado localmente")
            return
        }

        if (!forceSync && token == previous) {
            Log.d(TAG, "Token FCM sin cambios")
            return
        }

        try {
            val response = RetrofitClient.api.registerFcmToken(FcmTokenRequest(token))
            if (response.isSuccessful && response.body()?.success == true) {
                Log.d(TAG, "Token FCM sincronizado con el servidor")
            } else {
                Log.w(
                    TAG,
                    "No se pudo sincronizar token FCM (${response.code()}). " +
                        "Verifica el endpoint POST usuario/fcm-token en el backend."
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sincronizando token FCM: ${e.message}")
        }
    }
}
