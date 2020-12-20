package com.atiurin.ultron.core.espressoweb

import android.view.View
import androidx.test.espresso.web.model.ElementReference
import androidx.test.espresso.web.model.WindowReference
import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.DriverAtoms.findMultipleElements
import androidx.test.espresso.web.webdriver.Locator
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.action.WebInteractionAction
import com.atiurin.ultron.core.espressoweb.action.WebInteractionActionExecutor
import com.atiurin.ultron.core.espressoweb.assertion.EspressoWebAssertionType
import com.atiurin.ultron.extensions.*
import org.hamcrest.Matcher

class `$$`(
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
        resultHandler: (WebOperationResult<WebInteractionAction<List<ElementReference>>>) -> Unit =
            (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<List<ElementReference>>>) -> Unit)
    ): List<`$`> {
        return findMultipleElements(locator, matcher, timeoutMs, resultHandler).map {
            `$`(
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
        resultHandler: (WebOperationResult<WebInteractionAction<List<ElementReference>>>) -> Unit =
            (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<List<ElementReference>>>) -> Unit)
    ): List<ElementReference> {
        val result = WebLifecycle.execute(
            WebInteractionActionExecutor(
                WebInteractionAction(
                    webInteractionBlock = {
                        webViewInteraction.withNoTimeout()
                            .perform(DriverAtoms.findMultipleElements(locator, matcher))
                    },
                    name = "EspressoWeb findElement",
                    type = EspressoWebAssertionType.CONTAINS_TEXT,
                    description = "WebAssertion getText during $timeoutMs ms",
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
        ): `$$` {
            return `$$`(Locator.CLASS_NAME, value)
        }

        fun cssSelectors(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher
        ): `$$` {
            return `$$`(Locator.CSS_SELECTOR, value)
        }

        fun ids(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher
        ): `$$` {
            return `$$`(Locator.ID, value)
        }

        fun linkTexts(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher
        ): `$$` {
            return `$$`(Locator.LINK_TEXT, value)
        }

        fun names(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher
        ): `$$` {
            return `$$`(Locator.NAME, value)
        }

        fun partialLinkTexts(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher
        ): `$$` {
            return `$$`(Locator.PARTIAL_LINK_TEXT, value)
        }

        fun tagNames(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher
        ): `$$` {
            return `$$`(Locator.TAG_NAME, value)
        }

        fun xpaths(
            value: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher
        ): `$$` {
            return `$$`(Locator.XPATH, value)
        }

        fun elements(
            locator: Locator,
            matcher: String,
            webViewMatcher: Matcher<View> = UltronConfig.Espresso.webViewMatcher
        ): `$$` {
            return `$$`(locator, matcher, webViewMatcher)
        }
    }
}