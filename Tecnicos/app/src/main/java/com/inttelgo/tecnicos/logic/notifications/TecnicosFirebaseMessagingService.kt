package com.inttelgo.tecnicos.logic.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TecnicosFirebaseMessagingService : FirebaseMessagingService() {
    private val tag = "TecnicosFCM"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(tag, "Nuevo token FCM recibido")
        CoroutineScope(Dispatchers.IO).launch {
            FcmTokenManager.saveAndSyncToken(applicationContext, token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(tag, "Mensaje FCM recibido from=${message.from}")

        val title = message.notification?.title
            ?: message.data["title"]
            ?: getString(com.inttelgo.tecnicos.R.string.app_name)
        val body = message.notification?.body
            ?: message.data["body"]
            ?: message.data["mensaje"]
            ?: "Tienes una nueva notificación"

        // En primer plano hay que mostrar la notificación manualmente.
        // En segundo plano, si viene "notification", el sistema la muestra solo.
        if (message.notification != null || message.data.isNotEmpty()) {
            NotificationHelper.showNotification(
                context = applicationContext,
                title = title,
                body = body
            )
        }
    }
}
