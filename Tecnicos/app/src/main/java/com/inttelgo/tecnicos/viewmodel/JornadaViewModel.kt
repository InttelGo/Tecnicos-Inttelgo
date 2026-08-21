package com.inttelgo.tecnicos.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.inttelgo.tecnicos.logic.Model.BiometricCheck
import com.inttelgo.tecnicos.logic.Model.Jornada
import com.inttelgo.tecnicos.logic.Model.JornadaCheckType
import com.inttelgo.tecnicos.logic.Model.JornadaQrPayload
import com.inttelgo.tecnicos.logic.Model.UpdateJornadaRequest
import com.inttelgo.tecnicos.logic.persistence.JornadaSession
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import com.inttelgo.tecnicos.logic.persistence.WorkSchedule
import com.inttelgo.tecnicos.logic.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

sealed class JornadaUiState {
    data object Idle : JornadaUiState()
    data object Loading : JornadaUiState()
    data class Success(val message: String) : JornadaUiState()
    data class Error(val message: String) : JornadaUiState()
}

@RequiresApi(Build.VERSION_CODES.O)
class JornadaViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

    private val tag = "JornadaViewModel"
    private val gson = Gson()
    private val dayFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    private val _uiState = MutableStateFlow<JornadaUiState>(JornadaUiState.Idle)
    val uiState: StateFlow<JornadaUiState> = _uiState.asStateFlow()

    private val _jornada = MutableStateFlow<Jornada?>(null)
    val jornada: StateFlow<Jornada?> = _jornada.asStateFlow()

    private val _showIngresoButton = MutableStateFlow(false)
    val showIngresoButton: StateFlow<Boolean> = _showIngresoButton.asStateFlow()

    private val _showSalidaButton = MutableStateFlow(false)
    val showSalidaButton: StateFlow<Boolean> = _showSalidaButton.asStateFlow()

    private var isChecking = false
    private var isRegistering = false

    /** Evita repetir GET al navegar; solo se consulta al abrir la app / login. */
    private var hasLoadedThisSession = false

    private fun setJornada(value: Jornada?) {
        _jornada.value = value
        JornadaSession.update(value)
    }

    /**
     * Carga jornada una sola vez por sesión cuando ya se obtuvo con éxito.
     * Si falló el parseo/red, permite reintentar en el próximo loadJornadaOnce.
     */
    fun loadJornadaOnce(context: Context) {
        updateButtonVisibility()
        if (hasLoadedThisSession && _jornada.value?.id != null) return
        if (UserPreferences(context).getUser() == null) return
        if (isChecking) return
        refreshJornada(context)
    }

    /** Usar al cerrar sesión para permitir nueva carga en el próximo login. */
    fun resetSession() {
        hasLoadedThisSession = false
        setJornada(null)
        JornadaSession.clear()
        _uiState.value = JornadaUiState.Idle
        updateButtonVisibility()
    }

    private fun refreshJornada(context: Context) {
        if (isChecking || isRegistering) return
        val userId = UserPreferences(context).getUser()?.id ?: return
        val day = WorkSchedule.nowBogota().toLocalDate().format(dayFormatter)

        isChecking = true
        viewModelScope.launch {
            try {
                Log.d(tag, "GET jornada user=$userId day=$day")
                val response = repository.getJornadaByDay(userId.toString(), day)
                val body = response.body()
                // Si HTTP 200 pero body null, Gson falló al parsear (tipos incompatibles).
                if (response.isSuccessful && body == null) {
                    val raw = response.errorBody()?.string()
                    Log.e(tag, "HTTP OK pero body null (posible error de parseo Gson). rawError=$raw")
                    setJornada(null)
                    hasLoadedThisSession = false
                    return@launch
                }
                if (response.isSuccessful && body?.success == true && body.jornada?.id != null) {
                    setJornada(body.jornada)
                    hasLoadedThisSession = true
                    Log.d(
                        tag,
                        "Jornada cargada id=${body.jornada.id} id_jornada=${body.jornada.id_jornada} " +
                            "primer_servicio=${body.jornada.primer_servicio}"
                    )
                } else {
                    setJornada(null)
                    hasLoadedThisSession = false
                    Log.w(
                        tag,
                        "Jornada no disponible code=${response.code()} success=${body?.success} " +
                            "jornadaId=${body?.jornada?.id} msg=${body?.message} error=${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                hasLoadedThisSession = false
                Log.e(tag, "Error consultando jornada: ${e.message}", e)
            } finally {
                isChecking = false
                updateButtonVisibility()
            }
        }
    }

    fun updateButtonVisibility() {
        val jornada = _jornada.value
        val now = WorkSchedule.nowBogota()
        // Solo ingreso/salida: si están null o sin hora, se muestra el botón (según horario/bypass).
        _showIngresoButton.value = jornada?.ingreso?.hora.isNullOrBlank() &&
            WorkSchedule.shouldRequestCheckIn(now)
        _showSalidaButton.value = jornada?.salida?.hora.isNullOrBlank() &&
            WorkSchedule.shouldRequestCheckOut(now)
        Log.d(
            tag,
            "FAB visibility ingreso=${_showIngresoButton.value} salida=${_showSalidaButton.value} " +
                "ingresoHora=${jornada?.ingreso?.hora} salidaHora=${jornada?.salida?.hora} " +
                "jornadaId=${jornada?.id} bypass=${WorkSchedule.BYPASS_JORNADA_SCHEDULE}"
        )
    }

    fun parseJornadaQr(raw: String): JornadaQrPayload? {
        return try {
            val payload = gson.fromJson(raw, JornadaQrPayload::class.java)
            if (payload == null ||
                payload.id <= 0 ||
                payload.usuario.isBlank() ||
                payload.oficina.id <= 0 ||
                payload.oficina.descripcion.isBlank()
            ) {
                null
            } else {
                payload
            }
        } catch (e: Exception) {
            Log.e(tag, "QR inválido: $raw - ${e.message}")
            null
        }
    }

    fun registerAfterBiometric(
        context: Context,
        type: JornadaCheckType,
        qr: JornadaQrPayload
    ) {
        if (isRegistering) return
        val userId = UserPreferences(context).getUser()?.id ?: return
        val jornadaId = _jornada.value?.id
        if (jornadaId == null) {
            _uiState.value = JornadaUiState.Error("No hay jornada del día para actualizar")
            return
        }

        isRegistering = true
        _uiState.value = JornadaUiState.Loading

        val nowIso = WorkSchedule.nowBogota()
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        // Se guarda hora/huella + el contenido completo del QR.
        val check = BiometricCheck(
            hora = nowIso,
            huella = true,
            id = qr.id,
            usuario = qr.usuario,
            fecha_creacion = qr.fecha_creacion,
            oficina = qr.oficina
        )
        val body = when (type) {
            JornadaCheckType.INGRESO -> UpdateJornadaRequest(ingreso = check)
            JornadaCheckType.SALIDA -> UpdateJornadaRequest(salida = check)
        }

        viewModelScope.launch {
            try {
                val response = repository.updateJornada(
                    userId = userId.toString(),
                    jornadaId = jornadaId.toString(),
                    body = body
                )
                if (!response.isSuccessful || response.body()?.success != true) {
                    _uiState.value = JornadaUiState.Error(
                        response.body()?.message ?: "No se pudo registrar ${type.name.lowercase()}"
                    )
                    return@launch
                }

                _jornada.value = response.body()?.jornada ?: when (type) {
                    JornadaCheckType.INGRESO -> _jornada.value?.copy(ingreso = check)
                    JornadaCheckType.SALIDA -> _jornada.value?.copy(salida = check)
                }
                JornadaSession.update(_jornada.value)
                val label = if (type == JornadaCheckType.INGRESO) "Ingreso" else "Salida"
                _uiState.value = JornadaUiState.Success("$label registrado correctamente")
                updateButtonVisibility()
            } catch (e: Exception) {
                Log.e(tag, "Error registrando jornada: ${e.message}", e)
                _uiState.value = JornadaUiState.Error("Error de conexión al registrar jornada")
            } finally {
                isRegistering = false
            }
        }
    }

    fun clearMessage() {
        if (_uiState.value is JornadaUiState.Error || _uiState.value is JornadaUiState.Success) {
            _uiState.value = JornadaUiState.Idle
        }
    }
}
