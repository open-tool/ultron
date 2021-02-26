package com.atiurin.ultron.utils

import android.os.SystemClock
import com.atiurin.ultron.exceptions.UltronException

object AssertUtils {
    fun assertTrue(block: () -> Boolean, timeoutMs: Long = 5_000, desc: String = "") {
        val startTime = SystemClock.elapsedRealtime()
        var result = false
        while (!result && (SystemClock.elapsedRealtime() < startTime + timeoutMs)){
            result = block()
        }
        if (!result) throw UltronException("Assertion '$desc' failed during $timeoutMs ms")
    }
}