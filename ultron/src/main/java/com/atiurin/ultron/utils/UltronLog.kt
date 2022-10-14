package com.atiurin.ultron.utils

import android.os.SystemClock
import android.util.Log

object UltronLog {
    const val LOG_TAG = "Ultron"
    fun info(message: String) = Log.i(LOG_TAG, message)
    fun info(message: String, throwable: Throwable) = Log.i(LOG_TAG, message, throwable)
    fun debug(message: String) = Log.d(LOG_TAG, message)
    fun debug(message: String, throwable: Throwable) = Log.d(LOG_TAG, message, throwable)
    fun warn(message: String) = Log.w(LOG_TAG, message)
    fun warn(message: String, throwable: Throwable) = Log.w(LOG_TAG, message, throwable)
    fun error(message: String) = Log.e(LOG_TAG, message)
    fun error(message: String, throwable: Throwable) = Log.e(LOG_TAG, message, throwable)
    fun <R> time(desc: String, block: () -> R) : R{
        val startTime = SystemClock.elapsedRealtime()
        val result = block()
        debug("$desc duration ${SystemClock.elapsedRealtime() - startTime} ms")
        return result
    }
}