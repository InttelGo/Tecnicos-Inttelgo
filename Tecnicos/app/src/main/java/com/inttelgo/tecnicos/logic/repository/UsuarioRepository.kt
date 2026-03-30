package com.inttelgo.tecnicos.logic.repository

import com.inttelgo.tecnicos.logic.Model.LoginRequest
import com.inttelgo.tecnicos.logic.Model.Request.UbicationRequest
import com.inttelgo.tecnicos.network.RetrofitClient

class UsuarioRepository {
    suspend fun login(loginRequest: LoginRequest) = RetrofitClient.api.login(loginRequest)

    suspend fun ubication(ubicationRequest: UbicationRequest) = RetrofitClient.api.ubication(ubicationRequest)
}
