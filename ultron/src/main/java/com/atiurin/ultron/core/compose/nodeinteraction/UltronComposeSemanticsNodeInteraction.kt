package com.atiurin.ultron.core.compose.nodeinteraction

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.semantics.*
import androidx.compose.ui.test.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.Dp
import com.atiurin.ultron.core.common.CommonOperationType
import com.atiurin.ultron.core.common.UltronOperationType
import com.atiurin.ultron.core.common.assertion.DefaultOperationAssertion
import com.atiurin.ultron.core.common.assertion.EmptyOperationAssertion
import com.atiurin.ultron.core.common.assertion.OperationAssertion
import com.atiurin.ultron.core.common.options.*
import com.atiurin.ultron.core.compose.ComposeRuleContainer.getComposeRule
import com.atiurin.ultron.core.compose.operation.ComposeOperationExecutor
import com.atiurin.ultron.core.compose.operation.ComposeOperationResult
import com.atiurin.ultron.core.compose.operation.ComposeOperationType.*
import com.atiurin.ultron.core.compose.operation.UltronComposeOperation
import com.atiurin.ultron.core.compose.operation.UltronComposeOperationLifecycle
import com.atiurin.ultron.core.compose.option.ComposeSwipeOption
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.extensions.*
import com.atiurin.ultron.listeners.setListenersState
import com.atiurin.ultron.utils.runOnUiThread
import java.util.concurrent.atomic.AtomicReference

