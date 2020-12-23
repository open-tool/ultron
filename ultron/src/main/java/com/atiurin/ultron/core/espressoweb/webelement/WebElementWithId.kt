package com.atiurin.ultron.core.espressoweb.webelement

import android.view.View
import androidx.test.espresso.web.assertion.WebViewAssertions
import androidx.test.espresso.web.assertion.WebViewAssertions.webContent
import androidx.test.espresso.web.matcher.DomMatchers
import androidx.test.espresso.web.matcher.DomMatchers.elementById
import androidx.test.espresso.web.model.Atoms
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.model.WindowReference
import androidx.test.espresso.web.webdriver.Locator
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.DocumentParserAtom
import com.atiurin.ultron.core.espressoweb.WebLifecycle
import com.atiurin.ultron.core.espressoweb.WebOperationResult
import com.atiurin.ultron.core.espressoweb.assertion.EspressoWebAssertionType
import com.atiurin.ultron.core.espressoweb.assertion.WebInteractionAssertion
import com.atiurin.ultron.core.espressoweb.assertion.WebInteractionAssertionExecutor
import com.atiurin.ultron.custom.espresso.matcher.ElementWithAttributeMatcher
import com.atiurin.ultron.custom.espresso.matcher.ElementWithAttributeMatcher.Companion.withAttribute
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.w3c.dom.Document

class WebElementWithId(
    override val value: String,
    override val webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
    override val elementReference: ElementReference? = null,
    override val windowReference: WindowReference? = null
) : WebElement(Locator.ID, value, webViewMatcher, elementReference, windowReference) {
    fun hasAttribute(
        attributeName: String,
        attributeValueMatcher: Matcher<String>,
        timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
        resultHandler: (WebOperationResult<WebInteractionAssertion<Document>>) -> Unit =
            UltronConfig.Espresso.WebInteractionAssertionConfig.resultHandler as (WebOperationResult<WebInteractionAssertion<Document>>) -> Unit
    ) = apply {
        WebLifecycle.execute(
            WebInteractionAssertionExecutor(
                WebInteractionAssertion(
                    assertionBlock = {
                        webViewInteraction.check(
                            webContent(
                                elementById(
                                    value, withAttribute(
                                        attributeName,
                                        attributeValueMatcher
                                    )
                                )
                            )
                        )
                    },
                    name = "Assert element with ${locator.type}, value = '$value' has attribute '$attributeName' matches $attributeValueMatcher",
                    type = EspressoWebAssertionType.CONTAINS_TEXT,
                    description = "Assert element has attribute '$attributeName' matches $attributeValueMatcher during $timeoutMs ms",
                    timeoutMs = timeoutMs
                )
            ), resultHandler
        )
    }

}