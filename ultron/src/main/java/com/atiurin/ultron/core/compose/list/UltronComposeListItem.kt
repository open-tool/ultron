package com.atiurin.ultron.core.compose.list

import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.semantics.AccessibilityAction
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.MouseInjectionScope
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.Dp
import com.atiurin.ultron.core.common.options.*
import com.atiurin.ultron.core.compose.nodeinteraction.*
import com.atiurin.ultron.core.compose.operation.ComposeOperationResult
import com.atiurin.ultron.core.compose.operation.UltronComposeOperation
import com.atiurin.ultron.core.compose.option.ComposeSwipeOption
import com.atiurin.ultron.exceptions.UltronException

open class UltronComposeListItem {
    lateinit var executor: ComposeItemExecutor

    constructor(ultronComposeList: UltronComposeList, itemMatcher: SemanticsMatcher) {
        setExecutor(ultronComposeList, itemMatcher)
    }
    constructor(ultronComposeList: UltronComposeList, index: Int) {
        setExecutor(ultronComposeList, index)
    }

    /**
     * Use this constructor to inherit from [UltronComposeListItem]
     * Don't create an instance of subclass. Use [UltronComposeList.item] instead
     */
    protected constructor()

    fun setExecutor(ultronComposeList: UltronComposeList, itemMatcher: SemanticsMatcher) {
        this.executor = MatcherComposeItemExecutor(ultronComposeList, itemMatcher)
    }
    fun setExecutor(ultronComposeList: UltronComposeList, index: Int) {
        this.executor = IndexComposeItemExecutor(ultronComposeList, index)
    }
    fun withTimeout(timeoutMs: Long) = getItemUltronComposeInteraction().withTimeout(timeoutMs)
    fun withResultHandler(resultHandler: (ComposeOperationResult<UltronComposeOperation>) -> Unit) =
        getItemUltronComposeInteraction().withResultHandler(resultHandler)

    fun getChild(childMatcher: SemanticsMatcher): UltronComposeSemanticsNodeInteraction = executor.getItemChildInteraction(childMatcher)
    fun getItemUltronComposeInteraction(): UltronComposeSemanticsNodeInteraction = executor.getItemInteraction()
    fun scrollToItem(offset: Int = 0): UltronComposeListItem = apply { executor.scrollToItem(offset) }

    fun click(option: ClickOption? = null) = apply { getItemUltronComposeInteraction().click(option) }
    fun clickCenterLeft(option: ClickOption? = null) = apply { getItemUltronComposeInteraction().clickCenterLeft(option) }
    fun clickCenterRight(option: ClickOption? = null) = apply { getItemUltronComposeInteraction().clickCenterRight(option) }
    fun clickTopCenter(option: ClickOption? = null) = apply { getItemUltronComposeInteraction().clickTopCenter(option) }
    fun clickTopLeft(option: ClickOption? = null) = apply { getItemUltronComposeInteraction().clickTopLeft(option) }
    fun clickTopRight(option: ClickOption? = null) = apply { getItemUltronComposeInteraction().clickTopRight(option) }
    fun clickBottomCenter(option: ClickOption? = null) = apply { getItemUltronComposeInteraction().clickBottomCenter(option) }
    fun clickBottomLeft(option: ClickOption? = null) = apply { getItemUltronComposeInteraction().clickBottomLeft(option) }
    fun clickBottomRight(option: ClickOption? = null) = apply { getItemUltronComposeInteraction().clickBottomRight(option) }

    fun longClick(option: LongClickOption? = null) = apply { getItemUltronComposeInteraction().longClick(option) }
    fun longClickCenterLeft(option: LongClickOption? = null) = apply { getItemUltronComposeInteraction().longClickCenterLeft(option) }
    fun longClickCenterRight(option: LongClickOption? = null) = apply { getItemUltronComposeInteraction().longClickCenterRight(option) }
    fun longClickTopCenter(option: LongClickOption? = null) = apply { getItemUltronComposeInteraction().longClickTopCenter(option) }
    fun longClickTopLeft(option: LongClickOption? = null) = apply { getItemUltronComposeInteraction().longClickTopLeft(option) }
    fun longClickTopRight(option: LongClickOption? = null) = apply { getItemUltronComposeInteraction().longClickTopRight(option) }
    fun longClickBottomCenter(option: LongClickOption? = null) = apply { getItemUltronComposeInteraction().longClickBottomCenter(option) }
    fun longClickBottomLeft(option: LongClickOption? = null) = apply { getItemUltronComposeInteraction().longClickBottomLeft(option) }
    fun longClickBottomRight(option: LongClickOption? = null) = apply { getItemUltronComposeInteraction().longClickBottomRight(option) }

