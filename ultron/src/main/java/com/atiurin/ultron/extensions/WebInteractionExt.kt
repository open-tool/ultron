package com.atiurin.ultron.extensions

import androidx.test.espresso.web.assertion.WebViewAssertions
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.model.Atoms.script
import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.webdriver.DriverAtoms
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.action.EspressoActionType
import com.atiurin.ultron.core.espresso.action.ViewInteractionEspressoAction
import com.atiurin.ultron.core.espressoweb.WebLifecycle
import com.atiurin.ultron.core.espressoweb.WebOperationResult
import com.atiurin.ultron.core.espressoweb.action.WebInteractionAction
import com.atiurin.ultron.core.espressoweb.action.WebInteractionActionExecutor
import com.atiurin.ultron.core.espressoweb.assertion.WebInteractionAssertion
import com.atiurin.ultron.core.espressoweb.assertion.WebInteractionAssertionExecutor
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.containsString

fun <T> Web.WebInteraction<T>.webClick() {
    WebLifecycle.execute(
        WebInteractionActionExecutor(
            WebInteractionAction(
                webInteraction = this,
                atom = DriverAtoms.webClick(),
                name = "DriverAtoms webClick()",
                type = EspressoActionType.CLICK,
                description = "",
                timeoutMs = 0L
            )
        )
    )
}

fun <T> Web.WebInteraction<T>.webKeys(text: String) {
    WebLifecycle.execute(
        WebInteractionActionExecutor(
            WebInteractionAction(
                webInteraction = this,
                atom = DriverAtoms.webKeys(text),
                name = "DriverAtoms webKeys with text '$text'",
                type = EspressoActionType.CLICK,
                description = "",
                timeoutMs = 0L
            )
        )
    )
}

fun <T> Web.WebInteraction<T>.evalJS(script: String) {
    WebLifecycle.execute(
        WebInteractionActionExecutor(
            WebInteractionAction(
                webInteraction = this,
                atom = script(script),
                name = "Evaluate JS",
                type = EspressoActionType.CLICK,
                description = "",
                timeoutMs = 0L
            )
        )
    )
}

@JvmOverloads
fun <T> Web.WebInteraction<T>.containsText(
    text: String,
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAssertion<T, String>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionAssertionConfig.resultHandler as (WebOperationResult<WebInteractionAssertion<T, String>>) -> Unit)
) {
    WebLifecycle.execute(
        WebInteractionAssertionExecutor(
            WebInteractionAssertion(
                webInteraction = this,
                webAssertion = webMatches(DriverAtoms.getText(), containsString(text)),
                name = "DriverAtoms containsText '$text' WebAssertion ${this.getViewMatcher()} ",
                type = EspressoActionType.CLICK,
                description = "WebAssertion  containsText $text",
                timeoutMs = timeoutMs
            )
        ),resultHandler
    )
}