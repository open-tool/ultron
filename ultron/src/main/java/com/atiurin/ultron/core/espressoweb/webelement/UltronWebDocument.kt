package com.atiurin.ultron.core.espressoweb.webelement

import android.view.View
import androidx.test.espresso.web.assertion.WebAssertion
import androidx.test.espresso.web.model.Atoms
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.model.Evaluation
import androidx.test.espresso.web.model.WindowReference
import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.webdriver.DriverAtoms
import com.atiurin.ultron.core.common.UltronOperationType
import com.atiurin.ultron.core.common.assertion.DefaultOperationAssertion
import com.atiurin.ultron.core.common.assertion.OperationAssertion
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.UltronWebLifecycle
import com.atiurin.ultron.core.espressoweb.operation.*
import com.atiurin.ultron.exceptions.UltronException
import org.hamcrest.Matcher

/**
 * Represents document of webView
 * Provides methods for interaction with document
 */
class UltronWebDocument {
    companion object {
        /**
         * Performs a force enable of Javascript on a WebView.
         *
         * <p>All WebView interactions are done via Javascript - therefore the WebView we are working on
         * must support Javascript evaluation.
         *
         * <p>Enabling Javascript may cause the WebView under test to be reloaded. This is necessary to
         * ensure the test infrastructure javascript bridges are loaded by the WebView.
         */
        @JvmStatic
        @JvmOverloads
        fun forceJavascriptEnabled(
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler: (WebOperationResult<WebInteractionOperation<Void>>) -> Unit = UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<Void>>) -> Unit
        ) {
            val webViewInteraction = Web.onWebView(webViewMatcher)
            executeOperationVoid(
                webInteractionBlock = { webViewInteraction.forceJavascriptEnabled() },
                name = "WebView forceJavascriptEnabled on $webViewMatcher",
                type = EspressoWebOperationType.WEB_EVAL_JS_SCRIPT,
                description = "WebDocument forceJavascriptEnabled' on $webViewMatcher during $timeoutMs ms",
                timeoutMs = timeoutMs,
                resultHandler = resultHandler
            )
        }

