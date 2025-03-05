package com.inttelgo.tecnicos.logic.persistence

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.inttelgo.tecnicos.R

private const val CHANNEL_ID = "video_upload_channel"

fun showNotification(context: Context, title: String, message: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Video Compression"
        val descriptionText = "Notificación cuando el video se ha enviado"
        val importance = NotificationManager.IMPORTANCE_HIGH // IMPORTANTE: Debe ser HIGH o MAX en MIUI
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            enableLights(true)
            enableVibration(true)
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.logo_mano) // Asegúrate de tener un ícono válido
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_MAX) // IMPORTANTE: PRIORIDAD MÁXIMA en MIUI
        .setDefaults(NotificationCompat.DEFAULT_ALL) // Activa sonido y vibración
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notify(1, builder.build())
        }
    }
}
