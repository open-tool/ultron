package com.atiurin.ultron.core.config

import android.view.View
import android.webkit.WebView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.PerformException
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.Configurator
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import com.atiurin.ultron.core.common.*
import com.atiurin.ultron.core.compose.operation.ComposeOperationResult
import com.atiurin.ultron.core.compose.operation.UltronComposeOperation
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.core.espresso.UltronEspressoOperationLifecycle
import com.atiurin.ultron.core.espresso.assertion.EspressoAssertionType
import com.atiurin.ultron.core.espressoweb.UltronWebLifecycle
import com.atiurin.ultron.core.espressoweb.operation.WebInteractionOperation
import com.atiurin.ultron.core.espressoweb.operation.WebOperationResult
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperation
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperationResult
import com.atiurin.ultron.core.uiautomator.UltronUiAutomatorLifecycle
import com.atiurin.ultron.core.uiautomator.uiobject.UiAutomatorUiSelectorOperation
import com.atiurin.ultron.exceptions.*
import com.atiurin.ultron.listeners.UltronLifecycleListener
import com.atiurin.ultron.testlifecycle.setupteardown.ConditionExecutorWrapper
import com.atiurin.ultron.testlifecycle.setupteardown.ConditionsExecutor
import com.atiurin.ultron.testlifecycle.setupteardown.DefaultConditionExecutorWrapper
import com.atiurin.ultron.testlifecycle.setupteardown.DefaultConditionsExecutor
import junit.framework.AssertionFailedError
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher

object UltronConfig {
    var LOGCAT_TAG = "Ultron"
    val operationsExcludedFromListeners = mutableListOf<UltronOperationType>(EspressoAssertionType.IDENTIFY_RECYCLER_VIEW)
    var isListenersOn = true

    fun addGlobalListener(lifecycleListener: UltronLifecycleListener){
        UltronEspressoOperationLifecycle.addListener(lifecycleListener)
        UltronWebLifecycle.addListener(lifecycleListener)
        UltronUiAutomatorLifecycle.addListener(lifecycleListener)
    }

    fun removeGlobalListener(listenerId: String){
        UltronEspressoOperationLifecycle.removeListener(listenerId)
        UltronWebLifecycle.removeListener(listenerId)
        UltronUiAutomatorLifecycle.removeListener(listenerId)
    }

    fun <T: UltronLifecycleListener> removeGlobalListener(clazz: Class<T>){
        UltronEspressoOperationLifecycle.removeListener(clazz)
        UltronWebLifecycle.removeListener(clazz)
        UltronUiAutomatorLifecycle.removeListener(clazz)
    }


    class Espresso {
        companion object {
            const val DEFAULT_ACTION_TIMEOUT = 5_000L
            const val DEFAULT_ASSERTION_TIMEOUT = 5_000L
            const val DEFAULT_RECYCLER_VIEW_LOAD_TIMEOUT = 5_000L
            const val DEFAULT_RECYCLER_VIEW_OPERATION_TIMEOUT = 5_000L

            var ESPRESSO_OPERATION_POLLING_TIMEOUT = 0L //ms
            var ACTION_TIMEOUT = DEFAULT_ACTION_TIMEOUT
            var ASSERTION_TIMEOUT = DEFAULT_ASSERTION_TIMEOUT
            var RECYCLER_VIEW_LOAD_TIMEOUT = DEFAULT_RECYCLER_VIEW_LOAD_TIMEOUT
            var RECYCLER_VIEW_OPERATIONS_TIMEOUT = DEFAULT_RECYCLER_VIEW_OPERATION_TIMEOUT
            var RECYCLER_VIEW_ITEM_SEARCH_LIMIT = -1
            var INCLUDE_VIEW_HIERARCHY_TO_EXCEPTION = false //where it applicable

            var resultAnalyzer: OperationResultAnalyzer = UltronDefaultOperationResultAnalyzer()

            inline fun setResultAnalyzer(crossinline block: (OperationResult<Operation>) -> Boolean) {
                resultAnalyzer = object : OperationResultAnalyzer {
                    override fun <Op : Operation, OpRes : OperationResult<Op>> analyze(
                        operationResult: OpRes
                    ): Boolean {
                        return block(operationResult as OperationResult<Operation>)
                    }
                }
            }

            var webViewMatcher: Matcher<View> = allOf(isAssignableFrom(WebView::class.java), isDisplayed())
        }

        class ViewActionConfig {
            companion object {
                var allowedExceptions = mutableListOf<Class<out Throwable>>(
                    UltronWrapperException::class.java,
                    UltronException::class.java,
                    UltronAssertionException::class.java,
                    PerformException::class.java,
                    NoMatchingViewException::class.java
                )
                val resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit = {
                    resultAnalyzer.analyze(it)
                }
            }
        }

