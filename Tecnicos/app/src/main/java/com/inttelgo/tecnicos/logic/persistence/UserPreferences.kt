package com.inttelgo.tecnicos.logic.persistence

import android.content.Context
import android.content.SharedPreferences

class UserPreferences (context: Context){
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

    companion object {
        const val KEY_ID = "username"
    }

    fun saveUser(id: String) {
        sharedPreferences.edit().apply {
            putString(KEY_ID, id)
            apply()
        }
    }

    fun getId(): String? = sharedPreferences.getString(KEY_ID, null)

    fun clearUser() {
        sharedPreferences.edit().remove("username").apply()
    }
}