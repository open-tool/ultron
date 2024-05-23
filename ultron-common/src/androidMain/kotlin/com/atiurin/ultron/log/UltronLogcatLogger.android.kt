package com.atiurin.ultron.log

import android.util.Log

class UltronLogcatLogger : ULogger(){
    companion object {
        const val LOG_TAG = "Ultron"
    }
    override fun info(message: String) = Log.i(LOG_TAG, message)
    override fun info(message: String, throwable: Throwable) = Log.i(LOG_TAG, message, throwable)
    override fun debug(message: String) = Log.d(LOG_TAG, message)
    override fun debug(message: String, throwable: Throwable) = Log.d(LOG_TAG, message, throwable)
    override fun warn(message: String) = Log.w(LOG_TAG, message)
    override fun warn(message: String, throwable: Throwable) = Log.w(LOG_TAG, message, throwable)
    override fun error(message: String) = Log.e(LOG_TAG, message)
    override fun error(message: String, throwable: Throwable) = Log.e(LOG_TAG, message, throwable)
}