        /**
         * Evaluate JS on webView
         */
        @JvmStatic
        @JvmOverloads
        fun evalJS(
            script: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null,
            timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler: (WebOperationResult<WebInteractionOperation<Evaluation>>) -> Unit = UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<Evaluation>>) -> Unit
        ) {
            val webViewInteraction = if (windowReference != null) Web.onWebView(webViewMatcher).inWindow(windowReference)
            else Web.onWebView(webViewMatcher)
            executeOperation(
                webInteractionBlock = { webViewInteraction.perform(Atoms.script(script)) },
                name = "WebView Evaluate JS script on $webViewMatcher",
                type = EspressoWebOperationType.WEB_EVAL_JS_SCRIPT,
                description = "WebDocument Evaluate JS '$script' on $webViewMatcher during $timeoutMs ms",
                timeoutMs = timeoutMs,
                resultHandler = resultHandler
            )
        }

        /** use any webAssertion to assert it safely */
        @JvmStatic
        @JvmOverloads
        fun <T> assertThat(
            webAssertion: WebAssertion<T>,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null,
            timeoutMs: Long = UltronConfig.Espresso.ASSERTION_TIMEOUT,
            resultHandler: (WebOperationResult<WebInteractionOperation<T>>) -> Unit = UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<T>>) -> Unit
        ) {
            val webViewInteraction = if (windowReference != null) Web.onWebView(webViewMatcher).inWindow(windowReference)
            else Web.onWebView(webViewMatcher)
            executeOperation(
                webInteractionBlock = { webViewInteraction.check(webAssertion) },
                name = "WebView custom AssertThat",
                type = EspressoWebOperationType.WEB_VIEW_ASSERT_THAT,
                description = "Assert webView with type ${EspressoWebOperationType.WEB_VIEW_ASSERT_THAT} matches custom condition during $timeoutMs ms, webAssertion atom script ${webAssertion.atom.script}",
                timeoutMs = timeoutMs,
                resultHandler = resultHandler
            )
        }

        /** Finds the currently active element in the document. */
        @JvmStatic
        @JvmOverloads
        fun selectActiveElement(
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null,
            timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler: (WebOperationResult<WebInteractionOperation<ElementReference>>) -> Unit = UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<ElementReference>>) -> Unit
        ): ElementReference {
            val webViewInteraction = if (windowReference != null) Web.onWebView(webViewMatcher).inWindow(windowReference)
            else Web.onWebView(webViewMatcher)
            return executeOperation(
                webInteractionBlock = { webViewInteraction.perform(DriverAtoms.selectActiveElement()) },
                name = "WebView selectActiveElement",
                type = EspressoWebOperationType.WEB_SELECT_ACTIVE_ELEMENT,
                description = "WebView selectActiveElement of $webViewMatcher during $timeoutMs ms",
                timeoutMs = timeoutMs,
                resultHandler = resultHandler
            )
        }

        /** Selects a subframe of the currently selected window by it's index. */
        @JvmStatic
        @JvmOverloads
        fun selectFrameByIndex(
            index: Int,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null,
            timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler: (WebOperationResult<WebInteractionOperation<WindowReference>>) -> Unit = UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<WindowReference>>) -> Unit
        ): WindowReference {
            val webViewInteraction = if (windowReference != null) Web.onWebView(webViewMatcher).inWindow(windowReference)
            else Web.onWebView(webViewMatcher)
            return executeOperation(
                webInteractionBlock = { webViewInteraction.perform(DriverAtoms.selectFrameByIndex(index)) },
                name = "WebView selectFrameByIndex '$index'",
                type = EspressoWebOperationType.WEB_SELECT_FRAME_BY_INDEX,
                description = "WebView selectFrameByIndex '$index' on $webViewMatcher during $timeoutMs ms",
                timeoutMs = timeoutMs,
                resultHandler
            )
        }

        /** Selects a subframe of the given window by it's index. */
        @JvmStatic
        @JvmOverloads
        fun selectFrameByIndex(
            index: Int,
            root: WindowReference,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null,
            timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler: (WebOperationResult<WebInteractionOperation<WindowReference>>) -> Unit = UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<WindowReference>>) -> Unit
        ): WindowReference {
            val webViewInteraction = if (windowReference != null) Web.onWebView(webViewMatcher).inWindow(windowReference)
            else Web.onWebView(webViewMatcher)
            return executeOperation(
                webInteractionBlock = { webViewInteraction.perform(DriverAtoms.selectFrameByIndex(index, root)) },
                name = "WebView selectFrameByIndex '$index' with root",
                type = EspressoWebOperationType.WEB_SELECT_FRAME_BY_INDEX,
                description = "WebView selectFrameByIndex '$index' with root '$root' on $webViewMatcher during  $timeoutMs ms",
                timeoutMs = timeoutMs,
                resultHandler = resultHandler
            )
        }

        /** Selects a subframe of the current window by it's name or id. */
        @JvmStatic
        @JvmOverloads
        fun selectFrameByIdOrName(
            idOrName: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null,
            timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler: (WebOperationResult<WebInteractionOperation<WindowReference>>) -> Unit = UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<WindowReference>>) -> Unit
        ): WindowReference {
            val webViewInteraction = if (windowReference != null) Web.onWebView(webViewMatcher).inWindow(windowReference)
            else Web.onWebView(webViewMatcher)
            return executeOperation(
                webInteractionBlock = { webViewInteraction.perform(DriverAtoms.selectFrameByIdOrName(idOrName)) },
                name = "WebView selectFrameByIdOrName '$idOrName'",
                type = EspressoWebOperationType.WEB_SELECT_FRAME_BY_ID_OR_NAME,
                description = "WebView selectFrameByIdOrName '$idOrName' on $webViewMatcher during $timeoutMs ms",
                timeoutMs = timeoutMs,
                resultHandler = resultHandler
            )
        }

        /** Selects a subframe of the given window by it's name or id. */
        @JvmStatic
        @JvmOverloads
        fun selectFrameByIdOrName(
            idOrName: String,
            root: WindowReference,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null,
            timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler: (WebOperationResult<WebInteractionOperation<WindowReference>>) -> Unit = UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<WindowReference>>) -> Unit
        ): WindowReference {
            val webViewInteraction = if (windowReference != null) Web.onWebView(webViewMatcher).inWindow(windowReference)
            else Web.onWebView(webViewMatcher)
            return executeOperation(
                webInteractionBlock = { webViewInteraction.perform(DriverAtoms.selectFrameByIdOrName(idOrName, root)) },
                name = "WebView selectFrameByIdOrName '$idOrName' with root",
                type = EspressoWebOperationType.WEB_SELECT_FRAME_BY_ID_OR_NAME,
                description = "WebView selectFrameByIdOrName '$idOrName' with root '$root' on $webViewMatcher during $timeoutMs ms",
                timeoutMs = timeoutMs,
                resultHandler = resultHandler
            )
        }

        fun <R> executeOperation(
            webInteractionBlock: () -> Web.WebInteraction<R>,
            name: String,
            type: UltronOperationType,
            description: String,
            timeoutMs: Long,
            resultHandler: (WebOperationResult<WebInteractionOperation<R>>) -> Unit,
            assertion: OperationAssertion = DefaultOperationAssertion("") {}
        ): R {
            val result = UltronWebLifecycle.execute(
                WebInteractionOperationExecutor(
                    WebInteractionOperation(
                        webInteractionBlock = webInteractionBlock, name = name, type = type, description = description, timeoutMs = timeoutMs, assertion = assertion
                    )
                ), resultHandler
            )
            return (result.operationIterationResult as WebInteractionOperationIterationResult<R>).webInteraction?.get()
                ?: throw UltronException("Couldn't get result of $name")
        }

        fun executeOperationVoid(
            webInteractionBlock: () -> Web.WebInteraction<Void>,
            name: String,
            type: UltronOperationType,
            description: String,
            timeoutMs: Long,
            resultHandler: (WebOperationResult<WebInteractionOperation<Void>>) -> Unit,
            assertion: OperationAssertion = DefaultOperationAssertion("") {}
        ){
            UltronWebLifecycle.execute(
                WebInteractionOperationExecutor(
                    WebInteractionOperation(
                        webInteractionBlock = webInteractionBlock, name = name, type = type, description = description, timeoutMs = timeoutMs, assertion = assertion
                    )
                ), resultHandler
            )
        }
    }
}