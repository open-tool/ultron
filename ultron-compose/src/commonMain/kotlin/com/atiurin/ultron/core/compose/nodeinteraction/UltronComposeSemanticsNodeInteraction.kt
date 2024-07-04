package com.atiurin.ultron.core.compose.nodeinteraction


import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.semantics.*
import androidx.compose.ui.test.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.Dp
import com.atiurin.ultron.core.common.CommonOperationType
import com.atiurin.ultron.core.common.ElementInfo
import com.atiurin.ultron.core.common.DefaultElementInfo
import com.atiurin.ultron.core.common.UltronOperationType
import com.atiurin.ultron.core.common.assertion.DefaultOperationAssertion
import com.atiurin.ultron.core.common.assertion.EmptyOperationAssertion
import com.atiurin.ultron.core.common.assertion.OperationAssertion
import com.atiurin.ultron.core.common.options.*
import com.atiurin.ultron.core.compose.SemanticsNodeInteractionProviderContainer
import com.atiurin.ultron.core.compose.config.UltronComposeConfig
import com.atiurin.ultron.core.compose.operation.ComposeOperationExecutor
import com.atiurin.ultron.core.compose.operation.ComposeOperationResult
import com.atiurin.ultron.core.compose.operation.ComposeOperationType.*
import com.atiurin.ultron.core.compose.operation.UltronComposeOperation
import com.atiurin.ultron.core.compose.operation.UltronComposeOperationLifecycle
import com.atiurin.ultron.core.compose.operation.UltronComposeOperationParams
import com.atiurin.ultron.core.compose.option.ComposeSwipeOption
import com.atiurin.ultron.exceptions.UltronOperationException
import com.atiurin.ultron.extensions.*
import com.atiurin.ultron.listeners.setListenersState
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update

