package com.inttelgo.tecnicos.logic.Model

data class Tarea(
    val prioridad: Prioridad? = null,
    val barrio: Barrio? = null,
    val id: Int = 0,
    val observacion: String = "",
    val observacion_tecnico: String = "",
    val fecha_creacion: String ="",
    val fecha_edicion: String = "",
    val fecha_habil: String ="",
    val fecha_finalizacion: String = "",
    val reserved_at: String = "",
    val cuenta: Cuenta? = null,
    val tipo: TipoTarea? = null,
    val tipo_area: String? = null,
    val estado: EstadoTarea? = null,
    val usuarioCreacion: Usuario? = null,
    val usuarioEdicion: Usuario? = null,
    val operator_by: Usuario? = null,
    val assistant_by: Usuario? = null,
    val cliente: Cliente? = null
)

data class TareaWithFiltersResponse (
    val success: Boolean,
    val tareas: List<Tarea>,
    val totalPages: Int,
    val mensaje: String
)

data class TareaResponse(
    val success: Boolean = false,
    val mensaje: String = "",
    val tarea: Tarea? = null,
)
