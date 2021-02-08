package com.atiurin.ultron.core.espressoweb.webelement

import android.view.View
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.model.WindowReference
import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.Locator
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.UltronWebLifecycle
import com.atiurin.ultron.core.espressoweb.operation.*
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebDocument.Companion.executeOperation
import org.hamcrest.Matcher

/**
 * Represents the list of web objects with [locator] and [value]
 * It is required to create [UltronWebElements] object using method in Companion object
 * like [UltronWebElements.Companion.ids], [UltronWebElements.Companion.xpaths], [UltronWebElements.Companion.elements] and etc
 */
class UltronWebElements(
    val locator: Locator,
    val value: String,
    private val webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
    private val windowReference: WindowReference? = null,
    private val timeoutMs: Long? = null,
    private val resultHandler: (WebOperationResult<WebInteractionOperation<*>>) -> Unit = UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler
) {
    private val webViewInteraction: Web.WebInteraction<Void>
        get() {
            return if (windowReference != null) onWebView(webViewMatcher).inWindow(windowReference)
            else onWebView(webViewMatcher)
        }

    fun withTimeout(timeoutMs: Long): UltronWebElements {
        return UltronWebElements(this.locator, this.value, this.webViewMatcher, this.windowReference, timeoutMs, this.resultHandler)
    }

    fun withResultHandler(resultHandler: (WebOperationResult<WebInteractionOperation<*>>) -> Unit): UltronWebElements {
        return UltronWebElements(this.locator, this.value, this.webViewMatcher, this.windowReference, this.timeoutMs, resultHandler)
    }

    /**
     * @return list of [UltronWebElement], empty list if no elements found
     */
    fun getElements(): List<UltronWebElement> {
        return findMultipleElements(locator, value).map {
            UltronWebElement(
                locator, value, webViewMatcher, it
            )
        }
    }

    /**
     * @return size of elements, 0 if no elements found
     */
    fun getSize() = getElements().size

    private fun findMultipleElements(
        locator: Locator, matcher: String
    ): List<ElementReference> {
        return executeOperation(
            webInteractionBlock = {
                webViewInteraction.withNoTimeout().perform(DriverAtoms.findMultipleElements(locator, matcher))
            },
            name = "WebElementsList(${locator.type} = '$matcher') findMultipleElements",
            type = EspressoWebOperationType.WEB_FIND_MULTIPLE_ELEMENTS,
            description = "WebElementsList(${locator.type} = '$matcher') findMultipleElements during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler as (WebOperationResult<WebInteractionOperation<List<ElementReference>>>) -> Unit
        )
    }

    companion object {
        fun classNames(
            value: String, webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher, windowReference: WindowReference? = null
        ): UltronWebElements {
            return UltronWebElements(Locator.CLASS_NAME, value, webViewMatcher, windowReference)
        }

        fun cssSelectors(
            value: String, webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher, windowReference: WindowReference? = null
        ): UltronWebElements {
            return UltronWebElements(Locator.CSS_SELECTOR, value, webViewMatcher, windowReference)
        }

        fun ids(
            value: String, webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher, windowReference: WindowReference? = null
        ): UltronWebElements {
            return UltronWebElements(Locator.ID, value, webViewMatcher, windowReference)
        }

        fun linkTexts(
            value: String, webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher, windowReference: WindowReference? = null
        ): UltronWebElements {
            return UltronWebElements(Locator.LINK_TEXT, value, webViewMatcher, windowReference)
        }

        fun names(
            value: String, webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher, windowReference: WindowReference? = null
        ): UltronWebElements {
            return UltronWebElements(Locator.NAME, value, webViewMatcher, windowReference)
        }

        fun partialLinkTexts(
            value: String, webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher, windowReference: WindowReference? = null
        ): UltronWebElements {
            return UltronWebElements(Locator.PARTIAL_LINK_TEXT, value, webViewMatcher, windowReference)
        }

        fun tagNames(
            value: String, webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher, windowReference: WindowReference? = null
        ): UltronWebElements {
            return UltronWebElements(Locator.TAG_NAME, value, webViewMatcher, windowReference)
        }

        fun xpaths(
            value: String, webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher, windowReference: WindowReference? = null
        ): UltronWebElements {
            return UltronWebElements(Locator.XPATH, value, webViewMatcher, windowReference)
        }

        fun elements(
            locator: Locator,
            matcher: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            windowReference: WindowReference? = null
        ): UltronWebElements {
            return UltronWebElements(locator, matcher, webViewMatcher, windowReference)
        }
    }
}