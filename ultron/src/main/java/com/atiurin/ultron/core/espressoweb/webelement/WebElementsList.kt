package com.atiurin.ultron.core.espressoweb.webelement

import android.view.View
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.model.WindowReference
import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.Locator
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.WebLifecycle
import com.atiurin.ultron.core.espressoweb.operation.*
import org.hamcrest.Matcher

class WebElementsList(
    val locator: Locator,
    val value: String,
    internal val webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
    internal val windowReference: WindowReference? = null
) {
    private val webViewInteraction: Web.WebInteraction<Void>
        get() {
            return if (windowReference != null) onWebView(webViewMatcher).inWindow(windowReference)
            else onWebView(webViewMatcher)
        }

    /**
     * @return list of [WebElement], empty list if no elements found
     */
    fun getElements(
        timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
        resultHandler: (WebOperationResult<WebInteractionOperation<List<ElementReference>>>) -> Unit =
            (UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<List<ElementReference>>>) -> Unit)
    ): List<WebElement> {
        return findMultipleElements(locator, value, timeoutMs, resultHandler).map {
            WebElement(
                locator,
                value,
                webViewMatcher,
                it
            )
        }
    }

    /**
     * @return size of elements, 0 if no elements found
     */
    fun getSize(): Int {
        return getElements().size
    }

    private fun findMultipleElements(
        locator: Locator,
        matcher: String,
        timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
        resultHandler: (WebOperationResult<WebInteractionOperation<List<ElementReference>>>) -> Unit =
            (UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<List<ElementReference>>>) -> Unit)
    ): List<ElementReference> {
        val result = WebLifecycle.execute(
            WebInteractionOperationExecutor(
                WebInteractionOperation(
                    webInteractionBlock = {
                        webViewInteraction.withNoTimeout()
                            .perform(DriverAtoms.findMultipleElements(locator, matcher))
                    },
                    name = "WebElementsList(${locator.type} = '$matcher') findMultipleElements",
                    type = EspressoWebOperationType.WEB_FIND_MULTIPLE_ELEMENTS,
                    description = "WebElementsList(${locator.type} = '$matcher') findMultipleElements during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return (result.operationIterationResult as WebInteractionOperationIterationResult<List<ElementReference>>).webInteraction?.get()
            ?: emptyList()
    }

    companion object {
        fun classNames(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null
        ): WebElementsList {
            return WebElementsList(Locator.CLASS_NAME, value, webViewMatcher, windowReference)
        }

        fun cssSelectors(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null
        ): WebElementsList {
            return WebElementsList(Locator.CSS_SELECTOR, value, webViewMatcher, windowReference)
        }

        fun ids(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null
        ): WebElementsList {
            return WebElementsList(Locator.ID, value, webViewMatcher, windowReference)
        }

        fun linkTexts(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null
        ): WebElementsList {
            return WebElementsList(Locator.LINK_TEXT, value, webViewMatcher, windowReference)
        }

        fun names(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null
        ): WebElementsList {
            return WebElementsList(Locator.NAME, value, webViewMatcher, windowReference)
        }

        fun partialLinkTexts(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null
        ): WebElementsList {
            return WebElementsList(Locator.PARTIAL_LINK_TEXT, value, webViewMatcher, windowReference)
        }

        fun tagNames(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null
        ): WebElementsList {
            return WebElementsList(Locator.TAG_NAME, value, webViewMatcher, windowReference)
        }

        fun xpaths(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null
        ): WebElementsList {
            return WebElementsList(Locator.XPATH, value, webViewMatcher, windowReference)
        }

        fun elements(
            locator: Locator,
            matcher: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null
        ): WebElementsList {
            return WebElementsList(locator, matcher, webViewMatcher, windowReference)
        }
    }
}