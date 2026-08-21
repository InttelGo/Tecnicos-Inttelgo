package com.inttelgo.tecnicos.network

import android.util.Log
import kotlinx.coroutines.delay
import retrofit2.Response
import java.io.IOException

object HttpRetry {
    private const val TAG = "HttpRetry"
    private val transientCodes = setOf(408, 429, 502, 503, 504)

    suspend fun <T> run(
        attempts: Int = 3,
        block: suspend () -> Response<T>
    ): Response<T> {
        var lastError: IOException? = null
        repeat(attempts) { index ->
            try {
                val response = block()
                val retryable = !response.isSuccessful && response.code() in transientCodes
                if (!retryable || index == attempts - 1) {
                    return response
                }
                Log.w(TAG, "HTTP ${response.code()} reintento ${index + 1}/$attempts")
                response.errorBody()?.close()
            } catch (e: IOException) {
                lastError = e
                if (index == attempts - 1) throw e
                Log.w(TAG, "IOException reintento ${index + 1}/$attempts: ${e.message}")
            }
            delay(2000L * (index + 1))
        }
        throw lastError ?: IOException("No se pudo completar la petición")
    }

    fun commsMessage(code: Int): String {
        val extra = when (code) {
            408, 504 -> " La red tardó demasiado. Reintenta o usa una red más estable."
            413 -> " Los archivos pesan demasiado."
            502, 503 -> " El servidor no respondió a tiempo. Reintenta."
            401, 403 -> " La sesión puede haber expirado."
            else -> ""
        }
        return "Error al comunicarse con el servidor (código $code).$extra"
    }
}
