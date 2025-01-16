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

    @GET
    suspend fun getSupport(
        @Url url: String
    ):SupportResult

    @GET
    suspend fun getObs(
        @Url url: String
    ): resultHistory

    @GET
    suspend fun getPictures(
        @Url url: String
    ): PictureResult

    @GET
    suspend fun setObs(
        @Url url: String
    ): Int
    @GET
    suspend fun getBarrios(
        @Url url: String
    ): BarrioResult

    @GET
    suspend fun getTypeI(
        @Url url: String
    ): PlanResult

    @GET
    suspend fun getArticles(
        @Url url: String
    ): ArticuloResult

    @GET
    suspend fun setInventary(
        @Url url: String
    )

    @GET
    suspend fun setInstalacion(
        @Url url: String
    ): Int

    companion object {
        fun encodeToBase64(url: String): String = Base64.encodeToString(url.toByteArray(), Base64.DEFAULT)
    }
}