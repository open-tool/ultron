---
sidebar_position: 4
---

# Ultron Compose API

The framework provides an extended API for Compose UI testing. Basically, it's available for `SemanticsMatcher` object. It could be created by functions like `hasTestTag()`,  `hasText()` and etc.
```kotlin
//config
fun withTimeout(timeoutMs: Long)  // to change an operation timeout from default one
fun withResultHandler(resultHandler: (ComposeOperationResult<UltronComposeOperation>) -> Unit) // provide a scope to modify operation result processing
fun <T> isSuccess(action: UltronComposeSemanticsNodeInteraction.() -> T): Boolean
fun withAssertion(assertion: OperationAssertion)
fun withAssertion(name: String = "", isListened: Boolean = false, block: () -> Unit)
fun withUseUnmergedTree(value: Boolean) 
fun withName(name: String) // specify custom name for UI element, it'll be visible in log, exception, and step name for detailed allure report
fun withDescription(description: String) // analog of fun withName(name: String) for matchers of UltronComposeList, UltronComposeListItem, and child of UltronComposeListItem
fun withMetaInfo(meta: Any) // allows association of custom info with UI element

//actions
fun click(option: ClickOption? = null)
fun clickCenterLeft(option: ClickOption? = null)
fun clickCenterRight(option: ClickOption? = null)
fun clickTopCenter(option: ClickOption? = null)
fun clickTopLeft(option: ClickOption? = null)
fun clickTopRight(option: ClickOption? = null)
fun clickBottomCenter(option: ClickOption? = null)
fun clickBottomLeft(option: ClickOption? = null)
fun clickBottomRight(option: ClickOption? = null)
fun longClick(option: LongClickOption? = null)
fun longClickCenterLeft(option: LongClickOption? = null)
fun longClickCenterRight(option: LongClickOption? = null)
fun longClickTopCenter(option: LongClickOption? = null)
fun longClickTopLeft(option: LongClickOption? = null)
fun longClickTopRight(option: LongClickOption? = null)
fun longClickBottomCenter(option: LongClickOption? = null)
fun longClickBottomLeft(option: LongClickOption? = null)
fun longClickBottomRight(option: LongClickOption? = null)
fun doubleClick(option: DoubleClickOption? = null)
fun doubleClickCenterLeft(option: DoubleClickOption? = null)
fun doubleClickCenterRight(option: DoubleClickOption? = null)
fun doubleClickTopCenter(option: DoubleClickOption? = null)
fun doubleClickTopLeft(option: DoubleClickOption? = null)
fun doubleClickTopRight(option: DoubleClickOption? = null)
fun doubleClickBottomCenter(option: DoubleClickOption? = null)
fun doubleClickBottomLeft(option: DoubleClickOption? = null)
fun doubleClickBottomRight(option: DoubleClickOption? = null)
fun swipeDown(option: ComposeSwipeOption? = null)
fun swipeUp(option: ComposeSwipeOption? = null)
fun swipeLeft(option: ComposeSwipeOption? = null)
fun swipeRight(option: ComposeSwipeOption? = null)
fun scrollTo()
fun scrollToIndex(index: Int)
fun scrollToKey(key: String)
fun scrollToNode(matcher: SemanticsMatcher)
fun imeAction()
fun pressKey(keyEvent: KeyEvent)
fun getText(): String?
fun inputText(text: String)
fun typeText(text: String)
fun inputTextSelection(selection: TextRange)
fun setSelection(startIndex: Int = 0, endIndex: Int = 0, traversalMode: Boolean)
fun selectText(range: TextRange)
fun clearText()
fun replaceText(text: String)
fun copyText()
fun pasteText()
fun cutText()
fun setText(text: String)
fun setText(text: AnnotatedString)
fun collapse()
fun expand()
fun dismiss()
fun setProgress(value: Float)
fun captureToImage(): ImageBitmap

fun performMouseInput(block: MouseInjectionScope.() -> Unit)
fun performSemanticsAction(key: SemanticsPropertyKey<AccessibilityAction<() -> Boolean>>) 
fun perform(params: UltronComposeOperationParams? = null, block: (SemanticsNodeInteraction) -> Unit)
fun <T> execute(params: UltronComposeOperationParams? = null, block: (SemanticsNodeInteraction) -> T): T

fun getNode(): SemanticsNode
fun <T> getNodeConfigProperty(key: SemanticsPropertyKey<T>): T

//asserts
fun assertIsDisplayed()
fun assertIsNotDisplayed() 
fun assertExists()
fun assertDoesNotExist()
fun assertIsEnabled() 
fun assertIsNotEnabled() 
fun assertIsFocused() 
fun assertIsNotFocused() 
fun assertIsSelected() 
fun assertIsNotSelected()
fun assertIsSelectable()
fun assertIsOn() 
fun assertIsOff() 
fun assertIsToggleable() 
fun assertHasClickAction() 
fun assertHasNoClickAction() 
fun assertTextEquals(vararg expected: String, option: TextEqualsOption? = null)
fun assertTextContains(expected: String, option: TextContainsOption? = null)
fun assertContentDescriptionEquals(vararg expected: String)
fun assertContentDescriptionContains(expected: String, option: ContentDescriptionContainsOption? = null)
fun assertValueEquals(expected: String) 
fun assertRangeInfoEquals(range: ProgressBarRangeInfo)
fun assertHeightIsAtLeast(minHeight: Dp) 
fun assertHeightIsEqualTo(expectedHeight: Dp)
fun assertWidthIsAtLeast(minWidth: Dp) 
fun assertWidthIsEqualTo(expectedWidth: Dp) 
fun assertMatches(matcher: SemanticsMatcher, messagePrefixOnError: (() -> String)? = null) 
```

