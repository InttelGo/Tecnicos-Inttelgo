package com.inttelgo.tecnicos.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.inttelgo.tecnicos.logic.Model.EvidenciaMedia
import com.inttelgo.tecnicos.logic.Model.Filter
import com.inttelgo.tecnicos.logic.Model.ObsTicket
import com.inttelgo.tecnicos.logic.Model.Sorting
import com.inttelgo.tecnicos.logic.Model.Ticket
import com.inttelgo.tecnicos.logic.Model.PrimerServicioTipo
import com.inttelgo.tecnicos.logic.persistence.JornadaSession
import com.inttelgo.tecnicos.logic.persistence.Localizacion
import com.inttelgo.tecnicos.logic.repository.SoporteRepository
import com.inttelgo.tecnicos.network.HttpRetry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class SoporteViewModel (private val repository: SoporteRepository = SoporteRepository()): ViewModel(){

    private val locationService: Localizacion= Localizacion()

    private val tag = "SoporteViewModel"
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _ticketsData = MutableStateFlow<List<Ticket>?>(emptyList())
    val ticketsData: StateFlow<List<Ticket>?> = _ticketsData

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _totalPages = MutableStateFlow(1)
    val totalPages: StateFlow<Int> = _totalPages

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage

    private val _consultCheck =  MutableStateFlow(false)
    val consultCheck: StateFlow<Boolean> = _consultCheck

    private val _ticketData = MutableStateFlow<Ticket?>(null)
    val ticketData: StateFlow<Ticket?> = _ticketData

    private val _histories = MutableStateFlow<List<ObsTicket>?>(emptyList())
    val histories : StateFlow<List<ObsTicket>?> = _histories

    // null = no se ha consultado; lista vacía = observación sin evidencias
    private val _evidencias = MutableStateFlow<List<EvidenciaMedia>?>(null)
    val evidencias: StateFlow<List<EvidenciaMedia>?> = _evidencias

    private val _loadingEvidencias = MutableStateFlow(false)
    val loadingEvidencias: StateFlow<Boolean> = _loadingEvidencias

    private val _isUploadingFile = MutableStateFlow(false)
    val isUploadingFile: StateFlow<Boolean> = _isUploadingFile

    private val _warningMessage = MutableStateFlow<String?>(null)
    val warningMessage: StateFlow<String?> = _warningMessage

    private val _uploadingLoading = MutableStateFlow(false)
    val uploadingLoading: StateFlow<Boolean> = _uploadingLoading

    fun consultMoreTickets(filters: List<Filter>, limit: Int = 10, sorting: Sorting){
        _loading.value = true
        val gson = Gson()
        val filtersjson = gson.toJson(filters)
        val sortingjson = gson.toJson(sorting)
        viewModelScope.launch {
            try {
                val result = repository.consultWitFilter(filtersjson, currentPage.value, limit, sortingjson)
                if(result.isSuccessful){
                    result.body()?.let {
                        Log.d(tag, it.toString())
                        if(it.success){
                            if (currentPage.value == 1){
                                _ticketsData.value = it.tickets
                            }else{
                                _ticketsData.value = (_ticketsData.value ?: emptyList()) + it.tickets
                            }
                            _totalPages.value = it.totalPages
                            _currentPage.value++
                        }else{
                            _errorMessage.value = it.mensaje
                        }
                    }
                }else{
                    _errorMessage.value = HttpRetry.commsMessage(result.code())
                }
            }catch (e: Exception){
                _errorMessage.value = "Ha ocurrido un error en la conexion"
                e.message?.let { Log.e(tag, it) }
            }finally {
                _loading.value = false
            }
        }
    }

    fun consultTicketById(id: String){
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = repository.consultById(id)
                if(result.isSuccessful){
                    result.body()?.let{
                        Log.d(tag, it.toString())
                        if(it.success){
                            _ticketData.value = it.ticket
                            _consultCheck.value = true
                        }else{
                            _errorMessage.value = it.mensaje
                        }
                    }
                }else{
                    _errorMessage.value = HttpRetry.commsMessage(result.code())
                }
            }catch (e: Exception){
                _errorMessage.value = "Ha ocurrido un error en la conexion"
                e.message?.let { Log.e(tag, it) }
            }finally {
                _loading.value = false
            }
        }
    }

    fun consultMoreObsByTicket(id: String, filters: List<Filter>,limit: Int = 10, sorting: Sorting){
        val gson = Gson()
        val filtersJson = gson.toJson(filters)
        val sortingJson = gson.toJson(sorting)
        viewModelScope.launch {
            try {
                val result = repository.consultObsWitFilterAndId(id, filtersJson, currentPage.value, limit, sortingJson)
                if(result.isSuccessful){
                    result.body()?.let {
                        Log.d(tag, it.toString())
                        if(it.success){
                            if (currentPage.value == 1){
                                _histories.value = it.observaciones
                            }else{
                                _histories.value = ((_histories.value ?: emptyList()) + it.observaciones) as List<ObsTicket>?
                            }
                            _totalPages.value = it.totalPages
                            _currentPage.value++
                        }else{
                            _errorMessage.value = it.mensaje
                        }
                    }
                }else{
                    _errorMessage.value = HttpRetry.commsMessage(result.code())
                }
            }catch (e: Exception){
                _errorMessage.value = "Ha ocurrido un error en la conexion"
                e.message?.let { Log.e(tag, it) }
            }finally {
                _loading.value = false
            }
        }
    }

    fun consultEvidencias(idTicket: String, idObservacion: String){
        _loadingEvidencias.value = true
        viewModelScope.launch {
            try {
                val result = repository.consultByObsTicket(idTicket, idObservacion)
                if(result.isSuccessful){
                    result.body()?.let {
                        Log.d(tag, it.toString())
                        val list = it.resolvedEvidencias()
                        if (it.success || it.observacion != null || it.evidencias != null) {
                            _evidencias.value = list
                        } else {
                            _errorMessage.value = it.mensaje
                        }
                    }
                }else{
                    _errorMessage.value = HttpRetry.commsMessage(result.code())
                }
            }catch (e: Exception){
                _errorMessage.value = "Ha ocurrido un error en la conexion"
                e.message?.let { Log.e(tag, it) }
            } finally {
                _loadingEvidencias.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun createObs(
        id: String,
        selectedImages: MutableState<List<Uri?>>,
        observacion: MutableState<String>,
        signatureBitmap: Bitmap?,
        context: Context,
        esEncargado: Boolean,
        nombreEncargado: String?,
        identificacionEncargado: String?
    ){
        _uploadingLoading.value = true

        viewModelScope.launch{
            try {
                val ubicacion = locationService.getUserLocation(context)
                if (ubicacion == null) {
                    _errorMessage.value = "No se pudo obtener la ubicación"
                    return@launch
                }

                val result = repository.createObs(
                    id,
                    observacion= observacion.value,
                    images = selectedImages.value,
                    latitud = ubicacion.latitude,
                    longitud = ubicacion.longitude,
                    context = context,
                    signatureBitmap = signatureBitmap,
                    esEncargado = esEncargado,
                    nombreEncargado = nombreEncargado,
                    identificacionEncargado = identificacionEncargado
                )
                if(result.isSuccessful){
                    result.body()?.let { data->
                        Log.d(tag, data.toString())
                        if(data.success){
                            JornadaSession.registerPrimerServicioIfNeeded(
                                context = context,
                                servicioId = id,
                                tipo = PrimerServicioTipo.TICKET
                            )
                            _successMessage.value = data.mensaje?.takeIf { it.isNotBlank() }
                                ?: "Observación registrada correctamente"
                        }else{
                            _errorMessage.value = data.mensaje?.takeIf { it.isNotBlank() }
                                ?: "No se pudo registrar la observación"
                        }
                    } ?: run {
                        _errorMessage.value = "Respuesta vacía del servidor"
                    }
                }else{
                    _errorMessage.value = HttpRetry.commsMessage(result.code())
                }
            }catch (e: Exception){
                _errorMessage.value = "Ha ocurrido un error en la conexion"
                e.message?.let { error -> Log.e(tag, error) }
            }finally {
                _uploadingLoading.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun finishObs(
        id: String,
        selectedImages: MutableState<List<Uri?>>,
        observacion: MutableState<String>,
        signatureBitmap: Bitmap?,
        context: Context,
        esEncargado: Boolean,
        nombreEncargado: String?,
        identificacionEncargado: String?
    ){
        _uploadingLoading.value = true

        viewModelScope.launch{
            try {
                val ubicacion = locationService.getUserLocation(context)
                if (ubicacion == null) {
                    _errorMessage.value = "No se pudo obtener la ubicación"
                    return@launch
                }

                val result = repository.finishObs(
                    id,
                    observacion= observacion.value,
                    images = selectedImages.value,
                    latitud = ubicacion.latitude,
                    longitud = ubicacion.longitude,
                    signatureBitmap = signatureBitmap,
                    context = context,
                    esEncargado = esEncargado,
                    nombreEncargado = nombreEncargado,
                    identificacionEncargado = identificacionEncargado
                )
                if(result.isSuccessful){
                    result.body()?.let { data->
                        Log.d(tag, data.toString())
                        if(data.success){
                            JornadaSession.registerPrimerServicioIfNeeded(
                                context = context,
                                servicioId = id,
                                tipo = PrimerServicioTipo.TICKET
                            )
                            _successMessage.value = data.mensaje?.takeIf { it.isNotBlank() }
                                ?: "Ticket finalizado correctamente"
                        }else{
                            _errorMessage.value = data.mensaje?.takeIf { it.isNotBlank() }
                                ?: "No se pudo finalizar el ticket"
                        }
                    } ?: run {
                        _errorMessage.value = "Respuesta vacía del servidor"
                    }
                }else{
                    _errorMessage.value = HttpRetry.commsMessage(result.code())
                }
            }catch (e: Exception){
                _errorMessage.value = "Ha ocurrido un error en la conexion"
                e.message?.let { error -> Log.e(tag, error) }
            }finally {
                _uploadingLoading.value = false
            }
        }
    }

    fun clearEvidencias(){
        _evidencias.value = null
    }

    fun resetPagination() {
        _currentPage.value = 1
        _ticketsData.value = emptyList()
    }

    fun clearHistories(){
        _histories.value = emptyList()
        _currentPage.value = 1
    }

    fun clearMessages(){
        _successMessage.value=null
        _warningMessage.value=null
        _errorMessage.value=null
    }
}