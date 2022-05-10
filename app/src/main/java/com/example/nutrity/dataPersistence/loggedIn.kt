package com.example.nutrity.dataPersistence

import android.app.Application

class loggedIn : Application() {

    companion object {
        lateinit var prefs: Prefs
    }

    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
    }
}