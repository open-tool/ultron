package com.atiurin.ultron.extensions

import android.view.View
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.EspressoKey
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import com.atiurin.ultron.core.common.assertion.DefaultOperationAssertion
import com.atiurin.ultron.core.common.assertion.OperationAssertion
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.custom.espresso.base.createRootViewPicker
import com.atiurin.ultron.listeners.setListenersState
import com.atiurin.ultron.utils.runOnUiThread
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matcher

fun ViewInteraction.isSuccess(action: ViewInteraction.() -> Unit): Boolean = runCatching { action() }.isSuccess

fun ViewInteraction.withTimeout(timeoutMs: Long) = UltronEspressoInteraction(this).withTimeout(timeoutMs)
fun ViewInteraction.withResultHandler(resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit) = UltronEspressoInteraction(this).withResultHandler(resultHandler)
fun ViewInteraction.withAssertion(assertion: OperationAssertion) = UltronEspressoInteraction(this).withAssertion(assertion)
fun ViewInteraction.withAssertion(name: String = "", isListened: Boolean = false, block: () -> Unit) =
    UltronEspressoInteraction(this).withAssertion(DefaultOperationAssertion(name, block.setListenersState(isListened)))

//root view searching
fun ViewInteraction.withSuitableRoot(): ViewInteraction {
    val viewMatcher: Matcher<View>? = this.getViewMatcher()
    var decorView: View? = null
    runOnUiThread { decorView = viewMatcher?.let { createRootViewPicker(it).get() } }
    return when {
        decorView != null -> { this.inRoot(withDecorView(`is`(decorView))) }
        else -> { this }
    }
}

//actions
fun ViewInteraction.click() = UltronEspressoInteraction(this).click()
fun ViewInteraction.doubleClick() = UltronEspressoInteraction(this).doubleClick()
fun ViewInteraction.longClick() = UltronEspressoInteraction(this).longClick()

fun ViewInteraction.clickTopLeft(offsetX: Int = 0, offsetY: Int = 0) = UltronEspressoInteraction(this).clickTopLeft(offsetX, offsetY)
fun ViewInteraction.clickTopCenter(offsetY: Int) = UltronEspressoInteraction(this).clickTopCenter(offsetY)
fun ViewInteraction.clickTopRight(offsetX: Int = 0, offsetY: Int = 0) = UltronEspressoInteraction(this).clickTopRight(offsetX, offsetY)
fun ViewInteraction.clickCenterRight(offsetX: Int = 0) = UltronEspressoInteraction(this).clickCenterRight(offsetX)
fun ViewInteraction.clickBottomRight(offsetX: Int = 0, offsetY: Int = 0) = UltronEspressoInteraction(this).clickBottomRight(offsetX, offsetY)
fun ViewInteraction.clickBottomCenter(offsetY: Int = 0) = UltronEspressoInteraction(this).clickBottomCenter(offsetY)
fun ViewInteraction.clickBottomLeft(offsetX: Int = 0, offsetY: Int = 0) = UltronEspressoInteraction(this).clickBottomLeft(offsetX, offsetY)
fun ViewInteraction.clickCenterLeft(offsetX: Int = 0) = UltronEspressoInteraction(this).clickCenterLeft(offsetX)

fun ViewInteraction.typeText(text: String) = UltronEspressoInteraction(this).typeText(text)
fun ViewInteraction.replaceText(text: String) = UltronEspressoInteraction(this).replaceText(text)
fun ViewInteraction.clearText() = UltronEspressoInteraction(this).clearText()
fun ViewInteraction.pressKey(keyCode: Int) = UltronEspressoInteraction(this).pressKey(keyCode)
fun ViewInteraction.pressKey(key: EspressoKey) = UltronEspressoInteraction(this).pressKey(key)
fun ViewInteraction.closeSoftKeyboard() = UltronEspressoInteraction(this).closeSoftKeyboard()
fun ViewInteraction.swipeLeft() = UltronEspressoInteraction(this).swipeLeft()
fun ViewInteraction.swipeRight() = UltronEspressoInteraction(this).swipeRight()
fun ViewInteraction.swipeUp() = UltronEspressoInteraction(this).swipeUp()
fun ViewInteraction.swipeDown() = UltronEspressoInteraction(this).swipeDown()
fun ViewInteraction.scrollTo() = UltronEspressoInteraction(this).scrollTo()
fun ViewInteraction.perform(viewAction: ViewAction) = UltronEspressoInteraction(this).perform(viewAction)

//assertions
fun ViewInteraction.isDisplayed() = UltronEspressoInteraction(this).isDisplayed()
fun ViewInteraction.isNotDisplayed() = UltronEspressoInteraction(this).isNotDisplayed()
fun ViewInteraction.exists() = UltronEspressoInteraction(this).exists()
fun ViewInteraction.doesNotExist() = UltronEspressoInteraction(this).doesNotExist()
fun ViewInteraction.isCompletelyDisplayed() = UltronEspressoInteraction(this).isCompletelyDisplayed()
fun ViewInteraction.isDisplayingAtLeast(percentage: Int) =
    UltronEspressoInteraction(this).isDisplayingAtLeast(percentage)

fun ViewInteraction.isEnabled() = UltronEspressoInteraction(this).isEnabled()
fun ViewInteraction.isNotEnabled() = UltronEspressoInteraction(this).isNotEnabled()
fun ViewInteraction.isSelected() = UltronEspressoInteraction(this).isSelected()
fun ViewInteraction.isNotSelected() = UltronEspressoInteraction(this).isNotSelected()
fun ViewInteraction.isClickable() = UltronEspressoInteraction(this).isClickable()
fun ViewInteraction.isNotClickable() = UltronEspressoInteraction(this).isNotClickable()
fun ViewInteraction.isChecked() = UltronEspressoInteraction(this).isChecked()
fun ViewInteraction.isNotChecked() = UltronEspressoInteraction(this).isNotChecked()
fun ViewInteraction.isFocusable() = UltronEspressoInteraction(this).isFocusable()
fun ViewInteraction.isNotFocusable() = UltronEspressoInteraction(this).isNotFocusable()
fun ViewInteraction.hasFocus() = UltronEspressoInteraction(this).hasFocus()
fun ViewInteraction.isJavascriptEnabled() = UltronEspressoInteraction(this).isJavascriptEnabled()
fun ViewInteraction.hasText(text: String) = UltronEspressoInteraction(this).hasText(text)
fun ViewInteraction.hasText(resourceId: Int) = UltronEspressoInteraction(this).hasText(resourceId)
fun ViewInteraction.hasText(stringMatcher: Matcher<String>) =
    UltronEspressoInteraction(this).hasText(stringMatcher)

fun ViewInteraction.textContains(text: String) = UltronEspressoInteraction(this).textContains(text)
fun ViewInteraction.hasContentDescription(text: String) =
    UltronEspressoInteraction(this).hasContentDescription(text)

fun ViewInteraction.hasContentDescription(resourceId: Int) =
    UltronEspressoInteraction(this).hasContentDescription(resourceId)

fun ViewInteraction.hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) =
    UltronEspressoInteraction(this).hasContentDescription(charSequenceMatcher)

fun ViewInteraction.contentDescriptionContains(text: String) =
    UltronEspressoInteraction(this).contentDescriptionContains(text)

fun ViewInteraction.assertMatches(condition: Matcher<View>) =
    UltronEspressoInteraction(this).assertMatches(condition)
