package com.atiurin.ultron.extensions

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
import com.atiurin.ultron.core.common.assertion.OperationAssertion
import com.atiurin.ultron.core.common.options.*
import com.atiurin.ultron.core.compose.nodeinteraction.*
import com.atiurin.ultron.core.compose.operation.ComposeOperationResult
import com.atiurin.ultron.core.compose.operation.UltronComposeOperation
import com.atiurin.ultron.core.compose.option.ComposeSwipeOption

fun SemanticsMatcher.isSuccess(action: SemanticsMatcher.() -> Unit) = UltronComposeSemanticsNodeInteraction(this).isSuccess { action() }
fun SemanticsMatcher.withTimeout(timeoutMs: Long) = UltronComposeSemanticsNodeInteraction(this, timeoutMs = timeoutMs)
fun SemanticsMatcher.withResultHandler(resultHandler: ((ComposeOperationResult<UltronComposeOperation>) -> Unit)) =
    UltronComposeSemanticsNodeInteraction(this, resultHandler = resultHandler)
fun SemanticsMatcher.withUseUnmergedTree(value: Boolean) = UltronComposeSemanticsNodeInteraction(this, useUnmergedTree = value )
fun SemanticsMatcher.withAssertion(assertion: OperationAssertion) = UltronComposeSemanticsNodeInteraction(this, assertion = assertion)
fun SemanticsMatcher.withAssertion(name: String = "", isListened: Boolean = false, block: () -> Unit) =
    UltronComposeSemanticsNodeInteraction(this).withAssertion(name, isListened, block)

fun SemanticsMatcher.getText() = UltronComposeSemanticsNodeInteraction(this).getText()
fun SemanticsMatcher.click(option: ClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).click(option)
fun SemanticsMatcher.clickCenterLeft(option: ClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).clickCenterLeft(option)
fun SemanticsMatcher.clickCenterRight(option: ClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).clickCenterRight(option)
fun SemanticsMatcher.clickTopCenter(option: ClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).clickTopCenter(option)
fun SemanticsMatcher.clickTopLeft(option: ClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).clickTopLeft(option)
fun SemanticsMatcher.clickTopRight(option: ClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).clickTopRight(option)
fun SemanticsMatcher.clickBottomCenter(option: ClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).clickBottomCenter(option)
fun SemanticsMatcher.clickBottomLeft(option: ClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).clickBottomLeft(option)
fun SemanticsMatcher.clickBottomRight(option: ClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).clickBottomRight(option)
fun SemanticsMatcher.longClick(option: LongClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).longClick(option)
fun SemanticsMatcher.longClickCenterLeft(option: LongClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).longClickCenterLeft(option)
fun SemanticsMatcher.longClickCenterRight(option: LongClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).longClickCenterRight(option)
fun SemanticsMatcher.longClickTopCenter(option: LongClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).longClickTopCenter(option)
fun SemanticsMatcher.longClickTopLeft(option: LongClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).longClickTopLeft(option)
fun SemanticsMatcher.longClickTopRight(option: LongClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).longClickTopRight(option)
fun SemanticsMatcher.longClickBottomCenter(option: LongClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).longClickBottomCenter(option)
fun SemanticsMatcher.longClickBottomLeft(option: LongClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).longClickBottomLeft(option)
fun SemanticsMatcher.longClickBottomRight(option: LongClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).longClickBottomRight(option)
fun SemanticsMatcher.doubleClick(option: DoubleClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).doubleClick(option)
fun SemanticsMatcher.doubleClickCenterLeft(option: DoubleClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).doubleClickCenterLeft(option)
fun SemanticsMatcher.doubleClickCenterRight(option: DoubleClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).doubleClickCenterRight(option)
fun SemanticsMatcher.doubleClickTopCenter(option: DoubleClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).doubleClickTopCenter(option)
fun SemanticsMatcher.doubleClickTopLeft(option: DoubleClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).doubleClickTopLeft(option)
fun SemanticsMatcher.doubleClickTopRight(option: DoubleClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).doubleClickTopRight(option)
fun SemanticsMatcher.doubleClickBottomCenter(option: DoubleClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).doubleClickBottomCenter(option)
fun SemanticsMatcher.doubleClickBottomLeft(option: DoubleClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).doubleClickBottomLeft(option)
fun SemanticsMatcher.doubleClickBottomRight(option: DoubleClickOption? = null) = UltronComposeSemanticsNodeInteraction(this).doubleClickBottomRight(option)
fun SemanticsMatcher.swipeDown(option: ComposeSwipeOption? = null) = UltronComposeSemanticsNodeInteraction(this).swipeDown(option)
fun SemanticsMatcher.swipeUp(option: ComposeSwipeOption? = null) = UltronComposeSemanticsNodeInteraction(this).swipeUp(option)
fun SemanticsMatcher.swipeLeft(option: ComposeSwipeOption? = null) = UltronComposeSemanticsNodeInteraction(this).swipeLeft(option)
fun SemanticsMatcher.swipeRight(option: ComposeSwipeOption? = null) = UltronComposeSemanticsNodeInteraction(this).swipeRight(option)
fun SemanticsMatcher.scrollTo() = UltronComposeSemanticsNodeInteraction(this).scrollTo()
fun SemanticsMatcher.scrollToIndex(index: Int) = UltronComposeSemanticsNodeInteraction(this).scrollToIndex(index)
fun SemanticsMatcher.scrollToKey(key: String) = UltronComposeSemanticsNodeInteraction(this).scrollToKey(key)
fun SemanticsMatcher.scrollToNode(matcher: SemanticsMatcher) = UltronComposeSemanticsNodeInteraction(this).scrollToNode(matcher)
fun SemanticsMatcher.imeAction() = UltronComposeSemanticsNodeInteraction(this).imeAction()
fun SemanticsMatcher.pressKey(keyEvent: KeyEvent) = UltronComposeSemanticsNodeInteraction(this).pressKey(keyEvent)
fun SemanticsMatcher.inputText(text: String) = UltronComposeSemanticsNodeInteraction(this).inputText(text)
fun SemanticsMatcher.typeText(text: String) = UltronComposeSemanticsNodeInteraction(this).typeText(text)
fun SemanticsMatcher.inputTextSelection(selection: TextRange) = UltronComposeSemanticsNodeInteraction(this).inputTextSelection(selection)
fun SemanticsMatcher.setSelection(startIndex: Int = 0, endIndex: Int = 0, traversalMode: Boolean) =
    UltronComposeSemanticsNodeInteraction(this).setSelection(startIndex, endIndex, traversalMode)
