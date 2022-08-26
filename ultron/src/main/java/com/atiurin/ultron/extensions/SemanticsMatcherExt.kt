package com.atiurin.ultron.extensions

import android.view.View
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.semantics.AccessibilityAction
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.MouseInjectionScope
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.Dp
import androidx.test.espresso.Espresso
import com.atiurin.ultron.core.common.options.*
import com.atiurin.ultron.core.compose.UltronComposeSemanticsMatcher
import com.atiurin.ultron.core.compose.operation.ComposeOperationResult
import com.atiurin.ultron.core.compose.operation.UltronComposeOperation
import com.atiurin.ultron.core.compose.option.ComposeSwipeOption
import org.hamcrest.Matcher

fun SemanticsMatcher.isSuccess(action: SemanticsMatcher.() -> Unit) = UltronComposeSemanticsMatcher(this).isSuccess { action() }
fun SemanticsMatcher.withTimeout(timeoutMs: Long) = UltronComposeSemanticsMatcher(this, timeoutMs = timeoutMs)
fun SemanticsMatcher.withResultHandler(resultHandler: ((ComposeOperationResult<UltronComposeOperation>) -> Unit)) = UltronComposeSemanticsMatcher(this, resultHandler = resultHandler)
fun SemanticsMatcher.withUseUnmergedTree(value: Boolean) = UltronComposeSemanticsMatcher(this, useUnmergedTree = value)
fun SemanticsMatcher.getText() = UltronComposeSemanticsMatcher(this).getText()
fun SemanticsMatcher.click(option: ClickOption? = null) = UltronComposeSemanticsMatcher(this).click(option)
fun SemanticsMatcher.clickCenterLeft(option: ClickOption? = null) = UltronComposeSemanticsMatcher(this).clickCenterLeft(option)
fun SemanticsMatcher.clickCenterRight(option: ClickOption? = null) = UltronComposeSemanticsMatcher(this).clickCenterRight(option)
fun SemanticsMatcher.clickTopCenter(option: ClickOption? = null) = UltronComposeSemanticsMatcher(this).clickTopCenter(option)
fun SemanticsMatcher.clickTopLeft(option: ClickOption? = null) = UltronComposeSemanticsMatcher(this).clickTopLeft(option)
fun SemanticsMatcher.clickTopRight(option: ClickOption? = null) = UltronComposeSemanticsMatcher(this).clickTopRight(option)
fun SemanticsMatcher.clickBottomCenter(option: ClickOption? = null) = UltronComposeSemanticsMatcher(this).clickBottomCenter(option)
fun SemanticsMatcher.clickBottomLeft(option: ClickOption? = null) = UltronComposeSemanticsMatcher(this).clickBottomLeft(option)
fun SemanticsMatcher.clickBottomRight(option: ClickOption? = null) = UltronComposeSemanticsMatcher(this).clickBottomRight(option)
fun SemanticsMatcher.longClick(option: LongClickOption? = null) = UltronComposeSemanticsMatcher(this).longClick(option)
fun SemanticsMatcher.longClickCenterLeft(option: LongClickOption? = null) = UltronComposeSemanticsMatcher(this).longClickCenterLeft(option)
fun SemanticsMatcher.longClickCenterRight(option: LongClickOption? = null) = UltronComposeSemanticsMatcher(this).longClickCenterRight(option)
fun SemanticsMatcher.longClickTopCenter(option: LongClickOption? = null) = UltronComposeSemanticsMatcher(this).longClickTopCenter(option)
fun SemanticsMatcher.longClickTopLeft(option: LongClickOption? = null) = UltronComposeSemanticsMatcher(this).longClickTopLeft(option)
fun SemanticsMatcher.longClickTopRight(option: LongClickOption? = null) = UltronComposeSemanticsMatcher(this).longClickTopRight(option)
fun SemanticsMatcher.longClickBottomCenter(option: LongClickOption? = null) = UltronComposeSemanticsMatcher(this).longClickBottomCenter(option)
fun SemanticsMatcher.longClickBottomLeft(option: LongClickOption? = null) = UltronComposeSemanticsMatcher(this).longClickBottomLeft(option)
fun SemanticsMatcher.longClickBottomRight(option: LongClickOption? = null) = UltronComposeSemanticsMatcher(this).longClickBottomRight(option)
fun SemanticsMatcher.doubleClick(option: DoubleClickOption? = null) = UltronComposeSemanticsMatcher(this).doubleClick(option)
fun SemanticsMatcher.doubleClickCenterLeft(option: DoubleClickOption? = null) = UltronComposeSemanticsMatcher(this).doubleClickCenterLeft(option)
fun SemanticsMatcher.doubleClickCenterRight(option: DoubleClickOption? = null) = UltronComposeSemanticsMatcher(this).doubleClickCenterRight(option)
fun SemanticsMatcher.doubleClickTopCenter(option: DoubleClickOption? = null) = UltronComposeSemanticsMatcher(this).doubleClickTopCenter(option)
fun SemanticsMatcher.doubleClickTopLeft(option: DoubleClickOption? = null) = UltronComposeSemanticsMatcher(this).doubleClickTopLeft(option)
fun SemanticsMatcher.doubleClickTopRight(option: DoubleClickOption? = null) = UltronComposeSemanticsMatcher(this).doubleClickTopRight(option)
fun SemanticsMatcher.doubleClickBottomCenter(option: DoubleClickOption? = null) = UltronComposeSemanticsMatcher(this).doubleClickBottomCenter(option)
fun SemanticsMatcher.doubleClickBottomLeft(option: DoubleClickOption? = null) = UltronComposeSemanticsMatcher(this).doubleClickBottomLeft(option)
fun SemanticsMatcher.doubleClickBottomRight(option: DoubleClickOption? = null) = UltronComposeSemanticsMatcher(this).doubleClickBottomRight(option)
fun SemanticsMatcher.swipeDown(option: ComposeSwipeOption? = null) = UltronComposeSemanticsMatcher(this).swipeDown(option)
fun SemanticsMatcher.swipeUp(option: ComposeSwipeOption? = null) = UltronComposeSemanticsMatcher(this).swipeUp(option)
fun SemanticsMatcher.swipeLeft(option: ComposeSwipeOption? = null) = UltronComposeSemanticsMatcher(this).swipeLeft(option)
fun SemanticsMatcher.swipeRight(option: ComposeSwipeOption? = null) = UltronComposeSemanticsMatcher(this).swipeRight(option)
fun SemanticsMatcher.scrollTo() = UltronComposeSemanticsMatcher(this).scrollTo()
fun SemanticsMatcher.scrollToIndex(index: Int) = UltronComposeSemanticsMatcher(this).scrollToIndex(index)
fun SemanticsMatcher.scrollToKey(key: String) = UltronComposeSemanticsMatcher(this).scrollToKey(key)
fun SemanticsMatcher.scrollToNode(matcher: SemanticsMatcher) = UltronComposeSemanticsMatcher(this).scrollToNode(matcher)
fun SemanticsMatcher.imeAction() = UltronComposeSemanticsMatcher(this).imeAction()
fun SemanticsMatcher.pressKey(keyEvent: KeyEvent) = UltronComposeSemanticsMatcher(this).pressKey(keyEvent)
fun SemanticsMatcher.inputText(text: String) = UltronComposeSemanticsMatcher(this).inputText(text)
fun SemanticsMatcher.typeText(text: String) = UltronComposeSemanticsMatcher(this).typeText(text)
fun SemanticsMatcher.inputTextSelection(selection: TextRange) = UltronComposeSemanticsMatcher(this).inputTextSelection(selection)
fun SemanticsMatcher.setSelection(startIndex: Int = 0, endIndex: Int = 0, traversalMode: Boolean) = UltronComposeSemanticsMatcher(this).setSelection(startIndex, endIndex, traversalMode)
fun SemanticsMatcher.selectText(range: TextRange) = UltronComposeSemanticsMatcher(this).selectText(range)
fun SemanticsMatcher.clearText() = UltronComposeSemanticsMatcher(this).clearText()
fun SemanticsMatcher.replaceText(text: String) = UltronComposeSemanticsMatcher(this).replaceText(text)
fun SemanticsMatcher.copyText() = UltronComposeSemanticsMatcher(this).copyText()
fun SemanticsMatcher.pasteText() = UltronComposeSemanticsMatcher(this).pasteText()
fun SemanticsMatcher.cutText() = UltronComposeSemanticsMatcher(this).cutText()
fun SemanticsMatcher.setText(text: String) = UltronComposeSemanticsMatcher(this).setText(text)
fun SemanticsMatcher.setText(text: AnnotatedString) = UltronComposeSemanticsMatcher(this).setText(text)
fun SemanticsMatcher.collapse() = UltronComposeSemanticsMatcher(this).collapse()
fun SemanticsMatcher.expand() = UltronComposeSemanticsMatcher(this).expand()
fun SemanticsMatcher.dismiss() = UltronComposeSemanticsMatcher(this).dismiss()
fun SemanticsMatcher.setProgress(value: Float) = UltronComposeSemanticsMatcher(this).setProgress(value)
fun SemanticsMatcher.captureToImage(): ImageBitmap = UltronComposeSemanticsMatcher(this).captureToImage()


