package com.example.nutrity.dataPersistence

import android.content.Context
import android.content.SharedPreferences

class Prefs(val context: Context) {

    private val SHARED_NAME = "Mydtb"
    private val SHARED_EMAIL = "user"
    private val SHARED_PASS = "pass"
    private val SHARED_LOGGED = "logged"

    val storage: SharedPreferences = context.getSharedPreferences(SHARED_NAME, 0)

    fun saveLogged(logged: Boolean) {
        storage.edit().putBoolean(SHARED_LOGGED, logged).apply()
    }

    fun getLogged(): Boolean {
        return storage.getBoolean(SHARED_LOGGED, false)
    }

    fun wipe() {
        storage.edit().clear().apply()
    }

}