fun SemanticsMatcher.selectText(range: TextRange) = UltronComposeSemanticsNodeInteraction(this).selectText(range)
fun SemanticsMatcher.clearText() = UltronComposeSemanticsNodeInteraction(this).clearText()
fun SemanticsMatcher.replaceText(text: String) = UltronComposeSemanticsNodeInteraction(this).replaceText(text)
fun SemanticsMatcher.copyText() = UltronComposeSemanticsNodeInteraction(this).copyText()
fun SemanticsMatcher.pasteText() = UltronComposeSemanticsNodeInteraction(this).pasteText()
fun SemanticsMatcher.cutText() = UltronComposeSemanticsNodeInteraction(this).cutText()
fun SemanticsMatcher.setText(text: String) = UltronComposeSemanticsNodeInteraction(this).setText(text)
fun SemanticsMatcher.setText(text: AnnotatedString) = UltronComposeSemanticsNodeInteraction(this).setText(text)
fun SemanticsMatcher.collapse() = UltronComposeSemanticsNodeInteraction(this).collapse()
fun SemanticsMatcher.expand() = UltronComposeSemanticsNodeInteraction(this).expand()
fun SemanticsMatcher.dismiss() = UltronComposeSemanticsNodeInteraction(this).dismiss()
fun SemanticsMatcher.setProgress(value: Float) = UltronComposeSemanticsNodeInteraction(this).setProgress(value)
fun SemanticsMatcher.captureToImage(): ImageBitmap = UltronComposeSemanticsNodeInteraction(this).captureToImage()


@OptIn(ExperimentalTestApi::class)
fun SemanticsMatcher.performMouseInput(block: MouseInjectionScope.() -> Unit) = UltronComposeSemanticsNodeInteraction(this).performMouseInput(block)
fun SemanticsMatcher.performSemanticsAction(key: SemanticsPropertyKey<AccessibilityAction<() -> Boolean>>) =
    UltronComposeSemanticsNodeInteraction(this).performSemanticsAction(key)
fun <T> SemanticsMatcher.perform(block: (SemanticsNodeInteraction) -> T, option: PerformCustomBlockOption? = null) =
    UltronComposeSemanticsNodeInteraction(this).perform(option, block)

