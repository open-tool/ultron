package com.atiurin.ultron.core.espressoweb.webelement

import android.view.View
import androidx.test.espresso.web.assertion.WebAssertion
import androidx.test.espresso.web.assertion.WebViewAssertions.webContent
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.model.Evaluation
import androidx.test.espresso.web.model.WindowReference
import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.Locator
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.operation.EspressoWebOperationType
import com.atiurin.ultron.core.espressoweb.operation.WebInteractionOperation
import com.atiurin.ultron.core.espressoweb.operation.WebOperationResult
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebDocument.Companion.executeOperation
import com.atiurin.ultron.core.espressoweb.webelement.UltronWebDocument.Companion.executeOperationVoid
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.containsString
import org.w3c.dom.Document

/**
 * It is required to create WebElement object using method in Companion object
 * like [UltronWebElement.Companion.id], [UltronWebElement.Companion.xpath], [UltronWebElement.Companion.element] and etc
 */
open class UltronWebElement internal constructor(
    open val locator: Locator,
    open val value: String,
    private val webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
    private val elementReference: ElementReference? = null,
    private val windowReference: WindowReference? = null,
    open val timeoutMs: Long? = null,
    open val resultHandler: (WebOperationResult<WebInteractionOperation<*>>) -> Unit = UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler
) {
    private val contextualElements = mutableListOf<UltronWebElement>()

    fun withContextual(ultronWebElement: UltronWebElement) = apply {
        contextualElements.add(ultronWebElement)
    }

    open fun withTimeout(timeoutMs: Long): UltronWebElement {
        return UltronWebElement(
            this.locator, this.value, this.webViewMatcher, this.elementReference, this.windowReference, timeoutMs, this.resultHandler
        )
    }

    open fun withResultHandler(resultHandler: (WebOperationResult<WebInteractionOperation<*>>) -> Unit): UltronWebElement {
        return UltronWebElement(
            this.locator, this.value, this.webViewMatcher, this.elementReference, this.windowReference, this.timeoutMs, resultHandler
        )
    }

    internal val webViewInteraction: Web.WebInteraction<Void>
        get() {
            return if (windowReference != null) onWebView(webViewMatcher).inWindow(windowReference)
            else onWebView(webViewMatcher)
        }


    val webInteractionBlock: () -> Web.WebInteraction<Void>
        get() = {
            var wvi = if (elementReference == null) {
                webViewInteraction.withElement(findElement(locator, value))
            } else webViewInteraction.withElement(elementReference)
            contextualElements.forEach {
                wvi = wvi.withContextualElement(
                    findElement(it.locator, it.value)
                )
            }
            wvi
        }

    /** Clears content from an editable element. */
    fun clearElement() = apply {
        executeOperation(
            webInteractionBlock = { webInteractionBlock().perform(DriverAtoms.clearElement()) },
            name = "ClearElement of WebElement(${locator.type} = '$value')",
            type = EspressoWebOperationType.WEB_CLEAR_ELEMENT,
            description = "ClearElement of WebElement(${locator.type} = '$value') during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler = resultHandler as (WebOperationResult<WebInteractionOperation<Evaluation>>) -> Unit
        )
    }

    /** Returns the visible text beneath a given DOM element. */
    fun getText(): String {
        return executeOperation(
            webInteractionBlock = { webInteractionBlock().perform(DriverAtoms.getText()) },
            name = "GetText of WebElement(${locator.type} = '$value')",
            type = EspressoWebOperationType.WEB_GET_TEXT,
            description = "GetText of WebElement(${locator.type} = '$value') during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler = resultHandler as (WebOperationResult<WebInteractionOperation<String>>) -> Unit
        )
    }

    /** Simulates the javascript events to click on a particular element. */
    fun webClick() = apply {
        executeOperation(
            webInteractionBlock = {
                webInteractionBlock().perform(DriverAtoms.webClick())
            },
            name = "WebClick on WebElement(${locator.type} = '$value')",
            type = EspressoWebOperationType.WEB_CLICK,
            description = "WebClick on WebElement(${locator.type} = '$value') during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler = resultHandler as (WebOperationResult<WebInteractionOperation<Evaluation>>) -> Unit
        )
    }

    /** Simulates javascript key events sent to a certain element. */
    fun webKeys(text: String) = apply {
        executeOperation(
            webInteractionBlock = {
                webInteractionBlock().perform(DriverAtoms.webKeys(text))
            },
            name = "WebKeys text '$text' on WebElement(${locator.type} = '$value')",
            type = EspressoWebOperationType.WEB_KEYS,
            description = "WebKeys text '$text' on  WebElement(${locator.type} = '$value') during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler = resultHandler as (WebOperationResult<WebInteractionOperation<Evaluation>>) -> Unit
        )
    }

    /** Simulates javascript clear and key events sent to a certain element. */
    fun replaceText(
        text: String
    ) = apply {
        executeOperation(
            webInteractionBlock = {
                webInteractionBlock().perform(DriverAtoms.clearElement()).perform(DriverAtoms.webKeys(text))
            },
            name = " WebElement(${locator.type} = '$value') ReplaceText to '$text'",
            type = EspressoWebOperationType.WEB_REPLACE_TEXT,
            description = " WebElement(${locator.type} = '$value') ReplaceText to '$text' during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler = resultHandler as (WebOperationResult<WebInteractionOperation<Evaluation>>) -> Unit
        )
    }

    /** Returns {@code true} if the desired element is in view after scrolling. */
    fun webScrollIntoViewBoolean(
    ): Boolean {
        return executeOperation(
            webInteractionBlock = { webInteractionBlock().perform(DriverAtoms.webScrollIntoView()) },
            name = " WebElement(${locator.type} = '$value') WebScrollIntoView",
            type = EspressoWebOperationType.WEB_SCROLL_INTO_VIEW,
            description = " WebElement(${locator.type} = '$value') WebScrollIntoView during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler = resultHandler as (WebOperationResult<WebInteractionOperation<Boolean>>) -> Unit
        )
    }

    /** Executes scroll to view. */
    fun webScrollIntoView() = apply {
        executeOperation(
            webInteractionBlock = { webInteractionBlock().perform(DriverAtoms.webScrollIntoView()) },
            name = " WebElement(${locator.type} = '$value') WebScrollIntoView",
            type = EspressoWebOperationType.WEB_SCROLL_INTO_VIEW,
            description = " WebElement(${locator.type} = '$value') WebScrollIntoView during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT,
            resultHandler = resultHandler as (WebOperationResult<WebInteractionOperation<Boolean>>) -> Unit
        )
    }

    /** Asserts that DOM element contains visible text beneath it self. */
    fun containsText(
        text: String
    ) = apply {
        executeOperation(
            webInteractionBlock = {
                webInteractionBlock().check(
                    webMatches(DriverAtoms.getText(), containsString(text))
                )
            },
            name = "WebElement(${locator.type} = '$value') ContainsText '$text'",
            type = EspressoWebOperationType.WEB_CONTAINS_TEXT,
            description = "Assert WebElement(${locator.type} = '$value') ContainsText = '$text' during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT,
            resultHandler = resultHandler as (WebOperationResult<WebInteractionOperation<String>>) -> Unit
        )
    }

    /** Asserts that DOM element has visible text beneath it self. */
    fun hasText(text: String) = apply {
        executeOperation(
            webInteractionBlock = {
                webInteractionBlock().check(
                    webMatches(DriverAtoms.getText(), `is`(text))
                )
            },
            name = "WebElement(${locator.type} = '$value') HasText '$text'",
            type = EspressoWebOperationType.WEB_HAS_TEXT,
            description = "Assert WebElement(${locator.type} = '$value') HasText = '$text' during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT,
            resultHandler = resultHandler as (WebOperationResult<WebInteractionOperation<String>>) -> Unit
        )
    }

    /** Asserts that element exists in webView */
    fun exists() = apply {
        executeOperationVoid(
            webInteractionBlock = {
                webInteractionBlock()
            },
            name = "WebElement(${locator.type} = '$value') exists",
            type = EspressoWebOperationType.WEB_EXISTS,
            description = "Assert WebElement(${locator.type} = '$value') exists during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT,
            resultHandler = resultHandler as (WebOperationResult<WebInteractionOperation<Void>>) -> Unit
        )
    }

    protected fun hasElementAttribute(
        attributeName: String, attributeValueMatcher: Matcher<String>, documentMatcher: Matcher<Document>
    ) = apply {
        executeOperation(
            webInteractionBlock = {
                webViewInteraction.check(
                    webContent(
                        documentMatcher
                    )
                )
            },
            name = " WebElement(${locator.type} = '$value') hasAttribute '$attributeName' matches $attributeValueMatcher",
            type = EspressoWebOperationType.WEB_HAS_ATTRIBUTE,
            description = "Assert WebElement(${locator.type} = '$value') hasAttribute '$attributeName' matches $attributeValueMatcher during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT,
            resultHandler = resultHandler as (WebOperationResult<WebInteractionOperation<Document>>) -> Unit
        )
    }


    /** use any webAssertion to assert it safely */
    fun <T> assertThat(
        webAssertion: WebAssertion<T>
    ) = apply {
        executeOperation(
            webInteractionBlock = {
                webInteractionBlock().check(webAssertion)
            },
            name = "WebElement(${locator.type} = '$value') custom webAssertion",
            type = EspressoWebOperationType.WEB_ASSERT_THAT,
            description = "WebElement(${locator.type} = '$value') custom webAssertion during $timeoutMs ms",
            timeoutMs = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT,
            resultHandler = resultHandler as (WebOperationResult<WebInteractionOperation<T>>) -> Unit
        )
    }

    /** Transforms any action or assertion to Boolean value */
    fun isSuccess(block: UltronWebElement.() -> Unit): Boolean = runCatching { block() }.isSuccess

    /** Removes the Element and Window references from this interaction */
    fun reset() = apply { webViewInteraction.reset() }

    companion object {
        fun className(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            elementReference: ElementReference? = null,
            windowReference: WindowReference? = null
        ): UltronWebElement {
            return UltronWebElement(
                Locator.CLASS_NAME, value, webViewMatcher, elementReference, windowReference
            )
        }

        fun cssSelector(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            elementReference: ElementReference? = null,
            windowReference: WindowReference? = null
        ): UltronWebElement {
            return UltronWebElement(
                Locator.CSS_SELECTOR, value, webViewMatcher, elementReference, windowReference
            )
        }

        fun id(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            elementReference: ElementReference? = null,
            windowReference: WindowReference? = null
        ): UltronWebElementId {
            return UltronWebElementId(value, webViewMatcher, elementReference, windowReference)
        }

        fun linkText(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            elementReference: ElementReference? = null,
            windowReference: WindowReference? = null
        ): UltronWebElement {
            return UltronWebElement(
                Locator.LINK_TEXT, value, webViewMatcher, elementReference, windowReference
            )
        }

        fun name(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            elementReference: ElementReference? = null,
            windowReference: WindowReference? = null
        ): UltronWebElement {
            return UltronWebElement(
                Locator.NAME, value, webViewMatcher, elementReference, windowReference
            )
        }

        fun partialLinkText(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            elementReference: ElementReference? = null,
            windowReference: WindowReference? = null
        ): UltronWebElement {
            return UltronWebElement(
                Locator.PARTIAL_LINK_TEXT, value, webViewMatcher, elementReference, windowReference
            )
        }

        fun tagName(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            elementReference: ElementReference? = null,
            windowReference: WindowReference? = null
        ): UltronWebElement {
            return UltronWebElement(
                Locator.TAG_NAME, value, webViewMatcher, elementReference, windowReference
            )
        }

        fun xpath(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            elementReference: ElementReference? = null,
            windowReference: WindowReference? = null
        ): UltronWebElementXpath {
            return UltronWebElementXpath(
                value, webViewMatcher, elementReference, windowReference
            )
        }

        fun element(
            locator: Locator,
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            elementReference: ElementReference? = null,
            windowReference: WindowReference? = null
        ): UltronWebElement {
            return UltronWebElement(
                locator, value, webViewMatcher, elementReference, windowReference
            )
        }
    }
}