open class UltronComposeSemanticsNodeInteraction constructor(
    val semanticsNodeInteraction: SemanticsNodeInteraction,
    val timeoutMs: Long = UltronComposeConfig.params.operationTimeoutMs,
    val resultHandler: ((ComposeOperationResult<UltronComposeOperation>) -> Unit) = UltronComposeConfig.resultHandler,
    val assertion: OperationAssertion = EmptyOperationAssertion(),
    val elementInfo: ElementInfo = DefaultElementInfo()
) {
    constructor(
        matcher: SemanticsMatcher,
        useUnmergedTree: Boolean = UltronComposeConfig.params.useUnmergedTree,
        timeoutMs: Long = UltronComposeConfig.params.operationTimeoutMs,
        resultHandler: ((ComposeOperationResult<UltronComposeOperation>) -> Unit) = UltronComposeConfig.resultHandler,
        assertion: OperationAssertion = EmptyOperationAssertion(),
        elementInfo: ElementInfo = DefaultElementInfo()
    ) : this(SemanticsNodeInteractionProviderContainer.getProvider().onNode(matcher, useUnmergedTree), timeoutMs, resultHandler, assertion, elementInfo)

    init {
        if (elementInfo.name.isEmpty()) elementInfo.name = semanticsNodeInteraction.getSelectorDescription()
    }

    fun <T> isSuccess(action: UltronComposeSemanticsNodeInteraction.() -> T): Boolean = runCatching { action() }.isSuccess

    fun withResultHandler(resultHandler: (ComposeOperationResult<UltronComposeOperation>) -> Unit) = UltronComposeSemanticsNodeInteraction(
        semanticsNodeInteraction, this.timeoutMs, resultHandler, this.assertion, this.elementInfo
    )

    fun withTimeout(timeoutMs: Long) = UltronComposeSemanticsNodeInteraction(
        semanticsNodeInteraction, timeoutMs, this.resultHandler, this.assertion, this.elementInfo
    )


    fun withAssertion(assertion: OperationAssertion) = UltronComposeSemanticsNodeInteraction(semanticsNodeInteraction, timeoutMs, resultHandler, assertion, this.elementInfo)
    fun withAssertion(name: String = "", isListened: Boolean = false, block: () -> Unit) =
        UltronComposeSemanticsNodeInteraction(semanticsNodeInteraction, timeoutMs, resultHandler, DefaultOperationAssertion(name, block.setListenersState(isListened)), this.elementInfo)

    fun withName(name: String) = apply { elementInfo.name = name }
    
    fun withMetaInfo(meta: Any) = apply { elementInfo.meta = meta }
    
    internal fun click(position: UltronComposeOffsets, option: ClickOption? = null) = apply {
        val _option = option ?: ClickOption(0, 0)
        executeOperation(
            operationBlock = {
                semanticsNodeInteraction.performTouchInput {
                    val offset = getUltronComposeOffset(position).let { it.copy(x = it.x + _option.xOffset, y = it.y + _option.yOffset) }
                    this.click(offset)
                }
            },
            name = "Click on '${elementInfo.name}' ${position.name}",
            type = CLICK,
            description = "Compose click on '${elementInfo.name}' ${position.name} with option = '$_option' during $timeoutMs ms",
        )
    }

    internal fun longClick(position: UltronComposeOffsets, option: LongClickOption? = null) = apply {
        var _option = option
        executeOperation(
            operationBlock = {
                semanticsNodeInteraction.performTouchInput {
                    _option = _option ?: LongClickOption(0, 0, getDefaultLongClickDuration())
                    val offset = getUltronComposeOffset(position).let { it.copy(x = it.x + (_option?.xOffset ?: 0L), y = it.y + (_option?.yOffset ?: 0)) }
                    this.longClick(offset, _option?.durationMs ?: getDefaultLongClickDuration())
                }
            },
            name = "LongClick on '${elementInfo.name}' ${position.name}",
            type = LONG_CLICK,
            description = "Compose longClick on '${elementInfo.name}' ${position.name} with option = '$_option' during $timeoutMs ms",
        )
    }

    internal fun doubleClick(position: UltronComposeOffsets, option: DoubleClickOption? = null) = apply {
        var _option = option
        executeOperation(
            operationBlock = {
                semanticsNodeInteraction.performTouchInput {
                    _option = _option ?: DoubleClickOption(0, 0, getDefaultDoubleClickDelay())
                    val offset = getUltronComposeOffset(position).let { it.copy(x = it.x + (_option?.xOffset ?: 0L), y = it.y + (_option?.yOffset ?: 0)) }
                    this.doubleClick(offset, delayMillis = _option?.delayMs ?: getDefaultDoubleClickDelay())
                }
            },
            name = "DoubleClick on '${elementInfo.name}' ${position.name}",
            type = DOUBLE_CLICK,
            description = "Compose doubleClick on '${elementInfo.name}' ${position.name} with option = '$_option' during $timeoutMs ms",
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
            name = "SwipeDown '${elementInfo.name}'",
            type = SWIPE_DOWN,
            description = "Compose swipeDown '${elementInfo.name}' with option = '$_option' during $timeoutMs ms ",
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
            name = "SwipeUp '${elementInfo.name}'",
            type = SWIPE_UP,
            description = "Compose swipeUp '${elementInfo.name}' with option = '$_option' during $timeoutMs ms ",
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
            name = "SwipeLeft to '${elementInfo.name}'",
            type = SWIPE_LEFT,
            description = "Compose swipeLeft '${elementInfo.name}' with option = '$_option' during $timeoutMs ms ",
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
            name = "SwipeRight to '${elementInfo.name}'",
            type = SWIPE_RIGHT,
            description = "Compose swipeRight '${elementInfo.name}' with option = '$_option' during $timeoutMs ms ",
        )
    }

    fun scrollTo() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performScrollTo() },
            name = "ScrollTo on '${elementInfo.name}'",
            type = SCROLL_TO,
            description = "Compose scrollTo on '${elementInfo.name} during $timeoutMs ms",
        )
    }

    fun scrollToIndex(index: Int) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performScrollToIndex(index) },
            name = "ScrollToIndex '$index' on '${elementInfo.name}'",
            type = SCROLL_TO_INDEX,
            description = "Compose scrollToIndex $index on '${elementInfo.name} during $timeoutMs ms",
        )
    }

    fun scrollToKey(key: Any) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performScrollToKey(key) },
            name = "ScrollToKey '$key' on '${elementInfo.name}'",
            type = SCROLL_TO_KEY,
            description = "Compose scrollToKey '$key' on '${elementInfo.name} during $timeoutMs ms",
        )
    }

    fun scrollToNode(matcher: SemanticsMatcher) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performScrollToNode(matcher) },
            name = "ScrollToNode '${matcher.description}' on '${elementInfo.name}'",
            type = SCROLL_TO_NODE,
            description = "Compose scrollToNode with matcher '${matcher.description}' on '${elementInfo.name} during $timeoutMs ms",
        )
    }

    fun inputText(text: String) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performTextInput(text) },
            name = "InputText '$text' to '${elementInfo.name}'",
            type = TEXT_INPUT,
            description = "Compose inputText '$text' to '${elementInfo.name} during $timeoutMs ms",
        )
    }

    fun typeText(text: String) = inputText(text)

    @OptIn(ExperimentalTestApi::class)
    fun inputTextSelection(range: TextRange) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performTextInputSelection(range) },
            name = "TextInputSelection '$range' to '${elementInfo.name}'",
            type = TEXT_INPUT_SELECTION,
            description = "Compose inputTextSelection range '$range' to '${elementInfo.name} during $timeoutMs ms",
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
            name = "SetSelection from $startIndex to $endIndex for '${elementInfo.name}'",
            type = SET_SELECTION,
            description = "Compose setSelection from $startIndex to $endIndex for  '${elementInfo.name} with traversalMode = $traversalMode during $timeoutMs ms",
        )
    }


    fun imeAction() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performImeAction() },
            name = "PerformImeAction to '${elementInfo.name}'",
            type = IME_ACTION,
            description = "Compose performImeAction to '${elementInfo.name} during $timeoutMs ms",
        )
    }

    fun pressKey(key: KeyEvent) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performKeyPress(key) },
            name = "PressKey '$key' to '${elementInfo.name}'",
            type = PRESS_KEY,
            description = "Compose pressKey event '$key' to '${elementInfo.name} during $timeoutMs ms",
        )
    }

    fun getText(): String? {
        var text: String? = null
        executeOperation(
            operationBlock = {
                val textValues = semanticsNodeInteraction.getOneOfConfigFields(CONFIG_TEXT_FIELDS_LIST)
                text = (textValues as Collection<AnnotatedString>).firstOrNull().toString()
            },
            name = "Get text from '${elementInfo.name}'",
            type = GET_TEXT,
            description = "Compose getText from '${elementInfo.name}' during $timeoutMs ms",
        )
        return text
    }

    fun clearText() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performTextClearance() },
            name = "ClearText in '${elementInfo.name}'",
            type = CLEAR_TEXT,
            description = "Compose clearText in '${elementInfo.name} during $timeoutMs ms",
        )
    }

    fun replaceText(text: String) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performTextReplacement(text) },
            name = "ReplaceText to '$text' in '${elementInfo.name}'",
            type = REPLACE_TEXT,
            description = "Compose replaceText to '$text' in '${elementInfo.name} during $timeoutMs ms",
        )
    }

    /**
     * Action could be executed only if there is a text selection at the moment.
     */
    fun copyText() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.CopyText) },
            name = "CopyText to clipboard from '${elementInfo.name}'",
            type = COPY_TEXT,
            description = "Compose copyText to clipboard from '${elementInfo.name} during $timeoutMs ms",
        )
    }

    fun pasteText() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.PasteText) },
            name = "PasteText from clipboard to '${elementInfo.name}'",
            type = PASTE_TEXT,
            description = "Compose pasteText from clipboard to '${elementInfo.name} during $timeoutMs ms",
        )
    }

    fun cutText() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.CutText) },
            name = "CutText to clipboard from '${elementInfo.name}'",
            type = CUT_TEXT,
            description = "Compose cutText to clipboard from '${elementInfo.name} during $timeoutMs ms",
        )
    }

    fun setText(text: AnnotatedString) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.SetText) { it.invoke(text) } },
            name = "SetText '${text.text}' to '${elementInfo.name}'",
            type = SET_TEXT,
            description = "Compose setText '${text.text}' to '${elementInfo.name} during $timeoutMs ms",
        )
    }

    fun setText(text: String) = setText(AnnotatedString(text))

    fun collapse() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.Collapse) },
            name = "Collapse '${elementInfo.name}'",
            type = COLLAPSE,
            description = "Compose collapse '${elementInfo.name} during $timeoutMs ms",
        )
    }

    fun expand() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.Expand) },
            name = "Expand '${elementInfo.name}'",
            type = EXPAND,
            description = "Compose expand '${elementInfo.name} during $timeoutMs ms",
        )
    }

    fun dismiss() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.Dismiss) },
            name = "Dismiss '${elementInfo.name}'",
            type = DISMISS,
            description = "Compose dismiss '${elementInfo.name} during $timeoutMs ms",
        )
    }

    fun setProgress(value: Float) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performSemanticsAction(SemanticsActions.SetProgress) { it.invoke(value) } },
            name = "SetProgress $value to '${elementInfo.name}'",
            type = SET_PROGRESS,
            description = "Compose setProgress $value to '${elementInfo.name} during $timeoutMs ms",
        )
    }

    @OptIn(ExperimentalTestApi::class)
    fun performMouseInput(block: MouseInjectionScope.() -> Unit) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performMouseInput(block) },
            name = "MouseInput to '${elementInfo.name}'",
            type = MOUSE_INPUT,
            description = "Compose performMouseInput to '${elementInfo.name} during $timeoutMs ms",
        )
    }

    fun performSemanticsAction(key: SemanticsPropertyKey<AccessibilityAction<() -> Boolean>>) = apply { performSemanticsAction(key) { it.invoke() } }

    fun <T : Function<Boolean>> performSemanticsAction(key: SemanticsPropertyKey<AccessibilityAction<T>>, invocation: (T) -> Unit) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.performSemanticsAction(key, invocation) },
            name = "SemanticAction '${key.name}' to '${elementInfo.name}'",
            type = SEMANTIC_ACTION,
            description = "Compose semanticAction '${key.name}' to '${elementInfo.name} during $timeoutMs ms",
        )
    }

    /**
     * @deprecated Use the method 'perform' with 'UltronComposeOperationParams' instead.
     *
     * This method has been deprecated in favor of the more generalized 'perform' method that
     * allows you to perform a custom Compose operation using 'UltronComposeOperationParams'.
     * The new method provides improved flexibility and consistency in handling operation parameters.
     *
     * @param option The deprecated option parameter.
     * @param block The deprecated block parameter.
     * @return The result of the deprecated operation.
     */
    @Deprecated("Use the method 'execute' instead", ReplaceWith("execute(params, block)"))
    fun <T> perform(
        option: PerformCustomBlockOption, block: (SemanticsNodeInteraction) -> T
    ): T {
        val container: AtomicRef<T?> = atomic(null)
        executeOperation(
            operationBlock = { container.update { block(semanticsNodeInteraction) } },
            name = "Perform '${option.description}''${elementInfo.name}'",
            type = option.operationType,
            description = "Compose operation '${option.operationType}' to '${elementInfo.name}' with option '$option' during $timeoutMs ms",
        )
        return container.value ?: throw UltronOperationException("Unable to perform provided operation and get valuable result. Actual result of perform block is null.")
    }


    @Deprecated("Use the method 'execute' instead", ReplaceWith("execute(params, block)"))
    fun <T> perform(params: UltronComposeOperationParams? = null, block: (SemanticsNodeInteraction) -> T): T {
        val _params = params ?: getDefaultOperationParams()
        val container : AtomicRef<T?> = atomic(null)
        executeOperation(
            operationBlock = { container.update { block(semanticsNodeInteraction) } },
            name = _params.operationName,
            description = _params.operationDescription,
            type = _params.operationType,
        )
        return container.value ?: throw UltronOperationException("Unable to perform provided operation and get valuable result. Actual result of perform block is null.")
    }

    /**
     * Performs a custom Compose operation using the provided parameters and operation block.
     *
     * This method allows you to execute a custom Compose operation by providing the parameters
     * for the operation and a block of code that defines the operation's behavior. The operation
     * block receives a SemanticsNodeInteraction as a parameter and is responsible for performing
     * the desired interaction with the semantics node.
     *
     * @param params The optional parameters for the Compose operation. If null, default operation
     *               parameters are used.
     * @param block The block of code that defines the behavior of the custom operation. The block
     *              receives a SemanticsNodeInteraction as a parameter.
     * @return An updated instance of the class.
     */
    fun perform(params: UltronComposeOperationParams? = null, block: (SemanticsNodeInteraction) -> Unit) = apply {
        val _params = params ?: getDefaultOperationParams()
        executeOperation(
            operationBlock = { block(semanticsNodeInteraction) },
            name = _params.operationName,
            description = _params.operationDescription,
            type = _params.operationType,
        )
    }

    /**
     * This method allows you to execute a custom Compose operation by providing the parameters
     * for the operation and a block of code that defines the operation's behavior. The operation
     * block receives a SemanticsNodeInteraction as a parameter and is responsible for performing
     * the desired interaction with the semantics node.
     *
     * @param <T> The type of the result returned by the operation block.
     * @param params The optional parameters for the Compose operation. If null, default operation
     *               parameters are used.
     * @param block The block of code that defines the behavior of the custom operation. The block
     *              receives a SemanticsNodeInteraction as a parameter and returns a value of type T.
     * @return The result of the operation block.
     */
    fun <T> execute(params: UltronComposeOperationParams? = null, block: (SemanticsNodeInteraction) -> T): T {
        val _params = params ?: getDefaultOperationParams()
        val container : AtomicRef<T?> = atomic(null)
        executeOperation(
            operationBlock = { container.update { block(semanticsNodeInteraction) } },
            name = _params.operationName,
            description = _params.operationDescription,
            type = _params.operationType,
        )
        return container.value ?: throw UltronOperationException("Unable to perform provided operation and get valuable result. Actual result of perform block is null.")
    }

    fun getNode(): SemanticsNode = execute(
        UltronComposeOperationParams(
            operationName = "Get SemanticsNode of '${elementInfo.name}'",
            operationDescription = "Compose get SemanticsNode of '${elementInfo.name}' during $timeoutMs ms",
            operationType = GET_SEMANTICS_NODE
        )
    ) {
        it.fetchSemanticsNode()
    }

    fun <T> getNodeConfigProperty(key: SemanticsPropertyKey<T>): T = execute(
        UltronComposeOperationParams(
            operationName = "Get SemanticsNode config property '${key.name}' of '${elementInfo.name}'",
            operationDescription = "Compose get SemanticsNode config property '${key.name}' of '${elementInfo.name}' during $timeoutMs ms",
            operationType = GET_SEMANTICS_NODE
        )
    ) {
        it.fetchSemanticsNode().config[key]
    }


    fun printToLog(tag: String, maxDepth: Int = Int.MAX_VALUE) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.printToLog(tag, maxDepth) },
            name = "Print Semantics info to log for '${elementInfo.name}'",
            type = PRINT_TO_LOG,
            description = "Compose printToLog for '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    //assertions
    fun assertIsDisplayed() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsDisplayed() },
            name = "Assert '${elementInfo.name}' is displayed",
            type = IS_DISPLAYED,
            description = "Compose assertIsDisplayed '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertIsNotDisplayed() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsNotDisplayed() },
            name = "Assert '${elementInfo.name}' is not displayed",
            type = IS_NOT_DISPLAYED,
            description = "Compose assertIsNotDisplayed '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertExists() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertExists() },
            name = "Assert '${elementInfo.name}' exists",
            type = EXISTS,
            description = "Compose assertExists '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    /**
     * note: this assertion works immediately
     */
    fun assertDoesNotExist() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertDoesNotExist() },
            name = "Assert '${elementInfo.name}' doesn't exist",
            type = DOES_NOT_EXIST,
            description = "Compose assertDoesNotExist '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertIsEnabled() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsEnabled() },
            name = "Assert '${elementInfo.name}' is enabled",
            type = IS_ENABLED,
            description = "Compose assertIsEnabled '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertIsNotEnabled() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsNotEnabled() },
            name = "Assert '${elementInfo.name}' is not enabled",
            type = IS_NOT_ENABLED,
            description = "Compose assertIsNotEnabled '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertIsFocused() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsFocused() },
            name = "Assert '${elementInfo.name}' is focused",
            type = IS_FOCUSED,
            description = "Compose assertIsFocused '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertIsNotFocused() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsNotFocused() },
            name = "Assert '${elementInfo.name}' is not focused",
            type = IS_NOT_FOCUSED,
            description = "Compose assertIsNotFocused'${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertIsSelected() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsSelected() },
            name = "Assert '${elementInfo.name}' is selected",
            type = IS_SELECTED,
            description = "Compose assertIsSelected to '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertIsNotSelected() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsNotSelected() },
            name = "Assert '${elementInfo.name}'",
            type = IS_NOT_SELECTED,
            description = "Compose assertIsNotSelected to '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertIsSelectable() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsSelectable() },
            name = "Assert '${elementInfo.name}' is selectable",
            type = IS_SELECTABLE,
            description = "Compose assertIsSelectable '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertIsOn() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsOn() },
            name = "Assert '${elementInfo.name}' is on",
            type = IS_ON,
            description = "Compose assertIsOn '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertIsOff() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsOff() },
            name = "Assert '${elementInfo.name}' is off",
            type = IS_OFF,
            description = "Compose assertIsOff '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertIsToggleable() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertIsToggleable() },
            name = "Assert '${elementInfo.name}' Is Toggleable",
            type = IS_TOGGLEABLE,
            description = "Compose assertIsToggleable '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertHasClickAction() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertHasClickAction() },
            name = "Assert '${elementInfo.name}' has click action",
            type = HAS_CLICK_ACTION,
            description = "Compose assertHasClickAction '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertHasNoClickAction() = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertHasNoClickAction() },
            name = "Assert '${elementInfo.name}' has no click action",
            type = HAS_NO_CLICK_ACTION,
            description = "Compose assertHasNoClickAction '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertTextEquals(vararg expected: String, option: TextEqualsOption? = null) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertTextEquals(*expected, includeEditableText = option?.includeEditableText ?: true) },
            name = "AssertTextEquals '${expected.toList()}' in '${elementInfo.name}'",
            type = TEXT_EQUALS,
            description = "Compose assertTextEquals '${expected.toList()}' with option = '$option' in '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertTextContains(expected: String, option: TextContainsOption? = null) = apply {
        val _option = option ?: TextContainsOption()
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertTextContains(expected, _option.substring, _option.ignoreCase) },
            name = "AssertTextContains '$expected' in '${elementInfo.name}'",
            type = CONTAINS_TEXT,
            description = "Compose assertTextContains '$expected' with option = '$_option' in '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertContentDescriptionEquals(vararg expected: String) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertContentDescriptionEquals(*expected) },
            name = "Assert ContentDescription equals '${expected.toList()}' in '${elementInfo.name}'",
            type = HAS_CONTENT_DESCRIPTION,
            description = "Compose assertContentDescriptionEquals '${expected.toList()}' in '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertContentDescriptionContains(expected: String, option: ContentDescriptionContainsOption? = null) = apply {
        val _option = option ?: ContentDescriptionContainsOption()
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertContentDescriptionContains(expected, _option.substring, _option.ignoreCase) },
            name = "Assert ContentDescription contains '$expected' in '${elementInfo.name}'",
            type = CONTENT_DESCRIPTION_CONTAINS_TEXT,
            description = "Compose assertContentDescriptionContains '$expected' with option = '$_option' in '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertValueEquals(expected: String) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertValueEquals(expected) },
            name = "AssertValueEquals '$expected' in '${elementInfo.name}'",
            type = VALUE_EQUALS,
            description = "Compose assertValueEquals '$expected' in '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertRangeInfoEquals(range: ProgressBarRangeInfo) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertRangeInfoEquals(range) },
            name = "AssertRangeInfoEquals '$range' in '${elementInfo.name}'",
            type = PROGRESS_BAR_RANGE_EQUALS,
            description = "Compose assertRangeInfoEquals '$range' in '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertHeightIsAtLeast(minHeight: Dp) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertHeightIsAtLeast(minHeight) },
            name = "AssertHeightIsAtLeast '$minHeight' of '${elementInfo.name}'",
            type = HEIGHT_IS_AT_LEAST,
            description = "Compose operation '$HEIGHT_IS_AT_LEAST' '$minHeight' of '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertHeightIsEqualTo(expectedHeight: Dp) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertHeightIsEqualTo(expectedHeight) },
            name = "AssertHeightIsEqualTo '$expectedHeight' of '${elementInfo.name}'",
            type = HEIGHT_IS_EQUAL_TO,
            description = "Compose assertHeightIsEqualTo '$expectedHeight' of '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertWidthIsAtLeast(minWidth: Dp) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertWidthIsAtLeast(minWidth) },
            name = "AssertWidthIsAtLeast '$minWidth' of '${elementInfo.name}'",
            type = WIDTH_IS_AT_LEAST,
            description = "Compose assertWidthIsAtLeast '$minWidth' of '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertWidthIsEqualTo(expectedWidth: Dp) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assertWidthIsEqualTo(expectedWidth) },
            name = "AssertWidthIsEqualTo '$expectedWidth' of '${elementInfo.name}'",
            type = WIDTH_IS_EQUAL_TO,
            description = "Compose assertWidthIsEqualTo '$expectedWidth' of '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun assertMatches(matcher: SemanticsMatcher, messagePrefixOnError: (() -> String)? = null) = apply {
        executeOperation(
            operationBlock = { semanticsNodeInteraction.assert(matcher, messagePrefixOnError) },
            name = "Assert '${elementInfo.name}' matches '${matcher.description}'",
            type = ASSERT_MATCHES,
            description = "Compose assertMatches '${matcher.description}' to '${elementInfo.name}' during $timeoutMs ms",
        )
    }

    fun executeOperation(operation: UltronComposeOperation) = UltronComposeOperationLifecycle.execute(
        ComposeOperationExecutor(operation), resultHandler
    )

    fun executeOperation(
        operationBlock: () -> Unit, name: String = "empty name", type: UltronOperationType = CommonOperationType.DEFAULT, description: String = "empty description"
    ) = UltronComposeOperationLifecycle.execute(ComposeOperationExecutor(getComposeOperation(operationBlock, name, type, description)), resultHandler)

    fun getComposeOperation(operationBlock: () -> Unit, name: String, type: UltronOperationType, description: String) = UltronComposeOperation(
        operationBlock = operationBlock, name = name, type = type, description = description, timeoutMs = timeoutMs, assertion = assertion, elementInfo = elementInfo
    )

    private fun getDefaultOperationParams() = UltronComposeOperationParams(
        operationName = "Anonymous Compose operation on '${elementInfo.name}'",
        operationDescription = "Anonymous Compose operation on '${elementInfo.name}' during $timeoutMs ms",
    )


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
            operation: UltronComposeOperation, resultHandler: (ComposeOperationResult<UltronComposeOperation>) -> Unit = UltronComposeConfig.resultHandler
        ) {
            UltronComposeOperationLifecycle.execute(
                ComposeOperationExecutor(operation), resultHandler
            )
        }

        private const val CONFIG_TEXT_FIELD_NAME = "Text"
        private const val CONFIG_EDITABLE_TEXT_FIELD_NAME = "EditableText"
        val CONFIG_TEXT_FIELDS_LIST = listOf(CONFIG_TEXT_FIELD_NAME, CONFIG_EDITABLE_TEXT_FIELD_NAME)
        internal const val DEFAULT_SWIPE_DURATION = 200L
    }
}
