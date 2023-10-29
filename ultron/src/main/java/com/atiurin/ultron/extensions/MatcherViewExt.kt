package com.atiurin.ultron.extensions

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.EspressoKey
import com.atiurin.ultron.core.common.assertion.DefaultOperationAssertion
import com.atiurin.ultron.core.common.assertion.OperationAssertion
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.core.espresso.action.UltronEspressoActionParams
import com.atiurin.ultron.listeners.setListenersState
import org.hamcrest.Matcher

fun Matcher<View>.isSuccess(action: Matcher<View>.() -> Unit): Boolean =
    onView(this).isSuccess { action() }

fun Matcher<View>.withTimeout(timeoutMs: Long) =
    UltronEspressoInteraction(onView(this)).withTimeout(timeoutMs)

fun Matcher<View>.withResultHandler(resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit) =
    UltronEspressoInteraction(onView(this)).withResultHandler(resultHandler)

fun Matcher<View>.withAssertion(assertion: OperationAssertion) =
    UltronEspressoInteraction(onView(this)).withAssertion(assertion)

fun Matcher<View>.withAssertion(name: String = "", isListened: Boolean = false, block: () -> Unit) =
    UltronEspressoInteraction(onView(this)).withAssertion(
        DefaultOperationAssertion(
            name,
            block.setListenersState(isListened)
        )
    )

fun Matcher<View>.withName(name: String) = UltronEspressoInteraction(onView(this)).withName(name)
fun Matcher<View>.withMetaInfo(meta: Any) =
    UltronEspressoInteraction(onView(this)).withMetaInfo(meta)

//actions
fun Matcher<View>.click() = UltronEspressoInteraction(onView(this)).click()
fun Matcher<View>.doubleClick() = UltronEspressoInteraction(onView(this)).doubleClick()
fun Matcher<View>.longClick() = UltronEspressoInteraction(onView(this)).longClick()

fun Matcher<View>.clickTopLeft(offsetX: Int = 0, offsetY: Int = 0) =
    UltronEspressoInteraction(onView(this)).clickTopLeft(offsetX, offsetY)

fun Matcher<View>.clickTopCenter(offsetY: Int) =
    UltronEspressoInteraction(onView(this)).clickTopCenter(offsetY)

fun Matcher<View>.clickTopRight(offsetX: Int = 0, offsetY: Int = 0) =
    UltronEspressoInteraction(onView(this)).clickTopRight(offsetX, offsetY)

fun Matcher<View>.clickCenterRight(offsetX: Int = 0) =
    UltronEspressoInteraction(onView(this)).clickCenterRight(offsetX)

fun Matcher<View>.clickBottomRight(offsetX: Int = 0, offsetY: Int = 0) =
    UltronEspressoInteraction(onView(this)).clickBottomRight(offsetX, offsetY)

fun Matcher<View>.clickBottomCenter(offsetY: Int = 0) =
    UltronEspressoInteraction(onView(this)).clickBottomCenter(offsetY)

fun Matcher<View>.clickBottomLeft(offsetX: Int = 0, offsetY: Int = 0) =
    UltronEspressoInteraction(onView(this)).clickBottomLeft(offsetX, offsetY)

fun Matcher<View>.clickCenterLeft(offsetX: Int = 0) =
    UltronEspressoInteraction(onView(this)).clickCenterLeft(offsetX)

fun Matcher<View>.typeText(text: String) = UltronEspressoInteraction(onView(this)).typeText(text)
fun Matcher<View>.replaceText(text: String) =
    UltronEspressoInteraction(onView(this)).replaceText(text)

fun Matcher<View>.clearText() = UltronEspressoInteraction(onView(this)).clearText()
fun Matcher<View>.pressKey(keyCode: Int) = UltronEspressoInteraction(onView(this)).pressKey(keyCode)
fun Matcher<View>.pressKey(key: EspressoKey) = UltronEspressoInteraction(onView(this)).pressKey(key)
fun Matcher<View>.closeSoftKeyboard() = UltronEspressoInteraction(onView(this)).closeSoftKeyboard()
fun Matcher<View>.swipeLeft() = UltronEspressoInteraction(onView(this)).swipeLeft()
fun Matcher<View>.swipeRight() = UltronEspressoInteraction(onView(this)).swipeRight()
fun Matcher<View>.swipeUp() = UltronEspressoInteraction(onView(this)).swipeUp()
fun Matcher<View>.swipeDown() = UltronEspressoInteraction(onView(this)).swipeDown()
fun Matcher<View>.scrollTo() = UltronEspressoInteraction(onView(this)).scrollTo()
fun Matcher<View>.perform(viewAction: ViewAction, description: String = "") =
    UltronEspressoInteraction(onView(this)).perform(viewAction, description)

