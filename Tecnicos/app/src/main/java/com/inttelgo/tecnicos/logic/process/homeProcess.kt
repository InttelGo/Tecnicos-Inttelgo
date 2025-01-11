package com.inttelgo.tecnicos.logic.process
import com.inttelgo.tecnicos.logic.Model.Barrio
import com.inttelgo.tecnicos.logic.Model.Ticket

open class homeProcess {
    open fun generarConjunto(tickets: List<Ticket>, barrios: List<Barrio>): MutableMap<String, List<Ticket>>{
        val ticketsArreglados = mutableMapOf<String, List<Ticket>>()
        barrios.forEach{ barrio ->
            ticketsArreglados[barrio.prefijo] = tickets.filter { encontrarPrefijo( it.cliente.barrio, barrio.prefijo) }
        }
        return ticketsArreglados
    }

    private fun encontrarPrefijo(clientebarrio: String, barrio: String) :Boolean{
        return clientebarrio==barrio
    }
}