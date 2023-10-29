package com.atiurin.ultron.core.espressoweb.webelement

import android.view.View
import androidx.test.espresso.web.matcher.DomMatchers.elementById
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.model.WindowReference
import androidx.test.espresso.web.webdriver.Locator
import com.atiurin.ultron.core.common.ElementInfo
import com.atiurin.ultron.core.common.DefaultElementInfo
import com.atiurin.ultron.core.common.assertion.EmptyOperationAssertion
import com.atiurin.ultron.core.common.assertion.OperationAssertion
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.operation.WebInteractionOperation
import com.atiurin.ultron.core.espressoweb.operation.WebOperationResult
import com.atiurin.ultron.custom.espresso.matcher.ElementWithAttributeMatcher.Companion.withAttribute
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`

class UltronWebElementId(
    override val value: String,
    private val webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher,
    private val elementReference: ElementReference? = null,
    private val windowReference: WindowReference? = null,
    override val timeoutMs: Long? = null,
    override val resultHandler: (WebOperationResult<WebInteractionOperation<*>>) -> Unit = UltronConfig.Espresso.WebInteractionOperationConfig.resultHandler,
    override val assertion: OperationAssertion = EmptyOperationAssertion(),
    override val elementInfo: ElementInfo = DefaultElementInfo()
) : UltronWebElement(
    Locator.ID, value, webViewMatcher, elementReference, windowReference, timeoutMs, resultHandler, assertion, elementInfo
) {
    init {
        if (this.elementInfo.name.isEmpty()) {
            this.elementInfo.name = "WebElement(locator = $locator, value = '$value')"
        }
    }
    override fun withTimeout(timeoutMs: Long) = UltronWebElementId(
        this.value, this.webViewMatcher, this.elementReference, this.windowReference, timeoutMs, this.resultHandler, this.assertion, this.elementInfo
    )

    override fun withResultHandler(resultHandler: (WebOperationResult<WebInteractionOperation<*>>) -> Unit) = UltronWebElementId(
        this.value, this.webViewMatcher, this.elementReference, this.windowReference, this.timeoutMs, resultHandler, this.assertion, this.elementInfo
    )

    override fun withAssertion(assertion: OperationAssertion) = UltronWebElementId(
        this.value, this.webViewMatcher, this.elementReference, this.windowReference, this.timeoutMs, this.resultHandler, assertion, this.elementInfo
    )

    override fun withName(name: String) = apply { elementInfo.name = name }

    override fun withMetaInfo(meta: Any) = apply { elementInfo.meta = meta }

    fun hasAttribute(
        attributeName: String, attributeValueMatcher: Matcher<String>
    ) = apply {
        this.hasElementAttribute(
            attributeName, attributeValueMatcher, elementById(
                value, withAttribute(
                    attributeName, attributeValueMatcher
                )
            )
        )
    }

    fun hasAttribute(
        attributeName: String, attributeValue: String
    ) = apply {
        val matcher = `is`(attributeValue)
        this.hasElementAttribute(
            attributeName, matcher, elementById(
                value, withAttribute(
                    attributeName, matcher
                )
            )
        )
    }
}