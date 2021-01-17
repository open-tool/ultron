package com.atiurin.ultron.utils

import android.util.Log

object LogUtils {
    fun <T> calcDuration(desc: String, block: () -> T): T {
        val startTime = System.currentTimeMillis()
        val res = block()
        val execTime = System.currentTimeMillis() - startTime
        Log.d("Ultron", "$desc execution time = $execTime")
        return res
    }
}