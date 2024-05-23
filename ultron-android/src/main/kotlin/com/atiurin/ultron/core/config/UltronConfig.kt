package com.atiurin.ultron.core.config

import android.annotation.SuppressLint
import android.view.View
import android.webkit.WebView
import androidx.test.espresso.AmbiguousViewMatcherException
import androidx.test.espresso.DaggerBaseLayerComponent
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.base.ActiveRootLister
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.internal.platform.os.ControlledLooper
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.Configurator
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.core.common.OperationResultAnalyzer
import com.atiurin.ultron.core.common.UltronDefaultOperationResultAnalyzer
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
import com.atiurin.ultron.exceptions.UltronAssertionException
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.exceptions.UltronOperationException
import com.atiurin.ultron.exceptions.UltronUiAutomatorException
import com.atiurin.ultron.exceptions.UltronWrapperException
import com.atiurin.ultron.extensions.simpleClassName
import com.atiurin.ultron.listeners.LogLifecycleListener
import com.atiurin.ultron.listeners.UltronLifecycleListener
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.log.getFileLogger
import com.atiurin.ultron.testlifecycle.setupteardown.ConditionExecutorWrapper
import com.atiurin.ultron.testlifecycle.setupteardown.ConditionsExecutor
import com.atiurin.ultron.testlifecycle.setupteardown.DefaultConditionExecutorWrapper
import com.atiurin.ultron.testlifecycle.setupteardown.DefaultConditionsExecutor
import junit.framework.AssertionFailedError
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
import kotlin.reflect.KClass

object UltronConfig {
    init {
        UltronCommonConfig.operationsExcludedFromListeners.apply {
            add(EspressoAssertionType.IDENTIFY_RECYCLER_VIEW)
        }
    }

    @Deprecated(
        message = "Default moved to UltronCommonConfig.Defaults",
        replaceWith = ReplaceWith(expression = "UltronCommonConfig.Defaults.OPERATION_TIMEOUT_MS")
    )
    val DEFAULT_OPERATION_TIMEOUT_MS = UltronCommonConfig.Defaults.OPERATION_TIMEOUT_MS

    var params: UltronConfigParams = UltronConfigParams()

    fun applyRecommended() {
        params = UltronConfigParams()
        modify()
    }

    fun apply(block: UltronConfigParams.() -> Unit) {
        params.block()
        modify()
    }

    private fun modify() {
        Espresso.ACTION_TIMEOUT = params.operationTimeoutMs
        Espresso.ASSERTION_TIMEOUT = params.operationTimeoutMs
        UltronCommonConfig.addListener(LogLifecycleListener())
        if (params.logToFile) {
            UltronLog.addLogger(getFileLogger())
        } else {
            UltronLog.removeLogger(UltronLog.fileLogger.id)
        }
        if (params.accelerateUiAutomator) {
            UiAutomator.speedUp()
        }
        UltronLog.info("UltronConfig applied with params $params")
    }

    @Deprecated(
        message = "Listeners storage moved to UltronCommonConfig",
        replaceWith = ReplaceWith(expression = "UltronCommonConfig.addListener(Listener)")
    )
    fun addGlobalListener(lifecycleListener: UltronLifecycleListener) {
        UltronLog.info(
            "Add Ultron global listener ${lifecycleListener.simpleClassName()}. " +
                    "It's applied for Espresso, EspressoWeb and UiAutomator operations."
        )
        UltronCommonConfig.addListener(lifecycleListener)
    }

    @Deprecated(
        message = "Listeners storage moved to UltronCommonConfig",
        replaceWith = ReplaceWith(expression = "UltronCommonConfig.removeListener(listenerId)")
    )
    fun removeGlobalListener(listenerId: String) {
        UltronLog.info("Remove Ultron global listener with id ${listenerId}. ")
        UltronCommonConfig.removeListener(listenerId)
    }

    @Deprecated(
        message = "Listeners storage moved to UltronCommonConfig",
        replaceWith = ReplaceWith(expression = "UltronCommonConfig.removeListener(Listener::class)")
    )
    fun <T : UltronLifecycleListener> removeGlobalListener(clazz: KClass<T>) {
        UltronLog.info("Remove Ultron global listener  ${clazz.simpleName}. ")
        UltronCommonConfig.removeListener(clazz)
    }

    class Log {
        companion object {
            @Deprecated(
                message = "Log config moved to UltronCommonConfig",
                replaceWith = ReplaceWith(expression = "UltronCommonConfig.logDateFormat")
            )
            var dateFormat = UltronCommonConfig.logDateFormat
        }
    }

    class Espresso {
        companion object {
            val baseLayerComponent = DaggerBaseLayerComponent.create()
            val activeRootLister: ActiveRootLister = baseLayerComponent.activeRootLister()
            val uiController: UiController = baseLayerComponent.uiController()

            @SuppressLint("RestrictedApi")
            val controlledLooper: ControlledLooper = baseLayerComponent.controlledLooper()

            const val DEFAULT_RECYCLER_VIEW_LOAD_TIMEOUT = 5_000L
            const val DEFAULT_RECYCLER_VIEW_OPERATION_TIMEOUT = 5_000L

            var ESPRESSO_OPERATION_POLLING_TIMEOUT = UltronCommonConfig.Defaults.POLLING_TIMEOUT_MS
            var ACTION_TIMEOUT = UltronCommonConfig.operationTimeoutMs
            var ASSERTION_TIMEOUT = UltronCommonConfig.operationTimeoutMs
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
                    NoMatchingViewException::class.java,
                    AmbiguousViewMatcherException::class.java,
                    UltronOperationException::class.java
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
                    UltronOperationException::class.java,
                    PerformException::class.java,
                    NoMatchingViewException::class.java,
                    AssertionFailedError::class.java,
                    AmbiguousViewMatcherException::class.java
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
            var UIAUTOMATOR_OPERATION_POLLING_TIMEOUT = UltronCommonConfig.Defaults.POLLING_TIMEOUT_MS
            var OPERATION_TIMEOUT = UltronCommonConfig.Defaults.OPERATION_TIMEOUT_MS

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

            var uiDevice: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

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

    class Conditions {
        companion object {
            @Deprecated(
                message = "ConditionExecutorWrapper moved to UltronAndroidCommonConfig.",
                replaceWith = ReplaceWith("UltronAndroidCommonConfig.Conditions.conditionExecutorWrapper")
            )
            var conditionExecutorWrapper: ConditionExecutorWrapper = UltronAndroidCommonConfig.Conditions.conditionExecutorWrapper

            @Deprecated(
                message = "ConditionsExecutor moved to UltronAndroidCommonConfig.",
                replaceWith = ReplaceWith("UltronAndroidCommonConfig.Conditions.conditionsExecutor")
            )
            var conditionsExecutor: ConditionsExecutor =  UltronAndroidCommonConfig.Conditions.conditionsExecutor
        }
    }
}
