package com.atiurin.sampleapp

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

object MyApplication : Application() {
    var context: Context? = null
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    @SuppressLint("StaticFieldLeak")
    var CONTACTS_LOADING_TIMEOUT_MS = 1000L
}