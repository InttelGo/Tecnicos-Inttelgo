package com.inttelgo.tecnicos.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inttelgo.tecnicos.logic.Model.Barrio
import com.inttelgo.tecnicos.logic.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel (private val repository: HomeRepository = HomeRepository()) : ViewModel(){
    private val tag = "HomeViewModel"
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    private val _warningMessage = MutableStateFlow<String?>(null)
    val warningMessage: StateFlow<String?> = _warningMessage
    private val _barrios = MutableStateFlow<List<Barrio>?>(emptyList())
    val barrios: StateFlow<List<Barrio>?> = _barrios
    fun consultBarrios(){
        viewModelScope.launch {
            try {
                val result =  repository.consultAllNeighborhoods()
                if(result.isSuccessful){
                    result.body()?.let {
                        Log.d(tag, it.toString())
                        if(it.success){
                            _barrios.value = it.barrios
                        }else{
                            _errorMessage.value = it.mensaje
                        }
                    }
                }else{
                    _errorMessage.value = "Ocurrio un error en la consulta de los barrios"

                }

            }catch (e: Exception){
                _errorMessage.value = "Ha ocurrido un error en la conexion"
                e.message?.let { Log.e(tag, it) }
            }
        }
    }
}