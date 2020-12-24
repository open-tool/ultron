package com.atiurin.ultron.core.espressoweb.webelement

import android.view.View
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.model.WindowReference
import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.Locator
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.WebLifecycle
import com.atiurin.ultron.core.espressoweb.operation.*
import org.hamcrest.Matcher

class WebElementsList(
    val locator: Locator,
    val matcher: String,
    val webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
    val windowReference: WindowReference? = null
) {
    private val webViewInteraction =
        if (windowReference != null) Web.onWebView(webViewMatcher).inWindow(windowReference)
        else Web.onWebView(webViewMatcher)

    fun getElements(
        timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
        resultHandler: (WebOperationResult<WebInteractionOperation<List<ElementReference>>>) -> Unit =
            (UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<List<ElementReference>>>) -> Unit)
    ): List<WebElement> {
        return findMultipleElements(locator, matcher, timeoutMs, resultHandler).map {
            WebElement(
                locator,
                matcher,
                webViewMatcher,
                it
            )
        }
    }

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
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher
        ): WebElementsList {
            return WebElementsList(Locator.CLASS_NAME, value)
        }

        fun cssSelectors(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher
        ): WebElementsList {
            return WebElementsList(Locator.CSS_SELECTOR, value)
        }

        fun ids(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher
        ): WebElementsList {
            return WebElementsList(Locator.ID, value)
        }

        fun linkTexts(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher
        ): WebElementsList {
            return WebElementsList(Locator.LINK_TEXT, value)
        }

        fun names(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher
        ): WebElementsList {
            return WebElementsList(Locator.NAME, value)
        }

        fun partialLinkTexts(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher
        ): WebElementsList {
            return WebElementsList(Locator.PARTIAL_LINK_TEXT, value)
        }

        fun tagNames(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher
        ): WebElementsList {
            return WebElementsList(Locator.TAG_NAME, value)
        }

        fun xpaths(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher
        ): WebElementsList {
            return WebElementsList(Locator.XPATH, value)
        }

        fun elements(
            locator: Locator,
            matcher: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher
        ): WebElementsList {
            return WebElementsList(locator, matcher, webViewMatcher)
        }
    }
}