package com.inttelgo.tecnicos.logic.persistence

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.inttelgo.tecnicos.logic.Model.Usuario
import androidx.core.content.edit
import com.inttelgo.tecnicos.logic.Model.UsuarioAuth

class UserPreferences (context: Context){
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

    private val gson = Gson()
    companion object {
        const val KEY_USER_JSON = "usuario_json"
        const val KEY_NAME = "username"
        const val KEY_COLOR= "#000000"
        const val KEY_TOKEN = "auth_token"
        const val KEY_TOKEN_EXPIRY = "token_expiry_time"
        const val KEY_USERNAME = "saved_username"
        const val KEY_PASSWORD = "saved_password"
        const val KEY_FCM_TOKEN = "fcm_token"
    }

    fun saveUser(usuario: UsuarioAuth) {
        val json = gson.toJson(usuario)
        sharedPreferences.edit { putString(KEY_USER_JSON, json) }
    }

    fun getUser(): UsuarioAuth? {
        val json = sharedPreferences.getString(KEY_USER_JSON, null)
        return json?.let {gson.fromJson(it, UsuarioAuth::class.java)}
    }

    fun getName(): String? = sharedPreferences.getString(KEY_NAME, null)

    fun getColor(): String? = sharedPreferences.getString(KEY_COLOR, "#000000")

    // Nuevos métodos para token
    fun saveToken(token: String) {
        val expiryTime = System.currentTimeMillis() + (60 * 60 * 1000) // 1 hora desde ahora
        sharedPreferences.edit {
            putString(KEY_TOKEN, token)
            putLong(KEY_TOKEN_EXPIRY, expiryTime)
        }
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun isTokenValid(): Boolean {
        val expiryTime = sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0)
        return expiryTime > System.currentTimeMillis()
    }

    // Métodos para credenciales (para auto-login)
    fun saveCredentials(username: String, password: String) {
        sharedPreferences.edit {
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password)
        }
    }

    fun getSavedUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }

    fun getSavedPassword(): String? {
        return sharedPreferences.getString(KEY_PASSWORD, null)
    }

    fun hasSavedCredentials(): Boolean {
        return !getSavedUsername().isNullOrEmpty() && !getSavedPassword().isNullOrEmpty()
    }

    fun saveFcmToken(token: String) {
        sharedPreferences.edit { putString(KEY_FCM_TOKEN, token) }
    }

    fun getFcmToken(): String? = sharedPreferences.getString(KEY_FCM_TOKEN, null)

    fun clearUser() {
        sharedPreferences.edit {
            remove(KEY_USER_JSON)
            remove(KEY_TOKEN)
            remove(KEY_TOKEN_EXPIRY)
            remove(KEY_USERNAME)
            remove(KEY_PASSWORD)
            remove(KEY_FCM_TOKEN)
        }
    }
}