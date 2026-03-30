package com.inttelgo.tecnicos.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inttelgo.tecnicos.logic.Model.UpdateProfileRequest
import com.inttelgo.tecnicos.logic.Model.UsuarioAuth
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import com.inttelgo.tecnicos.logic.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val context: Context? = null,
    private val repository: ProfileRepository = ProfileRepository(context)
) : ViewModel() {

    private val tag = "ProfileViewModel"

    private val _userProfile = MutableStateFlow<UsuarioAuth?>(null)
    val userProfile: StateFlow<UsuarioAuth?> = _userProfile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    fun loadUserProfile(context: Context) {
        _isLoading.value = true
        val user = UserPreferences(context).getUser()
        viewModelScope.launch {
            try {
                val result = repository.getUserProfile(user?.id.toString())
                if (result.isSuccessful) {
                    result.body()?.let { response ->
                        Log.d(tag, response.toString())
                        if (response.success) {
                            _userProfile.value = response.usuario
                        } else {
                            _errorMessage.value = response.message
                        }
                    }
                } else {
                    _errorMessage.value = "Error al cargar el perfil del usuario"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ha ocurrido un error en la conexión"
                e.message?.let { Log.e(tag, it) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(name: String, email: String, phone: String, context: Context) {
        val user = UserPreferences(context).getUser()
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.updateUserProfile(user?.id.toString(),
                    UpdateProfileRequest(
                        nombre_1 = name,
                        correo_personal = email,
                        telefono_1 = phone
                    )
                )
                if (result.isSuccessful) {
                    result.body()?.let { response ->
                        Log.d(tag, response.toString())
                        if (response.success) {
                            _userProfile.value = response.usuario
                            _successMessage.value = response.message
                        } else {
                            _errorMessage.value = response.message
                        }
                    }
                } else {
                    _errorMessage.value = "Error al actualizar el perfil"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ha ocurrido un error en la conexión"
                e.message?.let { Log.e(tag, it) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                repository.clearUserSession()
                _userProfile.value = null
            } catch (e: Exception) {
                Log.e(tag, "Error al cerrar sesión: ${e.message}")
            }
        }
    }

    fun clearMessages() {
        _successMessage.value = null
        _errorMessage.value = null
    }
}