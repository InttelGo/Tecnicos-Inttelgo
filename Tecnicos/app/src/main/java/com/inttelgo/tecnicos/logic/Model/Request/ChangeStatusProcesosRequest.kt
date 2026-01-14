package com.inttelgo.tecnicos.logic.Model.Request

data class ChangeStatusProcesosRequest(
    val idInstalacion: Int,
    val id_new_estado: Int
)
