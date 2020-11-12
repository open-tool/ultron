package com.atiurin.ultron.extensions

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.EspressoKey
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.action.ViewActionConfig
import org.hamcrest.Matcher

fun Matcher<View>.isSuccess(
    action: Matcher<View>.() -> Unit
): Boolean {
    return onView(this).isSuccess { action() }
}

fun Matcher<View>.click(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).click(timeoutMs, resultHandler)
}

fun Matcher<View>.doubleClick(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).doubleClick(timeoutMs, resultHandler)
}

fun Matcher<View>.longClick(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).longClick(timeoutMs, resultHandler)
}

fun Matcher<View>.typeText(
    text: String, timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).typeText(text,timeoutMs, resultHandler)
}

fun Matcher<View>.replaceText(
    text: String, timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).replaceText(text, timeoutMs, resultHandler)
}

fun Matcher<View>.clearText(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).clearText(timeoutMs, resultHandler)
}

fun Matcher<View>.pressKey(
    keyCode: Int,
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).pressKey(keyCode, timeoutMs, resultHandler)
}

fun Matcher<View>.pressKey(
    key: EspressoKey, timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).pressKey(key, timeoutMs, resultHandler)
}

fun Matcher<View>.closeSoftKeyboard(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).closeSoftKeyboard(timeoutMs, resultHandler)
}

fun Matcher<View>.swipeLeft(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).swipeLeft(timeoutMs, resultHandler)
}

fun Matcher<View>.swipeRight(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).swipeRight(timeoutMs, resultHandler)
}

fun Matcher<View>.swipeUp(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).swipeUp(timeoutMs, resultHandler)
}

fun Matcher<View>.swipeDown(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).swipeDown(timeoutMs, resultHandler)
}

fun Matcher<View>.scrollTo(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).scrollTo(timeoutMs, resultHandler)
}

fun Matcher<View>.execute(
    viewAction: ViewAction, timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) {
    onView(this).execute(viewAction, timeoutMs, resultHandler)
}
//assertions


fun Matcher<View>.isDisplayed(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).isDisplayed(timeoutMs, resultHandler)
}

fun Matcher<View>.isNotDisplayed(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).isNotDisplayed(timeoutMs, resultHandler)
}

fun Matcher<View>.isCompletelyDisplayed(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).isCompletelyDisplayed(timeoutMs, resultHandler)
}

fun Matcher<View>.isDisplayingAtLeast(
    percentage: Int, timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).isDisplayingAtLeast(percentage, timeoutMs, resultHandler)
}

fun Matcher<View>.isEnabled(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).isEnabled(timeoutMs, resultHandler)
}

fun Matcher<View>.isNotEnabled(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).isNotEnabled(timeoutMs, resultHandler)
}

fun Matcher<View>.isSelected(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).isSelected(timeoutMs, resultHandler)
}

fun Matcher<View>.isNotSelected(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).isNotSelected(timeoutMs, resultHandler)
}

fun Matcher<View>.isClickable(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).isClickable(timeoutMs, resultHandler)
}

fun Matcher<View>.isNotClickable(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).isNotClickable(timeoutMs, resultHandler)
}

fun Matcher<View>.isChecked(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).isChecked(timeoutMs, resultHandler)
}

fun Matcher<View>.isNotChecked(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).isNotChecked(timeoutMs, resultHandler)
}

fun Matcher<View>.isFocusable(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).isFocusable(timeoutMs, resultHandler)
}

fun Matcher<View>.isNotFocusable(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).isNotFocusable(timeoutMs, resultHandler)
}

fun Matcher<View>.hasFocus(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).hasFocus(timeoutMs, resultHandler)
}

fun Matcher<View>.isJavascriptEnabled(
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).isJavascriptEnabled(timeoutMs, resultHandler)
}

fun Matcher<View>.hasText(
    text: String,
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).hasText(text, timeoutMs, resultHandler)
}

fun Matcher<View>.hasText(
    resourceId: Int,
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).hasText(resourceId, timeoutMs, resultHandler)
}

fun Matcher<View>.hasText(
    stringMatcher: Matcher<String>,
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).hasText(stringMatcher, timeoutMs, resultHandler)
}

fun Matcher<View>.containsText(
    text: String,
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).containsText(text, timeoutMs, resultHandler)
}

fun Matcher<View>.contentDescriptionContains(
    text: String,
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).contentDescriptionContains(text, timeoutMs, resultHandler)
}

fun Matcher<View>.hasContentDescription(
    text: String,
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).hasContentDescription(text, timeoutMs, resultHandler)
}

fun Matcher<View>.hasContentDescription(
    resourceId: Int,
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).hasContentDescription(resourceId, timeoutMs, resultHandler)
}

fun Matcher<View>.hasContentDescription(
    charSequenceMatcher: Matcher<CharSequence>,
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).hasContentDescription(charSequenceMatcher, timeoutMs, resultHandler)
}

fun Matcher<View>.assertMatches(
    condition: Matcher<View>,
    timeoutMs: Long = ViewActionConfig.ACTION_TIMEOUT,
    resultHandler: (EspressoOperationResult) -> Unit = ViewActionConfig.defaultResultHandler
) = apply {
    onView(this).assertMatches(condition, timeoutMs, resultHandler)
}