        class ViewAssertionConfig {
            companion object {
                var allowedExceptions = mutableListOf<Class<out Throwable>>(
                    UltronWrapperException::class.java,
                    UltronException::class.java,
                    UltronAssertionException::class.java,
                    PerformException::class.java,
                    NoMatchingViewException::class.java,
                    AssertionFailedError::class.java
                )
                val resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit = {
                    resultAnalyzer.analyze(it)
                }
            }
        }

        class WebInteractionOperationConfig {
            companion object {
                var allowedExceptions = mutableListOf<Class<out Throwable>>(
                    UltronWrapperException::class.java,
                    UltronException::class.java,
                    PerformException::class.java,
                    NoMatchingViewException::class.java,
                    AssertionFailedError::class.java,
                    RuntimeException::class.java
                )
                val resultHandler: (WebOperationResult<WebInteractionOperation<*>>) -> Unit =
                    {
                        resultAnalyzer.analyze(it)
                    }
            }
        }
    }

    class UiAutomator {
        companion object {
            var UIAUTOMATOR_OPERATION_POLLING_TIMEOUT = 0L //ms
            var OPERATION_TIMEOUT = 5_000L

            var resultAnalyzer: OperationResultAnalyzer = UltronDefaultOperationResultAnalyzer()

            inline fun setResultAnalyzer(crossinline block: (OperationResult<Operation>) -> Boolean) {
                resultAnalyzer = object : OperationResultAnalyzer {
                    override fun <Op : Operation, OpRes : OperationResult<Op>> analyze(
                        operationResult: OpRes
                    ): Boolean {
                        return block(operationResult as OperationResult<Operation>)
                    }
                }
            }

            var uiDevice: UiDevice =
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

            fun setTimeout(timeoutMs: Long) {
                Configurator.getInstance().apply {
                    waitForIdleTimeout = timeoutMs
                    waitForSelectorTimeout = timeoutMs
                }
            }

            fun speedUp() {
                setTimeout(0)
            }
        }

        //UiSelector
        class UiObjectConfig {
            companion object {
                var allowedExceptions = mutableListOf<Class<out Throwable>>(
                    UltronWrapperException::class.java,
                    UltronAssertionException::class.java,
                    UltronException::class.java,
                    UltronUiAutomatorException::class.java,
                    UiObjectNotFoundException::class.java,
                    NullPointerException::class.java,
                )
                val resultHandler: (UiAutomatorOperationResult<UiAutomatorUiSelectorOperation>) -> Unit =
                    {
                        resultAnalyzer.analyze(it)
                    }
            }
        }

        //BySelector
        class UiObject2Config {
            companion object {
                var allowedExceptions = mutableListOf<Class<out Throwable>>(
                    UltronWrapperException::class.java,
                    UltronAssertionException::class.java,
                    UltronException::class.java,
                    UltronUiAutomatorException::class.java,
                    UiObjectNotFoundException::class.java,
                    NullPointerException::class.java,
                )
                val resultHandler: (UiAutomatorOperationResult<UiAutomatorOperation>) -> Unit =
                    {
                        resultAnalyzer.analyze(it)
                    }
            }
        }
    }

    class Compose {
        companion object {
            const val DEFAULT_LAZY_COLUMN_OPERATIONS_TIMEOUT = 10_000L
            const val DEFAULT_OPERATION_TIMEOUT = 5_000L
            var COMPOSE_OPERATION_POLLING_TIMEOUT = 0L //ms
            var LAZY_COLUMN_OPERATIONS_TIMEOUT = DEFAULT_LAZY_COLUMN_OPERATIONS_TIMEOUT
            var LAZY_COLUMN_ITEM_SEARCH_LIMIT = -1
            var OPERATION_TIMEOUT = DEFAULT_OPERATION_TIMEOUT

            var resultAnalyzer: OperationResultAnalyzer = UltronDefaultOperationResultAnalyzer()

            inline fun setResultAnalyzer(crossinline block: (OperationResult<Operation>) -> Boolean) {
                resultAnalyzer = object : OperationResultAnalyzer {
                    override fun <Op : Operation, OpRes : OperationResult<Op>> analyze(
                        operationResult: OpRes
                    ): Boolean {
                        return block(operationResult as OperationResult<Operation>)
                    }
                }
            }

            val resultHandler: (ComposeOperationResult<UltronComposeOperation>) -> Unit = {
                resultAnalyzer.analyze(it)
            }

            var allowedExceptions = mutableListOf<Class<out Throwable>>(
                AssertionError::class.java,
                UltronWrapperException::class.java,
                UltronAssertionException::class.java,
                UltronException::class.java,
                PerformException::class.java
            )
        }
    }

    class Conditions{
        companion object{
            var conditionExecutorWrapper: ConditionExecutorWrapper = DefaultConditionExecutorWrapper()
            var conditionsExecutor: ConditionsExecutor = DefaultConditionsExecutor()
        }
    }
}
