package com.atiurin.sampleapp.framework

import android.util.Log

fun step(description: String, action: () -> Unit) {
    val desc = if (description.isNullOrEmpty()) "empty desc" else description
    Log.d("ALLURE step", desc)
    action()
}