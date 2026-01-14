package com.inttelgo.tecnicos.logic.Model

import androidx.compose.ui.graphics.vector.ImageVector

data class UserProfile(
    val idUsuario: String,
    val nombre1: String,
    val nombre2: String,
    val apellido1: String,
    val apellido2: String,
    val correo_personal: String,
    val perfil: Perfil,
    val oficina: Oficina,
    val telefono1 : String,
    val telefono2 : String,
    val direccion : String,
    val identificacion : String
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
    val name: String,
    val email: String,
    val phone: String
)

data class UserProfileResponse(
    val success: Boolean,
    val message: String,
    val user: UserProfile?
)