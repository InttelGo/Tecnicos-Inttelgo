package com.inttelgo.tecnicos.logic.Model

data class Tarea(
    val id_tarea: Int = 0,
    val id_cuenta: Int = 0,
    val num_cuenta: String ="",
    val barrio: Barrio? = null,
    val prioridad: Prioridad? = null,
    val observacion: String = "",
    val observacion_tecnico: String = "",
    val fecha_creacion: String ="",
    val fecha_edicion: String = "",
    val fecha_habil: String ="",
    val fecha_finalizacion: String = "",
    val tipo: TipoTarea? = null,
    val tipo_area: String? = null,
    val estado: EstadoTarea? = null,
    val tecnico: Usuario? = null,
    val usuario: Usuario? = null,
    val uusuarioEdicion: Usuario? = null,
    val cuenta: Cuenta? = null,
    val cliente: Cliente? = null
)