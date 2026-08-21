package com.inttelgo.tecnicos.logic.Model

/**
 * Grupo: (operator_by = id OR assistant_by = id)
 * Se enlaza con AND al resto de filtros (estado, búsqueda, etc.)
 * para que el OR no rompa las restricciones de estado.
 */
fun assignedToUserFilter(userId: Int, logic: String? = "AND"): Filter = Filter(
    logic = logic,
    filters = listOf(
        Filter(
            column = "operator_by",
            operator = "equals",
            value = userId.toString()
        ),
        Filter(
            column = "assistant_by",
            operator = "equals",
            value = userId.toString(),
            logic = "OR"
        )
    )
)

fun Usuario.displayName(): String {
    return listOfNotNull(nombre_1, apellido_1)
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { "Usuario" }
}