    fun doubleClick(option: DoubleClickOption? = null) = apply { getItemUltronComposeInteraction().doubleClick(option) }
    fun doubleClickCenterLeft(option: DoubleClickOption? = null) = apply { getItemUltronComposeInteraction().doubleClickCenterLeft(option) }
    fun doubleClickCenterRight(option: DoubleClickOption? = null) = apply { getItemUltronComposeInteraction().doubleClickCenterRight(option) }
    fun doubleClickTopCenter(option: DoubleClickOption? = null) = apply { getItemUltronComposeInteraction().doubleClickTopCenter(option) }
    fun doubleClickTopLeft(option: DoubleClickOption? = null) = apply { getItemUltronComposeInteraction().doubleClickTopLeft(option) }
    fun doubleClickTopRight(option: DoubleClickOption? = null) = apply { getItemUltronComposeInteraction().doubleClickTopRight(option) }
    fun doubleClickBottomCenter(option: DoubleClickOption? = null) = apply { getItemUltronComposeInteraction().doubleClickBottomCenter(option) }
    fun doubleClickBottomLeft(option: DoubleClickOption? = null) = apply { getItemUltronComposeInteraction().doubleClickBottomLeft(option) }
    fun doubleClickBottomRight(option: DoubleClickOption? = null) = apply { getItemUltronComposeInteraction().doubleClickBottomRight(option) }
    fun swipeDown(option: ComposeSwipeOption? = null) = apply { getItemUltronComposeInteraction().swipeDown(option) }
    fun swipeUp(option: ComposeSwipeOption? = null) = apply { getItemUltronComposeInteraction().swipeUp(option) }
    fun swipeLeft(option: ComposeSwipeOption? = null) = apply { getItemUltronComposeInteraction().swipeLeft(option) }
    fun swipeRight(option: ComposeSwipeOption? = null) = apply { getItemUltronComposeInteraction().swipeRight(option) }

    fun imeAction() =  apply { getItemUltronComposeInteraction().imeAction() }
    fun pressKey(keyEvent: KeyEvent) =  apply { getItemUltronComposeInteraction().pressKey(keyEvent) }
    fun getText(): String? = getItemUltronComposeInteraction().getText()
    fun inputText(text: String) =  apply { getItemUltronComposeInteraction().inputText(text) }
    fun inputTextSelection(selection: TextRange) =  apply {  getItemUltronComposeInteraction().inputTextSelection(selection) }
    fun clearText() =  apply { getItemUltronComposeInteraction().clearText() }
    fun replaceText(text: String) =  apply { getItemUltronComposeInteraction().replaceText(text) }

    @OptIn(ExperimentalTestApi::class)
    fun performMouseInput(block: MouseInjectionScope.() -> Unit) = apply { getItemUltronComposeInteraction().performMouseInput(block) }
    fun performSemanticsAction(key: SemanticsPropertyKey<AccessibilityAction<() -> Boolean>>) = apply { getItemUltronComposeInteraction().performSemanticsAction(key) }
    fun <T> perform(
        option: PerformCustomBlockOption? = null,
        block: (SemanticsNodeInteraction) -> T
    ) = getItemUltronComposeInteraction().perform(option, block)
    
