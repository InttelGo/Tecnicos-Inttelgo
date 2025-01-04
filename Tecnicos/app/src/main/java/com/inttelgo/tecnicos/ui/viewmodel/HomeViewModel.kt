package com.inttelgo.tecnicos.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _processData = MutableStateFlow<Proceso?>(null)
    val processData: StateFlow<Proceso?> =_processData

    private val _checkProcess = MutableStateFlow(false)
    val checkProcess: StateFlow<Boolean> = _checkProcess

    private val _tickets = MutableStateFlow<List<Ticket>?>(null)
    val tickets: MutableStateFlow<List<Ticket>?> = _tickets

    fun searchProcess(search: String){
        val service = RetroFitServiceFactory.makeRetroFitService()
        viewModelScope.launch {
            try {
                val result = service.getProcessData("https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/process.php")}&search=${search}")
                _checkProcess.value = result.success
                _processData.value = result.proceso
                Log.d("searchProcess", "sucess: ${result.success}")
                Log.d("searchProcess", "Process: ${result.proceso}")
            }catch ( e: Exception ){
                _errorMessage.value = e.message
            }
        }
    }

    fun ticketsList(){
        val service = RetroFitServiceFactory.makeRetroFitService()
        viewModelScope.launch {
            try {
                val result = service.getTickets("https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/tickets.php")}")
                _checkProcess.value = result.success
                _tickets.value = result.tickets
                Log.d("ticketsList", "Tickets: ${result.tickets}")
            }catch ( e: Exception ){
                _errorMessage.value = e.message
            }
        }
    }
}