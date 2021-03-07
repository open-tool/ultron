package com.atiurin.ultron.utils

import android.os.SystemClock
import com.atiurin.ultron.exceptions.UltronException

object AssertUtils {
    fun assertTrue(block: () -> Boolean, timeoutMs: Long = 5_000, desc: String = "") {
        val startTime = SystemClock.elapsedRealtime()
        while (SystemClock.elapsedRealtime() < startTime + timeoutMs){
            if (block()) return
        }
        throw UltronException("Assertion '$desc' failed during $timeoutMs ms")
    }
    fun <R> assertTrueAndReturn(resultContainer: R, block: (R) -> Boolean, timeoutMs: Long = 5_000, desc: String = ""): R {
        val finishTime = SystemClock.elapsedRealtime() + timeoutMs
        while (SystemClock.elapsedRealtime() < finishTime){
            if (block(resultContainer)) return resultContainer
        }
        throw UltronException("Assertion '$desc' failed during $timeoutMs ms")
    }

}