package com.inttelgo.tecnicos.logic.persistence

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.inttelgo.tecnicos.logic.Model.Jornada
import com.inttelgo.tecnicos.logic.Model.PrimerServicio
import com.inttelgo.tecnicos.logic.Model.UpdateJornadaRequest
import com.inttelgo.tecnicos.logic.repository.UsuarioRepository
import java.time.format.DateTimeFormatter

/**
 * Caché de jornada de la sesión y registro de primer_servicio.
 * Si primer_servicio ya existe, no se vuelve a enviar.
 */
object JornadaSession {
    private const val TAG = "JornadaSession"

    @Volatile
    var jornada: Jornada? = null
        private set

    fun update(jornada: Jornada?) {
        this.jornada = jornada
    }

    fun clear() {
        jornada = null
    }

    fun hasPrimerServicio(): Boolean = jornada?.primer_servicio != null

    /**
     * Registra primer_servicio en la jornada del día si aún no existe.
     * @param servicioId id de la tarea / ticket / instalación
     * @param tipo [com.inttelgo.tecnicos.logic.Model.PrimerServicioTipo]
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun registerPrimerServicioIfNeeded(
        context: Context,
        servicioId: String,
        tipo: String,
        repository: UsuarioRepository = UsuarioRepository()
    ) {
        val current = jornada
        if (current?.primer_servicio != null) {
            Log.d(TAG, "primer_servicio ya existe; no se actualiza")
            return
        }
        val jornadaId = current?.id
        if (jornadaId == null) {
            Log.w(TAG, "No hay jornada cargada; no se registra primer_servicio")
            return
        }
        val userId = UserPreferences(context).getUser()?.id
        if (userId == null) {
            Log.w(TAG, "No hay usuario; no se registra primer_servicio")
            return
        }
        val idNumerico = servicioId.toIntOrNull()
        if (idNumerico == null) {
            Log.w(TAG, "servicioId no numérico: $servicioId")
            return
        }

        val hora = WorkSchedule.nowBogota()
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val primerServicio = PrimerServicio(
            id = idNumerico,
            tipo = tipo,
            hora = hora
        )
        val body = UpdateJornadaRequest(primer_servicio = primerServicio)

        try {
            Log.d(TAG, "PUT primer_servicio id=$idNumerico tipo=$tipo hora=$hora")
            val response = repository.updateJornada(
                userId = userId.toString(),
                jornadaId = jornadaId.toString(),
                body = body
            )
            if (response.isSuccessful && response.body()?.success == true) {
                jornada = response.body()?.jornada
                    ?: current.copy(primer_servicio = primerServicio)
                Log.d(TAG, "primer_servicio registrado OK")
            } else {
                Log.w(
                    TAG,
                    "No se pudo registrar primer_servicio code=${response.code()} msg=${response.body()?.message}"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error registrando primer_servicio: ${e.message}", e)
        }
    }
}
