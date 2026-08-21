package com.inttelgo.tecnicos.viewmodel



import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inttelgo.tecnicos.logic.Model.LoginRequest
import com.inttelgo.tecnicos.logic.notifications.FcmTokenManager
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import com.inttelgo.tecnicos.logic.repository.UsuarioRepository
import com.inttelgo.tecnicos.network.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel (private val repository: UsuarioRepository = UsuarioRepository()) : ViewModel() {

    private val tag = "LoginViewModel"
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow<Boolean?>(false)
    val isLoading: StateFlow<Boolean?> = _isLoading

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage
    @SuppressLint("NewApi")
    fun loginWithEmail(context: Context, username: String, password: String, navigateToHome: () -> Unit) {
        if (username.isEmpty() || password.isEmpty()) {
            _errorMessage.value = "Todos los campos son requeridos"
            resetErrorMessageAfterDelay()
        } else {
            _isLoading.value = true
            viewModelScope.launch {
                try {
                    val result = repository.login(LoginRequest(username, password))
                    Log.d(tag, result.toString())
                    if(result.isSuccessful){
                        val userPreferences = UserPreferences(context)
                        result.body()?.let {
                            Log.d(tag, it.toString())
                            if(it.success){
                                if(it.data != null) {
                                    Log.d(tag, it.data.toString())
                                    userPreferences.saveUser(it.data.usuario)
                                    userPreferences.saveToken(it.data.token) // Guardar token
                                    RetrofitClient.updateAuthToken(it.data.token, context)
                                    FcmTokenManager.registerCurrentToken(context)
                                }else{
                                    _errorMessage.value = it.message
                                }
                                userPreferences.saveCredentials(username, password) // Guardar credenciales para auto-login
                                _successMessage.value = it.message
                                navigateToHome()
                            }else{
                                _errorMessage.value = it.message
                            }
                        }
                    }else{
                        Log.d(tag, result.body().toString())
                        if(result.code() == 401) _errorMessage.value = "Credenciales invalidas" else _errorMessage.value = result.body()?.message
                        resetErrorMessageAfterDelay()
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Ha ocurrido un error en la conexion"
                    e.message?.let { Log.e(tag, it) }
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun autoLogin(context: Context, navigateToHome: () -> Unit, navigateToLogin: () -> Unit) {
        val userPreferences = UserPreferences(context)

        if (userPreferences.hasSavedCredentials() && userPreferences.getUser() != null) {
            val username = userPreferences.getSavedUsername()!!
            val password = userPreferences.getSavedPassword()!!

            _isLoading.value = true
            viewModelScope.launch {
                try {
                    Log.d(tag, "Intentando auto-login con $username y $password")
                    val result = repository.login(LoginRequest(username, password))
                    if (result.isSuccessful && result.body()?.success == true) {
                        result.body()?.let {
                            if(it.data != null){
                                Log.d(tag, it.data.usuario.toString())
                                userPreferences.saveUser(it.data.usuario)
                                userPreferences.saveToken(it.data.token)
                                RetrofitClient.updateAuthToken(it.data.token, context)
                                FcmTokenManager.registerCurrentToken(context)
                                navigateToHome()
                            }else {
                                userPreferences.clearUser()
                                navigateToLogin()
                            }
                        }
                    } else {
                        // Si falla el auto-login, limpiar credenciales y mostrar login
                        userPreferences.clearUser()
                        navigateToLogin()
                    }
                } catch (e: Exception) {
                    Log.e(tag, "Error en auto-login: ${e.message}")
                    userPreferences.clearUser()
                    navigateToLogin()
                } finally {
                    _isLoading.value = false
                }
            }
        } else {
            navigateToLogin()
        }
    }

    private fun resetErrorMessageAfterDelay() {
        viewModelScope.launch {
            delay(5000) // 5000 milisegundos = 5 segundos
            _errorMessage.value = null
        }
    }

    fun clearMessages(){
        _errorMessage.value = null
        _successMessage.value = null
    }
}