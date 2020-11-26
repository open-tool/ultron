package com.atiurin.ultron.core.config

import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.PerformException
import androidx.test.uiautomator.UiObjectNotFoundException
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.resultanalyzer.DefaultOperationResultAnalyzer
import junit.framework.AssertionFailedError

object UltronConfig {
    var LOGCAT_TAG = "Ultron"

    class Espresso {
        companion object {
            var ESPRESSO_OPERATION_POLLING_TIMEOUT = 50L //ms
            var ACTION_TIMEOUT = 5_000L
            var ASSERTION_TIMEOUT = 5_000L
        }

        class ViewActionConfig {
            companion object {
                var allowedExceptions = mutableListOf<Class<out Throwable>>(
                        PerformException::class.java,
                        NoMatchingViewException::class.java
                )
                var resultAnalyzer = DefaultOperationResultAnalyzer()
                val resultHandler: (EspressoOperationResult) -> Unit = {
                    resultAnalyzer.analyze(it)
                }
            }
        }

        class ViewAssertionConfig {
            companion object {
                var allowedExceptions = mutableListOf<Class<out Throwable>>(
                        PerformException::class.java,
                        NoMatchingViewException::class.java,
                        AssertionFailedError::class.java
                )
                var resultAnalyzer = DefaultOperationResultAnalyzer()
                val resultHandler: (EspressoOperationResult) -> Unit = {
                    ViewActionConfig.resultAnalyzer.analyze(it)
                }
            }
        }
    }

    class UiAutomator {
        companion object {
            var UIAUTOMATOR_OPERATION_POLLING_TIMEOUT = 50L //ms
            var ACTION_TIMEOUT = 5_000L
            var ASSERTION_TIMEOUT = 5_000L
        }

        class UiObjectConfig {
            companion object {
                var allowedExceptions = mutableListOf<Class<out Throwable>>(
                        UiObjectNotFoundException::class.java
                )
                var resultAnalyzer = DefaultOperationResultAnalyzer()
                val resultHandler: (OperationResult) -> Unit = {
                    resultAnalyzer.analyze(it)
                }
            }
        }

        class UiObject2Config {
            companion object {
                var allowedExceptions = mutableListOf<Class<out Throwable>>(
                        UiObjectNotFoundException::class.java
                )
                var resultAnalyzer = DefaultOperationResultAnalyzer()
                val resultHandler: (OperationResult) -> Unit = {
                    resultAnalyzer.analyze(it)
                }
            }
        }
    }
}