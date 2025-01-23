package com.inttelgo.tecnicos.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inttelgo.tecnicos.logic.Model.Barrio
import com.inttelgo.tecnicos.logic.Model.Proceso
import com.inttelgo.tecnicos.logic.Model.RetroFitService
import com.inttelgo.tecnicos.logic.Model.Ticket
import com.inttelgo.tecnicos.logic.RetroFitServiceFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel(){

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _warningMessage = MutableStateFlow<String?>(null)
    val warningMessage: StateFlow<String?> = _warningMessage

    private val _checkProcessData = MutableStateFlow(false)
    val checkProcessData: StateFlow<Boolean> = _checkProcessData

    private val _checkBarrios = MutableStateFlow(false)
    val checkBarrios: StateFlow<Boolean> = _checkBarrios

    private val _processData = MutableStateFlow<Proceso?>(null)
    val processData: StateFlow<Proceso?> =_processData

    private val _checkProcess = MutableStateFlow(false)
    val checkProcess: StateFlow<Boolean> = _checkProcess

    private val _tickets = MutableStateFlow<List<Ticket>?>(null)
    val tickets: MutableStateFlow<List<Ticket>?> = _tickets

    private val _barrios = MutableStateFlow<List<Barrio>?>(null)
    val barrios: StateFlow<List<Barrio>?> = _barrios

    fun searchProcess(search: String){ //buscar el proceso
        // Expresión regular que busca caracteres que no sean dígitos
        val regex = "[^0-9]".toRegex()
        if(regex.containsMatchIn(search)){ //Si contiene digitos no realiza el proceso
            _errorMessage.value = "El proceso $search contiene caracteres no válidos"
            return
        }else{
            val service = RetroFitServiceFactory.makeRetroFitService()//Lammado al retrofit para la solicitud GET
            viewModelScope.launch {
                try {
                    /*
                    * La funcion
                    * */
                    Log.d("searchProcess", "https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/process.php")}&search=${search}")
                    val result = service.getProcessData("https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/process.php")}&search=${search}")

                    _checkProcessData.value = result.success
                    _processData.value = result.proceso
                    Log.d("searchProcess", "sucess: ${result.success}")
                    Log.d("searchProcess", "Process: ${result.proceso}")
                    if(!result.success){
                        _warningMessage.value = "El proceso $search no existe"
                    }
                }catch ( e: Exception ){
                    _errorMessage.value = e.message
                }
            }
        }
    }

    fun ticketsList(prioritySelected: MutableState<Int>) {
        val service = RetroFitServiceFactory.makeRetroFitService()
        Log.d("ticketsList", prioritySelected.value.toString())
        viewModelScope.launch {
            try {
                Log.d("ticketsList", "sucess: ${RetroFitService.encodeToBase64("pages/ticket.php")}")
                val result = service.getTickets("https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/ticket.php")}&prioridad=${prioritySelected.value}")
                _checkProcess.value = result.success
                _tickets.value = result.tickets
                Log.d("ticketsList", "sucess: ${result.success}")
                Log.d("ticketsList", "Process: ${result.tickets}")
                val result2 = service.getBarrios("https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/barrio.php")}")
                _checkBarrios.value = result2.success
                _barrios.value = result2.barrios
                Log.d("ticketsList", "sucess: ${result2.success}")
                Log.d("ticketsList", "Process: ${result2.barrios}")
            }catch ( e: Exception ){
                _errorMessage.value = e.message
            }
        }
    }

    fun ActualizarEstadoI(
        idInstalacion: String,
        estado: Int,
        id: String,
        proceso: Proceso?,
        navigateToUploadImage: (id: String, type: String) -> Unit
    ) {
        val service = RetroFitServiceFactory.makeRetroFitService()
        viewModelScope.launch {
            try {
                Log.d("Actualizar estado I", "https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/process.php")}&id=$idInstalacion&estado=$estado&fecha_ini=${proceso?.fecha_ini}&id_tec_ini=$id")
                val result = service.setInstalacion("https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/process.php")}&id=$idInstalacion&estado=$estado&fecha_ini=${proceso?.fecha_ini}&id_tec_ini=$id")
                Log.d("Actualizar estado I", result.toString())
                navigateToUploadImage( idInstalacion, "Proceso")
            }catch (e: Exception){
                _errorMessage.value = e.message
            }
        }
    }

    fun setNofifies(){
        _errorMessage.value = null
        _warningMessage.value = null
    }
}