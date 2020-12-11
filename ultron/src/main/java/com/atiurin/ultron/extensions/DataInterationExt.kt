package com.atiurin.ultron.extensions

import android.view.View
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.ViewAction
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

fun DataInteraction.isSuccess(
    action: DataInteraction.() -> Unit
): Boolean {
    var success = true
    try {
        action()
    }catch (th: Throwable){
        success = false
    }
    return success
}

fun DataInteraction.webClick(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAction>) -> Unit = ViewActionConfig.dataInteractionResultHandler
) = apply {
    ViewActionLifecycle.execute(
        DataInteractionActionExecutor(
            DataInteractionEspressoAction(
                dataInteraction = this,
                viewAction = ViewActions.click(),
                name = "Click to ${this.getDataMatcher()}",
                type = EspressoActionType.CLICK,
                description = "DataInteraction with type '${EspressoActionType.CLICK}'.Click to ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}


fun DataInteraction.doubleClick(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAction>) -> Unit = ViewActionConfig.dataInteractionResultHandler
) = apply {
    ViewActionLifecycle.execute(
        DataInteractionActionExecutor(
            DataInteractionEspressoAction(
                dataInteraction = this,
                viewAction = ViewActions.doubleClick(),
                name = "Double click to ${this.getDataMatcher()}",
                type = EspressoActionType.DOUBLE_CLICK,
                description = "DataInteraction with type '${EspressoActionType.DOUBLE_CLICK}'.Double click to ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.longClick(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAction>) -> Unit = ViewActionConfig.dataInteractionResultHandler
) = apply {
    ViewActionLifecycle.execute(
        DataInteractionActionExecutor(
            DataInteractionEspressoAction(
                dataInteraction = this,
                viewAction = ViewActions.longClick(),
                name = "Long click to ${this.getDataMatcher()}",
                type = EspressoActionType.LONG_CLICK,
                description = "DataInteraction with type '${EspressoActionType.LONG_CLICK}'. Long click to ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.typeText(
    text: String,
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAction>) -> Unit = ViewActionConfig.dataInteractionResultHandler
) = apply {
    ViewActionLifecycle.execute(
        DataInteractionActionExecutor(
            DataInteractionEspressoAction(
                dataInteraction = this,
                viewAction = ViewActions.typeText(text),
                name = "Type text '$text' to ${this.getDataMatcher()}",
                type = EspressoActionType.TYPE_TEXT,
                description = "DataInteraction with type '${EspressoActionType.TYPE_TEXT}'. Type text '$text' to ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.replaceText(
    text: String,
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAction>) -> Unit = ViewActionConfig.dataInteractionResultHandler
) = apply {
    ViewActionLifecycle.execute(
        DataInteractionActionExecutor(
            DataInteractionEspressoAction(
                dataInteraction = this,
                viewAction = ViewActions.replaceText(text),
                name = "Replace text '$text' to ${this.getDataMatcher()}",
                type = EspressoActionType.REPLACE_TEXT,
                description = "DataInteraction with type '${EspressoActionType.REPLACE_TEXT}'. Replace text '$text' to ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.clearText(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAction>) -> Unit = ViewActionConfig.dataInteractionResultHandler
) = apply {
    ViewActionLifecycle.execute(
        DataInteractionActionExecutor(
            DataInteractionEspressoAction(
                dataInteraction = this,
                viewAction = ViewActions.clearText(),
                name = "Clear text in ${this.getDataMatcher()}",
                type = EspressoActionType.CLEAR_TEXT,
                description = "DataInteraction with type '${EspressoActionType.CLEAR_TEXT}'. Clear text in ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.pressKey(
    keyCode: Int,
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAction>) -> Unit = ViewActionConfig.dataInteractionResultHandler
) = apply {
    ViewActionLifecycle.execute(
        DataInteractionActionExecutor(
            DataInteractionEspressoAction(
                dataInteraction = this,
                viewAction = ViewActions.pressKey(keyCode),
                name = "Press key '$keyCode' in ${this.getDataMatcher()}",
                type = EspressoActionType.PRESS_KEY,
                description = "DataInteraction with type '${EspressoActionType.PRESS_KEY}'. Press key '$keyCode' in ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.pressKey(
    key: EspressoKey,
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAction>) -> Unit = ViewActionConfig.dataInteractionResultHandler
) = apply {
    ViewActionLifecycle.execute(
        DataInteractionActionExecutor(
            DataInteractionEspressoAction(
                dataInteraction = this,
                viewAction = ViewActions.pressKey(key),
                name = "Press key '$key' in ${this.getDataMatcher()}",
                type = EspressoActionType.PRESS_KEY,
                description = "DataInteraction with type '${EspressoActionType.PRESS_KEY}'. Press key '$key' in ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.closeSoftKeyboard(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAction>) -> Unit = ViewActionConfig.dataInteractionResultHandler
) = apply {
    ViewActionLifecycle.execute(
        DataInteractionActionExecutor(
            DataInteractionEspressoAction(
                dataInteraction = this,
                viewAction = ViewActions.closeSoftKeyboard(),
                name = "Close softKeyboard with ${this.getDataMatcher()}",
                type = EspressoActionType.CLOSE_SOFT_KEYBOARD,
                description = "DataInteraction with type '${EspressoActionType.CLOSE_SOFT_KEYBOARD}'. Close softKeyboard with ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.swipeLeft(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAction>) -> Unit = ViewActionConfig.dataInteractionResultHandler
) = apply {
    ViewActionLifecycle.execute(
        DataInteractionActionExecutor(
            DataInteractionEspressoAction(
                dataInteraction = this,
                viewAction = ViewActions.swipeLeft(),
                name = "Swipe Left with ${this.getDataMatcher()}",
                type = EspressoActionType.SWIPE_LEFT,
                description = "DataInteraction with type '${EspressoActionType.SWIPE_LEFT}'. Swipe Left with ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.swipeRight(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAction>) -> Unit = ViewActionConfig.dataInteractionResultHandler
) = apply {
    ViewActionLifecycle.execute(
        DataInteractionActionExecutor(
            DataInteractionEspressoAction(
                dataInteraction = this,
                viewAction = ViewActions.swipeRight(),
                name = "Swipe Right with ${this.getDataMatcher()}",
                type = EspressoActionType.SWIPE_LEFT,
                description = "DataInteraction with type '${EspressoActionType.SWIPE_RIGHT}'. Swipe Right with ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.swipeUp(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAction>) -> Unit = ViewActionConfig.dataInteractionResultHandler
) = apply {
    ViewActionLifecycle.execute(
        DataInteractionActionExecutor(
            DataInteractionEspressoAction(
                dataInteraction = this,
                viewAction = ViewActions.swipeUp(),
                name = "Swipe Up with ${this.getDataMatcher()}",
                type = EspressoActionType.SWIPE_UP,
                description = "DataInteraction with type '${EspressoActionType.SWIPE_UP}'. Swipe Up with ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.swipeDown(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAction>) -> Unit = ViewActionConfig.dataInteractionResultHandler
) = apply {
    ViewActionLifecycle.execute(
        DataInteractionActionExecutor(
            DataInteractionEspressoAction(
                dataInteraction = this,
                viewAction = ViewActions.swipeDown(),
                name = "Swipe Down with ${this.getDataMatcher()}",
                type = EspressoActionType.SWIPE_DOWN,
                description = "DataInteraction with type '${EspressoActionType.SWIPE_DOWN}'. Swipe Down with ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.scrollTo(
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAction>) -> Unit = ViewActionConfig.dataInteractionResultHandler
) = apply {
    ViewActionLifecycle.execute(
        DataInteractionActionExecutor(
            DataInteractionEspressoAction(
                dataInteraction = this,
                viewAction = ViewActions.scrollTo(),
                name = "Scroll to ${this.getDataMatcher()}",
                type = EspressoActionType.SCROLL,
                description = "DataInteraction with type '${EspressoActionType.SCROLL}'. Scroll to ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.execute(
    viewAction: ViewAction,
    timeoutMs: Long = ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAction>) -> Unit = ViewActionConfig.dataInteractionResultHandler
) {
    ViewActionLifecycle.execute(
        DataInteractionActionExecutor(
            DataInteractionEspressoAction(
                dataInteraction = this,
                viewAction = viewAction,
                name = "Custom action ${this.getDataMatcher()}",
                type = EspressoActionType.CUSTOM,
                description = "DataInteraction with type '${EspressoActionType.CUSTOM}'. Custom action ${viewAction.description} to ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}
//assertions

fun DataInteraction.isDisplayed(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAssertion>) -> Unit = ViewAssertionConfig.dataInteractionResultHandler
) = apply {
    ViewAssertionLifecycle.execute(
        DataInteractionAssertionExecutor(
            DataInteractionEspressoAssertion(
                dataInteraction = this,
                matcher = ViewMatchers.isDisplayed(),
                name = "IsDisplayed of ${this.getDataMatcher()}",
                type = EspressoAssertionType.IS_DISPLAYED,
                description = "DataInteraction with type '${EspressoAssertionType.IS_DISPLAYED}'. IsDisplayed of ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.isCompletelyDisplayed(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAssertion>) -> Unit = ViewAssertionConfig.dataInteractionResultHandler
) = apply {
    ViewAssertionLifecycle.execute(
        DataInteractionAssertionExecutor(
            DataInteractionEspressoAssertion(
                dataInteraction = this,
                matcher = ViewMatchers.isCompletelyDisplayed(),
                name = "IsCompletelyDisplayed of ${this.getDataMatcher()}",
                type = EspressoAssertionType.IS_COMPLETELY_DISPLAYED,
                description = "DataInteraction with type '${EspressoAssertionType.IS_COMPLETELY_DISPLAYED}'. IsCompletelyDisplayed of ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.isDisplayingAtLeast(
    percentage: Int,
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAssertion>) -> Unit = ViewAssertionConfig.dataInteractionResultHandler
) = apply {
    ViewAssertionLifecycle.execute(
        DataInteractionAssertionExecutor(
            DataInteractionEspressoAssertion(
                dataInteraction = this,
                matcher = ViewMatchers.isDisplayingAtLeast(percentage),
                name = "IsDisplayingAtLeast $percentage % of ${this.getDataMatcher()}",
                type = EspressoAssertionType.IS_DISPLAYING_AT_LEAST,
                description = "DataInteraction with type '${EspressoAssertionType.IS_DISPLAYING_AT_LEAST}'. IsDisplayingAtLeast $percentage % of ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.isEnabled(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAssertion>) -> Unit = ViewAssertionConfig.dataInteractionResultHandler
) = apply {
    ViewAssertionLifecycle.execute(
        DataInteractionAssertionExecutor(
            DataInteractionEspressoAssertion(
                dataInteraction = this,
                matcher = ViewMatchers.isEnabled(),
                name = "IsEnabled of ${this.getDataMatcher()}",
                type = EspressoAssertionType.IS_ENABLED,
                description = "DataInteraction with type '${EspressoAssertionType.IS_ENABLED}'. IsEnabled of ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.isSelected(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAssertion>) -> Unit = ViewAssertionConfig.dataInteractionResultHandler
) = apply {
    ViewAssertionLifecycle.execute(
        DataInteractionAssertionExecutor(
            DataInteractionEspressoAssertion(
                dataInteraction = this,
                matcher = ViewMatchers.isSelected(),
                name = "IsSelected of ${this.getDataMatcher()}",
                type = EspressoAssertionType.IS_SELECTED,
                description = "DataInteraction with type '${EspressoAssertionType.IS_SELECTED}'. IsSelected of ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.isClickable(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAssertion>) -> Unit = ViewAssertionConfig.dataInteractionResultHandler
) = apply {
    ViewAssertionLifecycle.execute(
        DataInteractionAssertionExecutor(
            DataInteractionEspressoAssertion(
                dataInteraction = this,
                matcher = ViewMatchers.isClickable(),
                name = "IsClickable of ${this.getDataMatcher()}",
                type = EspressoAssertionType.IS_CLICKABLE,
                description = "DataInteraction with type '${EspressoAssertionType.IS_CLICKABLE}'. IsClickable of ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.isChecked(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAssertion>) -> Unit = ViewAssertionConfig.dataInteractionResultHandler
) = apply {
    ViewAssertionLifecycle.execute(
        DataInteractionAssertionExecutor(
            DataInteractionEspressoAssertion(
                dataInteraction = this,
                matcher = ViewMatchers.isChecked(),
                name = "IsChecked of ${this.getDataMatcher()}",
                type = EspressoAssertionType.IS_CHECKED,
                description = "DataInteraction with type '${EspressoAssertionType.IS_CHECKED}'. IsChecked of ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.isNotChecked(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAssertion>) -> Unit = ViewAssertionConfig.dataInteractionResultHandler
) = apply {
    ViewAssertionLifecycle.execute(
        DataInteractionAssertionExecutor(
            DataInteractionEspressoAssertion(
                dataInteraction = this,
                matcher = ViewMatchers.isNotChecked(),
                name = "IsNotChecked of ${this.getDataMatcher()}",
                type = EspressoAssertionType.IS_NOT_CHECKED,
                description = "DataInteraction with type '${EspressoAssertionType.IS_NOT_CHECKED}'. IsNotChecked of ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.isFocusable(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAssertion>) -> Unit = ViewAssertionConfig.dataInteractionResultHandler
) = apply {
    ViewAssertionLifecycle.execute(
        DataInteractionAssertionExecutor(
            DataInteractionEspressoAssertion(
                dataInteraction = this,
                matcher = ViewMatchers.isFocusable(),
                name = "IsFocusable of ${this.getDataMatcher()}",
                type = EspressoAssertionType.IS_FOCUSABLE,
                description = "DataInteraction with type '${EspressoAssertionType.IS_FOCUSABLE}'. IsFocusable of ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.hasFocus(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAssertion>) -> Unit = ViewAssertionConfig.dataInteractionResultHandler
) = apply {
    ViewAssertionLifecycle.execute(
        DataInteractionAssertionExecutor(
            DataInteractionEspressoAssertion(
                dataInteraction = this,
                matcher = ViewMatchers.hasFocus(),
                name = "HasFocus of ${this.getDataMatcher()}",
                type = EspressoAssertionType.HAS_FOCUS,
                description = "DataInteraction with type '${EspressoAssertionType.HAS_FOCUS}'. HasFocus of ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.isJavascriptEnabled(
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAssertion>) -> Unit = ViewAssertionConfig.dataInteractionResultHandler
) = apply {
    ViewAssertionLifecycle.execute(
        DataInteractionAssertionExecutor(
            DataInteractionEspressoAssertion(
                dataInteraction = this,
                matcher = ViewMatchers.isJavascriptEnabled(),
                name = "IsJavascriptEnabled of ${this.getDataMatcher()}",
                type = EspressoAssertionType.IS_JS_ENABLED,
                description = "DataInteraction with type '${EspressoAssertionType.IS_JS_ENABLED}'. IsJavascriptEnabled of ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.hasText(
    text: String,
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAssertion>) -> Unit = ViewAssertionConfig.dataInteractionResultHandler
) = apply {
    ViewAssertionLifecycle.execute(
        DataInteractionAssertionExecutor(
            DataInteractionEspressoAssertion(
                dataInteraction = this,
                matcher = ViewMatchers.withText(text),
                name = "HasText '$text' of ${this.getDataMatcher()}",
                type = EspressoAssertionType.HAS_TEXT,
                description = "DataInteraction with type '${EspressoAssertionType.HAS_TEXT}'. HasText '$text' of ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}

fun DataInteraction.assertMatches(
    condition: Matcher<View>,
    timeoutMs: Long = ASSERTION_TIMEOUT,
    resultHandler: (EspressoOperationResult<DataInteractionEspressoAssertion>) -> Unit = ViewAssertionConfig.dataInteractionResultHandler
) = apply {
    ViewAssertionLifecycle.execute(
        DataInteractionAssertionExecutor(
            DataInteractionEspressoAssertion(
                dataInteraction = this,
                matcher = condition,
                name = "AssertMatches '$condition' of ${this.getDataMatcher()}",
                type = EspressoAssertionType.ASSERT_MATCHES,
                description = "DataInteraction with type '${EspressoAssertionType.ASSERT_MATCHES}'. AssertMatches '$condition' of ${this.getDataMatcher()} with root ${this.getRootMatcher()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            )
        ), resultHandler
    )
}