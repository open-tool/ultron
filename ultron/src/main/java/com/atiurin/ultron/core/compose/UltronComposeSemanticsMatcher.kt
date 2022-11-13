//package com.atiurin.ultron.core.compose
//
//import androidx.compose.ui.graphics.ImageBitmap
//import androidx.compose.ui.input.key.KeyEvent
//import androidx.compose.ui.semantics.AccessibilityAction
//import androidx.compose.ui.semantics.ProgressBarRangeInfo
//import androidx.compose.ui.semantics.SemanticsPropertyKey
//import androidx.compose.ui.test.*
//import androidx.compose.ui.text.AnnotatedString
//import androidx.compose.ui.text.TextRange
//import androidx.compose.ui.unit.Dp
//import com.atiurin.ultron.core.common.assertion.DefaultOperationAssertion
//import com.atiurin.ultron.core.common.assertion.EmptyOperationAssertion
//import com.atiurin.ultron.core.common.assertion.OperationAssertion
//import com.atiurin.ultron.core.common.options.*
//import com.atiurin.ultron.core.compose.ComposeRuleContainer.getComposeRule
//import com.atiurin.ultron.core.compose.nodeinteraction.*
//import com.atiurin.ultron.core.compose.operation.ComposeOperationResult
//import com.atiurin.ultron.core.compose.operation.UltronComposeOperation
//import com.atiurin.ultron.core.compose.option.ComposeSwipeOption
//import com.atiurin.ultron.core.config.UltronConfig
//import com.atiurin.ultron.core.espresso.UltronEspressoInteraction
//import com.atiurin.ultron.listeners.setListenersState
//
//
//open class UltronComposeSemanticsMatcher(
//    val matcher: SemanticsMatcher,
//    val useUnmergedTree: Boolean = false,
//    val timeoutMs: Long = UltronConfig.Compose.OPERATION_TIMEOUT,
//    val resultHandler: ((ComposeOperationResult<UltronComposeOperation>) -> Unit) = UltronConfig.Compose.resultHandler,
//    val assertion: OperationAssertion = EmptyOperationAssertion()
//) {
//    fun <T> isSuccess(action: UltronComposeSemanticsMatcher.() -> T): Boolean = getUltronComposeInteraction().isSuccess { action() }
//    fun withResultHandler(handler: (ComposeOperationResult<UltronComposeOperation>) -> Unit) = getUltronComposeInteraction().withResultHandler(handler)
//    fun withAssertion(assertion: OperationAssertion) = getUltronComposeInteraction().withAssertion(assertion)
//    fun withAssertion(name: String = "", isListened: Boolean = false, block: () -> Unit) = getUltronComposeInteraction().withAssertion(name, isListened, block)
//    fun withTimeout(timeoutMs: Long) = UltronComposeSemanticsMatcher(matcher, useUnmergedTree, timeoutMs, resultHandler, assertion)
//    fun withUseUnmergedTree(value: Boolean) = UltronComposeSemanticsMatcher(matcher, value, timeoutMs, resultHandler, assertion)
//    fun click(option: ClickOption? = null) = getUltronComposeInteraction().click(option)
//    fun clickCenterLeft(option: ClickOption? = null) = getUltronComposeInteraction().clickCenterLeft(option)
//    fun clickCenterRight(option: ClickOption? = null) = getUltronComposeInteraction().clickCenterRight(option)
//    fun clickTopCenter(option: ClickOption? = null) = getUltronComposeInteraction().clickTopCenter(option)
//    fun clickTopLeft(option: ClickOption? = null) = getUltronComposeInteraction().clickTopLeft(option)
//    fun clickTopRight(option: ClickOption? = null) = getUltronComposeInteraction().clickTopRight(option)
//    fun clickBottomCenter(option: ClickOption? = null) = getUltronComposeInteraction().clickBottomCenter(option)
//    fun clickBottomLeft(option: ClickOption? = null) = getUltronComposeInteraction().clickBottomLeft(option)
//    fun clickBottomRight(option: ClickOption? = null) = getUltronComposeInteraction().clickBottomRight(option)
//
//    fun longClick(option: LongClickOption? = null) = getUltronComposeInteraction().longClick(option)
//    fun longClickCenterLeft(option: LongClickOption? = null) = getUltronComposeInteraction().longClickCenterLeft(option)
//    fun longClickCenterRight(option: LongClickOption? = null) = getUltronComposeInteraction().longClickCenterRight(option)
//    fun longClickTopCenter(option: LongClickOption? = null) = getUltronComposeInteraction().longClickTopCenter(option)
//    fun longClickTopLeft(option: LongClickOption? = null) = getUltronComposeInteraction().longClickTopLeft(option)
//    fun longClickTopRight(option: LongClickOption? = null) = getUltronComposeInteraction().longClickTopRight(option)
//    fun longClickBottomCenter(option: LongClickOption? = null) = getUltronComposeInteraction().longClickBottomCenter(option)
//    fun longClickBottomLeft(option: LongClickOption? = null) = getUltronComposeInteraction().longClickBottomLeft(option)
//    fun longClickBottomRight(option: LongClickOption? = null) = getUltronComposeInteraction().longClickBottomRight(option)
//
//    fun doubleClick(option: DoubleClickOption? = null) = getUltronComposeInteraction().doubleClick(option)
//    fun doubleClickCenterLeft(option: DoubleClickOption? = null) = getUltronComposeInteraction().doubleClickCenterLeft(option)
//    fun doubleClickCenterRight(option: DoubleClickOption? = null) = getUltronComposeInteraction().doubleClickCenterRight(option)
//    fun doubleClickTopCenter(option: DoubleClickOption? = null) = getUltronComposeInteraction().doubleClickTopCenter(option)
//    fun doubleClickTopLeft(option: DoubleClickOption? = null) = getUltronComposeInteraction().doubleClickTopLeft(option)
//    fun doubleClickTopRight(option: DoubleClickOption? = null) = getUltronComposeInteraction().doubleClickTopRight(option)
//    fun doubleClickBottomCenter(option: DoubleClickOption? = null) = getUltronComposeInteraction().doubleClickBottomCenter(option)
//    fun doubleClickBottomLeft(option: DoubleClickOption? = null) = getUltronComposeInteraction().doubleClickBottomLeft(option)
//    fun doubleClickBottomRight(option: DoubleClickOption? = null) = getUltronComposeInteraction().doubleClickBottomRight(option)
//
//    fun swipeDown(option: ComposeSwipeOption? = null) = getUltronComposeInteraction().swipeDown(option)
//    fun swipeUp(option: ComposeSwipeOption? = null) = getUltronComposeInteraction().swipeUp(option)
//    fun swipeLeft(option: ComposeSwipeOption? = null) = getUltronComposeInteraction().swipeLeft(option)
//    fun swipeRight(option: ComposeSwipeOption? = null) = getUltronComposeInteraction().swipeRight(option)
//
//    fun scrollTo() = getUltronComposeInteraction().scrollTo()
//    fun scrollToIndex(index: Int) = getUltronComposeInteraction().scrollToIndex(index)
//    fun scrollToKey(key: String) = getUltronComposeInteraction().scrollToKey(key)
//    fun scrollToNode(matcher: SemanticsMatcher) = getUltronComposeInteraction().scrollToNode(matcher)
//    fun imeAction() = getUltronComposeInteraction().imeAction()
//    fun pressKey(keyEvent: KeyEvent) = getUltronComposeInteraction().pressKey(keyEvent)
//
//    // text
//    fun getText(): String? = getUltronComposeInteraction().getText()
//    fun inputText(text: String) = getUltronComposeInteraction().inputText(text)
//    fun typeText(text: String) = getUltronComposeInteraction().typeText(text)
//    fun inputTextSelection(selection: TextRange) = getUltronComposeInteraction().inputTextSelection(selection)
//    fun selectText(range: TextRange) = getUltronComposeInteraction().selectText(range)
//    fun setSelection(startIndex: Int = 0, endIndex: Int = 0, traversalMode: Boolean) = getUltronComposeInteraction().setSelection(startIndex, endIndex, traversalMode)
//    fun clearText() = getUltronComposeInteraction().clearText()
//    fun replaceText(text: String) = getUltronComposeInteraction().replaceText(text)
//    fun copyText() = getUltronComposeInteraction().copyText()
//    fun pasteText() = getUltronComposeInteraction().pasteText()
//    fun cutText() = getUltronComposeInteraction().cutText()
//    fun setText(text: String) = getUltronComposeInteraction().setText(text)
//    fun setText(text: AnnotatedString) = getUltronComposeInteraction().setText(text)
//    fun collapse() = getUltronComposeInteraction().collapse()
//    fun expand() = getUltronComposeInteraction().expand()
//    fun dismiss() = getUltronComposeInteraction().dismiss()
//    fun setProgress(value: Float) = getUltronComposeInteraction().setProgress(value)
//    fun captureToImage(): ImageBitmap = getUltronComposeInteraction().captureToImage()
//
//    @OptIn(ExperimentalTestApi::class)
//    fun performMouseInput(block: MouseInjectionScope.() -> Unit) = getUltronComposeInteraction().performMouseInput(block)
//    fun performSemanticsAction(key: SemanticsPropertyKey<AccessibilityAction<() -> Boolean>>) = getUltronComposeInteraction().performSemanticsAction(key)
//    fun <T> perform(
//        option: PerformCustomBlockOption? = null,
//        block: (SemanticsNodeInteraction) -> T
//    ) = getUltronComposeInteraction().perform(option, block)
//
//    //assertions
//    fun assertIsDisplayed() = getUltronComposeInteraction().assertIsDisplayed()
//    fun assertIsNotDisplayed() = getUltronComposeInteraction().assertIsNotDisplayed()
//    fun assertExists() = getUltronComposeInteraction().assertExists()
//    fun assertDoesNotExist() = getUltronComposeInteraction().assertDoesNotExist()
//    fun assertIsEnabled() = getUltronComposeInteraction().assertIsEnabled()
//    fun assertIsNotEnabled() = getUltronComposeInteraction().assertIsNotEnabled()
//    fun assertIsFocused() = getUltronComposeInteraction().assertIsFocused()
//    fun assertIsNotFocused() = getUltronComposeInteraction().assertIsNotFocused()
//    fun assertIsSelected() = getUltronComposeInteraction().assertIsSelected()
//    fun assertIsNotSelected() = getUltronComposeInteraction().assertIsNotSelected()
//    fun assertIsSelectable() = getUltronComposeInteraction().assertIsSelectable()
//    fun assertIsOn() = getUltronComposeInteraction().assertIsOn()
//    fun assertIsOff() = getUltronComposeInteraction().assertIsOff()
//    fun assertIsToggleable() = getUltronComposeInteraction().assertIsToggleable()
//    fun assertHasClickAction() = getUltronComposeInteraction().assertHasClickAction()
//    fun assertHasNoClickAction() = getUltronComposeInteraction().assertHasNoClickAction()
//    fun assertTextEquals(vararg expected: String, option: TextEqualsOption? = null) = getUltronComposeInteraction().assertTextEquals(*expected, option = option)
//    fun assertTextContains(expected: String, option: TextContainsOption? = null) = getUltronComposeInteraction().assertTextContains(expected, option)
//    fun assertContentDescriptionEquals(vararg expected: String) = getUltronComposeInteraction().assertContentDescriptionEquals(*expected)
//    fun assertContentDescriptionContains(expected: String, option: ContentDescriptionContainsOption? = null) =
//        getUltronComposeInteraction().assertContentDescriptionContains(expected, option)
//
//    fun assertValueEquals(expected: String) = getUltronComposeInteraction().assertValueEquals(expected)
//    fun assertRangeInfoEquals(range: ProgressBarRangeInfo) = getUltronComposeInteraction().assertRangeInfoEquals(range)
//    fun assertHeightIsAtLeast(minHeight: Dp) = getUltronComposeInteraction().assertHeightIsAtLeast(minHeight)
//    fun assertHeightIsEqualTo(expectedHeight: Dp) = getUltronComposeInteraction().assertHeightIsEqualTo(expectedHeight)
//    fun assertWidthIsAtLeast(minWidth: Dp) = getUltronComposeInteraction().assertWidthIsAtLeast(minWidth)
//    fun assertWidthIsEqualTo(expectedWidth: Dp) = getUltronComposeInteraction().assertWidthIsEqualTo(expectedWidth)
//    fun assertMatches(matcher: SemanticsMatcher, messagePrefixOnError: (() -> String)? = null) =
//        getUltronComposeInteraction().assertMatches(matcher, messagePrefixOnError)
//
//    open fun getUltronComposeInteraction() = UltronComposeSemanticsNodeInteraction(getComposeRule().onNode(matcher, useUnmergedTree), timeoutMs, resultHandler, assertion)
//}