@OptIn(ExperimentalTestApi::class)
fun SemanticsMatcher.performMouseInput(block: MouseInjectionScope.() -> Unit) = UltronComposeSemanticsMatcher(this).performMouseInput(block)
fun SemanticsMatcher.performSemanticsAction(key: SemanticsPropertyKey<AccessibilityAction<() -> Boolean>>) = UltronComposeSemanticsMatcher(this).performSemanticsAction(key)
fun <T> SemanticsMatcher.perform(block: (SemanticsNodeInteraction) -> T, option: PerformCustomBlockOption? = null) = UltronComposeSemanticsMatcher(this).perform(option, block)

//asserts
fun SemanticsMatcher.assertIsDisplayed() = UltronComposeSemanticsMatcher(this).assertIsDisplayed()
fun SemanticsMatcher.assertIsNotDisplayed() = UltronComposeSemanticsMatcher(this).assertIsNotDisplayed()
fun SemanticsMatcher.assertExists() = UltronComposeSemanticsMatcher(this).assertExists()
fun SemanticsMatcher.assertDoesNotExist() = UltronComposeSemanticsMatcher(this).assertDoesNotExist()
fun SemanticsMatcher.assertIsEnabled() = UltronComposeSemanticsMatcher(this).assertIsEnabled()
fun SemanticsMatcher.assertIsNotEnabled() = UltronComposeSemanticsMatcher(this).assertIsNotEnabled()
fun SemanticsMatcher.assertIsFocused() = UltronComposeSemanticsMatcher(this).assertIsFocused()
fun SemanticsMatcher.assertIsNotFocused() = UltronComposeSemanticsMatcher(this).assertIsNotFocused()
fun SemanticsMatcher.assertIsSelected() = UltronComposeSemanticsMatcher(this).assertIsSelected()
fun SemanticsMatcher.assertIsNotSelected() = UltronComposeSemanticsMatcher(this).assertIsNotSelected()
fun SemanticsMatcher.assertIsSelectable() = UltronComposeSemanticsMatcher(this).assertIsSelectable()
fun SemanticsMatcher.assertIsOn() = UltronComposeSemanticsMatcher(this).assertIsOn()
fun SemanticsMatcher.assertIsOff() = UltronComposeSemanticsMatcher(this).assertIsOff()
fun SemanticsMatcher.assertIsToggleable() = UltronComposeSemanticsMatcher(this).assertIsToggleable()
fun SemanticsMatcher.assertHasClickAction() = UltronComposeSemanticsMatcher(this).assertHasClickAction()
fun SemanticsMatcher.assertHasNoClickAction() = UltronComposeSemanticsMatcher(this).assertHasNoClickAction()
fun SemanticsMatcher.assertTextEquals(vararg expected: String, option: TextEqualsOption? = null) = UltronComposeSemanticsMatcher(this).assertTextEquals(*expected, option = option)
fun SemanticsMatcher.assertTextContains(expected: String, option: TextContainsOption? = null) = UltronComposeSemanticsMatcher(this).assertTextContains(expected, option)
fun SemanticsMatcher.assertContentDescriptionEquals(vararg expected: String) = UltronComposeSemanticsMatcher(this).assertContentDescriptionEquals(*expected)
fun SemanticsMatcher.assertContentDescriptionContains(expected: String, option: ContentDescriptionContainsOption? = null) =
    UltronComposeSemanticsMatcher(this).assertContentDescriptionContains(expected, option)

