package com.inttelgo.tecnicos.logic.process
import android.util.Log
import com.inttelgo.tecnicos.logic.Model.Barrio
import com.inttelgo.tecnicos.logic.Model.Tarea
import com.inttelgo.tecnicos.logic.Model.Ticket
import kotlin.collections.forEach

open class homeProcess {
    open fun generarConjunto(tickets: List<Ticket>, barrios: List<Barrio>): MutableMap<String, List<Ticket>>{
        val ticketsArreglados = mutableMapOf<String, List<Ticket>>()
        barrios.forEach{ barrio ->
            ticketsArreglados[barrio.prefijo] = tickets.filter { encontrarPrefijo( it.barrio!!.prefijo, barrio.prefijo) }
        }
        return ticketsArreglados
    }

    open fun generarConjuntoTareas(tareas: List<Tarea>, barrios: List<Barrio>): MutableMap<String, List<Tarea>> {
        Log.d("Tarea", tareas.toString())
        Log.d("Tarea", barrios.toString())
        val tareasArregladas = mutableMapOf<String, List<Tarea>>()

        // Filtrar solo tareas que tienen barrio no nulo
        val tareasConBarrio = tareas.filter { it.barrio != null }

        barrios.forEach { barrio ->
            tareasArregladas[barrio.prefijo] = tareasConBarrio.filter { tarea ->
                encontrarPrefijo(tarea.barrio!!.prefijo, barrio.prefijo)
            }
        }
        Log.d("Tarea", tareasArregladas.toString())
        return tareasArregladas
    }

    private fun encontrarPrefijo(clientebarrio: String, barrio: String) :Boolean{
        return clientebarrio==barrio
    }
}