package com.inttelgo.tecnicos.logic.Model

import android.util.Base64
import retrofit2.http.GET
import retrofit2.http.Url

interface RetroFitService {

    @GET
    suspend fun getUserData(
        @Url url: String
    ): RemoteUserResult

    @GET
    suspend fun getProcessData(
        @Url url: String
    ): ProcessResult

    @GET
    suspend fun getTickets(
        @Url url: String
    ):TicketResult

    companion object {
        fun encodeToBase64(url: String): String = Base64.encodeToString(url.toByteArray(), Base64.DEFAULT)
    }
}