package com.atiurin.ultron.utils

import android.os.SystemClock
import com.atiurin.ultron.exceptions.UltronAssertionException
import java.util.concurrent.atomic.AtomicReference

object AssertUtils {
    fun assertTrue(block: () -> Boolean, timeoutMs: Long = 5_000, desc: String = "") {
        val startTime = SystemClock.elapsedRealtime()
        while (SystemClock.elapsedRealtime() < startTime + timeoutMs){
            if (block()) return
        }
        throw UltronAssertionException("Assertion '$desc' failed during $timeoutMs ms")
    }
    fun <R> assertTrueAndReturn(resultContainer: R, block: (R) -> Boolean, timeoutMs: Long = 5_000, desc: String = ""): R {
        val finishTime = SystemClock.elapsedRealtime() + timeoutMs
        while (SystemClock.elapsedRealtime() < finishTime){
            if (block(resultContainer)) return resultContainer
        }
        throw UltronAssertionException("Assertion '$desc' failed during $timeoutMs ms")
    }

    fun <R> assertTrueAndReturnValue(block: (AtomicReference<R>) -> Boolean, timeoutMs: Long = 5_000, desc: String = ""): R {
        val resultContainer = AtomicReference<R>()
        val finishTime = SystemClock.elapsedRealtime() + timeoutMs
        while (SystemClock.elapsedRealtime() < finishTime){
            if (block(resultContainer)) return resultContainer.get()
        }
        throw UltronAssertionException("Assertion '$desc' failed during $timeoutMs ms")
    }

    /**
     * Immediately throws an exception if block returns false
     */
    fun assertTrueWhileTime(block: () -> Boolean, timeoutMs: Long = 5_000, desc: String = ""){
        val startTime = SystemClock.elapsedRealtime()
        while (SystemClock.elapsedRealtime() < startTime + timeoutMs){
            if (!block()) throw UltronAssertionException("Assertion '$desc' failed")
        }
    }

}