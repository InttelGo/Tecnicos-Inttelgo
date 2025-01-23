package com.inttelgo.tecnicos.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inttelgo.tecnicos.logic.Model.ObsTicket
import com.inttelgo.tecnicos.logic.Model.Picture
import com.inttelgo.tecnicos.logic.Model.RetroFitService
import com.inttelgo.tecnicos.logic.Model.Ticket
import com.inttelgo.tecnicos.logic.RetroFitServiceFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SupportViewModel : ViewModel(){

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _warningMessage = MutableStateFlow<String?>(null)
    val warningMessage: StateFlow<String?> = _warningMessage

    private val _supportCheck = MutableStateFlow(false)
    val supportCheck: StateFlow<Boolean> = _supportCheck

    private val _supportData = MutableStateFlow<Ticket?>(null)
    val supportData: StateFlow<Ticket?> = _supportData

    private val _listCheck = MutableStateFlow(false)
    val listCheck: StateFlow<Boolean> = _listCheck

    private val _observationList = MutableStateFlow<List<ObsTicket>?>(null)
    val observationList: StateFlow<List<ObsTicket>?> = _observationList

    private val _picturesCheck = MutableStateFlow(false)
    val picturesCheck: StateFlow<Boolean> = _picturesCheck

    private val _pictureList = MutableStateFlow<List<Picture>?>(null)
    val pictureList: StateFlow<List<Picture>?> = _pictureList

    fun getSupportData(idTicket: String){
        val service = RetroFitServiceFactory.makeRetroFitService()
        viewModelScope.launch {
            try {
                Log.d("SupportList", idTicket)
                Log.d("SupportList","https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/ticket.php")}&id=$idTicket")
                val result = service.getSupport("https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/ticket.php")}&id=$idTicket")
                _supportCheck.value = result.success
                _supportData.value = result.ticket
                Log.d("SupportList", "Ticket: ${result.ticket}")
            }catch ( e: Exception ){
                _errorMessage.value = e.message
            }
        }
    }

    fun getObs(idTicket: String){
        val service = RetroFitServiceFactory.makeRetroFitService()
        viewModelScope.launch {
            try {
                Log.d("SupportList", "https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/obs_ticket.php")}&id=$idTicket")
                val result = service.getObs("https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/obs_ticket.php")}&id=$idTicket")
                _listCheck.value = result.sucess
                _observationList.value = result.obs_ticket
                if(result.sucess && result.obs_ticket.isEmpty()){
                    _warningMessage.value = "Este ticket no tiene observaciones"
                }
                if(!result.sucess && result.obs_ticket.isEmpty()){
                    _errorMessage.value = "Ha ocurrido un error en la consulta"
                }
                Log.d("SupportList", "Ticket: ${result.obs_ticket}")
            }catch ( e: Exception ){
                _errorMessage.value = e.message
            }
        }
    }

    fun getImgs(idTicket: String){
        val service = RetroFitServiceFactory.makeRetroFitService()
        viewModelScope.launch {
            try {
                val result = service.getPictures("https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/obs_ticket.php")}&idobs=$idTicket")
                _picturesCheck.value = result.sucess
                _pictureList.value = result.pictures
                Log.d("SupportList", "Ticket: ${result.pictures}")
            }catch ( e: Exception ){
                _errorMessage.value = e.message
            }
        }
    }
}