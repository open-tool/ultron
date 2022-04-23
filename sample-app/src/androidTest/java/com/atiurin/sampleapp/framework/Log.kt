package com.atiurin.sampleapp.framework

import android.os.SystemClock
import android.util.Log

object Log {
    const val LOG_TAG = "Ultron"
    fun info(message: String) = Log.i(LOG_TAG, message)
    fun debug(message: String) = Log.d(LOG_TAG, message)
    fun error(message: String, name: String) = Log.e(LOG_TAG, message)
    fun warn(message: String) = Log.w(LOG_TAG, message)
    fun <R> time(desc: String, block: () -> R) : R{
        val startTime = SystemClock.elapsedRealtime()
        val result = block()
        debug("$desc duration ${SystemClock.elapsedRealtime() - startTime} ms")
        return result
    }
}