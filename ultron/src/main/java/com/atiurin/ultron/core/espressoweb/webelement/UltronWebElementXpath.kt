package com.atiurin.ultron.core.espressoweb.webelement

import android.view.View
import androidx.test.espresso.web.matcher.DomMatchers.elementByXPath
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.model.WindowReference
import androidx.test.espresso.web.webdriver.Locator
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.operation.WebInteractionOperation
import com.atiurin.ultron.core.espressoweb.operation.WebOperationResult
import com.atiurin.ultron.custom.espresso.matcher.ElementWithAttributeMatcher.Companion.withAttribute
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matcher
import org.w3c.dom.Document

class UltronWebElementXpath(
    override val value: String,
    private val webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
    private val elementReference: ElementReference? = null,
    private val windowReference: WindowReference? = null,
    override val timeoutMs: Long? = null,
    override val resultHandler: (WebOperationResult<WebInteractionOperation<*>>) -> Unit = UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler
) : UltronWebElement(Locator.XPATH, value, webViewMatcher, elementReference, windowReference, timeoutMs, resultHandler) {
    override fun withTimeout(timeoutMs: Long): UltronWebElementXpath{
        return UltronWebElementXpath(this.value, this.webViewMatcher, this.elementReference, this.windowReference, timeoutMs, this.resultHandler)
    }

    override fun withResultHandler(resultHandler: (WebOperationResult<WebInteractionOperation<*>>) -> Unit): UltronWebElementXpath{
        return UltronWebElementXpath(this.value, this.webViewMatcher, this.elementReference, this.windowReference, this.timeoutMs, resultHandler)
    }

    fun hasAttribute(
        attributeName: String,
        attributeValueMatcher: Matcher<String>
    ) = apply {
        this.hasElementAttribute(
            attributeName, attributeValueMatcher, elementByXPath(
                value, withAttribute(
                    attributeName,
                    attributeValueMatcher
                )
            )
        )
    }

    fun hasAttribute(
        attributeName: String,
        attributeValue: String,
    ) = apply {
        val matcher = `is`(attributeValue)
        this.hasElementAttribute(
            attributeName, matcher, elementByXPath(
                value, withAttribute(
                    attributeName,
                    matcher
                )
            )
        )
    }
}