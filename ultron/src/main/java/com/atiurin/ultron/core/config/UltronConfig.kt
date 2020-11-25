package com.atiurin.ultron.core.config

import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.PerformException
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.resultanalyzer.DefaultOperationResultAnalyzer
import junit.framework.AssertionFailedError

object UltronConfig {
    var LOGCAT_TAG = "Ultron"
    var ESPRESSO_OPERATION_POLLING_TIMEOUT = 50L //ms

    class ViewActionConfig {
        companion object {
            var ACTION_TIMEOUT = 5_000L
            var allowedExceptions = mutableListOf<Class<out Throwable>>(
                    PerformException::class.java,
                    NoMatchingViewException::class.java
            )
            var defaultResultAnalyzer = DefaultOperationResultAnalyzer()
            val defaultResultHandler: (EspressoOperationResult) -> Unit = {
                defaultResultAnalyzer.analyze(it)
            }
        }
    }

    class ViewAssertionConfig {
        companion object {
            var ASSERTION_TIMEOUT = 5_000L
            var allowedExceptions = mutableListOf<Class<out Throwable>>(
                    PerformException::class.java,
                    NoMatchingViewException::class.java,
                    AssertionFailedError::class.java
            )
            var defaultResultAnalyzer = DefaultOperationResultAnalyzer()
            val defaultResultHandler: (EspressoOperationResult) -> Unit = {
                ViewActionConfig.defaultResultAnalyzer.analyze(it)
            }
        }
    }
}