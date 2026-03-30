package com.inttelgo.tecnicos.logic.process

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

open class OtherOperarions {
    private val tag = "OtherOperarions"

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatFechaBaseDatos(fechaRaw: String?): String {
        if (fechaRaw.isNullOrBlank()) return "---"

        return try {
            // El texto '2025-06-05T02:13:47.000Z' es un formato ISO_ZONED_DATE_TIME
            // Usamos ZonedDateTime porque termina en 'Z' (UTC)
            val fechaZonificada = ZonedDateTime.parse(fechaRaw)

            // Convertimos a la zona horaria local del teléfono del técnico
            val fechaLocal = fechaZonificada.toLocalDateTime()

            // Definir el formato de salida deseado (Ejem: 05/junio/2025 02:13)
            val formatterSalida = DateTimeFormatter.ofPattern(
                "dd/MMMM/yyyy HH:mm",
                Locale("es", "ES")
            )

            fechaLocal.format(formatterSalida)
        } catch (e: Exception) {
            Log.e(tag, "Error parseando fecha: $fechaRaw - Error: ${e.message}")

            // Intento de respaldo por si el formato cambia a uno sin zona horaria
            try {
                val limpia = fechaRaw.replace("T", " ").substringBefore(".")
                val formatterFallback = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val fecha = java.time.LocalDateTime.parse(limpia, formatterFallback)
                fecha.format(DateTimeFormatter.ofPattern("dd/MMMM/yyyy HH:mm", Locale("es", "ES")))
            } catch (e2: Exception) {
                fechaRaw // Si todo falla, devolvemos el original
            }
        }
    }
}