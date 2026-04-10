package com.inttelgo.tecnicos.viewmodel

import android.annotation.SuppressLint
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
import com.inttelgo.tecnicos.logic.Model.Articulo
import com.inttelgo.tecnicos.logic.Model.Filter
import com.inttelgo.tecnicos.logic.Model.FotoInsta
import com.inttelgo.tecnicos.logic.Model.Proceso
import com.inttelgo.tecnicos.logic.Model.Sorting
import com.inttelgo.tecnicos.logic.Model.updateInstallationBody
import com.inttelgo.tecnicos.logic.persistence.Localizacion
import com.inttelgo.tecnicos.logic.repository.ProcesoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProcesoViewModel(private val repository: ProcesoRepository = ProcesoRepository()) :
    ViewModel() {

    private val tag = "ProcesoViewModel"

    private val locationService: Localizacion= Localizacion()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _successFinishMessage = MutableStateFlow<String?>(null)
    val successFinishMessage: StateFlow<String?> = _successFinishMessage

    private val _warningMessage = MutableStateFlow<String?>(null)
    val warningMessage: StateFlow<String?> = _warningMessage

    private val _procesosData = MutableStateFlow<List<Proceso>?>(emptyList())
    val procesosData: StateFlow<List<Proceso>?> = _procesosData

    private val _checkProcessData = MutableStateFlow(false)
    val checkProcessData: StateFlow<Boolean> = _checkProcessData

    private val _totalPages = MutableStateFlow(1)
    val totalPages: StateFlow<Int> = _totalPages

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoadingInstalacion = MutableStateFlow(false)
    val isLoadingInstalacion: StateFlow<Boolean> = _isLoadingInstalacion

    private val _successChangeStatus = MutableStateFlow(false)
    val successChangeStatus: StateFlow<Boolean> = _successChangeStatus

    private val _articlesData= MutableStateFlow<List<Articulo>>(emptyList())
    val articlesData: StateFlow<List<Articulo>> = _articlesData

    private val _checkArticlesData = MutableStateFlow(true)
    val checkArticlesData: StateFlow<Boolean> = _checkArticlesData

    private val _isUploadingFile = MutableStateFlow(true)
    val isUploadingFile: StateFlow<Boolean> = _isUploadingFile

    private val _retrofitLoading = MutableStateFlow(false)
    val retrofitLoading: StateFlow<Boolean> = _retrofitLoading

    private val _selectedImages = MutableStateFlow<List<FotoInsta>?>(emptyList())
    val selectedImages: StateFlow<List<FotoInsta>?> = _selectedImages
    @SuppressLint("NewApi")
    fun consultMoreProcess(filters: List<Filter>, limit: Int = 10, sorting: List<Sorting>) {
        _isLoading.value = true
        val gson = Gson()
        val filtersjson = gson.toJson(filters)
        val sortingjson = gson.toJson(sorting)
        viewModelScope.launch {
            try {
                val result =
                    repository.consultWithFilter(filtersjson, currentPage.value, limit, sortingjson)
                if (result.isSuccessful) {
                    result.body()?.let {
                        if (currentPage.value == 1 || filters[0].operator == "equals") {
                            _procesosData.value = it.procesos
                        } else {
                            _procesosData.value =
                                (_procesosData.value ?: emptyList()) + it.procesos
                        }
                        _totalPages.value = it.total
                        _checkProcessData.value = true
                        _currentPage.value++
                    }
                } else {
                    _errorMessage.value = "Error al consultar las instalaciones"
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Ha ocurrido un error en la conexion"
                e.message?.let { Log.e(tag, it) }
            }
        }
    }

    fun update(proceso: Proceso, updateBody: updateInstallationBody, navigateToUploadImage: (id: String, type: String) -> Unit) {
        _isLoadingInstalacion.value = true
        viewModelScope.launch {
            try {
                if (proceso.estado?.id == 7) {
                    val result =  repository.update(proceso.id.toString(), updateBody)
                    result.let {
                        if (it.isSuccessful) {
                            result.body()?.let {
                                _successChangeStatus.value = it.success
                                _successMessage.value = it.message
                            }
                            navigateToUploadImage(proceso.id.toString(), "Proceso")
                            _isLoadingInstalacion.value = false
                        } else {
                            _errorMessage.value = "Error al consultar las instalaciones"
                        }
                    }
                } else {
                    navigateToUploadImage(proceso.id.toString(), "Proceso")
                    _isLoadingInstalacion.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ha ocurrido un error en la conexion"
                e.message?.let { Log.e(tag, it) }
            }
        }
    }

    fun consultEvicencias(id: String) {
        viewModelScope.launch {
            try {
                val result = repository.consultEvicencias(id)
                if (result.isSuccessful) {
                    result.body()?.let {
                        _selectedImages.value = it.medias
                    }
                    _isLoadingInstalacion.value = false
                } else {
                    _errorMessage.value = "Error al consultar las instalaciones"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ha ocurrido un error en la conexion"
                e.message?.let { Log.e(tag, it) }
            }
        }
    }

    fun consultArticulos(id: String){
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getArticulosInstalacion(id)
                if (result.isSuccessful) {
                    result.body()?.let {
                        Log.d(tag, it.toString())
                        if(it.success){
                            _articlesData.value = it.articulos
                            _checkArticlesData.value = it.success
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
                _isLoading.value = false
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

    @RequiresApi(Build.VERSION_CODES.P)
    fun createEvidencia(id: String, uri: Uri, context: Context) {
        _isUploadingFile.value = true
        // Log del archivo
        try {
            val fileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
            val fileSizeBytes = fileDescriptor?.statSize ?: 0
            val fileSizeMB = fileSizeBytes / (1024.0 * 1024.0)
            val mimeType = context.contentResolver.getType(uri)
            fileDescriptor?.close()
        } catch (e: Exception) {
            Log.e(tag, "❌ [createEvidencia] Error leyendo metadata del archivo: ${e.message}")
        }

        viewModelScope.launch {
            val ubicacion = locationService.getUserLocation(context)

            if (ubicacion == null) {
                Log.e(tag, "❌ [createEvidencia] No se pudo obtener la ubicación, abortando subida")
                _errorMessage.value = "No se pudo obtener la ubicación"
                _isUploadingFile.value = false
                return@launch
            }

            try {
                val startTime = System.currentTimeMillis()

                val result = repository.createEvidencia(
                    id,
                    image = uri,
                    latitud = ubicacion.latitude,
                    longitud = ubicacion.longitude,
                    context
                )

                val elapsed = System.currentTimeMillis() - startTime

                if (result.isSuccessful) {
                    result.body()?.let { resp ->

                        if (resp.success) {
                            resp.evidencia?.let { foto ->
                                _selectedImages.value = _selectedImages.value?.plus(listOf(foto))
                            } ?: Log.w(tag, "⚠️ [createEvidencia] success=true pero evidencia es null")
                        } else {
                            Log.e(tag, "❌ [createEvidencia] El servidor respondió success=false")
                        }
                    } ?: Log.e(tag, "❌ [createEvidencia] Body es null")
                } else {
                    val errorBody = result.errorBody()?.string()
                    Log.e(tag, "❌ [createEvidencia] Error HTTP ${result.code()}: $errorBody")
                    _errorMessage.value = "Error al subir el archivo (${result.code()})"
                }

            } catch (e: Exception) {
                Log.e(tag, "❌ [createEvidencia] Excepción: ${e::class.simpleName} - ${e.message}")
                Log.e(tag, "❌ [createEvidencia] StackTrace: ${e.stackTraceToString()}")
                _errorMessage.value = "Ha ocurrido un error en la conexion"
            } finally {
                _isUploadingFile.value = false
            }
        }
    }
    fun removeMedia(media: FotoInsta){
        viewModelScope.launch {
            val result = repository.deleteImage(media.id)

            if(result.isSuccessful){
                result.body()?.let { it ->
                    if(it.success){
                        _selectedImages.value = _selectedImages.value?.minus(media)
                    }else{
                        _errorMessage.value = it.message
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun finish(
        id: String,
        observacion: MutableState<String>,
        signatureBitmap: Bitmap?,
        context: Context
    ) {
        if (observacion.value.isEmpty()) {
            _errorMessage.value = "Todos los campos son requeridos"
            _errorMessage.value = null
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                var articulosjson = ""
                val articulosFiltrados = articlesData.value?.filter { it.cantidad > 0 } ?: emptyList()
                if (articulosFiltrados.isNotEmpty()) {
                    val gson = Gson()
                    articulosjson = gson.toJson(articulosFiltrados)
                }
                val result = repository.finish(
                    id,
                    observacion = observacion.value,
                    signatureBitmap,
                    context,
                    articulos = articulosjson
                )

                if (result.isSuccessful) {
                    result.body()?.let { it ->
                        if (it.success) {
                            _successFinishMessage.value = it.message
                        }
                    }
                }
            }
        }
    }

    // En ProcesoViewModel
    fun updateArticulo(updatedArticle: Articulo) {
        _articlesData.value = _articlesData.value.map {
            if (it.id == updatedArticle.id) updatedArticle else it
        }
    }


    fun resetPagination() {
        _currentPage.value = 1
        _procesosData.value = emptyList()
        _checkProcessData.value = false
    }

    fun clearMessages(){
        _successMessage.value = null
        _errorMessage.value = null
        _warningMessage.value = null
        _successFinishMessage.value = null
    }

}