### _Best practice_ 

> Use Page Object pattern. Specify page elements as properties of Page class

```kotlin
object SomePage : Page<SomePage>() {
    private val button = hasTestTag(ComposeTestTags.button)
    private val eventStatus = hasTestTag(ComposeTestTags.eventStatus)
}
```

Here `ComposeTestTags` could be an object that stores testTag constants.

Use this properties in page steps

```kotlin
object SomePage : Page<SomePage>() {
    //page elements
    fun someUserStepOnPage(expectedEventText: String) = apply {
         button.click()
         eventStatus.assertTextContains(expectedEventText)
    }
}
```

It's possible to use term `Screen` instead of `Page`. They are equals.

```kotlin
object SomeScreen : Screen<SomeScreen>() { ... }
```


## Extend framework with your own compose operations

Under the hood all Ultron compose operations are described in `UltronComposeSemanticsNodeInteraction` class. That is why you just need to extend this class using [kotlin extension function](https://kotlinlang.org/docs/extensions.html), e.g.

```kotlin
//new semantic matcher for assertion
fun hasProgress(value: Float): SemanticsMatcher = SemanticsMatcher.expectValue(GetProgress, value)

//add new operation
fun UltronComposeSemanticsNodeInteraction.assertProgress(expected: Float) = apply {
    executeOperation(
        operationBlock = { semanticsNodeInteraction.assert(hasProgress(expected)) },
        name = "Assert '${semanticsNodeInteraction.getDescription()}' has progress $expected",
        description = "Compose assertProgress = $expected in '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
    )
}

//extend SemanticsMatcher with your new operation
fun SemanticsMatcher.assertProgress(expected: Float) = UltronComposeSemanticsNodeInteraction(this).assertProgress(expected)
```
How to use
```kotlin
val progress = 0.7f
hasTestTag(ComposeElementsActivity.progressBar).setProgress(progress).assertProgress(progress)
```
You may ask what is `GetProgress`?

This is a feature of Compose framework. It's available to extend you app with custom SemanticsPropertyKey. Define it in app and assert it in tests.

```kotlin
//application code
@Composable
fun LinearProgressBar(statusState: MutableState<String>){
    val progressState = remember {
        mutableStateOf(0f)
    }
    LinearProgressIndicator(progress = progressState.value, modifier =
    Modifier
        .semantics {
            testTag = ComposeElementsActivity.progressBar
            setProgress { value ->
                progressState.value = value
                statusState.value = "set progress $value"
                true
            }
            progressBarRangeInfo = ProgressBarRangeInfo(progressState.value, 0f..progressState.value, 100)
        }
        .getProgress(progressState.value)
        .progressSemantics()
    )
}

val GetProgress = SemanticsPropertyKey<Float>("ProgressValue")
var SemanticsPropertyReceiver.getProgress by GetProgress

fun Modifier.getProgress(progress: Float): Modifier {
    return semantics { getProgress = progress }
}
```