fun SemanticsMatcher.assertValueEquals(expected: String) = UltronComposeSemanticsMatcher(this).assertValueEquals(expected)
fun SemanticsMatcher.assertRangeInfoEquals(range: ProgressBarRangeInfo) = UltronComposeSemanticsMatcher(this).assertRangeInfoEquals(range)
fun SemanticsMatcher.assertHeightIsAtLeast(minHeight: Dp) = UltronComposeSemanticsMatcher(this).assertHeightIsAtLeast(minHeight)
fun SemanticsMatcher.assertHeightIsEqualTo(expectedHeight: Dp) = UltronComposeSemanticsMatcher(this).assertHeightIsEqualTo(expectedHeight)
fun SemanticsMatcher.assertWidthIsAtLeast(minWidth: Dp) = UltronComposeSemanticsMatcher(this).assertWidthIsAtLeast(minWidth)
fun SemanticsMatcher.assertWidthIsEqualTo(expectedWidth: Dp) = UltronComposeSemanticsMatcher(this).assertWidthIsEqualTo(expectedWidth)
fun SemanticsMatcher.assertMatches(matcher: SemanticsMatcher, messagePrefixOnError: (() -> String)? = null) =
    UltronComposeSemanticsMatcher(this).assertMatches(matcher, messagePrefixOnError)