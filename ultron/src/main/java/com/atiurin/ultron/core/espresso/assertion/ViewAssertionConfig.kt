package com.atiurin.ultron.core.espresso.assertion

import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.PerformException
import junit.framework.AssertionFailedError

class ViewAssertionConfig() {
    companion object {
        var ASSERTION_TIMEOUT = 5_000L
        var allowedExceptions = mutableListOf<Class<out Throwable>>(
            PerformException::class.java,
            NoMatchingViewException::class.java,
            AssertionFailedError::class.java
        )
    }
}