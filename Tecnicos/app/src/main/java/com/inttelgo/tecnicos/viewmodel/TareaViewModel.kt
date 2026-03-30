package com.inttelgo.tecnicos.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.inttelgo.tecnicos.logic.Model.Articulo
import com.inttelgo.tecnicos.logic.Model.FotoSoporte
import com.inttelgo.tecnicos.logic.Model.Filter
import com.inttelgo.tecnicos.logic.Model.ObsTarea
import com.inttelgo.tecnicos.logic.Model.Sorting
import com.inttelgo.tecnicos.logic.Model.Tarea
import com.inttelgo.tecnicos.logic.persistence.Localizacion
import com.inttelgo.tecnicos.logic.repository.TareaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TareaViewModel(private val repository: TareaRepository = TareaRepository() ) : ViewModel() {
    private val locationService: Localizacion= Localizacion()

    private val tag = "TareaViewModel"
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _tareasData = MutableStateFlow<List<Tarea>?>(emptyList())
    val tareasData: StateFlow<List<Tarea>?> = _tareasData

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _loadingEvidencias = MutableStateFlow(false)
    val loadingEvidencias: StateFlow<Boolean> = _loadingEvidencias

    private val _totalPages = MutableStateFlow(1)
    val totalPages: StateFlow<Int> = _totalPages

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage

    private val _consultCheck =  MutableStateFlow(false)
    val consultCheck: StateFlow<Boolean> = _consultCheck

    private val _tareaData = MutableStateFlow<Tarea?>(null)
    val tareaData: StateFlow<Tarea?> = _tareaData

    private val _histories = MutableStateFlow<List<ObsTarea>?>(emptyList())
    val histories : StateFlow<List<ObsTarea>?> = _histories

    private val _evidencias = MutableStateFlow<List<FotoSoporte>?>(emptyList())
    val evidencias: StateFlow<List<FotoSoporte>?> = _evidencias

    private val _isUploadingFile = MutableStateFlow(false)
    val isUploadingFile: StateFlow<Boolean> = _isUploadingFile

    private val _warningMessage = MutableStateFlow<String?>(null)
    val warningMessage: StateFlow<String?> = _warningMessage

    private val _uploadingLoading = MutableStateFlow(false)
    val uploadingLoading: StateFlow<Boolean> = _uploadingLoading

    private val _articlesData = MutableStateFlow<List<Articulo>?>(emptyList())
    val articlesData: StateFlow<List<Articulo>?> = _articlesData


    fun consultMoreTareas(filters: List<Filter>, limit: Int = 10, sorting: Sorting){
        _loading.value = true
        val gson = Gson()
        val formjson = gson.toJson(mapOf("filters" to filters, "pagination" to currentPage.value,"limit" to limit, "sorting" to sorting ))
        viewModelScope.launch {
            try {
                Log.d(tag, formjson)
                val result = repository.consultWitFilter(formjson, 1)
                if(result.isSuccessful){
                    result.body()?.let {
                        Log.d(tag, it.toString())
                        if(it.success){
                            if (currentPage.value == 1){
                                _tareasData.value = it.tareas
                            }else{
                                _tareasData.value = (_tareasData.value ?: emptyList()) + it.tareas
                            }
                            _totalPages.value = it.totalPages
                            _currentPage.value++
                        }else{
                            _errorMessage.value = it.mensaje
                        }
                    }
                }else{
                    _errorMessage.value = "Error al comunicarse con el servidor"
                }
            }catch (e: Exception){
                _errorMessage.value = "Ha ocurrido un error en la conexion"
                e.message?.let { Log.e(tag, it) }
            }finally {
                _loading.value = false
            }
        }
    }

    fun consultTareaById(id: String){
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = repository.consultById(id)
                if(result.isSuccessful){
                    result.body()?.let{
                        Log.d(tag, it.toString())
                        if(it.success){
                            _tareaData.value = it.tarea
                            _consultCheck.value = true
                        }else{
                            _errorMessage.value = it.mensaje
                        }
                    }
                }else{
                    _errorMessage.value = "Error al comunicarse con el servidor"
                }
            }catch (e: Exception){
                _errorMessage.value = "Ha ocurrido un error en la conexion"
                e.message?.let { Log.e(tag, it) }
            }finally {
                _loading.value = false
            }
        }
    }

    fun consultMoreObsByTarea(id: String, filters: List<Filter>,limit: Int = 10, sorting: Sorting){
        val gson = Gson()
        val form = gson.toJson(mapOf("filters" to filters, "pagination" to _currentPage.value, "limit" to limit, "sorting" to sorting))
        viewModelScope.launch {
            try {
                val result = repository.consultObsWitFilterAndId(id, form)
                if(result.isSuccessful){
                    result.body()?.let {
                        Log.d(tag, it.toString())
                        if(it.success){
                            if (currentPage.value == 1){
                                _histories.value = it.observaciones
                            }else{
                                _histories.value = ((_histories.value ?: emptyList()) + it.observaciones) as List<ObsTarea>?
                            }
                            _totalPages.value = it.totalPages
                            _currentPage.value++
                        }else{
                            _errorMessage.value = it.mensaje
                        }
                    }
                }else{
                    _errorMessage.value = "Error al comunicarse con el servidor"
                }
            }catch (e: Exception){
                _errorMessage.value = "Ha ocurrido un error en la conexion"
                e.message?.let { Log.e(tag, it) }
            }finally {
                _loading.value = false
            }
        }
    }

    fun consultEvidencias(id: String){
        _loadingEvidencias.value = true
        viewModelScope.launch {
            try {
                val result = repository.consultByObsTarea(id)
                if(result.isSuccessful){
                    result.body()?.let {
                        Log.d(tag, it.toString())
                        if(it.success){
                            _evidencias.value = it.evidencias
                        }else{
                            _errorMessage.value = it.mensaje
                        }
                    }
                }else{
                    _errorMessage.value = "Error al comunicarse con el servidor"
                }
            }catch (e: Exception){
                _errorMessage.value = "Ha ocurrido un error en la conexion"
                e.message?.let { Log.e(tag, it) }
            }finally {
                _loadingEvidencias.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun createObs(id:String, selectedImages: MutableState<List<Uri?>>, observacion: MutableState<String>, context: Context){
        _uploadingLoading.value = true
        viewModelScope.launch{
            val ubicacion = locationService.getUserLocation(context)
            ubicacion?.let {
                try {
                    val result = repository.createObs(
                        id,
                        observacion= observacion.value,
                        images = selectedImages.value,
                        latitud = it.latitude,
                        longitud = it.longitude,
                        context
                    )
                    if(result.isSuccessful){
                        result.body()?.let { data->
                            Log.d(tag, data.toString())
                            if(data.success){
                                _successMessage.value = data.mensaje
                            }else{
                                _errorMessage.value = data.mensaje
                            }
                        }
                    }else{
                        _errorMessage.value = "Error al comunicarse con el servidor"
                    }
                }catch (e: Exception){
                    _errorMessage.value = "Ha ocurrido un error en la conexion"
                    e.message?.let { error -> Log.e(tag, error) }
                }finally {
                    _uploadingLoading.value = false
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun finishObs(id:String, selectedImages: MutableState<List<Uri?>>, observacion: MutableState<String>, context: Context){
        _uploadingLoading.value = true
        viewModelScope.launch{
            val ubicacion = locationService.getUserLocation(context)
            ubicacion?.let {
                try {
                    val result = repository.finishObs(
                        id,
                        observacion= observacion.value,
                        images = selectedImages.value,
                        latitud = it.latitude,
                        longitud = it.longitude,
                        articulos = articlesData,
                        context
                    )
                    if(result.isSuccessful){
                        result.body()?.let { data->
                            Log.d(tag, data.toString())
                            if(data.success){
                                _successMessage.value = data.mensaje
                            }else{
                                _errorMessage.value = data.mensaje
                            }
                        }
                    }else{
                        _errorMessage.value = "Error al comunicarse con el servidor"
                    }
                }catch (e: Exception){
                    _errorMessage.value = "Ha ocurrido un error en la conexion"
                    e.message?.let { error -> Log.e(tag, error) }
                }finally {
                    _uploadingLoading.value = false
                }
            }
        }
    }

    fun consultArticles(id: String){
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getArticulosTarea(id)
                if (result.isSuccessful) {
                    result.body()?.let {
                        Log.d(tag, it.toString())
                        if(it.success){
                            _articlesData.value = it.articulos
                        }else{
                            _errorMessage.value = it.message
                        }
                    }
                }else{
                    _errorMessage.value = "Error al consultar las instalaciones"
                }
            } catch (e: Exception){
                _errorMessage.value = "Ha ocurrido un error en la conexion"
                e.message?.let { Log.e(tag, it) }
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateArticuloCantidad(articuloId: String, nuevaCantidad: Int) {
        _articlesData.value = _articlesData.value?.map { articulo ->
            if (articulo.id == articuloId) {
                articulo.copy(cantidad = nuevaCantidad)
            } else {
                articulo
            }
        } ?: emptyList()
    }

    fun clearEvidencias(){
        _evidencias.value = emptyList()
    }

    fun resetPagination() {
        _currentPage.value = 1
        _tareasData.value = emptyList()
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