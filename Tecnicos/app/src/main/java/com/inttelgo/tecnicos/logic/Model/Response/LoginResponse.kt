package com.inttelgo.tecnicos.logic.Model.Response

import com.inttelgo.tecnicos.logic.Model.Usuario

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String,
    val usuario: Usuario
)
