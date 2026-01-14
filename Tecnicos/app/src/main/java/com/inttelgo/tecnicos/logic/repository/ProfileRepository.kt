package com.inttelgo.tecnicos.logic.repository

import android.content.Context
import com.inttelgo.tecnicos.logic.Model.UpdateProfileRequest
import com.inttelgo.tecnicos.logic.Model.UserProfileResponse
import com.inttelgo.tecnicos.logic.persistence.UserPreferences
import com.inttelgo.tecnicos.network.ApiService
import com.inttelgo.tecnicos.network.RetrofitClient
import retrofit2.Response

class ProfileRepository (private val context: Context? = null){
    private val apiService: ApiService = RetrofitClient.api

    suspend fun getUserProfile(): Response<UserProfileResponse> {
        return apiService.getUserProfile()
    }

    suspend fun updateUserProfile(request: UpdateProfileRequest): Response<UserProfileResponse> {
        return apiService.updateUserProfile(request)
    }

    suspend fun clearUserSession() {
        context?.let {
            val userPreferences = UserPreferences(it)
            userPreferences.clearUser()
            // Limpiar el token de RetrofitClient
            RetrofitClient.updateAuthToken("", it)
        }
    }
}