open class UltronComposeSemanticsNodeInteraction constructor(
    val semanticsNodeInteraction: SemanticsNodeInteraction,
    val timeoutMs: Long = UltronConfig.Compose.OPERATION_TIMEOUT,
    val resultHandler: ((ComposeOperationResult<UltronComposeOperation>) -> Unit) = UltronConfig.Compose.resultHandler,
    val assertion: OperationAssertion = EmptyOperationAssertion()
) {
    constructor(
        matcher: SemanticsMatcher,
        useUnmergedTree: Boolean = false,
        timeoutMs: Long = UltronConfig.Compose.OPERATION_TIMEOUT,
        resultHandler: ((ComposeOperationResult<UltronComposeOperation>) -> Unit) = UltronConfig.Compose.resultHandler,
        assertion: OperationAssertion = EmptyOperationAssertion()
    ) : this(getComposeRule().onNode(matcher, useUnmergedTree), timeoutMs, resultHandler, assertion)

    companion object {
        /**
         * Executes any compose action inside Ultron lifecycle
         */
        @Deprecated(
            "It doesn't support custom assertion call provided by [withAssertion]", ReplaceWith(
                "this.executeOperation(operation: UltronComposeOperation)"
            )
        )
        fun executeOperation(
            operation: UltronComposeOperation,
            resultHandler: (ComposeOperationResult<UltronComposeOperation>) -> Unit = UltronConfig.Compose.resultHandler
        ) {
            UltronComposeOperationLifecycle.execute(
                ComposeOperationExecutor(operation),
                resultHandler
            )
        }

        private const val CONFIG_TEXT_FIELD_NAME = "Text"
        private const val CONFIG_EDITABLE_TEXT_FIELD_NAME = "EditableText"
        val CONFIG_TEXT_FIELDS_LIST =
            listOf(CONFIG_TEXT_FIELD_NAME, CONFIG_EDITABLE_TEXT_FIELD_NAME)
        internal const val DEFAULT_SWIPE_DURATION = 200L
    }

    fun <T> isSuccess(action: UltronComposeSemanticsNodeInteraction.() -> T): Boolean = runCatching { action() }.isSuccess

    fun withResultHandler(resultHandler: (ComposeOperationResult<UltronComposeOperation>) -> Unit) =
        UltronComposeSemanticsNodeInteraction(
            semanticsNodeInteraction,
            this.timeoutMs,
            resultHandler
        )

    fun withTimeout(timeoutMs: Long) = UltronComposeSemanticsNodeInteraction(
        semanticsNodeInteraction,
        timeoutMs,
        this.resultHandler
    )

    fun withAssertion(assertion: OperationAssertion) = UltronComposeSemanticsNodeInteraction(semanticsNodeInteraction, timeoutMs, resultHandler, assertion)
    fun withAssertion(name: String = "", isListened: Boolean = false, block: () -> Unit) =
        UltronComposeSemanticsNodeInteraction(semanticsNodeInteraction, timeoutMs, resultHandler, DefaultOperationAssertion(name, block.setListenersState(isListened)))

    internal fun click(position: UltronComposeOffsets, option: ClickOption? = null) = apply {
        val _option = option ?: ClickOption(0, 0)
        executeOperation(
            operationBlock = {
                semanticsNodeInteraction.performTouchInput {
                    val offset = getUltronComposeOffset(position).let { it.copy(x = it.x + _option.xOffset, y = it.y + _option.yOffset) }
                    this.click(offset)
                }
            },
            name = "Click on '${semanticsNodeInteraction.getDescription()}' ${position.name}",
            type = CLICK,
            description = "Compose click on '${semanticsNodeInteraction.getDescription()}' ${position.name} with option = '$_option' during $timeoutMs ms",
        )
    }

    internal fun longClick(position: UltronComposeOffsets, option: LongClickOption? = null) = apply {
        var _option = option
        executeOperation(
            operationBlock = {
                semanticsNodeInteraction.performTouchInput {
                    _option = _option ?: LongClickOption(0, 0, getDefaultLongClickDuration())
                    val offset = getUltronComposeOffset(position)
                        .let { it.copy(x = it.x + (_option?.xOffset ?: 0L), y = it.y + (_option?.yOffset ?: 0)) }
                    this.longClick(offset, _option?.durationMs ?: getDefaultLongClickDuration())
                }
            },
            name = "LongClick on '${semanticsNodeInteraction.getDescription()}' ${position.name}",
            type = LONG_CLICK,
            description = "Compose longClick on '${semanticsNodeInteraction.getDescription()}' ${position.name} with option = '$_option' during $timeoutMs ms",
        )
    }

    internal fun doubleClick(position: UltronComposeOffsets, option: DoubleClickOption? = null) = apply {
        var _option = option
        executeOperation(
            operationBlock = {
                semanticsNodeInteraction.performTouchInput {
                    _option = _option ?: DoubleClickOption(0, 0, getDefaultDoubleClickDelay())
                    val offset = getUltronComposeOffset(position)
                        .let { it.copy(x = it.x + (_option?.xOffset ?: 0L), y = it.y + (_option?.yOffset ?: 0)) }
                    this.doubleClick(offset, delayMillis = _option?.delayMs ?: getDefaultDoubleClickDelay())
                }
            },
            name = "DoubleClick on '${semanticsNodeInteraction.getDescription()}' ${position.name}",
            type = DOUBLE_CLICK,
            description = "Compose doubleClick on '${semanticsNodeInteraction.getDescription()}' ${position.name} with option = '$_option' during $timeoutMs ms",
        )
    }

    internal fun swipeDown(option: ComposeSwipeOption? = null) = apply {
        val _option = option ?: ComposeSwipeOption(0f, 0f, 0f, 0f, DEFAULT_SWIPE_DURATION)
        executeOperation(
            operationBlock = {
                semanticsNodeInteraction.performTouchInput {
                    val position = provideSwipeDownPosition(_option)
                    swipe(position.start, position.end, _option.durationMs)
                }
            },
            name = "SwipeDown '${semanticsNodeInteraction.getDescription()}'",
            type = SWIPE_DOWN,
            description = "Compose swipeDown '${semanticsNodeInteraction.getDescription()}' with option = '$_option' during $timeoutMs ms ",
        )
    }

    internal fun swipeUp(option: ComposeSwipeOption? = null) = apply {
        val _option = option ?: ComposeSwipeOption(0f, 0f, 0f, 0f, DEFAULT_SWIPE_DURATION)
        executeOperation(
            operationBlock = {
                semanticsNodeInteraction.performTouchInput {
                    val position = provideSwipeUpPosition(_option)
                    swipe(position.start, position.end, _option.durationMs)
                }
            },
            name = "SwipeUp '${semanticsNodeInteraction.getDescription()}'",
            type = SWIPE_UP,
            description = "Compose swipeUp '${semanticsNodeInteraction.getDescription()}' with option = '$_option' during $timeoutMs ms ",
        )
    }

    internal fun swipeLeft(option: ComposeSwipeOption? = null) = apply {
        val _option = option ?: ComposeSwipeOption(0f, 0f, 0f, 0f, DEFAULT_SWIPE_DURATION)
        executeOperation(
            operationBlock = {
                semanticsNodeInteraction.performTouchInput {
                    val position = provideSwipeLeftPosition(_option)
                    this.swipe(position.start, position.end, _option.durationMs)
                }
            },
            name = "SwipeLeft to '${semanticsNodeInteraction.getDescription()}'",
            type = SWIPE_LEFT,
            description = "Compose swipeLeft '${semanticsNodeInteraction.getDescription()}' with option = '$_option' during $timeoutMs ms ",
        )
    }

    internal fun swipeRight(option: ComposeSwipeOption? = null) = apply {
        val _option = option ?: ComposeSwipeOption(0f, 0f, 0f, 0f, DEFAULT_SWIPE_DURATION)
        executeOperation(
            operationBlock = {
                semanticsNodeInteraction.performTouchInput {
                    val position = provideSwipeRightPosition(_option)
                    this.swipe(position.start, position.end, _option.durationMs)
                }
            },
            name = "SwipeRight to '${semanticsNodeInteraction.getDescription()}'",
            type = SWIPE_RIGHT,
            description = "Compose swipeRight '${semanticsNodeInteraction.getDescription()}' with option = '$_option' during $timeoutMs ms ",
        )
    }

    fun scrollTo() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performScrollTo() },
            name = "ScrollTo on '${semanticsNodeInteraction.getDescription()}'",
            type = SCROLL_TO,
            description = "Compose scrollTo on '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    fun scrollToIndex(index: Int) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performScrollToIndex(index) },
            name = "ScrollToIndex on '${semanticsNodeInteraction.getDescription()}'",
            type = SCROLL_TO_INDEX,
            description = "Compose scrollToIndex $index on '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    fun scrollToKey(key: String) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performScrollToKey(key) },
            name = "ScrollToIndex on '${semanticsNodeInteraction.getDescription()}'",
            type = SCROLL_TO_KEY,
            description = "Compose scrollToKey '$key' on '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    fun scrollToNode(matcher: SemanticsMatcher) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performScrollToNode(matcher) },
            name = "ScrollToNode on '${semanticsNodeInteraction.getDescription()}'",
            type = SCROLL_TO_NODE,
            description = "Compose scrollToNode with matcher '${matcher.description}' on '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    fun inputText(text: String) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performTextInput(text) },
            name = "InputText '$text' to '${semanticsNodeInteraction.getDescription()}'",
            type = TEXT_INPUT,
            description = "Compose inputText '$text' to '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    fun typeText(text: String) = inputText(text)

    @OptIn(ExperimentalTestApi::class)
    fun inputTextSelection(range: TextRange) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performTextInputSelection(range) },
            name = "TextInputSelection '$range' to '${semanticsNodeInteraction.getDescription()}'",
            type = TEXT_INPUT_SELECTION,
            description = "Compose inputTextSelection range '$range' to '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    fun selectText(range: TextRange) = inputTextSelection(range)

    /**
     *  in traversal mode we get selection from the `textSelectionRange` semantics which is
     *  selection in original text. In non-traversal mode selection comes from the Talkback
     *  and indices are relative to the transformed text
     */
    fun setSelection(startIndex: Int = 0, endIndex: Int = 0, traversalMode: Boolean) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.SetSelection) { it.invoke(startIndex, endIndex, traversalMode) } },
            name = "SetSelection from $startIndex to $endIndex for '${semanticsNodeInteraction.getDescription()}'",
            type = SET_SELECTION,
            description = "Compose setSelection from $startIndex to $endIndex for  '${semanticsNodeInteraction.getDescription()} with traversalMode = $traversalMode during $timeoutMs ms",
        )
    }


    fun imeAction() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performImeAction() },
            name = "PerformImeAction to '${semanticsNodeInteraction.getDescription()}'",
            type = IME_ACTION,
            description = "Compose performImeAction to '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    fun pressKey(key: KeyEvent) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performKeyPress(key) },
            name = "PressKey '$key' to '${semanticsNodeInteraction.getDescription()}'",
            type = PRESS_KEY,
            description = "Compose pressKey event '$key' to '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    fun getText(): String? {
        val text = AtomicReference<String>()
        executeOperation(
            operationBlock = {
                val textValues =
                    semanticsNodeInteraction.getOneOfConfigFields(CONFIG_TEXT_FIELDS_LIST)
                text.set((textValues as Collection<AnnotatedString>).firstOrNull().toString())
            },
            name = "Get text from '${semanticsNodeInteraction.getDescription()}'",
            type = GET_TEXT,
            description = "Compose getText from '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
        return text.get()
    }

    fun clearText() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performTextClearance() },
            name = "ClearText in '${semanticsNodeInteraction.getDescription()}'",
            type = CLEAR_TEXT,
            description = "Compose clearText in '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    fun replaceText(text: String) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performTextReplacement(text) },
            name = "ReplaceText to '$text' in '${semanticsNodeInteraction.getDescription()}'",
            type = REPLACE_TEXT,
            description = "Compose replaceText to '$text' in '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    /**
     * Action could be executed only if there is a text selection at the moment.
     */
    fun copyText() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.CopyText) },
            name = "CopyText to clipboard from '${semanticsNodeInteraction.getDescription()}'",
            type = COPY_TEXT,
            description = "Compose copyText to clipboard from '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    fun pasteText() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.PasteText) },
            name = "PasteText from clipboard to '${semanticsNodeInteraction.getDescription()}'",
            type = PASTE_TEXT,
            description = "Compose pasteText from clipboard to '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    fun cutText() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.CutText) },
            name = "CutText to clipboard from '${semanticsNodeInteraction.getDescription()}'",
            type = CUT_TEXT,
            description = "Compose cutText to clipboard from '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    fun setText(text: AnnotatedString) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.SetText) { it.invoke(text) } },
            name = "SetText '${text.text}' to '${semanticsNodeInteraction.getDescription()}'",
            type = SET_TEXT,
            description = "Compose setText '${text.text}' to '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    fun setText(text: String) = setText(AnnotatedString(text))

    fun collapse() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.Collapse) },
            name = "Collapse '${semanticsNodeInteraction.getDescription()}'",
            type = COLLAPSE,
            description = "Compose collapse '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    fun expand() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.Expand) },
            name = "Expand '${semanticsNodeInteraction.getDescription()}'",
            type = EXPAND,
            description = "Compose expand '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    fun dismiss() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.Dismiss) },
            name = "Dismiss '${semanticsNodeInteraction.getDescription()}'",
            type = DISMISS,
            description = "Compose dismiss '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    fun setProgress(value: Float) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.SetProgress) { it.invoke(value) } },
            name = "SetProgress $value to '${semanticsNodeInteraction.getDescription()}'",
            type = SET_PROGRESS,
            description = "Compose setProgress $value to '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    @OptIn(ExperimentalTestApi::class)
    fun performMouseInput(block: MouseInjectionScope.() -> Unit) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performMouseInput(block) },
            name = "MouseInput to '${semanticsNodeInteraction.getDescription()}'",
            type = MOUSE_INPUT,
            description = "Compose performMouseInput to '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    fun performSemanticsAction(key: SemanticsPropertyKey<AccessibilityAction<() -> Boolean>>) = apply { performSemanticsAction(key) { it.invoke() } }

    fun <T : Function<Boolean>> performSemanticsAction(key: SemanticsPropertyKey<AccessibilityAction<T>>, invocation: (T) -> Unit) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performSemanticsAction(key, invocation) },
            name = "SemanticAction '${key.name}' to '${semanticsNodeInteraction.getDescription()}'",
            type = SEMANTIC_ACTION,
            description = "Compose semanticAction '${key.name}' to '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
        )
    }

    fun <T : Function<Boolean>, R> SemanticsNodeInteraction.performSemanticsActionWithResult(
        key: SemanticsPropertyKey<AccessibilityAction<T>>,
        invocation: (T) -> R?
    ): R? {
        val node = fetchSemanticsNode("Failed to perform ${key.name} action.")
        requireSemantics(node, key) {
            "Failed to perform action ${key.name}"
        }
        return runOnUiThread {
            node.config[key].action?.let(invocation)
        }
    }


    fun captureToImage(): ImageBitmap {
        val image = AtomicReference<ImageBitmap>()
        executeOperation(
            operationBlock = { image.set(semanticsNodeInteraction.captureToImage()) },
            name = "CaptureImage for '${semanticsNodeInteraction.getDescription()}'",
            type = CAPTURE_IMAGE,
            description = "Compose captureToImage for '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
        return image.get()
    }

    fun <T> perform(
        option: PerformCustomBlockOption? = null,
        block: (SemanticsNodeInteraction) -> T
    ): T {
        val _option = option ?: PerformCustomBlockOption(CUSTOM)
        val result = AtomicReference<T>()
        executeOperation(
            operationBlock = { result.set(block(semanticsNodeInteraction)) },
            name = "Perform '${_option.description}''${semanticsNodeInteraction.getDescription()}'",
            type = _option.operationType,
            description = "Compose operation '${_option.operationType}' to '${semanticsNodeInteraction.getDescription()}' with option '$_option' during $timeoutMs ms",
        )
        return result.get()
    }

    fun assertIsDisplayed() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsDisplayed() },
            name = "Assert '${semanticsNodeInteraction.getDescription()}' is displayed",
            type = IS_DISPLAYED,
            description = "Compose assertIsDisplayed '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertIsNotDisplayed() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsNotDisplayed() },
            name = "Assert '${semanticsNodeInteraction.getDescription()}' is not displayed",
            type = IS_NOT_DISPLAYED,
            description = "Compose assertIsNotDisplayed '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertExists() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertExists() },
            name = "Assert '${semanticsNodeInteraction.getDescription()}' exists",
            type = EXISTS,
            description = "Compose assertExists '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    /**
     * note: this assertion works immediately
     */
    fun assertDoesNotExist() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertDoesNotExist() },
            name = "Assert '${semanticsNodeInteraction.getDescription()}' doesn't exist",
            type = DOES_NOT_EXIST,
            description = "Compose assertDoesNotExist '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertIsEnabled() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsEnabled() },
            name = "Assert '${semanticsNodeInteraction.getDescription()}' is enabled",
            type = IS_ENABLED,
            description = "Compose assertIsEnabled '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertIsNotEnabled() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsNotEnabled() },
            name = "Assert '${semanticsNodeInteraction.getDescription()}' is not enabled",
            type = IS_NOT_ENABLED,
            description = "Compose assertIsNotEnabled '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertIsFocused() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsFocused() },
            name = "Assert '${semanticsNodeInteraction.getDescription()}' is focused",
            type = IS_FOCUSED,
            description = "Compose assertIsFocused '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertIsNotFocused() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsNotFocused() },
            name = "Assert '${semanticsNodeInteraction.getDescription()}' is not focused",
            type = IS_NOT_FOCUSED,
            description = "Compose assertIsNotFocused'${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertIsSelected() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsSelected() },
            name = "Assert '${semanticsNodeInteraction.getDescription()}' is selected",
            type = IS_SELECTED,
            description = "Compose assertIsSelected to '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertIsNotSelected() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsNotSelected() },
            name = "Assert '${semanticsNodeInteraction.getDescription()}'",
            type = IS_NOT_SELECTED,
            description = "Compose assertIsNotSelected to '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertIsSelectable() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsSelectable() },
            name = "Assert '${semanticsNodeInteraction.getDescription()}' is selectable",
            type = IS_SELECTABLE,
            description = "Compose assertIsSelectable '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertIsOn() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsOn() },
            name = "Assert '${semanticsNodeInteraction.getDescription()}' is on",
            type = IS_ON,
            description = "Compose assertIsOn '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertIsOff() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsOff() },
            name = "Assert '${semanticsNodeInteraction.getDescription()}' is off",
            type = IS_OFF,
            description = "Compose assertIsOff '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertIsToggleable() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsToggleable() },
            name = "Assert '${semanticsNodeInteraction.getDescription()}' Is Toggleable",
            type = IS_TOGGLEABLE,
            description = "Compose assertIsToggleable '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertHasClickAction() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertHasClickAction() },
            name = "Assert '${semanticsNodeInteraction.getDescription()}' has click action",
            type = HAS_CLICK_ACTION,
            description = "Compose assertHasClickAction '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertHasNoClickAction() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertHasNoClickAction() },
            name = "Assert '${semanticsNodeInteraction.getDescription()}' has no click action",
            type = HAS_NO_CLICK_ACTION,
            description = "Compose assertHasNoClickAction '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertTextEquals(vararg expected: String, option: TextEqualsOption? = null) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertTextEquals(*expected, includeEditableText = option?.includeEditableText ?: true) },
            name = "AssertTextEquals '${expected.toList()}' in '${semanticsNodeInteraction.getDescription()}'",
            type = TEXT_EQUALS,
            description = "Compose assertTextEquals '${expected.toList()}' with option = '$option' in '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertTextContains(expected: String, option: TextContainsOption? = null) = apply {
        val _option = option ?: TextContainsOption()
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertTextContains(expected, _option.substring, _option.ignoreCase) },
            name = "AssertTextContains '$expected' in '${semanticsNodeInteraction.getDescription()}'",
            type = CONTAINS_TEXT,
            description = "Compose assertTextContains '$expected' with option = '$_option' in '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertContentDescriptionEquals(vararg expected: String) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertContentDescriptionEquals(*expected) },
            name = "Assert ContentDescription equals '${expected.toList()}' in '${semanticsNodeInteraction.getDescription()}'",
            type = HAS_CONTENT_DESCRIPTION,
            description = "Compose assertContentDescriptionEquals '${expected.toList()}' in '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertContentDescriptionContains(expected: String, option: ContentDescriptionContainsOption? = null) = apply {
        val _option = option ?: ContentDescriptionContainsOption()
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertContentDescriptionContains(expected, _option.substring, _option.ignoreCase) },
            name = "Assert ContentDescription contains '$expected' in '${semanticsNodeInteraction.getDescription()}'",
            type = CONTENT_DESCRIPTION_CONTAINS_TEXT,
            description = "Compose assertContentDescriptionContains '$expected' with option = '$_option' in '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertValueEquals(expected: String) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertValueEquals(expected) },
            name = "AssertValueEquals '$expected' in '${semanticsNodeInteraction.getDescription()}'",
            type = VALUE_EQUALS,
            description = "Compose assertValueEquals '$expected' in '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertRangeInfoEquals(range: ProgressBarRangeInfo) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertRangeInfoEquals(range) },
            name = "AssertRangeInfoEquals '$range' in '${semanticsNodeInteraction.getDescription()}'",
            type = PROGRESS_BAR_RANGE_EQUALS,
            description = "Compose assertRangeInfoEquals '$range' in '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertHeightIsAtLeast(minHeight: Dp) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertHeightIsAtLeast(minHeight) },
            name = "AssertHeightIsAtLeast '$minHeight' of '${semanticsNodeInteraction.getDescription()}'",
            type = HEIGHT_IS_AT_LEAST,
            description = "Compose operation '$HEIGHT_IS_AT_LEAST' '$minHeight' of '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertHeightIsEqualTo(expectedHeight: Dp) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertHeightIsEqualTo(expectedHeight) },
            name = "AssertHeightIsEqualTo '$expectedHeight' of '${semanticsNodeInteraction.getDescription()}'",
            type = HEIGHT_IS_EQUAL_TO,
            description = "Compose assertHeightIsEqualTo '$expectedHeight' of '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertWidthIsAtLeast(minWidth: Dp) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertWidthIsAtLeast(minWidth) },
            name = "AssertWidthIsAtLeast '$minWidth' of '${semanticsNodeInteraction.getDescription()}'",
            type = WIDTH_IS_AT_LEAST,
            description = "Compose assertWidthIsAtLeast '$minWidth' of '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertWidthIsEqualTo(expectedWidth: Dp) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertWidthIsEqualTo(expectedWidth) },
            name = "AssertWidthIsEqualTo '$expectedWidth' of '${semanticsNodeInteraction.getDescription()}'",
            type = WIDTH_IS_EQUAL_TO,
            description = "Compose assertWidthIsEqualTo '$expectedWidth' of '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun assertMatches(matcher: SemanticsMatcher, messagePrefixOnError: (() -> String)? = null) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assert(matcher, messagePrefixOnError) },
            name = "Assert '${semanticsNodeInteraction.getDescription()}' matches '${matcher.description}'",
            type = ASSERT_MATCHES,
            description = "Compose assertMatches '${matcher.description}' to '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        )
    }

    fun executeOperation(operation: UltronComposeOperation) = UltronComposeOperationLifecycle.execute(ComposeOperationExecutor(operation), resultHandler)

    fun executeOperation(
        operationBlock: () -> Unit,
        name: String = "empty name",
        type: UltronOperationType = CommonOperationType.DEFAULT,
        description: String = "empty description"
    ) = UltronComposeOperationLifecycle.execute(ComposeOperationExecutor(getComposeOperation(operationBlock, name, type, description)), resultHandler)

    fun getComposeOperation(operationBlock: () -> Unit, name: String, type: UltronOperationType, description: String) =
        UltronComposeOperation(
            operationBlock = operationBlock,
            name = name,
            type = type,
            description = description,
            timeoutMs = timeoutMs,
            assertion = assertion
        )
}
