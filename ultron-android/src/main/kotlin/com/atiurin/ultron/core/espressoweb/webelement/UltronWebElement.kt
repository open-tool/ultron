package com.atiurin.ultron.core.espressoweb.webelement

import android.view.View
import androidx.test.espresso.web.assertion.WebAssertion
import androidx.test.espresso.web.assertion.WebViewAssertions.webContent
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.model.WindowReference
import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.Locator
import com.atiurin.ultron.core.common.CommonOperationType
import com.atiurin.ultron.core.common.DefaultElementInfo
import com.atiurin.ultron.core.common.ElementInfo
import com.atiurin.ultron.core.common.UltronOperationType
import com.atiurin.ultron.core.common.assertion.DefaultOperationAssertion
import com.atiurin.ultron.core.common.assertion.EmptyOperationAssertion
import com.atiurin.ultron.core.common.assertion.OperationAssertion
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.UltronWebLifecycle
import com.atiurin.ultron.core.espressoweb.operation.EspressoWebOperationType
import com.atiurin.ultron.core.espressoweb.operation.WebInteractionOperation
import com.atiurin.ultron.core.espressoweb.operation.WebInteractionOperationExecutor
import com.atiurin.ultron.core.espressoweb.operation.WebInteractionOperationIterationResult
import com.atiurin.ultron.core.espressoweb.operation.WebOperationResult
import com.atiurin.ultron.listeners.setListenersState
import com.atiurin.ultron.log.UltronLog
import org.hamcrest.Matcher
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.`is`
import org.w3c.dom.Document

/**
 * It is required to create WebElement object using method in Companion object
 * like [UltronWebElement.Companion.id], [UltronWebElement.Companion.xpath], [UltronWebElement.Companion.element] and etc
 */
@Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
open class UltronWebElement internal constructor(
    open val locator: Locator,
    open val value: String,
    private val webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
    private val elementReference: ElementReference? = null,
    private val windowReference: WindowReference? = null,
    open val timeoutMs: Long? = null,
    open val resultHandler: (WebOperationResult<WebInteractionOperation<*>>) -> Unit = UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler,
    open val assertion: OperationAssertion = EmptyOperationAssertion(),
    open val elementInfo: ElementInfo = DefaultElementInfo(),
    private val contextualElements: List<UltronWebElement> = mutableListOf<UltronWebElement>()
) {
    private fun getActionTimeout(): Long = timeoutMs ?: UltronConfig.Espresso.ACTION_TIMEOUT
    private fun getAssertionTimeout(): Long = timeoutMs ?: UltronConfig.Espresso.ASSERTION_TIMEOUT

    init {
        if (elementInfo != null && elementInfo.name.isEmpty()) {
            this.elementInfo.name = "WebElement(locator = $locator, value = '$value')"
        }
    }

    fun withContextual(ultronWebElement: UltronWebElement, extendElementName: Boolean = true): UltronWebElement {
        val newElement = UltronWebElement(
            this.locator,
            this.value,
            this.webViewMatcher,
            this.elementReference,
            this.windowReference,
            this.timeoutMs,
            this.resultHandler,
            this.assertion,
            this.elementInfo.copy(),
            contextualElements + ultronWebElement
        )
        if (extendElementName) {
            newElement.withName("${elementInfo.name} with contextual [locator = ${ultronWebElement.locator}, value = '${ultronWebElement.value}')]")
        }
        return newElement
    }

    open fun withTimeout(timeoutMs: Long): UltronWebElement {
        return UltronWebElement(
            this.locator,
            this.value,
            this.webViewMatcher,
            this.elementReference,
            this.windowReference,
            timeoutMs,
            this.resultHandler,
            this.assertion,
            this.elementInfo.copy(),
            this.contextualElements
        )
    }

    open fun withResultHandler(resultHandler: (WebOperationResult<WebInteractionOperation<*>>) -> Unit): UltronWebElement = UltronWebElement(
        this.locator,
        this.value,
        this.webViewMatcher,
        this.elementReference,
        this.windowReference,
        this.timeoutMs,
        resultHandler,
        this.assertion,
        this.elementInfo.copy(),
        this.contextualElements
    )

    open fun withAssertion(assertion: OperationAssertion): UltronWebElement = UltronWebElement(
        this.locator,
        this.value,
        this.webViewMatcher,
        this.elementReference,
        this.windowReference,
        this.timeoutMs,
        this.resultHandler,
        assertion,
        this.elementInfo.copy(),
        this.contextualElements
    )

    open fun withAssertion(
        name: String = "", isListened: Boolean = false, block: () -> Unit
    ): UltronWebElement = UltronWebElement(
        this.locator,
        this.value,
        this.webViewMatcher,
        this.elementReference,
        this.windowReference,
        this.timeoutMs,
        this.resultHandler,
        DefaultOperationAssertion(name, block.setListenersState(isListened)),
        this.elementInfo.copy(),
        this.contextualElements
    )

    open fun withName(name: String) = apply { elementInfo.name = name }

    open fun withMetaInfo(meta: Any) = apply { elementInfo.meta = meta }


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
            UltronLog.info("(UContextual): " + contextualElements.map { "[${it.locator}. ${it.value}]" }.toString())
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
            getUltronWebActionOperation(
                webInteractionBlock = { webInteractionBlock().perform(DriverAtoms.clearElement()) },
                name = "ClearElement of '${elementInfo.name}'",
                type = EspressoWebOperationType.WEB_CLEAR_ELEMENT,
                description = "ClearElement of '${elementInfo.name}' during $timeoutMs ms"
            )
        )
    }

    /** Returns the visible text beneath a given DOM element. */
    fun getText(): String {
        return executeOperation(
            getUltronWebActionOperation(
                webInteractionBlock = { webInteractionBlock().perform(DriverAtoms.getText()) },
                name = "GetText of '${elementInfo.name}'",
                type = EspressoWebOperationType.WEB_GET_TEXT,
                description = "GetText of '${elementInfo.name}' during $timeoutMs ms"
            )
        ) ?: ""
    }

    /** Simulates the javascript events to click on a particular element. */
    fun webClick() = apply {
        executeOperation(
            getUltronWebActionOperation(
                webInteractionBlock = {
                    webInteractionBlock().perform(DriverAtoms.webClick())
                },
                name = "WebClick on '${elementInfo.name}'",
                type = EspressoWebOperationType.WEB_CLICK,
                description = "WebClick on '${elementInfo.name}' during $timeoutMs ms"
            )
        )
    }

    /** Simulates javascript key events sent to a certain element. */
    fun webKeys(text: String) = apply {
        executeOperation(
            getUltronWebActionOperation(
                webInteractionBlock = {
                    webInteractionBlock().perform(DriverAtoms.webKeys(text))
                },
                name = "WebKeys text '$text' on '${elementInfo.name}'",
                type = EspressoWebOperationType.WEB_KEYS,
                description = "WebKeys text '$text' on '${elementInfo.name}' during $timeoutMs ms"
            )
        )
    }

    /** Simulates javascript clear and key events sent to a certain element. */
    fun replaceText(
        text: String
    ) = apply {
        executeOperation(
            getUltronWebActionOperation(
                webInteractionBlock = {
                    webInteractionBlock().perform(DriverAtoms.clearElement()).perform(DriverAtoms.webKeys(text))
                },
                name = "${elementInfo.name} ReplaceText to '$text'",
                type = EspressoWebOperationType.WEB_REPLACE_TEXT,
                description = "'${elementInfo.name}' ReplaceText to '$text' during $timeoutMs ms"
            )
        )
    }

    /** Returns {@code true} if the desired element is in view after scrolling. */
    fun webScrollIntoViewBoolean(): Boolean {
        return executeOperation(
            getUltronWebActionOperation(
                webInteractionBlock = { webInteractionBlock().perform(DriverAtoms.webScrollIntoView()) },
                name = "${elementInfo.name} WebScrollIntoView",
                type = EspressoWebOperationType.WEB_SCROLL_INTO_VIEW,
                description = "'${elementInfo.name}' WebScrollIntoView during $timeoutMs ms"
            )
        ) ?: false
    }

    /** Executes scroll to view. */
    fun webScrollIntoView() = apply {
        executeOperation(
            getUltronWebActionOperation(
                webInteractionBlock = { webInteractionBlock().perform(DriverAtoms.webScrollIntoView()) },
                name = "${elementInfo.name} WebScrollIntoView",
                type = EspressoWebOperationType.WEB_SCROLL_INTO_VIEW,
                description = "'${elementInfo.name}' WebScrollIntoView during $timeoutMs ms"
            )
        )
    }

    /** Asserts that DOM element contains visible text beneath it self. */
    fun containsText(
        text: String
    ) = apply {
        executeOperation(
            getUltronWebAssertionOperation(
                webInteractionBlock = {
                    webInteractionBlock().check(
                        webMatches(DriverAtoms.getText(), containsString(text))
                    )
                },
                name = "${elementInfo.name} ContainsText '$text'",
                type = EspressoWebOperationType.WEB_CONTAINS_TEXT,
                description = "Assert '${elementInfo.name}' ContainsText = '$text' during $timeoutMs ms"
            )
        )
    }

    /** Asserts that DOM element has visible text beneath it self. */
    fun hasText(text: String) = apply {
        executeOperation(
            getUltronWebAssertionOperation(
                webInteractionBlock = {
                    webInteractionBlock().check(
                        webMatches(DriverAtoms.getText(), `is`(text))
                    )
                },
                name = "${elementInfo.name} HasText '$text'",
                type = EspressoWebOperationType.WEB_HAS_TEXT,
                description = "Assert '${elementInfo.name}' HasText = '$text' during $timeoutMs ms"
            )
        )
    }

    /** Asserts that element exists in webView */
    fun exists() = apply {
        UltronLog.error("exists name = '${elementInfo.name}'")
        executeOperationVoid(
            getUltronWebAssertionOperation(
                webInteractionBlock = {
                    webInteractionBlock()
                },
                name = "${elementInfo.name} exists",
                type = EspressoWebOperationType.WEB_EXISTS,
                description = "Assert '${elementInfo.name}' exists during $timeoutMs ms",
            )
        )
    }

    protected fun hasElementAttribute(
        attributeName: String, attributeValueMatcher: Matcher<String>, documentMatcher: Matcher<Document>
    ) = apply {
        executeOperation(
            getUltronWebAssertionOperation(
                webInteractionBlock = {
                    webViewInteraction.check(
                        webContent(
                            documentMatcher
                        )
                    )
                },
                name = "${elementInfo.name} hasAttribute '$attributeName' matches $attributeValueMatcher",
                type = EspressoWebOperationType.WEB_HAS_ATTRIBUTE,
                description = "Assert '${elementInfo.name}' hasAttribute '$attributeName' matches $attributeValueMatcher during $timeoutMs ms"
            )
        )
    }


    /** use any webAssertion to assert it safely */
    fun <T> assertThat(
        webAssertion: WebAssertion<T>
    ) = apply {
        executeOperation(
            getUltronWebAssertionOperation(
                webInteractionBlock = {
                    webInteractionBlock().check(webAssertion)
                },
                name = "${elementInfo.name} custom webAssertion",
                type = EspressoWebOperationType.WEB_ASSERT_THAT,
                description = "${elementInfo.name} custom webAssertion during $timeoutMs ms"
            )
        )
    }

    /** Transforms any action or assertion to Boolean value */
    fun isSuccess(block: UltronWebElement.() -> Unit): Boolean = runCatching { block() }.isSuccess

    /** Removes the Element and Window references from this interaction */
    fun reset() = apply { webViewInteraction.reset() }

    fun <R> executeOperation(
        webInteractionOperation: WebInteractionOperation<R>
    ): R? {
        val result = UltronWebLifecycle.execute(
            executor = WebInteractionOperationExecutor(webInteractionOperation),
            resultHandler = resultHandler as (WebOperationResult<WebInteractionOperation<R>>) -> Unit
        )
        return (result.operationIterationResult as WebInteractionOperationIterationResult<R>).webInteraction?.get()
    }

    fun <R> executeOperationVoid(
        webInteractionOperation: WebInteractionOperation<R>
    ): WebOperationResult<WebInteractionOperation<R>> {
        return UltronWebLifecycle.execute(
            executor = WebInteractionOperationExecutor(webInteractionOperation),
            resultHandler = resultHandler as (WebOperationResult<WebInteractionOperation<R>>) -> Unit
        )
    }

    fun <R> getUltronWebActionOperation(
        webInteractionBlock: () -> Web.WebInteraction<R>, name: String, type: UltronOperationType = CommonOperationType.DEFAULT, description: String
    ) = WebInteractionOperation(
        webInteractionBlock, name, type, description, getActionTimeout(), assertion, elementInfo
    )

    fun <R> getUltronWebAssertionOperation(
        webInteractionBlock: () -> Web.WebInteraction<R>, name: String, type: UltronOperationType = CommonOperationType.DEFAULT, description: String
    ) = WebInteractionOperation(
        webInteractionBlock, name, type, description, getAssertionTimeout(), assertion, elementInfo
    )

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




