package com.atiurin.sampleapp.framework

import android.util.Log

object Log {
    const val LOG_TAG = "Ultron"
    fun info(message: String) = Log.i(LOG_TAG, message)
    fun debug(message: String) = Log.d(LOG_TAG, message)
    fun error(message: String) = Log.e(LOG_TAG, message)
    fun warn(message: String) = Log.w(LOG_TAG, message)
}