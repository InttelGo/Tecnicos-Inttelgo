package com.inttelgo.tecnicos.logic.Model.Response

import com.inttelgo.tecnicos.logic.Model.Tarea

data class TareaWithFiltersResponse (
    val success: Boolean,
    val tareas: List<Tarea>,
    val totalPages: Int,
    val mensaje: String
)