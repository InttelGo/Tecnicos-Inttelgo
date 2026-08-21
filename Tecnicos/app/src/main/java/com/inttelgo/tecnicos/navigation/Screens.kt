package com.inttelgo.tecnicos.navigation

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
enum class HomeSection {
    Soporte,
    Tareas,
    Procesos
}

@Serializable
data class Home(val section: HomeSection = HomeSection.Soporte)

@Serializable
data class Support(val idSupport: String)

@Serializable
data class Tarea(val idTarea: String)

@Serializable
data class Instalacion(val idInstalacion: String)

@Serializable
data class UploadImg(val id: String, val type: String)

@Serializable
object Profile

fun homeSectionFromUploadType(type: String): HomeSection = when {
    type.contains("ticket", ignoreCase = true) -> HomeSection.Soporte
    type.contains("tarea", ignoreCase = true) -> HomeSection.Tareas
    type.contains("Proceso", ignoreCase = true) ||
        type.contains("instalacion", ignoreCase = true) -> HomeSection.Procesos
    else -> HomeSection.Soporte
}
