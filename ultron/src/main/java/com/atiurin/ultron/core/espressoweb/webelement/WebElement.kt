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
import com.atiurin.ultron.core.espressoweb.WebLifecycle
import com.atiurin.ultron.core.espressoweb.operation.*
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.extensions.methodToBoolean
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.containsString
import org.w3c.dom.Document

/**
 * It is recommended to create WebElement object using method in Companion object
 * like [WebElement.Companion.id], [WebElement.Companion.xpath], [WebElement.Companion.element] and etc
 */
open class WebElement(
    open val locator: Locator,
    open val value: String,
    private val webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
    private val elementReference: ElementReference? = null,
    private val windowReference: WindowReference? = null
) {
    private val contextualElements = mutableListOf<WebElement>()

    fun withContextual(webElement: WebElement) = apply {
        contextualElements.add(webElement)
    }

    internal val webViewInteraction: Web.WebInteraction<Void>
        get() {
            return if (windowReference != null) onWebView(webViewMatcher).inWindow(windowReference)
            else onWebView(webViewMatcher)
        }


    internal val webInteractionBlock: () -> Web.WebInteraction<Void>
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
    fun clearElement(
        timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
        resultHandler: (WebOperationResult<WebInteractionOperation<Evaluation>>) -> Unit =
            (UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<Evaluation>>) -> Unit)
    ) = apply {
        val result = WebLifecycle.execute(
            WebInteractionOperationExecutor(
                WebInteractionOperation(
                    webInteractionBlock = { webInteractionBlock().perform(DriverAtoms.clearElement()) },
                    name = "ClearElement of WebElement(${locator.type} = '$value')",
                    type = EspressoWebOperationType.WEB_CLEAR_ELEMENT,
                    description = "ClearElement of WebElement(${locator.type} = '$value') during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /** Returns the visible text beneath a given DOM element. */
    fun getText(
        timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
        resultHandler: (WebOperationResult<WebInteractionOperation<String>>) -> Unit =
            (UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<String>>) -> Unit)
    ): String {
        val result = WebLifecycle.execute(
            WebInteractionOperationExecutor(
                WebInteractionOperation(
                    webInteractionBlock = { webInteractionBlock().perform(DriverAtoms.getText()) },
                    name = "GetText of WebElement(${locator.type} = '$value')",
                    type = EspressoWebOperationType.WEB_GET_TEXT,
                    description = "GetText of WebElement(${locator.type} = '$value') during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return (result.operationIterationResult as WebInteractionOperationIterationResult<String>).webInteraction?.get()
            ?: throw UltronException("Couldn't getText of locator '${locator.type}' and value '$value'")
    }

    /** Simulates the javascript events to click on a particular element. */
    fun webClick(
        timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
        resultHandler: (WebOperationResult<WebInteractionOperation<Evaluation>>) -> Unit =
            (UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<Evaluation>>) -> Unit)
    ) = apply {
        WebLifecycle.execute(
            WebInteractionOperationExecutor(
                WebInteractionOperation(
                    webInteractionBlock = {
                        webInteractionBlock().perform(DriverAtoms.webClick())
                    },
                    name = "WebClick on WebElement(${locator.type} = '$value')",
                    type = EspressoWebOperationType.WEB_CLICK,
                    description = "WebClick on WebElement(${locator.type} = '$value') during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /** Simulates javascript key events sent to a certain element. */
    fun webKeys(
        text: String,
        timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
        resultHandler: (WebOperationResult<WebInteractionOperation<Evaluation>>) -> Unit =
            UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<Evaluation>>) -> Unit
    ) = apply {
        WebLifecycle.execute(
            WebInteractionOperationExecutor(
                WebInteractionOperation(
                    webInteractionBlock = {
                        webInteractionBlock().perform(DriverAtoms.webKeys(text))
                    },
                    name = "WebKeys text '$text' on WebElement(${locator.type} = '$value')",
                    type = EspressoWebOperationType.WEB_KEYS,
                    description = "WebKeys text '$text' on  WebElement(${locator.type} = '$value') during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /** Simulates javascript clear and key events sent to a certain element. */
    fun replaceText(
        text: String,
        timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
        resultHandler: (WebOperationResult<WebInteractionOperation<Evaluation>>) -> Unit =
            UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<Evaluation>>) -> Unit
    ) = apply {
        WebLifecycle.execute(
            WebInteractionOperationExecutor(
                WebInteractionOperation(
                    webInteractionBlock = {
                        webInteractionBlock()
                            .perform(DriverAtoms.clearElement())
                            .perform(DriverAtoms.webKeys(text))
                    },
                    name = " WebElement(${locator.type} = '$value') ReplaceText to '$text'",
                    type = EspressoWebOperationType.WEB_REPLACE_TEXT,
                    description = " WebElement(${locator.type} = '$value') ReplaceText to '$text' during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /** Returns {@code true} if the desired element is in view after scrolling. */
    fun webScrollIntoViewBoolean(
        timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
        resultHandler: (WebOperationResult<WebInteractionOperation<Boolean>>) -> Unit =
            UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<Boolean>>) -> Unit
    ) : Boolean{
        val result = WebLifecycle.execute(
            WebInteractionOperationExecutor(
                WebInteractionOperation(
                    webInteractionBlock = { webInteractionBlock().perform(DriverAtoms.webScrollIntoView()) },
                    name = " WebElement(${locator.type} = '$value') WebScrollIntoView",
                    type = EspressoWebOperationType.WEB_SCROLL_INTO_VIEW,
                    description = " WebElement(${locator.type} = '$value') WebScrollIntoView during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
        return (result.operationIterationResult as WebInteractionOperationIterationResult<Boolean>).webInteraction?.get()
            ?: false
    }

    /**
     * Returns webElement after scroll
     */
    fun webScrollIntoView(
        timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
        resultHandler: (WebOperationResult<WebInteractionOperation<Boolean>>) -> Unit =
            UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<Boolean>>) -> Unit
    ) = apply {
        val result = WebLifecycle.execute(
            WebInteractionOperationExecutor(
                WebInteractionOperation(
                    webInteractionBlock = { webInteractionBlock().perform(DriverAtoms.webScrollIntoView()) },
                    name = " WebElement(${locator.type} = '$value') WebScrollIntoView",
                    type = EspressoWebOperationType.WEB_SCROLL_INTO_VIEW,
                    description = " WebElement(${locator.type} = '$value') WebScrollIntoView during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /** Asserts that DOM element contains visible text beneath it self. */
    fun containsText(
        text: String,
        timeoutMs: Long = UltronConfig.Espresso.ASSERTION_TIMEOUT,
        resultHandler: (WebOperationResult<WebInteractionOperation<String>>) -> Unit =
            UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<String>>) -> Unit
    ) = apply {
        WebLifecycle.execute(
            WebInteractionOperationExecutor(
                WebInteractionOperation(
                    webInteractionBlock = {
                        webInteractionBlock().check(
                            webMatches(DriverAtoms.getText(), containsString(text))
                        )
                    },
                    name = "WebElement(${locator.type} = '$value') ContainsText '$text'",
                    type = EspressoWebOperationType.WEB_CONTAINS_TEXT,
                    description = "Assert WebElement(${locator.type} = '$value') ContainsText = '$text' during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /** Asserts that DOM element has visible text beneath it self. */
    fun hasText(
        text: String,
        timeoutMs: Long = UltronConfig.Espresso.ASSERTION_TIMEOUT,
        resultHandler: (WebOperationResult<WebInteractionOperation<String>>) -> Unit =
            UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<String>>) -> Unit
    ) = apply {
        WebLifecycle.execute(
            WebInteractionOperationExecutor(
                WebInteractionOperation(
                    webInteractionBlock = {
                        webInteractionBlock().check(
                            webMatches(DriverAtoms.getText(), `is`(text))
                        )
                    },
                    name = "WebElement(${locator.type} = '$value') HasText '$text'",
                    type = EspressoWebOperationType.WEB_HAS_TEXT,
                    description = "Assert WebElement(${locator.type} = '$value') HasText = '$text' during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /** Asserts that element exists in webView */
    fun exists(
        timeoutMs: Long = UltronConfig.Espresso.ASSERTION_TIMEOUT,
        resultHandler: (WebOperationResult<WebInteractionOperation<Void>>) -> Unit =
            UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<Void>>) -> Unit
    ) = apply {
        WebLifecycle.execute(
            WebInteractionOperationExecutor(
                WebInteractionOperation(
                    webInteractionBlock = {
                        webInteractionBlock()
                    },
                    name = "WebElement(${locator.type} = '$value') exists",
                    type = EspressoWebOperationType.WEB_EXISTS,
                    description = "Assert WebElement(${locator.type} = '$value') exists during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    protected fun hasElementAttribute(
        attributeName: String,
        attributeValueMatcher: Matcher<String>,
        timeoutMs: Long = UltronConfig.Espresso.ASSERTION_TIMEOUT,
        resultHandler: (WebOperationResult<WebInteractionOperation<Document>>) -> Unit =
            UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<Document>>) -> Unit,
        documentMatcher: Matcher<Document>
    ) = apply {
        WebLifecycle.execute(
            WebInteractionOperationExecutor(
                WebInteractionOperation(
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
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }


    /** use any webAssertion to assert it safely */
    fun <T> assertThat(
        webAssertion: WebAssertion<T>,
        timeoutMs: Long = UltronConfig.Espresso.ASSERTION_TIMEOUT,
        resultHandler: (WebOperationResult<WebInteractionOperation<T>>) -> Unit =
            UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<T>>) -> Unit
    ) = apply {
        WebLifecycle.execute(
            WebInteractionOperationExecutor(
                WebInteractionOperation(
                    webInteractionBlock = {
                        webInteractionBlock().check(webAssertion)
                    },
                    name = "WebElement(${locator.type} = '$value') custom webAssertion",
                    type = EspressoWebOperationType.WEB_ASSERT_THAT,
                    description = "WebElement(${locator.type} = '$value') custom webAssertion during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

    /** Transforms any action or assertion to Boolean value */
    fun isSuccess(block: WebElement.() -> Unit): Boolean = this.methodToBoolean(block)

    /** Removes the Element and Window references from this interaction */
    fun reset() = apply { webViewInteraction.reset() }

    companion object {
        fun className(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            elementReference: ElementReference? = null,
            windowReference: WindowReference? = null
        ): WebElement {
            return WebElement(
                Locator.CLASS_NAME,
                value,
                webViewMatcher,
                elementReference,
                windowReference
            )
        }

        fun cssSelector(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            elementReference: ElementReference? = null,
            windowReference: WindowReference? = null
        ): WebElement {
            return WebElement(
                Locator.CSS_SELECTOR,
                value,
                webViewMatcher,
                elementReference,
                windowReference
            )
        }

        fun id(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            elementReference: ElementReference? = null,
            windowReference: WindowReference? = null
        ): WebElementWithId {
            return WebElementWithId(value, webViewMatcher, elementReference, windowReference)
        }

        fun linkText(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            elementReference: ElementReference? = null,
            windowReference: WindowReference? = null
        ): WebElement {
            return WebElement(
                Locator.LINK_TEXT,
                value,
                webViewMatcher,
                elementReference,
                windowReference
            )
        }

        fun name(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            elementReference: ElementReference? = null,
            windowReference: WindowReference? = null
        ): WebElement {
            return WebElement(
                Locator.NAME,
                value,
                webViewMatcher,
                elementReference,
                windowReference
            )
        }

        fun partialLinkText(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            elementReference: ElementReference? = null,
            windowReference: WindowReference? = null
        ): WebElement {
            return WebElement(
                Locator.PARTIAL_LINK_TEXT,
                value,
                webViewMatcher,
                elementReference,
                windowReference
            )
        }

        fun tagName(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            elementReference: ElementReference? = null,
            windowReference: WindowReference? = null
        ): WebElement {
            return WebElement(
                Locator.TAG_NAME,
                value,
                webViewMatcher,
                elementReference,
                windowReference
            )
        }

        fun xpath(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            elementReference: ElementReference? = null,
            windowReference: WindowReference? = null
        ): WebElementWithXpath {
            return WebElementWithXpath(
                value,
                webViewMatcher,
                elementReference,
                windowReference
            )
        }

        fun element(
            locator: Locator,
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
            elementReference: ElementReference? = null,
            windowReference: WindowReference? = null
        ): WebElement {
            return WebElement(
                locator,
                value,
                webViewMatcher,
                elementReference,
                windowReference
            )
        }
    }
}




