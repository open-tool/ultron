package com.atiurin.sampleapp.framework.utils

import android.os.SystemClock
import org.junit.Assert

object AssertUtils {
    fun assertException(expected: Boolean = true, block: () -> Unit) {
        var exceptionOccurs = false
        try {
            block()
        } catch (ex: Throwable) {
            exceptionOccurs = true
        }
        Assert.assertEquals(expected, exceptionOccurs)
    }

    fun assertExecTimeMoreThen(time: Long, block: () -> Unit) {
        var startTime = System.currentTimeMillis()
        try {
            block()
        } catch (ex: Throwable) {
        }
        Assert.assertTrue(System.currentTimeMillis() - startTime >= time)
    }

    fun assertExecTimeLessThen(time: Long, block: () -> Unit) {
        var startTime = System.currentTimeMillis()
        try {
            block()
        } catch (ex: Throwable) {
        }
        Assert.assertTrue(System.currentTimeMillis() - startTime <= time)
    }
}