    fun assertIsDisplayed() = apply { getItemUltronComposeInteraction().assertIsDisplayed() }
    fun assertIsNotDisplayed() = apply { getItemUltronComposeInteraction().assertIsNotDisplayed() }
    fun assertExists() = apply { getItemUltronComposeInteraction().assertExists() }
    fun assertDoesNotExist() = apply { getItemUltronComposeInteraction().assertDoesNotExist() }
    fun assertIsEnabled() = apply { getItemUltronComposeInteraction().assertIsEnabled() }
    fun assertIsNotEnabled() = apply { getItemUltronComposeInteraction().assertIsNotEnabled() }
    fun assertIsFocused() = apply { getItemUltronComposeInteraction().assertIsFocused() }
    fun assertIsNotFocused() = apply { getItemUltronComposeInteraction().assertIsNotFocused() }
    fun assertIsSelected() = apply { getItemUltronComposeInteraction().assertIsSelected() }
    fun assertIsNotSelected() = apply { getItemUltronComposeInteraction().assertIsNotSelected() }
    fun assertIsSelectable() = apply { getItemUltronComposeInteraction().assertIsSelectable() }
    fun assertIsOn() = apply { getItemUltronComposeInteraction().assertIsOn() }
    fun assertIsOff() = apply { getItemUltronComposeInteraction().assertIsOff() }
    fun assertIsToggleable() = apply { getItemUltronComposeInteraction().assertIsToggleable() }
    fun assertHasClickAction() = apply { getItemUltronComposeInteraction().assertHasClickAction() }
    fun assertHasNoClickAction() = apply { getItemUltronComposeInteraction().assertHasNoClickAction() }
    fun assertTextEquals(vararg expected: String, option: TextEqualsOption? = null) = apply { getItemUltronComposeInteraction().assertTextEquals(*expected, option = option) }
    fun assertTextContains(expected: String, option: TextContainsOption? = null) = apply { getItemUltronComposeInteraction().assertTextContains(expected, option) }
    fun assertContentDescriptionEquals(vararg expected: String) = apply { getItemUltronComposeInteraction().assertContentDescriptionEquals(*expected) }
    fun assertContentDescriptionContains(expected: String, option: ContentDescriptionContainsOption? = null) =
        apply { getItemUltronComposeInteraction().assertContentDescriptionContains(expected, option) }

    fun assertValueEquals(expected: String) = apply { getItemUltronComposeInteraction().assertValueEquals(expected) }
    fun assertRangeInfoEquals(range: ProgressBarRangeInfo) = apply { getItemUltronComposeInteraction().assertRangeInfoEquals(range) }
    fun assertHeightIsAtLeast(minHeight: Dp) = apply { getItemUltronComposeInteraction().assertHeightIsAtLeast(minHeight) }
    fun assertHeightIsEqualTo(expectedHeight: Dp) = apply { getItemUltronComposeInteraction().assertHeightIsEqualTo(expectedHeight) }
    fun assertWidthIsAtLeast(minWidth: Dp) = apply { getItemUltronComposeInteraction().assertWidthIsAtLeast(minWidth) }
    fun assertWidthIsEqualTo(expectedWidth: Dp) = apply { getItemUltronComposeInteraction().assertWidthIsEqualTo(expectedWidth) }
    fun assertMatches(matcher: SemanticsMatcher, messagePrefixOnError: (() -> String)? = null) =
        apply { getItemUltronComposeInteraction().assertMatches(matcher, messagePrefixOnError) }

    fun assertTextEquals(expected: String) = apply { getItemUltronComposeInteraction().assertTextEquals(expected) }
    fun assertTextContains(expected: String) = apply { getItemUltronComposeInteraction().assertTextContains(expected) }



    companion object {
        inline fun <reified T : UltronComposeListItem> getInstance(
            ultronComposeList: UltronComposeList,
            itemMatcher: SemanticsMatcher
        ): T {
            val item = this.createUltronComposeListItemInstance<T>()
            item.setExecutor(ultronComposeList, itemMatcher)
            return item
        }

        inline fun <reified T : UltronComposeListItem> getInstance(
            ultronComposeList: UltronComposeList,
            position: Int
        ): T {
            val item = this.createUltronComposeListItemInstance<T>()
            item.setExecutor(ultronComposeList, position)
            return item
        }

        inline fun <reified T : UltronComposeListItem> createUltronComposeListItemInstance(): T {
            return try {
                T::class.java.newInstance()
            } catch (ex: Exception) {
                val desc = when {
                    T::class.isInner -> {
                        "${T::class.simpleName} is an inner class so you have to delete inner modifier (It is often when kotlin throws 'has no zero argument constructor' but real reason is an inner modifier)"
                    }
                    T::class.constructors.find { it.parameters.isEmpty() } == null -> {
                        "${T::class.simpleName} doesn't have a constructor without params (create an empty constructor)"
                    }
                    else -> ex.message
                }
                throw UltronException(
                    """
                    |Couldn't create an instance of ${T::class.simpleName}. 
                    |Possible reason: $desc 
                    |Original exception: ${ex.message}, cause ${ex.cause}
                """.trimMargin()
                )
            }
        }
    }
}