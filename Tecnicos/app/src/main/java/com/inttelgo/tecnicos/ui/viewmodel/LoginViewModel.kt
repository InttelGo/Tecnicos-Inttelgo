package com.inttelgo.tecnicos.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.inttelgo.tecnicos.logic.persistence.DatabaseHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel: ViewModel() {
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    //Login authentication
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val auth: FirebaseAuth = Firebase.auth

    fun loginWithEmail(username: String, password: String) {
        if(username.isEmpty() || password.isEmpty()){
            _errorMessage.value = "Todos los campos son requeridos"
        }else{
            val bd = DatabaseHelper
            val query = "SELECT * FROM users WHERE username = ? AND password = ?"

            bd.connection?.prepareStatement(query)?.use { statement ->
                statement.setString(1, username)
                statement.setString(2, password)
                val resultSet = statement.executeQuery()
                if (resultSet.next()) {
                    // El usuario existe, puedes proceder con el inicio de sesión
                    _isLoggedIn.value = true
                    val userId = resultSet.getString("id")  // Ejemplo de cómo obtener el ID del usuario
                    // Aquí puedes realizar cualquier otra acción que necesites con el ID del usuario
                } else {
                    // El usuario no existe o la contraseña es incorrecta
                    _isLoggedIn.value = false
                    _errorMessage.value = "Usuario o contraseña incorrectos."
                }
            }
        }
    }

}