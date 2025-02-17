package com.inttelgo.tecnicos.ui.viewmodel



import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inttelgo.tecnicos.logic.Model.Data
import com.inttelgo.tecnicos.logic.Model.RetroFitService
import com.inttelgo.tecnicos.logic.RetroFitServiceFactory
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _userData = MutableStateFlow<Data?>(null)
    val userData: StateFlow<Data?> = _userData

    @SuppressLint("NewApi")
    fun loginWithEmail(context: Context, username: String, password: String, navigateToHome: () -> Unit) {
        if (username.isEmpty() || password.isEmpty()) {
            _errorMessage.value = "Todos los campos son requeridos"
        } else {
            val service = RetroFitServiceFactory.makeRetroFitService()
            viewModelScope.launch {
                try {
                    Log.d("LoginViewModel", "https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/iniciarSesion.php")}&username=$username&password=$password")
                    val result = service.getUserData("https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/iniciarSesion.php")}&username=$username&password=$password")
                    if(result.success){
                        _userData.value = result.data
                        val userPreferences = UserPreferences(context)
                            userPreferences.saveUser(result.data.id_usuario)

                        Log.d("Ubicación obtenida", result.data.toString())
                        result.data.nombre_1?.let { userPreferences.saveName(it)}
                        result.data.color?.let { userPreferences.saveColor(it) }
                        navigateToHome()
                    }else{
                        _errorMessage.value = "Usuario o contraseña incorrectos"
                    }
                } catch (e: Exception) {
                    _errorMessage.value = e.message }
            }
        }
    }

    fun isLoggedUser(navigateToLogin: () -> Unit, id: String?) {
        if(id==null){
            navigateToLogin()
        }
        val service = RetroFitServiceFactory.makeRetroFitService()
        viewModelScope.launch {
            try {
                val result = service.getUserData("https://app.inttelgo.com/Tecnicos/?pid=${RetroFitService.encodeToBase64("pages/iniciarSesion.php")}&id=${id}")
                _userData.value = result.data
            } catch (e: Exception) {
                _errorMessage.value = e.message }
        }
    }
}