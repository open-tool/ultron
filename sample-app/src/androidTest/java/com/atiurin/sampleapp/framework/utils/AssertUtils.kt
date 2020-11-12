package com.atiurin.sampleapp.framework.utils

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
}