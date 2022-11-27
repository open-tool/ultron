package com.atiurin.ultron.extensions

import android.view.View
import androidx.test.espresso.DataInteraction
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.EspressoKey
import com.atiurin.ultron.core.common.assertion.DefaultOperationAssertion
import com.atiurin.ultron.core.common.assertion.OperationAssertion
import com.atiurin.ultron.core.espresso.EspressoOperationResult
import com.atiurin.ultron.core.espresso.UltronEspressoOperation
import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
import com.atiurin.ultron.listeners.setListenersState
import org.hamcrest.Matcher

fun DataInteraction.isSuccess(action: DataInteraction.() -> Unit): Boolean = runCatching { action() }.isSuccess
fun DataInteraction.withTimeout(timeoutMs: Long) = UltronEspressoInteraction(this).withTimeout(timeoutMs)
fun DataInteraction.withResultHandler(resultHandler: (EspressoOperationResult<UltronEspressoOperation>) -> Unit) = UltronEspressoInteraction(this).withResultHandler(resultHandler)
fun DataInteraction.withAssertion(assertion: OperationAssertion) = UltronEspressoInteraction(this).withAssertion(assertion)
fun DataInteraction.withAssertion(name: String = "", isListened: Boolean = false, block: () -> Unit) =
    UltronEspressoInteraction(this).withAssertion(DefaultOperationAssertion(name, block.setListenersState(isListened)))

//actions
fun DataInteraction.click() = UltronEspressoInteraction(this).click()
fun DataInteraction.doubleClick() = UltronEspressoInteraction(this).doubleClick()
fun DataInteraction.longClick() = UltronEspressoInteraction(this).longClick()

fun DataInteraction.clickTopLeft(offsetX: Int = 0, offsetY: Int = 0) = UltronEspressoInteraction(this).clickTopLeft(offsetX, offsetY)
fun DataInteraction.clickTopCenter(offsetY: Int) = UltronEspressoInteraction(this).clickTopCenter(offsetY)
fun DataInteraction.clickTopRight(offsetX: Int = 0, offsetY: Int = 0) = UltronEspressoInteraction(this).clickTopRight(offsetX, offsetY)
fun DataInteraction.clickCenterRight(offsetX: Int = 0) = UltronEspressoInteraction(this).clickCenterRight(offsetX)
fun DataInteraction.clickBottomRight(offsetX: Int = 0, offsetY: Int = 0) = UltronEspressoInteraction(this).clickBottomRight(offsetX, offsetY)
fun DataInteraction.clickBottomCenter(offsetY: Int = 0) = UltronEspressoInteraction(this).clickBottomCenter(offsetY)
fun DataInteraction.clickBottomLeft(offsetX: Int = 0, offsetY: Int = 0) = UltronEspressoInteraction(this).clickBottomLeft(offsetX, offsetY)
fun DataInteraction.clickCenterLeft(offsetX: Int = 0) = UltronEspressoInteraction(this).clickCenterLeft(offsetX)

fun DataInteraction.typeText(text: String) = UltronEspressoInteraction(this).typeText(text)
fun DataInteraction.replaceText(text: String) = UltronEspressoInteraction(this).replaceText(text)
fun DataInteraction.clearText() = UltronEspressoInteraction(this).clearText()
fun DataInteraction.pressKey(keyCode: Int) = UltronEspressoInteraction(this).pressKey(keyCode)
fun DataInteraction.pressKey(key: EspressoKey) = UltronEspressoInteraction(this).pressKey(key)
fun DataInteraction.closeSoftKeyboard() = UltronEspressoInteraction(this).closeSoftKeyboard()
fun DataInteraction.swipeLeft() = UltronEspressoInteraction(this).swipeLeft()
fun DataInteraction.swipeRight() = UltronEspressoInteraction(this).swipeRight()
fun DataInteraction.swipeUp() = UltronEspressoInteraction(this).swipeUp()
fun DataInteraction.swipeDown() = UltronEspressoInteraction(this).swipeDown()
fun DataInteraction.scrollTo() = UltronEspressoInteraction(this).scrollTo()
fun DataInteraction.perform(viewAction: ViewAction) = UltronEspressoInteraction(this).perform(viewAction)

//assertions
fun DataInteraction.isDisplayed() = UltronEspressoInteraction(this).isDisplayed()
fun DataInteraction.isNotDisplayed() = UltronEspressoInteraction(this).isNotDisplayed()
fun DataInteraction.exists() = UltronEspressoInteraction(this).exists()
fun DataInteraction.doesNotExist() = UltronEspressoInteraction(this).doesNotExist()
fun DataInteraction.isCompletelyDisplayed() = UltronEspressoInteraction(this).isCompletelyDisplayed()
fun DataInteraction.isDisplayingAtLeast(percentage: Int) =
    UltronEspressoInteraction(this).isDisplayingAtLeast(percentage)

fun DataInteraction.isEnabled() = UltronEspressoInteraction(this).isEnabled()
fun DataInteraction.isNotEnabled() = UltronEspressoInteraction(this).isNotEnabled()
fun DataInteraction.isSelected() = UltronEspressoInteraction(this).isSelected()
fun DataInteraction.isNotSelected() = UltronEspressoInteraction(this).isNotSelected()
fun DataInteraction.isClickable() = UltronEspressoInteraction(this).isClickable()
fun DataInteraction.isNotClickable() = UltronEspressoInteraction(this).isNotClickable()
fun DataInteraction.isChecked() = UltronEspressoInteraction(this).isChecked()
fun DataInteraction.isNotChecked() = UltronEspressoInteraction(this).isNotChecked()
fun DataInteraction.isFocusable() = UltronEspressoInteraction(this).isFocusable()
fun DataInteraction.isNotFocusable() = UltronEspressoInteraction(this).isNotFocusable()
fun DataInteraction.hasFocus() = UltronEspressoInteraction(this).hasFocus()
fun DataInteraction.isJavascriptEnabled() = UltronEspressoInteraction(this).isJavascriptEnabled()
fun DataInteraction.hasText(text: String) = UltronEspressoInteraction(this).hasText(text)
fun DataInteraction.hasText(resourceId: Int) = UltronEspressoInteraction(this).hasText(resourceId)
fun DataInteraction.hasText(stringMatcher: Matcher<String>) =
    UltronEspressoInteraction(this).hasText(stringMatcher)

fun DataInteraction.textContains(text: String) = UltronEspressoInteraction(this).textContains(text)
fun DataInteraction.hasContentDescription(text: String) =
    UltronEspressoInteraction(this).hasContentDescription(text)

fun DataInteraction.hasContentDescription(resourceId: Int) =
    UltronEspressoInteraction(this).hasContentDescription(resourceId)

fun DataInteraction.hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) =
    UltronEspressoInteraction(this).hasContentDescription(charSequenceMatcher)

fun DataInteraction.contentDescriptionContains(text: String) =
    UltronEspressoInteraction(this).contentDescriptionContains(text)

fun DataInteraction.assertMatches(condition: Matcher<View>) =
    UltronEspressoInteraction(this).assertMatches(condition)
