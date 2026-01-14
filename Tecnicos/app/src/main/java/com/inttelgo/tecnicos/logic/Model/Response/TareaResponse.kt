package com.inttelgo.tecnicos.logic.Model.Response

import com.inttelgo.tecnicos.logic.Model.Tarea

data class TareaResponse(
    val success: Boolean = false,
    val mensaje: String = "",
    val tarea: Tarea? = null,
)
