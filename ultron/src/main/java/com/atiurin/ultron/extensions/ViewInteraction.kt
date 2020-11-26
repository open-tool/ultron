package com.atiurin.ultron.extensions

import android.view.View
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.EspressoKey
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.ACTION_TIMEOUT
import com.atiurin.ultron.core.config.UltronConfig.Espresso.Companion.ASSERTION_TIMEOUT
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.action.*
import com.atiurin.ultron.core.espresso.assertion.*
import com.atiurin.ultron.core.config.UltronConfig.Espresso.ViewActionConfig
import com.atiurin.ultron.core.config.UltronConfig.Espresso.ViewAssertionConfig
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*

fun ViewInteraction.isSuccess(
    action: ViewInteraction.() -> Unit
): Boolean {
    var success = true
    try {
        action()
    } catch (th: Throwable) {
        success = false
    }
    return success
}

fun ViewInteraction.click(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.resultHandler
) = apply {
    ViewActionLifecycle.execute(
        ViewInteractionActionExecutor(
            ViewInteractionEspressoAction(
                viewInteraction = this,
                viewAction = ViewActions.click(),
                name = "Click to ${this.getViewMatcher()}",
                type = EspressoActionType.CLICK,
                description = "ViewInteraction action with type '${EspressoActionType.CLICK}'. Click to ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.doubleClick(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.resultHandler
) = apply {
    ViewActionLifecycle.execute(
        ViewInteractionActionExecutor(
            ViewInteractionEspressoAction(
                viewInteraction = this,
                viewAction = ViewActions.doubleClick(),
                name = "DoubleClick to ${this.getViewMatcher()}",
                type = EspressoActionType.CLICK,
                description = "ViewInteraction action with type '${EspressoActionType.DOUBLE_CLICK}'. Click to ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.longClick(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.resultHandler
) = apply {
    ViewActionLifecycle.execute(
        ViewInteractionActionExecutor(
            ViewInteractionEspressoAction(
                viewInteraction = this,
                viewAction = ViewActions.longClick(),
                name = "LongClick to ${this.getViewMatcher()}",
                type = EspressoActionType.LONG_CLICK,
                description = "ViewInteraction action with type '${EspressoActionType.LONG_CLICK}'. LongClick to ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.typeText(
    text: String,
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.resultHandler
) = apply {
    ViewActionLifecycle.execute(
        ViewInteractionActionExecutor(
            ViewInteractionEspressoAction(
                viewInteraction = this,
                viewAction = ViewActions.typeText(text),
                name = "Type text '$text' to ${this.getViewMatcher()}",
                type = EspressoActionType.TYPE_TEXT,
                description = "ViewInteraction action with type '${EspressoActionType.TYPE_TEXT}'. Type text '$text' to ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.replaceText(
    text: String,
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.resultHandler
) = apply {
    ViewActionLifecycle.execute(
        ViewInteractionActionExecutor(
            ViewInteractionEspressoAction(
                viewInteraction = this,
                viewAction = ViewActions.replaceText(text),
                name = "Replace text '$text' to ${this.getViewMatcher()}",
                type = EspressoActionType.REPLACE_TEXT,
                description = "ViewInteraction action with type '${EspressoActionType.REPLACE_TEXT}'. Replace text '$text' to ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.clearText(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.resultHandler
) = apply {
    ViewActionLifecycle.execute(
        ViewInteractionActionExecutor(
            ViewInteractionEspressoAction(
                viewInteraction = this,
                viewAction = ViewActions.clearText(),
                name = "Clear text in ${this.getViewMatcher()}",
                type = EspressoActionType.CLEAR_TEXT,
                description = "ViewInteraction action with type '${EspressoActionType.CLEAR_TEXT}'. Clear text in ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.pressKey(
    keyCode: Int,
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.resultHandler
) = apply {
    ViewActionLifecycle.execute(
        ViewInteractionActionExecutor(
            ViewInteractionEspressoAction(
                viewInteraction = this,
                viewAction = ViewActions.pressKey(keyCode),
                name = "PressKey code '$keyCode' with ${this.getViewMatcher()}",
                type = EspressoActionType.PRESS_KEY,
                description = "ViewInteraction action with type '${EspressoActionType.PRESS_KEY}'. PressKey code '$keyCode' with ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.pressKey(
    key: EspressoKey,
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.resultHandler
) = apply {
    ViewActionLifecycle.execute(
        ViewInteractionActionExecutor(
            ViewInteractionEspressoAction(
                viewInteraction = this,
                viewAction = ViewActions.pressKey(key),
                name = "Press EspressoKey '$key' with ${this.getViewMatcher()}",
                type = EspressoActionType.PRESS_KEY,
                description = "ViewInteraction action with type '${EspressoActionType.PRESS_KEY}'. Press EspressoKey '$key' with ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.closeSoftKeyboard(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.resultHandler
) = apply {
    ViewActionLifecycle.execute(
        ViewInteractionActionExecutor(
            ViewInteractionEspressoAction(
                viewInteraction = this,
                viewAction = ViewActions.closeSoftKeyboard(),
                name = "CloseSoftKeyboard with ${this.getViewMatcher()}",
                type = EspressoActionType.CLOSE_SOFT_KEYBOARD,
                description = "ViewInteraction action with type '${EspressoActionType.CLOSE_SOFT_KEYBOARD}'. CloseSoftKeyboard with ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.swipeLeft(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.resultHandler
) = apply {
    ViewActionLifecycle.execute(
        ViewInteractionActionExecutor(
            ViewInteractionEspressoAction(
                viewInteraction = this,
                viewAction = ViewActions.swipeLeft(),
                name = "SwipeLeft with ${this.getViewMatcher()}",
                type = EspressoActionType.SWIPE_LEFT,
                description = "ViewInteraction action with type '${EspressoActionType.SWIPE_LEFT}'. SwipeLeft with ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.swipeRight(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.resultHandler
) = apply {
    ViewActionLifecycle.execute(
        ViewInteractionActionExecutor(
            ViewInteractionEspressoAction(
                viewInteraction = this,
                viewAction = ViewActions.swipeRight(),
                name = "SwipeRight with ${this.getViewMatcher()}",
                type = EspressoActionType.SWIPE_RIGHT,
                description = "ViewInteraction action with type '${EspressoActionType.SWIPE_RIGHT}'. SwipeRight with ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.swipeUp(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.resultHandler
) = apply {
    ViewActionLifecycle.execute(
        ViewInteractionActionExecutor(
            ViewInteractionEspressoAction(
                viewInteraction = this,
                viewAction = ViewActions.swipeUp(),
                name = "SwipeUp with ${this.getViewMatcher()}",
                type = EspressoActionType.SWIPE_UP,
                description = "ViewInteraction action with type '${EspressoActionType.SWIPE_UP}'. SwipeUp with ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.swipeDown(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.resultHandler
) = apply {
    ViewActionLifecycle.execute(
        ViewInteractionActionExecutor(
            ViewInteractionEspressoAction(
                viewInteraction = this,
                viewAction = ViewActions.swipeDown(),
                name = "SwipeDown with ${this.getViewMatcher()}",
                type = EspressoActionType.SWIPE_DOWN,
                description = "ViewInteraction action with type '${EspressoActionType.SWIPE_DOWN}'. SwipeDown with ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.scrollTo(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.resultHandler
) = apply {
    ViewActionLifecycle.execute(
        ViewInteractionActionExecutor(
            ViewInteractionEspressoAction(
                viewInteraction = this,
                viewAction = ViewActions.scrollTo(),
                name = "ScrollTo with ${this.getViewMatcher()}",
                type = EspressoActionType.SCROLL,
                description = "ViewInteraction action with type '${EspressoActionType.SCROLL}'. ScrollTo with ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.execute(
    viewAction: ViewAction,
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.resultHandler
) {
    ViewActionLifecycle.execute(
        ViewInteractionActionExecutor(
            ViewInteractionEspressoAction(
                viewInteraction = this,
                viewAction = viewAction,
                name = "Custom action to ${this.getViewMatcher()}",
                type = EspressoActionType.CUSTOM,
                description = "ViewInteraction action with type '${EspressoActionType.CUSTOM}'. Custom action '${viewAction.description}' to ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

//assertions
fun ViewInteraction.isDisplayed(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = ViewMatchers.isDisplayed(),
                name = "IsDisplayed of ${this.getViewMatcher()}",
                type = EspressoAssertionType.IS_DISPLAYED,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.IS_DISPLAYED}'. IsDisplayed of ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.isNotDisplayed(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = not(ViewMatchers.isDisplayed()),
                name = "IsNotDisplayed of ${this.getViewMatcher()}",
                type = EspressoAssertionType.IS_NOT_DISPLAYED,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.IS_NOT_DISPLAYED}'. IsNotDisplayed of ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.isCompletelyDisplayed(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = ViewMatchers.isCompletelyDisplayed(),
                name = "IsCompletelyDisplayed of ${this.getViewMatcher()}",
                type = EspressoAssertionType.IS_COMPLETELY_DISPLAYED,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.IS_COMPLETELY_DISPLAYED}'. IsCompletelyDisplayed of ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.isDisplayingAtLeast(
    percentage: Int,
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = ViewMatchers.isDisplayingAtLeast(percentage),
                name = "IsDisplayingAtLeast '$percentage'% of ${this.getViewMatcher()}",
                type = EspressoAssertionType.IS_DISPLAYING_AT_LEAST,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.IS_DISPLAYING_AT_LEAST}'. IsDisplayingAtLeast '$percentage'% of ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.isEnabled(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = ViewMatchers.isEnabled(),
                name = "IsEnabled of ${this.getViewMatcher()}",
                type = EspressoAssertionType.IS_ENABLED,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.IS_ENABLED}'. IsEnabled of ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.isNotEnabled(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = not(ViewMatchers.isEnabled()),
                name = "IsNotEnabled of ${this.getViewMatcher()}",
                type = EspressoAssertionType.IS_NOT_ENABLED,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.IS_NOT_ENABLED}'. IsNotEnabled of ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.isSelected(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = ViewMatchers.isSelected(),
                name = "IsSelected of ${this.getViewMatcher()}",
                type = EspressoAssertionType.IS_SELECTED,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.IS_SELECTED}'. IsSelected of ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.isNotSelected(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = not(ViewMatchers.isSelected()),
                name = "IsNotSelected of ${this.getViewMatcher()}",
                type = EspressoAssertionType.IS_NOT_SELECTED,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.IS_NOT_SELECTED}'. IsNotSelected of ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.isClickable(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = ViewMatchers.isClickable(),
                name = "IsClickable of ${this.getViewMatcher()}",
                type = EspressoAssertionType.IS_CLICKABLE,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.IS_CLICKABLE}'. IsClickable of ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.isNotClickable(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = not(ViewMatchers.isClickable()),
                name = "IsNotClickable of ${this.getViewMatcher()}",
                type = EspressoAssertionType.IS_NOT_CLICKABLE,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.IS_NOT_CLICKABLE}'. IsNotClickable of ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.isChecked(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = ViewMatchers.isChecked(),
                name = "IsChecked of ${this.getViewMatcher()}",
                type = EspressoAssertionType.IS_CHECKED,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.IS_CHECKED}'. IsChecked of ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.isNotChecked(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = ViewMatchers.isNotChecked(),
                name = "IsNotChecked of ${this.getViewMatcher()}",
                type = EspressoAssertionType.IS_NOT_CHECKED,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.IS_NOT_CHECKED}'. IsNotChecked of ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.isFocusable(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = ViewMatchers.isFocusable(),
                name = "IsFocusable of ${this.getViewMatcher()}",
                type = EspressoAssertionType.IS_FOCUSABLE,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.IS_FOCUSABLE}'. IsFocusable of ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.isNotFocusable(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = not(ViewMatchers.isFocusable()),
                name = "IsNotFocusable of ${this.getViewMatcher()}",
                type = EspressoAssertionType.IS_NOT_FOCUSABLE,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.IS_NOT_FOCUSABLE}'. IsNotFocusable of ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.hasFocus(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = ViewMatchers.hasFocus(),
                name = "HasFocus of ${this.getViewMatcher()}",
                type = EspressoAssertionType.HAS_FOCUS,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.HAS_FOCUS}'. HasFocus of ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.isJavascriptEnabled(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = ViewMatchers.isJavascriptEnabled(),
                name = "IsJavascriptEnabled of ${this.getViewMatcher()}",
                type = EspressoAssertionType.IS_JS_ENABLED,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.IS_JS_ENABLED}'. IsJavascriptEnabled of ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.hasText(
    text: String,
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = ViewMatchers.withText(text),
                name = "HasText '$text' in ${this.getViewMatcher()}",
                type = EspressoAssertionType.HAS_TEXT,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.HAS_TEXT}'. HasText '$text' in ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.hasText(
    resourceId: Int,
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = ViewMatchers.withText(resourceId),
                name = "HasText with resourceId '$resourceId' in ${this.getViewMatcher()}",
                type = EspressoAssertionType.HAS_TEXT,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.HAS_TEXT}'. HasText with resourceId '$resourceId' in ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}


fun ViewInteraction.hasText(
    stringMatcher: Matcher<String>,
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = ViewMatchers.withText(stringMatcher),
                name = "HasText with matcher '$stringMatcher' in ${this.getViewMatcher()}",
                type = EspressoAssertionType.HAS_TEXT,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.HAS_TEXT}'. HasText with matcher '$stringMatcher' in ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.containsText(
    text: String,
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = ViewMatchers.withText(containsString(text)),
                name = "ContainsText '$text' in ${this.getViewMatcher()}",
                type = EspressoAssertionType.CONTAINS_TEXT,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.CONTAINS_TEXT}'. ContainsText '$text' in ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.hasContentDescription(
    text: String,
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = ViewMatchers.withContentDescription(text),
                name = "HasContentDescription '$text' in ${this.getViewMatcher()}",
                type = EspressoAssertionType.HAS_CONTENT_DESCRIPTION,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.HAS_CONTENT_DESCRIPTION}'. HasContentDescription '$text' in ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.hasContentDescription(
    resourceId: Int,
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = ViewMatchers.withContentDescription(resourceId),
                name = "HasContentDescription resourceId = '$resourceId' in ${this.getViewMatcher()}",
                type = EspressoAssertionType.HAS_CONTENT_DESCRIPTION,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.HAS_CONTENT_DESCRIPTION}'. HasContentDescription resourceId = '$resourceId' in ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.hasContentDescription(
    charSequenceMatcher: Matcher<CharSequence>,
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = ViewMatchers.withContentDescription(charSequenceMatcher),
                name = "HasContentDescription charSequenceMatcher = '$charSequenceMatcher' in ${this.getViewMatcher()}",
                type = EspressoAssertionType.HAS_CONTENT_DESCRIPTION,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.HAS_CONTENT_DESCRIPTION}'. HasContentDescription charSequenceMatcher = '$charSequenceMatcher' in ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.contentDescriptionContains(
    text: String,
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = ViewMatchers.withContentDescription(containsString(text)),
                name = "ContentDescriptionContains text '$text' in ${this.getViewMatcher()}",
                type = EspressoAssertionType.CONTENT_DESCRIPTION_CONTAINS_TEXT,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.CONTENT_DESCRIPTION_CONTAINS_TEXT}'. ContentDescriptionContains text '$text' in ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun ViewInteraction.assertMatches(
    condition: Matcher<View>, timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewAssertionConfig.resultHandler
) = apply {
    ViewAssertionLifecycle.assert(
        ViewInteractionAssertionExecutor(
            ViewInteractionEspressoAssertion(
                viewInteraction = this,
                matcher = condition,
                name = "Custom assertion with '$condition' of ${this.getViewMatcher()}",
                type = EspressoAssertionType.ASSERT_MATCHES,
                description = "ViewInteraction assertion with type '${EspressoAssertionType.ASSERT_MATCHES}'.Custom assertion with '$condition' of ${this.getViewMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}