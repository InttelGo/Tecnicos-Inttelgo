package com.inttelgo.tecnicos.logic.Model

data class ObsTicket(
    val fecha: String,
    val id_obs_ticket: String,
    val obs: String,
    val tecnico: Tecnico
)