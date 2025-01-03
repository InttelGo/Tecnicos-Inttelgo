package com.inttelgo.tecnicos.ui.viewmodel



import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inttelgo.tecnicos.logic.Model.Data
import com.inttelgo.tecnicos.logic.Model.RetroFitService
import com.inttelgo.tecnicos.logic.RetroFitServiceFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    // Login authentication
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _userData = MutableStateFlow<Data?>(null)
    val userData: StateFlow<Data?> = _userData

    @SuppressLint("NewApi")
    fun loginWithEmail(username: String, password: String, navigateToHome: () -> Unit) {
        if (username.isEmpty() || password.isEmpty()) {
            _errorMessage.value = "Todos los campos son requeridos"
        } else {
            val service = RetroFitServiceFactory.makeRetroFitService()
            viewModelScope.launch {
                try {
                    val result = service.getUserData("https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/iniciarSesion.php")}&username=$username&password=$password")
                    _isLoggedIn.value = true
                    _userData.value = result.data
                    navigateToHome()
                } catch (e: Exception) {
                    _errorMessage.value = e.message }
            }
        }
    }
}