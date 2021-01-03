package com.atiurin.ultron.core.espressoweb.webelement

import android.view.View
import androidx.test.espresso.web.matcher.DomMatchers
import androidx.test.espresso.web.matcher.DomMatchers.elementByXPath
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.model.WindowReference
import androidx.test.espresso.web.webdriver.Locator
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.operation.WebInteractionOperation
import com.atiurin.ultron.core.espressoweb.operation.WebOperationResult
import com.atiurin.ultron.custom.espresso.matcher.ElementWithAttributeMatcher
import com.atiurin.ultron.custom.espresso.matcher.ElementWithAttributeMatcher.Companion.withAttribute
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.w3c.dom.Document

class WebElementWithXpath(
    override val value: String,
    webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
    elementReference: ElementReference? = null,
    windowReference: WindowReference? = null
) : WebElement(Locator.XPATH, value, webViewMatcher, elementReference, windowReference) {
    fun hasAttribute(
        attributeName: String,
        attributeValueMatcher: Matcher<String>,
        timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
        resultHandler: (WebOperationResult<WebInteractionOperation<Document>>) -> Unit =
            UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<Document>>) -> Unit
    ) = apply {
        this.hasElementAttribute(
            attributeName, attributeValueMatcher, timeoutMs, resultHandler, elementByXPath(
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
        timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
        resultHandler: (WebOperationResult<WebInteractionOperation<Document>>) -> Unit =
            UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler as (WebOperationResult<WebInteractionOperation<Document>>) -> Unit
    ) = apply {
        val matcher = `is`(attributeValue)
        this.hasElementAttribute(
            attributeName, matcher, timeoutMs, resultHandler, elementByXPath(
                value, withAttribute(
                    attributeName,
                    matcher
                )
            )
        )
    }
}