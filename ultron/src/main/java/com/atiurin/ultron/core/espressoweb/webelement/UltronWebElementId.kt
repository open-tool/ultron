package com.atiurin.ultron.core.espressoweb.webelement

import android.view.View
import androidx.test.espresso.web.matcher.DomMatchers.elementById
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.model.WindowReference
import androidx.test.espresso.web.webdriver.Locator
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.operation.WebInteractionOperation
import com.atiurin.ultron.core.espressoweb.operation.WebOperationResult
import com.atiurin.ultron.custom.espresso.matcher.ElementWithAttributeMatcher.Companion.withAttribute
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.w3c.dom.Document

class UltronWebElementId(
    override val value: String,
    private val webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
    private val elementReference: ElementReference? = null,
    private val windowReference: WindowReference? = null,
    override val timeoutMs: Long? = null,
    override val resultHandler: (WebOperationResult<WebInteractionOperation<*>>) -> Unit = UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler
) : UltronWebElement(Locator.ID, value, webViewMatcher, elementReference, windowReference, timeoutMs, resultHandler) {
    override fun withTimeout(timeoutMs: Long): UltronWebElementId{
        return UltronWebElementId(this.value, this.webViewMatcher, this.elementReference, this.windowReference, timeoutMs, this.resultHandler)
    }

    override fun withResultHandler(resultHandler: (WebOperationResult<WebInteractionOperation<*>>) -> Unit): UltronWebElementId{
        return UltronWebElementId(this.value, this.webViewMatcher, this.elementReference, this.windowReference, this.timeoutMs, resultHandler)
    }

    fun hasAttribute(
        attributeName: String,
        attributeValueMatcher: Matcher<String>
    ) = apply {
        this.hasElementAttribute(
            attributeName, attributeValueMatcher, elementById(
                value, withAttribute(
                    attributeName,
                    attributeValueMatcher
                )
            )
        )
    }

    fun hasAttribute(
        attributeName: String,
        attributeValue: String
    ) = apply {
        val matcher = `is`(attributeValue)
        this.hasElementAttribute(
            attributeName, matcher, elementById(
                value, withAttribute(
                    attributeName,
                    matcher
                )
            )
        )
    }
}