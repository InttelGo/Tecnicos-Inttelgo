package com.inttelgo.tecnicos.logic.persistence

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

object DatabaseHelper {
    private const val URL = "jdbc:mysql://<TU_HOST>:<TU_PUERTO>/<TU_BASE_DE_DATOS>"
    private const val USER = "<TU_USUARIO>"
    private const val PASSWORD = "<TU_CONTRASEÑA>"
    public var connection: Connection? = null

    init {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD)
            println("Conexión a la base de datos establecida")
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al establecer la conexión a la base de datos")
        }
    }

    fun closeConnection() {
        connection?.close()
        println ("Conexión a la base de datos cerrada")
    }
}