package com.inttelgo.tecnicos.navigation

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object Home

@Serializable
data class Support(val idSupport: String)

@Serializable
data class Tarea(val idTarea: String)

@Serializable
data class UploadImg(val id: String, val type: String)

@Serializable
object Profile