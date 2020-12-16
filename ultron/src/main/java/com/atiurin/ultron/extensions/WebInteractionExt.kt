package com.atiurin.ultron.extensions

import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.model.*
import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.Locator
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espressoweb.WebInteractionOperationIterationResult
import com.atiurin.ultron.core.espressoweb.WebLifecycle
import com.atiurin.ultron.core.espressoweb.WebOperationResult
import com.atiurin.ultron.core.espressoweb.action.*
import com.atiurin.ultron.core.espressoweb.assertion.EspressoWebAssertionType
import com.atiurin.ultron.core.espressoweb.assertion.WebInteractionAssertion
import com.atiurin.ultron.core.espressoweb.assertion.WebInteractionAssertionExecutor
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.containsString

fun <T> (() -> Web.WebInteraction<T>).webClick(
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, Evaluation>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, Evaluation>>) -> Unit)
) {
    WebLifecycle.execute(
        WebInteractionActionExecutor(
            WebInteractionAction(
                webInteraction = this,
                atomBlock = { DriverAtoms.webClick() },
                name = "EspressoWeb webClick",
                type = EspressoWebActionType.WEB_CLICK,
                description = "DriverAtoms webClick with timeout $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun <T> Web.WebInteraction<T>.webClick(
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, Evaluation>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, Evaluation>>) -> Unit)
) {
    { this }.webClick(timeoutMs, resultHandler)
}

fun <T> (() -> Web.WebInteraction<T>).clearElement(
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, Evaluation>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, Evaluation>>) -> Unit)
) {
    WebLifecycle.execute(
        WebInteractionActionExecutor(
            WebInteractionAction(
                webInteraction = this,
                atomBlock = { DriverAtoms.clearElement() },
                name = "EspressoWeb clearElement",
                type = EspressoWebActionType.CLEAR_ELEMENT,
                description = "DriverAtom clearElement with timeout $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun <T> Web.WebInteraction<T>.clearElement(
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, Evaluation>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, Evaluation>>) -> Unit)
) {
    { this }.clearElement(timeoutMs, resultHandler)
}

fun <T> (() -> Web.WebInteraction<T>).webKeys(
    text: String,
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, Evaluation>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, Evaluation>>) -> Unit)
) {
    WebLifecycle.execute(
        WebInteractionActionExecutor(
            WebInteractionAction(
                webInteraction = this,
                atomBlock = { DriverAtoms.webKeys(text) },
                name = "EspressoWeb webKeys with text '$text'",
                type = EspressoWebActionType.WEB_KEYS,
                description = "DriverAtoms webKeys with text '$text' and timeout $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun <T> Web.WebInteraction<T>.webKeys(
    text: String,
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, Evaluation>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, Evaluation>>) -> Unit)
) {
    { this }.webKeys(text, timeoutMs, resultHandler)
}

fun <T> (() -> Web.WebInteraction<T>).selectActiveElement(
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, ElementReference>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, ElementReference>>) -> Unit)
): Atom<ElementReference> {
    lateinit var element: Atom<ElementReference>
    WebLifecycle.execute(
        WebInteractionActionExecutor(
            WebInteractionAction(
                webInteraction = this,
                atomBlock = {
                    element = DriverAtoms.selectActiveElement()
                    element
                },
                name = "EspressoWeb selectActiveElement",
                type = EspressoWebActionType.WEB_KEYS,
                description = "DriverAtoms selectActiveElement timeout $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return element
}

fun <T> Web.WebInteraction<T>.selectActiveElement(
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, ElementReference>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, ElementReference>>) -> Unit)
): Atom<ElementReference> {
    return { this }.selectActiveElement(timeoutMs, resultHandler)
}

 fun <T> (() -> Web.WebInteraction<T>).selectFrameByIndex(
    index: Int,
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, WindowReference>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, WindowReference>>) -> Unit)
): Atom<WindowReference> {
    lateinit var frame: Atom<WindowReference>
    WebLifecycle.execute(
        WebInteractionActionExecutor(
            WebInteractionAction(
                webInteraction = this,
                atomBlock = {
                    frame = DriverAtoms.selectFrameByIndex(index)
                    frame
                },
                name = "EspressoWeb selectFrameByIndex",
                type = EspressoWebActionType.WEB_KEYS,
                description = "DriverAtoms selectFrameByIndex '$index' timeout $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return frame
}

 fun <T> Web.WebInteraction<T>.selectFrameByIndex(
    index: Int,
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, WindowReference>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, WindowReference>>) -> Unit)
): Atom<WindowReference> {
    return { this }.selectFrameByIndex(index, timeoutMs, resultHandler)
}

 fun <T> (() -> Web.WebInteraction<T>).selectFrameByIndex(
    index: Int,
    root: WindowReference,
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, WindowReference>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, WindowReference>>) -> Unit)
): Atom<WindowReference> {
    lateinit var frame: Atom<WindowReference>
    WebLifecycle.execute(
        WebInteractionActionExecutor(
            WebInteractionAction(
                webInteraction = this,
                atomBlock = {
                    frame = DriverAtoms.selectFrameByIndex(index, root)
                    frame
                },
                name = "EspressoWeb selectFrameByIndex with root",
                type = EspressoWebActionType.WEB_KEYS,
                description = "DriverAtoms selectFrameByIndex '$index' with root '$root' timeout $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return frame
}

 fun <T> Web.WebInteraction<T>.selectFrameByIndex(
    index: Int,
    root: WindowReference,
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, WindowReference>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, WindowReference>>) -> Unit)
): Atom<WindowReference> {
    return { this }.selectFrameByIndex(index, root, timeoutMs, resultHandler)
}

  fun <T> (() -> Web.WebInteraction<T>).selectFrameByIdOrName(
    idOrName: String,
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, WindowReference>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, WindowReference>>) -> Unit)
): Atom<WindowReference> {
    lateinit var frame: Atom<WindowReference>
    WebLifecycle.execute(
        WebInteractionActionExecutor(
            WebInteractionAction(
                webInteraction = this,
                atomBlock = {
                    frame = DriverAtoms.selectFrameByIdOrName(idOrName)
                    frame
                },
                name = "EspressoWeb selectFrameByIdOrName '$idOrName'",
                type = EspressoWebActionType.WEB_KEYS,
                description = "DriverAtoms selectFrameByIdOrName '$idOrName' timeout $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return frame
}

fun <T> Web.WebInteraction<T>.selectFrameByIdOrName(
    idOrName: String,
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, WindowReference>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, WindowReference>>) -> Unit)
): Atom<WindowReference> {

    return { this }.selectFrameByIdOrName(idOrName, timeoutMs, resultHandler)
}

 fun <T> (() -> Web.WebInteraction<T>).selectFrameByIdOrName(
    idOrName: String,
    root: WindowReference,
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, WindowReference>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, WindowReference>>) -> Unit)
): WindowReference? {
    val result = WebLifecycle.execute(
        WebInteractionActionExecutor(
            WebInteractionAction(
                webInteraction = this,
                atomBlock = {
                    DriverAtoms.selectFrameByIdOrName(idOrName, root)
                },
                name = "EspressoWeb selectFrameByIdOrName with root",
                type = EspressoWebActionType.WEB_KEYS,
                description = "DriverAtoms selectFrameByIdOrName '$idOrName' with root '$root' timeout $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return (result.operationIterationResult as WebInteractionOperationIterationResult<WindowReference>).webInteraction?.get()
}

fun <T> Web.WebInteraction<T>.selectFrameByIdOrName(
    idOrName: String,
    root: WindowReference,
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, WindowReference>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, WindowReference>>) -> Unit)
): WindowReference? {
    return { this }.selectFrameByIdOrName(idOrName, root, timeoutMs, resultHandler)
}

fun <T> (() -> Web.WebInteraction<T>).webScrollIntoView(
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, Boolean>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, Boolean>>) -> Unit)
): Boolean? {
    val result = WebLifecycle.execute(
        WebInteractionActionExecutor(
            WebInteractionAction(
                webInteraction = this,
                atomBlock = {
                    DriverAtoms.webScrollIntoView()
                },
                name = "EspressoWeb webScrollIntoView",
                type = EspressoWebActionType.WEB_KEYS,
                description = "DriverAtoms webScrollIntoView timeout $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return (result.operationIterationResult as WebInteractionOperationIterationResult<Boolean>).webInteraction?.get()
}

fun <T> Web.WebInteraction<T>.webScrollIntoView(
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, Boolean>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, Boolean>>) -> Unit)
): Boolean? {
    return { this }.webScrollIntoView(timeoutMs, resultHandler)
}

fun <T> (() -> Web.WebInteraction<T>).script(
    script: String,
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, Evaluation>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, Evaluation>>) -> Unit)
) {
    WebLifecycle.execute(
        WebInteractionActionExecutor(
            WebInteractionAction(
                webInteraction = this,
                atomBlock = { Atoms.script(script) },
                name = "EspressoWeb Evaluate JS script",
                type = EspressoWebActionType.SCRIPT,
                description = "Evaluate JS '$script' during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun <T> Web.WebInteraction<T>.script(
    script: String,
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, Evaluation>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, Evaluation>>) -> Unit)
) {
    { this }.script(script, timeoutMs, resultHandler)
}

fun <T> (() -> Web.WebInteraction<T>).getText(
    timeoutMs: Long = UltronConfig.Espresso.ASSERTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, String>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, String>>) -> Unit)
): String? {
    val result = WebLifecycle.execute(
        WebInteractionActionExecutor(
            WebInteractionAction(
                webInteraction = this,
                atomBlock = {
                    DriverAtoms.getText()
                },
                name = "EspressoWeb getText",
                type = EspressoWebAssertionType.CONTAINS_TEXT,
                description = "WebAssertion getText during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return (result.operationIterationResult as WebInteractionOperationIterationResult<String>).webInteraction?.get()
}

fun <T> Web.WebInteraction<T>.getText(
    timeoutMs: Long = UltronConfig.Espresso.ASSERTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, String>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, String>>) -> Unit)
): String? {
    return { this }.getText(timeoutMs, resultHandler)
}

fun <T> (() -> Web.WebInteraction<T>).findMultipleElements(
    locator: Locator,
    matcher: String,
    timeoutMs: Long = UltronConfig.Espresso.ACTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAction<T, List<ElementReference>>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionActionConfig.resultHandler as (WebOperationResult<WebInteractionAction<T, List<ElementReference>>>) -> Unit)
): List<ElementReference>? {
    val result = WebLifecycle.execute(
        WebInteractionActionExecutor(
            WebInteractionAction(
                webInteraction = this,
                atomBlock = {
                    DriverAtoms.findMultipleElements(locator, matcher)
                },
                name = "EspressoWeb findElement",
                type = EspressoWebAssertionType.CONTAINS_TEXT,
                description = "WebAssertion getText during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
    return (result.operationIterationResult as WebInteractionOperationIterationResult<List<ElementReference>>).webInteraction?.get()
}

fun <T> (() -> Web.WebInteraction<T>).containsText(
    text: String,
    timeoutMs: Long = UltronConfig.Espresso.ASSERTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAssertion<T, String>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionAssertionConfig.resultHandler as (WebOperationResult<WebInteractionAssertion<T, String>>) -> Unit)
) {
    WebLifecycle.execute(
        WebInteractionAssertionExecutor(
            WebInteractionAssertion(
                webInteraction = this,
                webAssertion = webMatches(DriverAtoms.getText(), containsString(text)),
                name = "EspressoWeb containsText '$text' ",
                type = EspressoWebAssertionType.CONTAINS_TEXT,
                description = "WebAssertion containsText '$text' during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun <T> Web.WebInteraction<T>.containsText(
    text: String,
    timeoutMs: Long = UltronConfig.Espresso.ASSERTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAssertion<T, String>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionAssertionConfig.resultHandler as (WebOperationResult<WebInteractionAssertion<T, String>>) -> Unit)
) {
    { this }.containsText(text, timeoutMs, resultHandler)
}

fun <T> (() -> Web.WebInteraction<T>).hasText(
    text: String,
    timeoutMs: Long = UltronConfig.Espresso.ASSERTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAssertion<T, String>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionAssertionConfig.resultHandler as (WebOperationResult<WebInteractionAssertion<T, String>>) -> Unit)
) {
    WebLifecycle.execute(
        WebInteractionAssertionExecutor(
            WebInteractionAssertion(
                webInteraction = this,
                webAssertion = webMatches(DriverAtoms.getText(), `is`(text)),
                name = "EspressoWeb hasText '$text'",
                type = EspressoWebAssertionType.HAS_TEXT,
                description = "WebAssertion hasText '$text' during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun <T> Web.WebInteraction<T>.hasText(
    text: String,
    timeoutMs: Long = UltronConfig.Espresso.ASSERTION_TIMEOUT,
    resultHandler: (WebOperationResult<WebInteractionAssertion<T, String>>) -> Unit =
        (UltronConfig.Espresso.WebInteractionAssertionConfig.resultHandler as (WebOperationResult<WebInteractionAssertion<T, String>>) -> Unit)
) {
    { this }.hasText(text, timeoutMs, resultHandler)
}