//asserts
fun SemanticsMatcher.assertIsDisplayed() = UltronComposeSemanticsNodeInteraction(this).assertIsDisplayed()
fun SemanticsMatcher.assertIsNotDisplayed() = UltronComposeSemanticsNodeInteraction(this).assertIsNotDisplayed()
fun SemanticsMatcher.assertExists() = UltronComposeSemanticsNodeInteraction(this).assertExists()
fun SemanticsMatcher.assertDoesNotExist() = UltronComposeSemanticsNodeInteraction(this).assertDoesNotExist()
fun SemanticsMatcher.assertIsEnabled() = UltronComposeSemanticsNodeInteraction(this).assertIsEnabled()
fun SemanticsMatcher.assertIsNotEnabled() = UltronComposeSemanticsNodeInteraction(this).assertIsNotEnabled()
fun SemanticsMatcher.assertIsFocused() = UltronComposeSemanticsNodeInteraction(this).assertIsFocused()
fun SemanticsMatcher.assertIsNotFocused() = UltronComposeSemanticsNodeInteraction(this).assertIsNotFocused()
fun SemanticsMatcher.assertIsSelected() = UltronComposeSemanticsNodeInteraction(this).assertIsSelected()
fun SemanticsMatcher.assertIsNotSelected() = UltronComposeSemanticsNodeInteraction(this).assertIsNotSelected()
fun SemanticsMatcher.assertIsSelectable() = UltronComposeSemanticsNodeInteraction(this).assertIsSelectable()
fun SemanticsMatcher.assertIsOn() = UltronComposeSemanticsNodeInteraction(this).assertIsOn()
fun SemanticsMatcher.assertIsOff() = UltronComposeSemanticsNodeInteraction(this).assertIsOff()
fun SemanticsMatcher.assertIsToggleable() = UltronComposeSemanticsNodeInteraction(this).assertIsToggleable()
fun SemanticsMatcher.assertHasClickAction() = UltronComposeSemanticsNodeInteraction(this).assertHasClickAction()
fun SemanticsMatcher.assertHasNoClickAction() = UltronComposeSemanticsNodeInteraction(this).assertHasNoClickAction()
fun SemanticsMatcher.assertTextEquals(vararg expected: String, option: TextEqualsOption? = null) =
    UltronComposeSemanticsNodeInteraction(this).assertTextEquals(*expected, option = option)
fun SemanticsMatcher.assertTextContains(expected: String, option: TextContainsOption? = null) =
    UltronComposeSemanticsNodeInteraction(this).assertTextContains(expected, option)
fun SemanticsMatcher.assertContentDescriptionEquals(vararg expected: String) = UltronComposeSemanticsNodeInteraction(this).assertContentDescriptionEquals(*expected)
fun SemanticsMatcher.assertContentDescriptionContains(expected: String, option: ContentDescriptionContainsOption? = null) =
    UltronComposeSemanticsNodeInteraction(this).assertContentDescriptionContains(expected, option)

fun SemanticsMatcher.assertValueEquals(expected: String) = UltronComposeSemanticsNodeInteraction(this).assertValueEquals(expected)
fun SemanticsMatcher.assertRangeInfoEquals(range: ProgressBarRangeInfo) = UltronComposeSemanticsNodeInteraction(this).assertRangeInfoEquals(range)
fun SemanticsMatcher.assertHeightIsAtLeast(minHeight: Dp) = UltronComposeSemanticsNodeInteraction(this).assertHeightIsAtLeast(minHeight)
fun SemanticsMatcher.assertHeightIsEqualTo(expectedHeight: Dp) = UltronComposeSemanticsNodeInteraction(this).assertHeightIsEqualTo(expectedHeight)
fun SemanticsMatcher.assertWidthIsAtLeast(minWidth: Dp) = UltronComposeSemanticsNodeInteraction(this).assertWidthIsAtLeast(minWidth)
fun SemanticsMatcher.assertWidthIsEqualTo(expectedWidth: Dp) = UltronComposeSemanticsNodeInteraction(this).assertWidthIsEqualTo(expectedWidth)
fun SemanticsMatcher.assertMatches(matcher: SemanticsMatcher, messagePrefixOnError: (() -> String)? = null) =
    UltronComposeSemanticsNodeInteraction(this).assertMatches(matcher, messagePrefixOnError)