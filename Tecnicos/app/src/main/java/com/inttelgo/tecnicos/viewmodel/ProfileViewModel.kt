package com.inttelgo.tecnicos.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inttelgo.tecnicos.logic.Model.UpdateProfileRequest
import com.inttelgo.tecnicos.logic.Model.UserProfile
import com.inttelgo.tecnicos.logic.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val context: Context? = null,
    private val repository: ProfileRepository = ProfileRepository(context)
) : ViewModel() {

    private val tag = "ProfileViewModel"

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    fun loadUserProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.getUserProfile()
                if (result.isSuccessful) {
                    result.body()?.let { response ->
                        Log.d(tag, response.toString())
                        if (response.success) {
                            _userProfile.value = response.user
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

    fun updateProfile(name: String, email: String, phone: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.updateUserProfile(
                    UpdateProfileRequest(
                        name = name,
                        email = email,
                        phone = phone
                    )
                )
                if (result.isSuccessful) {
                    result.body()?.let { response ->
                        Log.d(tag, response.toString())
                        if (response.success) {
                            _userProfile.value = response.user
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