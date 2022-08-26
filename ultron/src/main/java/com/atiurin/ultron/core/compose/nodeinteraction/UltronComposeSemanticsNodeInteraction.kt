package com.atiurin.ultron.core.compose.nodeinteraction

import android.os.Looper
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.semantics.*
import androidx.compose.ui.test.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.Dp
import androidx.test.espresso.ViewInteraction
import androidx.test.platform.app.InstrumentationRegistry
import com.atiurin.ultron.core.common.options.*
import com.atiurin.ultron.core.compose.ComposeRuleContainer
import com.atiurin.ultron.core.compose.operation.ComposeOperationExecutor
import com.atiurin.ultron.core.compose.operation.ComposeOperationResult
import com.atiurin.ultron.core.compose.operation.ComposeOperationType.*
import com.atiurin.ultron.core.compose.operation.UltronComposeOperation
import com.atiurin.ultron.core.compose.operation.UltronComposeOperationLifecycle
import com.atiurin.ultron.core.compose.option.ComposeSwipeOption
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.extensions.*
import com.atiurin.ultron.utils.runOnUiThread
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask
import java.util.concurrent.atomic.AtomicReference

open class UltronComposeSemanticsNodeInteraction(
    val semanticsNodeInteraction: SemanticsNodeInteraction,
    val timeoutMs: Long = UltronConfig.Compose.OPERATION_TIMEOUT,
    val resultHandler: ((ComposeOperationResult<UltronComposeOperation>) -> Unit) = UltronConfig.Compose.resultHandler
) {
    companion object {
        /**
         * Executes any compose action inside Ultron lifecycle
         */
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
    fun <T> isSuccess(
        action: UltronComposeSemanticsNodeInteraction.() -> T
    ): Boolean {
        var success = true
        try {
            action()
        } catch (th: Throwable) {
            success = false
        }
        return success
    }

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

    internal fun click(position: UltronComposeOffsets, option: ClickOption? = null) = apply {
        val _option = option ?: ClickOption(0, 0)
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = {
                    semanticsNodeInteraction.performTouchInput {
                        val offset = getUltronComposeOffset(position).let { it.copy(x = it.x + _option.xOffset, y = it.y + _option.yOffset) }
                        this.click(offset)
                    }
                },
                name = "Click on '${semanticsNodeInteraction.getDescription()}' ${position.name}",
                type = CLICK,
                description = "Compose click on '${semanticsNodeInteraction.getDescription()}' ${position.name} with option = '$_option' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    internal fun longClick(position: UltronComposeOffsets, option: LongClickOption? = null) = apply {
        var _option = option
        executeOperation(
            operation = UltronComposeOperation(
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
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    internal fun doubleClick(position: UltronComposeOffsets, option: DoubleClickOption? = null) = apply {
        var _option = option
        executeOperation(
            operation = UltronComposeOperation(
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
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    internal fun swipeDown(option: ComposeSwipeOption? = null) = apply {
        val _option = option ?: ComposeSwipeOption(0f, 0f, 0f, 0f, DEFAULT_SWIPE_DURATION)
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = {
                    semanticsNodeInteraction.performTouchInput {
                        val position = provideSwipeDownPosition(_option)
                        swipe(position.start, position.end, _option.durationMs)
                    }
                },
                name = "SwipeDown '${semanticsNodeInteraction.getDescription()}'",
                type = SWIPE_DOWN,
                description = "Compose swipeDown '${semanticsNodeInteraction.getDescription()}' with option = '$_option' during $timeoutMs ms ",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    internal fun swipeUp(option: ComposeSwipeOption? = null) = apply {
        val _option = option ?: ComposeSwipeOption(0f, 0f, 0f, 0f, DEFAULT_SWIPE_DURATION)
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = {
                    semanticsNodeInteraction.performTouchInput {
                        val position = provideSwipeUpPosition(_option)
                        swipe(position.start, position.end, _option.durationMs)
                    }
                },
                name = "SwipeUp '${semanticsNodeInteraction.getDescription()}'",
                type = SWIPE_UP,
                description = "Compose swipeUp '${semanticsNodeInteraction.getDescription()}' with option = '$_option' during $timeoutMs ms ",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    internal fun swipeLeft(option: ComposeSwipeOption? = null) = apply {
        val _option = option ?: ComposeSwipeOption(0f, 0f, 0f, 0f, DEFAULT_SWIPE_DURATION)
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = {
                    semanticsNodeInteraction.performTouchInput {
                        val position = provideSwipeLeftPosition(_option)
                        this.swipe(position.start, position.end, _option.durationMs)
                    }
                },
                name = "SwipeLeft to '${semanticsNodeInteraction.getDescription()}'",
                type = SWIPE_LEFT,
                description = "Compose swipeLeft '${semanticsNodeInteraction.getDescription()}' with option = '$_option' during $timeoutMs ms ",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    internal fun swipeRight(option: ComposeSwipeOption? = null) = apply {
        val _option = option ?: ComposeSwipeOption(0f, 0f, 0f, 0f, DEFAULT_SWIPE_DURATION)
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = {
                    semanticsNodeInteraction.performTouchInput {
                        val position = provideSwipeRightPosition(_option)
                        this.swipe(position.start, position.end, _option.durationMs)
                    }
                },
                name = "SwipeRight to '${semanticsNodeInteraction.getDescription()}'",
                type = SWIPE_RIGHT,
                description = "Compose swipeRight '${semanticsNodeInteraction.getDescription()}' with option = '$_option' during $timeoutMs ms ",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    fun scrollTo() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performScrollTo() },
                name = "ScrollTo on '${semanticsNodeInteraction.getDescription()}'",
                type = SCROLL_TO,
                description = "Compose scrollTo on '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun scrollToIndex(index: Int) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performScrollToIndex(index) },
                name = "ScrollToIndex on '${semanticsNodeInteraction.getDescription()}'",
                type = SCROLL_TO_INDEX,
                description = "Compose scrollToIndex $index on '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler ?: UltronConfig.Compose.resultHandler
        )
    }

    fun scrollToKey(key: String) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performScrollToKey(key) },
                name = "ScrollToIndex on '${semanticsNodeInteraction.getDescription()}'",
                type = SCROLL_TO_KEY,
                description = "Compose scrollToKey '$key' on '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun scrollToNode(matcher: SemanticsMatcher) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performScrollToNode(matcher) },
                name = "ScrollToNode on '${semanticsNodeInteraction.getDescription()}'",
                type = SCROLL_TO_NODE,
                description = "Compose scrollToNode with matcher '${matcher.description}' on '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun inputText(text: String) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performTextInput(text) },
                name = "InputText '$text' to '${semanticsNodeInteraction.getDescription()}'",
                type = TEXT_INPUT,
                description = "Compose inputText '$text' to '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    fun typeText(text: String) = inputText(text)

    @OptIn(ExperimentalTestApi::class)
    fun inputTextSelection(range: TextRange) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performTextInputSelection(range) },
                name = "TextInputSelection '$range' to '${semanticsNodeInteraction.getDescription()}'",
                type = TEXT_INPUT_SELECTION,
                description = "Compose inputTextSelection range '$range' to '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
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
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.SetSelection) { it.invoke(startIndex, endIndex, traversalMode) } },
                name = "SetSelection from $startIndex to $endIndex for '${semanticsNodeInteraction.getDescription()}'",
                type = SET_SELECTION,
                description = "Compose setSelection from $startIndex to $endIndex for  '${semanticsNodeInteraction.getDescription()} with traversalMode = $traversalMode during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }


    fun imeAction() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performImeAction() },
                name = "PerformImeAction to '${semanticsNodeInteraction.getDescription()}'",
                type = IME_ACTION,
                description = "Compose performImeAction to '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    fun pressKey(key: KeyEvent) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performKeyPress(key) },
                name = "PressKey '$key' to '${semanticsNodeInteraction.getDescription()}'",
                type = PRESS_KEY,
                description = "Compose pressKey event '$key' to '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    fun getText(): String? {
        val text = AtomicReference<String>()
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = {
                    val textValues =
                        semanticsNodeInteraction.getOneOfConfigFields(CONFIG_TEXT_FIELDS_LIST)
                    text.set((textValues as Collection<AnnotatedString>).firstOrNull().toString())
                },
                name = "Get text from '${semanticsNodeInteraction.getDescription()}'",
                type = GET_TEXT,
                description = "Compose getText from '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
        return text.get()
    }

    fun clearText() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performTextClearance() },
                name = "ClearText in '${semanticsNodeInteraction.getDescription()}'",
                type = CLEAR_TEXT,
                description = "Compose clearText in '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    fun replaceText(text: String) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performTextReplacement(text) },
                name = "ReplaceText to '$text' in '${semanticsNodeInteraction.getDescription()}'",
                type = REPLACE_TEXT,
                description = "Compose replaceText to '$text' in '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    /**
     * Action could be executed only if there is a text selection at the moment.
     */
    fun copyText() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.CopyText) },
                name = "CopyText to clipboard from '${semanticsNodeInteraction.getDescription()}'",
                type = COPY_TEXT,
                description = "Compose copyText to clipboard from '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    fun pasteText() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.PasteText) },
                name = "PasteText from clipboard to '${semanticsNodeInteraction.getDescription()}'",
                type = PASTE_TEXT,
                description = "Compose pasteText from clipboard to '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    fun cutText() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.CutText) },
                name = "CutText to clipboard from '${semanticsNodeInteraction.getDescription()}'",
                type = CUT_TEXT,
                description = "Compose cutText to clipboard from '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    fun setText(text: AnnotatedString) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.SetText) { it.invoke(text) } },
                name = "SetText '${text.text}' to '${semanticsNodeInteraction.getDescription()}'",
                type = SET_TEXT,
                description = "Compose setText '${text.text}' to '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    fun setText(text: String) = setText(AnnotatedString(text))

    fun collapse() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.Collapse) },
                name = "Collapse '${semanticsNodeInteraction.getDescription()}'",
                type = COLLAPSE,
                description = "Compose collapse '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    fun expand() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.Expand) },
                name = "Expand '${semanticsNodeInteraction.getDescription()}'",
                type = EXPAND,
                description = "Compose expand '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    fun dismiss() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.Dismiss) },
                name = "Dismiss '${semanticsNodeInteraction.getDescription()}'",
                type = DISMISS,
                description = "Compose dismiss '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    fun setProgress(value: Float) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.SetProgress) { it.invoke(value) } },
                name = "SetProgress $value to '${semanticsNodeInteraction.getDescription()}'",
                type = SET_PROGRESS,
                description = "Compose setProgress $value to '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    @OptIn(ExperimentalTestApi::class)
    fun performMouseInput(block: MouseInjectionScope.() -> Unit) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performMouseInput(block) },
                name = "MouseInput to '${semanticsNodeInteraction.getDescription()}'",
                type = MOUSE_INPUT,
                description = "Compose performMouseInput to '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
        )
    }

    fun performSemanticsAction(key: SemanticsPropertyKey<AccessibilityAction<() -> Boolean>>) = apply { performSemanticsAction(key) { it.invoke() } }

    fun <T : Function<Boolean>> performSemanticsAction(key: SemanticsPropertyKey<AccessibilityAction<T>>, invocation: (T) -> Unit) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.performSemanticsAction(key, invocation) },
                name = "SemanticAction '${key.name}' to '${semanticsNodeInteraction.getDescription()}'",
                type = SEMANTIC_ACTION,
                description = "Compose semanticAction '${key.name}' to '${semanticsNodeInteraction.getDescription()} during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this@UltronComposeSemanticsNodeInteraction.resultHandler
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
        return runOnUiThread{
            node.config[key].action?.let(invocation)
        }
    }


    fun captureToImage(): ImageBitmap {
        val image = AtomicReference<ImageBitmap>()
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { image.set(semanticsNodeInteraction.captureToImage()) },
                name = "CaptureImage for '${semanticsNodeInteraction.getDescription()}'",
                type = CAPTURE_IMAGE,
                description = "Compose captureToImage for '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
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
            operation = UltronComposeOperation(
                operationBlock = { result.set(block(semanticsNodeInteraction)) },
                name = "Perform '${_option.description}''${semanticsNodeInteraction.getDescription()}'",
                type = _option.operationType,
                description = "Compose operation '${_option.operationType}' to '${semanticsNodeInteraction.getDescription()}' with option '$_option' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
        return result.get()
    }

    fun assertIsDisplayed() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertIsDisplayed() },
                name = "Assert '${semanticsNodeInteraction.getDescription()}' is displayed",
                type = IS_DISPLAYED,
                description = "Compose assertIsDisplayed '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertIsNotDisplayed() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertIsNotDisplayed() },
                name = "Assert '${semanticsNodeInteraction.getDescription()}' is not displayed",
                type = IS_NOT_DISPLAYED,
                description = "Compose assertIsNotDisplayed '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertExists() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertExists() },
                name = "Assert '${semanticsNodeInteraction.getDescription()}' exists",
                type = EXISTS,
                description = "Compose assertExists '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    /**
     * note: this assertion works immediately
     */
    fun assertDoesNotExist() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertDoesNotExist() },
                name = "Assert '${semanticsNodeInteraction.getDescription()}' doesn't exist",
                type = DOES_NOT_EXIST,
                description = "Compose assertDoesNotExist '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertIsEnabled() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertIsEnabled() },
                name = "Assert '${semanticsNodeInteraction.getDescription()}' is enabled",
                type = IS_ENABLED,
                description = "Compose assertIsEnabled '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertIsNotEnabled() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertIsNotEnabled() },
                name = "Assert '${semanticsNodeInteraction.getDescription()}' is not enabled",
                type = IS_NOT_ENABLED,
                description = "Compose assertIsNotEnabled '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertIsFocused() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertIsFocused() },
                name = "Assert '${semanticsNodeInteraction.getDescription()}' is focused",
                type = IS_FOCUSED,
                description = "Compose assertIsFocused '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertIsNotFocused() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertIsNotFocused() },
                name = "Assert '${semanticsNodeInteraction.getDescription()}' is not focused",
                type = IS_NOT_FOCUSED,
                description = "Compose assertIsNotFocused'${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertIsSelected() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertIsSelected() },
                name = "Assert '${semanticsNodeInteraction.getDescription()}' is selected",
                type = IS_SELECTED,
                description = "Compose assertIsSelected to '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertIsNotSelected() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertIsNotSelected() },
                name = "Assert '${semanticsNodeInteraction.getDescription()}'",
                type = IS_NOT_SELECTED,
                description = "Compose assertIsNotSelected to '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertIsSelectable() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertIsSelectable() },
                name = "Assert '${semanticsNodeInteraction.getDescription()}' is selectable",
                type = IS_SELECTABLE,
                description = "Compose assertIsSelectable '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertIsOn() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertIsOn() },
                name = "Assert '${semanticsNodeInteraction.getDescription()}' is on",
                type = IS_ON,
                description = "Compose assertIsOn '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertIsOff() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertIsOff() },
                name = "Assert '${semanticsNodeInteraction.getDescription()}' is off",
                type = IS_OFF,
                description = "Compose assertIsOff '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertIsToggleable() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertIsToggleable() },
                name = "Assert '${semanticsNodeInteraction.getDescription()}' Is Toggleable",
                type = IS_TOGGLEABLE,
                description = "Compose assertIsToggleable '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertHasClickAction() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertHasClickAction() },
                name = "Assert '${semanticsNodeInteraction.getDescription()}' has click action",
                type = HAS_CLICK_ACTION,
                description = "Compose assertHasClickAction '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertHasNoClickAction() = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertHasNoClickAction() },
                name = "Assert '${semanticsNodeInteraction.getDescription()}' has no click action",
                type = HAS_NO_CLICK_ACTION,
                description = "Compose assertHasNoClickAction '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertTextEquals(vararg expected: String, option: TextEqualsOption? = null) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertTextEquals(*expected, includeEditableText = option?.includeEditableText ?: true) },
                name = "AssertTextEquals '${expected.toList()}' in '${semanticsNodeInteraction.getDescription()}'",
                type = TEXT_EQUALS,
                description = "Compose assertTextEquals '${expected.toList()}' with option = '$option' in '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertTextContains(expected: String, option: TextContainsOption? = null) = apply {
        val _option = option ?: TextContainsOption()
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertTextContains(expected, _option.substring, _option.ignoreCase) },
                name = "AssertTextContains '$expected' in '${semanticsNodeInteraction.getDescription()}'",
                type = CONTAINS_TEXT,
                description = "Compose assertTextContains '$expected' with option = '$_option' in '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertContentDescriptionEquals(vararg expected: String) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertContentDescriptionEquals(*expected) },
                name = "Assert ContentDescription equals '${expected.toList()}' in '${semanticsNodeInteraction.getDescription()}'",
                type = HAS_CONTENT_DESCRIPTION,
                description = "Compose assertContentDescriptionEquals '${expected.toList()}' in '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertContentDescriptionContains(expected: String, option: ContentDescriptionContainsOption? = null) = apply {
        val _option = option ?: ContentDescriptionContainsOption()
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertContentDescriptionContains(expected, _option.substring, _option.ignoreCase) },
                name = "Assert ContentDescription contains '$expected' in '${semanticsNodeInteraction.getDescription()}'",
                type = CONTENT_DESCRIPTION_CONTAINS_TEXT,
                description = "Compose assertContentDescriptionContains '$expected' with option = '$_option' in '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertValueEquals(expected: String) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertValueEquals(expected) },
                name = "AssertValueEquals '$expected' in '${semanticsNodeInteraction.getDescription()}'",
                type = VALUE_EQUALS,
                description = "Compose assertValueEquals '$expected' in '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertRangeInfoEquals(range: ProgressBarRangeInfo) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertRangeInfoEquals(range) },
                name = "AssertRangeInfoEquals '$range' in '${semanticsNodeInteraction.getDescription()}'",
                type = PROGRESS_BAR_RANGE_EQUALS,
                description = "Compose assertRangeInfoEquals '$range' in '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertHeightIsAtLeast(minHeight: Dp) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertHeightIsAtLeast(minHeight) },
                name = "AssertHeightIsAtLeast '$minHeight' of '${semanticsNodeInteraction.getDescription()}'",
                type = HEIGHT_IS_AT_LEAST,
                description = "Compose operation '$HEIGHT_IS_AT_LEAST' '$minHeight' of '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertHeightIsEqualTo(expectedHeight: Dp) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertHeightIsEqualTo(expectedHeight) },
                name = "AssertHeightIsEqualTo '$expectedHeight' of '${semanticsNodeInteraction.getDescription()}'",
                type = HEIGHT_IS_EQUAL_TO,
                description = "Compose assertHeightIsEqualTo '$expectedHeight' of '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertWidthIsAtLeast(minWidth: Dp) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertWidthIsAtLeast(minWidth) },
                name = "AssertWidthIsAtLeast '$minWidth' of '${semanticsNodeInteraction.getDescription()}'",
                type = WIDTH_IS_AT_LEAST,
                description = "Compose assertWidthIsAtLeast '$minWidth' of '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertWidthIsEqualTo(expectedWidth: Dp) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assertWidthIsEqualTo(expectedWidth) },
                name = "AssertWidthIsEqualTo '$expectedWidth' of '${semanticsNodeInteraction.getDescription()}'",
                type = WIDTH_IS_EQUAL_TO,
                description = "Compose assertWidthIsEqualTo '$expectedWidth' of '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }

    fun assertMatches(matcher: SemanticsMatcher, messagePrefixOnError: (() -> String)? = null) = apply {
        executeOperation(
            operation = UltronComposeOperation(
                operationBlock = { semanticsNodeInteraction.assert(matcher, messagePrefixOnError) },
                name = "Assert '${semanticsNodeInteraction.getDescription()}' matches '${matcher.description}'",
                type = ASSERT_MATCHES,
                description = "Compose assertMatches '${matcher.description}' to '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
                timeoutMs = timeoutMs
            ),
            resultHandler = this.resultHandler
        )
    }
}
