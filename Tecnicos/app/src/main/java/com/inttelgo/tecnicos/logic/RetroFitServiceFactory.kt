package com.inttelgo.tecnicos.logic

import android.util.Log
import com.inttelgo.tecnicos.logic.Model.RetroFitService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroFitServiceFactory {
    private val baseUrl: String = "https://app.inttelgo.com/Tecnicos/"

    fun makeRetroFitService(): RetroFitService {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(RetroFitService::class.java)
    }
}