fun Matcher<View>.perform(
    params: UltronEspressoActionParams? = null,
    block: (uiController: UiController, view: View) -> Unit
) =
    UltronEspressoInteraction(onView(this)).perform(params, block)

fun <T> Matcher<View>.execute(
    params: UltronEspressoActionParams? = null,
    block: (uiController: UiController, view: View) -> T
): T =
    UltronEspressoInteraction(onView(this)).execute(params, block)

//assertions
fun Matcher<View>.isDisplayed() = UltronEspressoInteraction(onView(this)).isDisplayed()
fun Matcher<View>.isNotDisplayed() = UltronEspressoInteraction(onView(this)).isNotDisplayed()
fun Matcher<View>.exists() = UltronEspressoInteraction(onView(this)).exists()
fun Matcher<View>.doesNotExist() = UltronEspressoInteraction(onView(this)).doesNotExist()
fun Matcher<View>.isCompletelyDisplayed() =
    UltronEspressoInteraction(onView(this)).isCompletelyDisplayed()

fun Matcher<View>.isDisplayingAtLeast(percentage: Int) =
    UltronEspressoInteraction(onView(this)).isDisplayingAtLeast(percentage)

fun Matcher<View>.isEnabled() = UltronEspressoInteraction(onView(this)).isEnabled()
fun Matcher<View>.isNotEnabled() = UltronEspressoInteraction(onView(this)).isNotEnabled()
fun Matcher<View>.isSelected() = UltronEspressoInteraction(onView(this)).isSelected()
fun Matcher<View>.isNotSelected() = UltronEspressoInteraction(onView(this)).isNotSelected()
fun Matcher<View>.isClickable() = UltronEspressoInteraction(onView(this)).isClickable()
fun Matcher<View>.isNotClickable() = UltronEspressoInteraction(onView(this)).isNotClickable()
fun Matcher<View>.isChecked() = UltronEspressoInteraction(onView(this)).isChecked()
fun Matcher<View>.isNotChecked() = UltronEspressoInteraction(onView(this)).isNotChecked()
fun Matcher<View>.isFocusable() = UltronEspressoInteraction(onView(this)).isFocusable()
fun Matcher<View>.isNotFocusable() = UltronEspressoInteraction(onView(this)).isNotFocusable()
fun Matcher<View>.hasFocus() = UltronEspressoInteraction(onView(this)).hasFocus()
fun Matcher<View>.isJavascriptEnabled() =
    UltronEspressoInteraction(onView(this)).isJavascriptEnabled()

fun Matcher<View>.hasText(text: String) = UltronEspressoInteraction(onView(this)).hasText(text)
fun Matcher<View>.hasText(resourceId: Int) =
    UltronEspressoInteraction(onView(this)).hasText(resourceId)

fun Matcher<View>.hasText(stringMatcher: Matcher<String>) =
    UltronEspressoInteraction(onView(this)).hasText(stringMatcher)

fun Matcher<View>.textContains(text: String) =
    UltronEspressoInteraction(onView(this)).textContains(text)

fun Matcher<View>.hasContentDescription(text: String) =
    UltronEspressoInteraction(onView(this)).hasContentDescription(text)

fun Matcher<View>.hasContentDescription(resourceId: Int) =
    UltronEspressoInteraction(onView(this)).hasContentDescription(resourceId)

fun Matcher<View>.hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) =
    UltronEspressoInteraction(onView(this)).hasContentDescription(charSequenceMatcher)

fun Matcher<View>.contentDescriptionContains(text: String) =
    UltronEspressoInteraction(onView(this)).contentDescriptionContains(text)

fun Matcher<View>.assertMatches(condition: Matcher<View>) =
    UltronEspressoInteraction(onView(this)).assertMatches(condition)
