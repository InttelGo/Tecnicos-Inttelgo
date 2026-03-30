package com.inttelgo.tecnicos.logic.Model

import androidx.compose.ui.graphics.vector.ImageVector

data class Usuario(
    val id: Int = 0,
    val identificacion: String? ="",
    val nombre_1: String? = "",
    val nombre_2: String? = "",
    val apellido_1: String? = "",
    val apellido_2: String? ="" ,
    val telefono_1: String? = "",
    val telefono_2: String? = "",
    val direccion: String? = "",
    val correoPersonal: String? = "",
    val correoCorporativo: String? = "",
    val perfil: Perfil? = null,
)

data class UsuarioAuth(
    val id: Int = 0,
    val identification: String? ="",
    val name1: String? = "",
    val name2: String? = "",
    val lastname1: String? = "",
    val lastname2: String? ="" ,
    val phone1: String? = "",
    val phone2: String? = "",
    val address: String? = "",
    val emailPersonal: String? = "",
    val emailCorporative: String? = "",
    val profile: Perfil? = null,
    val office: Oficina? = null
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData? = null
)

data class LoginData(
    val token: String,
    val usuario: UsuarioAuth
)

data class Oficina(
    val id: String,
    val rol: String
)

data class InfoItem(
    val icon: ImageVector,
    val label: String,
    val value: String
)

data class UpdateProfileRequest(
    val nombre_1: String,
    val correo_personal: String,
    val telefono_1: String
)

data class UserProfileResponse(
    val success: Boolean,
    val message: String,
    val usuario: UsuarioAuth?
)