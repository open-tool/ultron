package com.atiurin.ultron.core.espressoweb.webelement

import android.view.View
import androidx.test.espresso.web.assertion.WebAssertion
import androidx.test.espresso.web.model.Atoms
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.model.Evaluation
import androidx.test.espresso.web.model.WindowReference
import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.webdriver.DriverAtoms
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.WebLifecycle
import com.atiurin.ultron.core.espressoweb.operation.*
import com.atiurin.ultron.exceptions.UltronException
import org.hamcrest.Matcher

class WebDocument {
    companion object {
        /**
         * Evaluate JS on webView
         */
        fun evalJS(
            script: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null,
            timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler: (WebOperationResult<WebInteractionOperation<Evaluation>>) -> Unit =
                UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<Evaluation>>) -> Unit
        ) {
            val webViewInteraction =
                if (windowReference != null) Web.onWebView(webViewMatcher).inWindow(windowReference)
                else Web.onWebView(webViewMatcher)
            WebLifecycle.execute(
                WebInteractionOperationExecutor(
                    WebInteractionOperation(
                        webInteractionBlock = { webViewInteraction.perform(Atoms.script(script)) },
                        name = "WebView Evaluate JS script on $webViewMatcher",
                        type = EspressoWebOperationType.WEB_EVAL_JS_SCRIPT,
                        description = "WebDocument Evaluate JS '$script' on $webViewMatcher during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        /** use any webAssertion to assert it safely */
        fun <T> assertThat(
            webAssertion: WebAssertion<T>,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null,
            timeoutMs: Long = UltronConfig.Espresso.ASSERTION_TIMEOUT,
            resultHandler: (WebOperationResult<WebInteractionOperation<T>>) -> Unit =
                UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<T>>) -> Unit
        ) = apply {
            val webViewInteraction =
                if (windowReference != null) Web.onWebView(webViewMatcher).inWindow(windowReference)
                else Web.onWebView(webViewMatcher)
            WebLifecycle.execute(
                WebInteractionOperationExecutor(
                    WebInteractionOperation(
                        webInteractionBlock = {
                            webViewInteraction.check(webAssertion)
                        },
                        name = "WebView custom AssertThat",
                        type = EspressoWebOperationType.WEB_VIEW_ASSERT_THAT,
                        description = "Assert webView with type ${EspressoWebOperationType.WEB_VIEW_ASSERT_THAT} matches custom condition during $timeoutMs ms, webAssertion atom script ${webAssertion.atom.script}",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
        }

        /** Finds the currently active element in the document. */
        fun selectActiveElement(
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null,
            timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler: (WebOperationResult<WebInteractionOperation<ElementReference>>) -> Unit =
                UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<ElementReference>>) -> Unit
        ): ElementReference {
            val webViewInteraction =
                if (windowReference != null) Web.onWebView(webViewMatcher).inWindow(windowReference)
                else Web.onWebView(webViewMatcher)
            val result = WebLifecycle.execute(
                WebInteractionOperationExecutor(
                    WebInteractionOperation(
                        webInteractionBlock = { webViewInteraction.perform(DriverAtoms.selectActiveElement()) },
                        name = "WebView selectActiveElement",
                        type = EspressoWebOperationType.WEB_SELECT_ACTIVE_ELEMENT,
                        description = "WebView selectActiveElement of $webViewMatcher during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
            return (result.operationIterationResult as WebInteractionOperationIterationResult<ElementReference>).webInteraction?.get()
                ?: throw UltronException("Couldn't select ElementReference to activeElement with webViewInteraction = $webViewInteraction")
        }

        /** Selects a subframe of the currently selected window by it's index. */
        fun selectFrameByIndex(
            index: Int,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null,
            timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler: (WebOperationResult<WebInteractionOperation<WindowReference>>) -> Unit =
                UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<WindowReference>>) -> Unit
        ): WindowReference {
            val webViewInteraction =
                if (windowReference != null) Web.onWebView(webViewMatcher).inWindow(windowReference)
                else Web.onWebView(webViewMatcher)
            val result = WebLifecycle.execute(
                WebInteractionOperationExecutor(
                    WebInteractionOperation(
                        webInteractionBlock = {
                            webViewInteraction.perform(
                                DriverAtoms.selectFrameByIndex(
                                    index
                                )
                            )
                        },
                        name = "WebView selectFrameByIndex '$index'",
                        type = EspressoWebOperationType.WEB_SELECT_FRAME_BY_INDEX,
                        description = "WebView selectFrameByIndex '$index' on $webViewMatcher during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
            return (result.operationIterationResult as WebInteractionOperationIterationResult<WindowReference>).webInteraction?.get()
                ?: throw UltronException("Couldn't select WindowReference by index '$index' with webViewInteraction = $webViewInteraction")
        }

        /** Selects a subframe of the given window by it's index. */
        fun selectFrameByIndex(
            index: Int,
            root: WindowReference,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null,
            timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler: (WebOperationResult<WebInteractionOperation<WindowReference>>) -> Unit =
                UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<WindowReference>>) -> Unit
        ): WindowReference {
            val webViewInteraction =
                if (windowReference != null) Web.onWebView(webViewMatcher).inWindow(windowReference)
                else Web.onWebView(webViewMatcher)
            val result = WebLifecycle.execute(
                WebInteractionOperationExecutor(
                    WebInteractionOperation(
                        webInteractionBlock = {
                            webViewInteraction.perform(
                                DriverAtoms.selectFrameByIndex(
                                    index,
                                    root
                                )
                            )
                        },
                        name = "WebView selectFrameByIndex '$index' with root",
                        type = EspressoWebOperationType.WEB_SELECT_FRAME_BY_INDEX,
                        description = "WebView selectFrameByIndex '$index' with root '$root' on $webViewMatcher during  $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
            return (result.operationIterationResult as WebInteractionOperationIterationResult<WindowReference>).webInteraction?.get()
                ?: throw UltronException("Couldn't select WindowReference by index '$index' with root with webViewInteraction = $webViewInteraction")
        }

        /** Selects a subframe of the current window by it's name or id. */
        fun selectFrameByIdOrName(
            idOrName: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null,
            timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler: (WebOperationResult<WebInteractionOperation<WindowReference>>) -> Unit =
                UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<WindowReference>>) -> Unit
        ): WindowReference {
            val webViewInteraction =
                if (windowReference != null) Web.onWebView(webViewMatcher).inWindow(windowReference)
                else Web.onWebView(webViewMatcher)
            val result = WebLifecycle.execute(
                WebInteractionOperationExecutor(
                    WebInteractionOperation(
                        webInteractionBlock = {
                            webViewInteraction.perform(
                                DriverAtoms.selectFrameByIdOrName(
                                    idOrName
                                )
                            )
                        },
                        name = "WebView selectFrameByIdOrName '$idOrName'",
                        type = EspressoWebOperationType.WEB_SELECT_FRAME_BY_ID_OR_NAME,
                        description = "WebView selectFrameByIdOrName '$idOrName' on $webViewMatcher during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
            return (result.operationIterationResult as WebInteractionOperationIterationResult<WindowReference>).webInteraction?.get()
                ?: throw UltronException("Couldn't select WindowReference by idOrName '$idOrName' with locator webViewInteraction = $webViewInteraction")
        }

        /** Selects a subframe of the given window by it's name or id. */
        fun selectFrameByIdOrName(
            idOrName: String,
            root: WindowReference,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null,
            timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler: (WebOperationResult<WebInteractionOperation<WindowReference>>) -> Unit =
                UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<WindowReference>>) -> Unit
        ): WindowReference {
            val webViewInteraction =
                if (windowReference != null) Web.onWebView(webViewMatcher).inWindow(windowReference)
                else Web.onWebView(webViewMatcher)
            val result = WebLifecycle.execute(
                WebInteractionOperationExecutor(
                    WebInteractionOperation(
                        webInteractionBlock = {
                            webViewInteraction.perform(
                                DriverAtoms.selectFrameByIdOrName(
                                    idOrName,
                                    root
                                )
                            )
                        },
                        name = "WebView selectFrameByIdOrName '$idOrName' with root",
                        type = EspressoWebOperationType.WEB_SELECT_FRAME_BY_ID_OR_NAME,
                        description = "WebView selectFrameByIdOrName '$idOrName' with root '$root' on $webViewMatcher during $timeoutMs ms",
                        timeoutMs = timeoutMs
                    )
                ), resultHandler
            )
            return (result.operationIterationResult as WebInteractionOperationIterationResult<WindowReference>).webInteraction?.get()
                ?: throw UltronException("Couldn't select WindowReference by idOrName '$idOrName' with root with webViewInteraction = $webViewInteraction")
        }
    }
}