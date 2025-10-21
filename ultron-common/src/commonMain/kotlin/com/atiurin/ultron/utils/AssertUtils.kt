package com.atiurin.ultron.utils

import com.atiurin.ultron.exceptions.UltronAssertionException
import kotlinx.atomicfu.AtomicRef

object AssertUtils {
    fun assertTrue(block: () -> Boolean, timeoutMs: Long = 5_000, desc: String = "" ) =
        assertTrue(block, timeoutMs) { desc }

    fun assertTrue(block: () -> Boolean, timeoutMs: Long = 5_000, desc: () -> String = { "" }) {
        val startTime = nowMs()
        while (nowMs() < startTime + timeoutMs){
            if (block()) return
        }
        throw UltronAssertionException("Assertion '${desc.invoke()}' failed during $timeoutMs ms")
    }
    fun <R> assertTrueAndValueInDesc(valueBlock: () -> R, assertionBlock: (R) -> Boolean, timeoutMs: Long = 5_000, desc: (R) -> String = { "" }) {
        val finishTime = nowMs() + timeoutMs
        var result = valueBlock()
        while (nowMs() < finishTime){
            if (assertionBlock(result)) return
            result = valueBlock()
        }
        throw UltronAssertionException("Assertion '${desc.invoke(result)}' failed during $timeoutMs ms")
    }

    fun <R> assertTrueAndReturn(resultContainer: R, block: (R) -> Boolean, timeoutMs: Long = 5_000, desc: String = ""): R {
        val finishTime = nowMs() + timeoutMs
        while (nowMs() < finishTime){
            if (block(resultContainer)) return resultContainer
        }
        throw UltronAssertionException("Assertion '$desc' failed during $timeoutMs ms")
    }

    fun <R> assertTrueAndReturnValue(valueBlock: () -> R, assertionBlock: (R) -> Boolean, timeoutMs: Long = 5_000, desc: String = ""): R {
        val finishTime = nowMs() + timeoutMs
        while (nowMs() < finishTime){
            val result = valueBlock()
            if (assertionBlock(result)) return result
        }
        throw UltronAssertionException("Assertion '$desc' failed during $timeoutMs ms")
    }

    /**
     * Immediately throws an exception if block returns false
     */
    fun assertTrueWhileTime(block: () -> Boolean, timeoutMs: Long = 5_000, desc: String = ""){
        val startTime = nowMs()
        while (nowMs() < startTime + timeoutMs){
            if (!block()) throw UltronAssertionException("Assertion '$desc' failed")
        }
    }

}