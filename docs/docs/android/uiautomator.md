---
sidebar_position: 4
---

# UI Automator 

**Ultron** makes UI Automator actions and assertions much more stable and simple. It wraps both UiObject and UiObject2.

# Speed up all UI Automator operations

**Ultron** operation could be significantly faster then UI Automator one. To accelerate all operations add single line of code in tests precondition.

```kotlin
@BeforeClass
@JvmStatic
fun speedUpAutomator() {
    UltronConfig.UiAutomator.speedUp()
    //or apply the config
    UltronConfig.apply {
        accelerateUiAutomator = true
    }
}
```

# How to use?

Compare following code snippets.

_UI Automator_

```kotlin
val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
device.wait(
    Until.findObject(
        By.res("com.atiurin.sampleapp:id", "button1")
    ), 5_000
).click()
val uiObject2 = device.wait(
    Until.findObject(
        By.res("com.atiurin.sampleapp:id", "last_event_status")
    ), 5_000
)
uiObject2.text = "Ultron"
Assert.assertEquals("Ultron", uiObject2.text)
```
_Ultron_

```kotlin
byResId(R.id.button1).click()
byResId(R.id.last_event_status).replaceText("Ultron").hasText("Ultron")
```

The last one looks a little bit better :)

`byResId(R.id.button1)` actually returns `UltronUiObject2`.

While the framework tries to execute UI Automator operation, it catches a list of specified exceptions and tries to repeat the operation during the timeout (5 seconds by default). Of course, you are able to customize the list of processed exceptions. It is also possible to specify a custom timeout for any operation. The configuration process for this part of the framework is explained below.

## `UltronUiObject2` api

There are factory methods to create `UltronUiObject2`.

```kotlin
byResId(@IntegerRes resourceId: Int): UltronUiObject2 // specify element with target application resourceId
by(bySelector: BySelector): UltronUiObject2           // eg by(By.res("com.android.camera2","shutter_button"))
```

To describe UI element with text or content description use following approach

```kotlin
val textElement = by(By.text("some text"))
val contentDescElement = by(By.desc("Content desc"))
```

`UltronUiObject2` has all methods of standart UiObject2 and also provide a lot of new features.

```kotlin
// data providers
getParent(): UltronUiObject2?                  // return this object's parent, or null if it has no parent
getChildren(): List<UltronUiObject2>           // return a collection of the child elements directly under this object. Empty list if no child exist
getChildCount(): Int
findObject(bySelector: BySelector): UltronUiObject2? // searches all elements under this object and returns the first object to match the criteria
findObjects(bySelector: BySelector): List<UltronUiObject2>  // searches all elements under this object and returns all objects that match the criteria
getApplicationPackage(): String?               // return the package name of the app that this object belongs to
getText(): String?                             // return view.text or null if view has no text
getClassName(): String                         // return the class name of the view represented by this object
getVisibleBounds(): Rect?                      // return the visible bounds of this object in screen coordinates
getVisibleCenter(): Point?                     // return a point in the center of the visible bounds of this object
getResourceName(): String?                     // return the fully qualified resource name for this object's id
getContentDescription(): String?               // return the content description for this object

//actions
click(duration: Long = 0)                      // A basic click is a touch down and touch up over the same point with no delay.
longClick()
clear()                                        // Clears the text content if object is an editable field
addText(text: String)                          // Add the text content if object is an editable field
legacySetText(text: String)                    // Set the text content by sending individual key codes
replaceText(text: String)                      // Set the text content if object is an editable field
drag(dest: Point, speed: Int = DEFAULT_DRAG_SPEED) // Drags object to the specified location
pinchClose(percent: Float, speed: Int = DEFAULT_PINCH_SPEED) // Performs a pinch close gesture on this object
swipe(direction: Direction, percent: Float, speed: Int = DEFAULT_SWIPE_SPEED) // Performs a swipe gesture on this object
swipeUp()
swipeDown()
swipeLeft()
swipeRight()
scroll(direction: Direction, percent: Float, speed: Int = DEFAULT_SCROLL_SPEED) // Performs a scroll gesture on this object
scrollUp()
scrollDown()
scrollLeft()
scrollRight()
fling(direction: Direction, speed: Int = DEFAULT_FLING_SPEED)         // Performs a fling gesture on this object
perform(actionBlock: UiObject2.() -> Unit, actionDescription: String) // custom action on UiObject2

//asserts
hasText(textMatcher: Matcher<String>)
hasText(text: String)
textContains(textSubstring: String)
textIsNullOrEmpty()
textIsNotNullOrEmpty()
hasContentDescription(contentDescMatcher: Matcher<String>)
hasContentDescription(contentDesc: String)
contentDescriptionContains(contentDescSubstring: String)
contentDescriptionIsNullOrEmpty()
contentDescriptionIsNotNullOrEmpty()
isCheckable()
isNotCheckable()
isChecked()
isNotChecked()
isClickable()
isNotClickable()
isEnabled()
isNotEnabled()
isFocusable()
isNotFocusable()
isFocused()
isNotFocused()
isLongClickable()
isNotLongClickable()
isScrollable()
isNotScrollable()
isSelected()
isNotSelected()
isDisplayed()
isNotDisplayed()
assertThat(assertBlock: UiObject2.() -> Boolean, assertionDescription: String) // custom assertion of UiObject2

//------ general ------ 
withTimeout(timeoutMs: Long)                     // set custom timeout for operations
withResultHandler(resultHandlerBlock)            // set custom result handler and process operation result 
withAssertion(assertion: OperationAssertion)     // define custom assertion of action success
withAssertion(name: String = "", isListened: Boolean = false, block: () -> Unit)
```

## `UltronUiObject` api

As it was mentioned before **Ultron** wraps UiObject too. There is a set of static methods to create `UltronUiObject`.

```kotlin
uiResId(@IntegerRes resourceId: Int): UltronUiObject // specify element with target application resourceId
ui(uiSelector: UiSelector): UltronUiObject
```

It has all methods of standart UiObject and also provide a lot of new features. As `UltronUiObject` has almost the same api as `UltronUiObject2` we don't list it.

## Best practice

Specify page elements as properties of PageObject class.

```kotlin
object SomePage : Page<SomePage>() {
    private val button = byResId(R.id.button1)
    private val eventStatus = byResId(R.id.last_event_status)
}
```

Use this properties in page steps
```kotlin
object SomePage : Page<SomePage>() {
    //page elements
    fun someUserStepOnPage(expectedEventText: String){
         button.click()
         eventStatus.hasText(expectedEventText)
    }
}
```
## Custom timeout for any operation

```kotlin
byResId(R.id.last_event_status).withTimeout(10_000).isDisplayed()
```
There are 2 ways of using custom timeout:
- Specify it for page property and it will be applied for all operations with this element
```kotlin
object SomePage : Page<SomePage>() {
    private val eventStatus = byResId(R.id.last_event_status).withTimeout(10_000)
}
```
- Specify it inside special step there the element operation could take more time. This timeout value will be applied only once for single operation.
```kotlin
object SomePage : Page<SomePage>() {
    fun someLongUserStep(expectedEventText: String){
         longRequestButton.click()
         eventStatus.withTimeout(20_000).hasText(expectedEventText)
    }
}
```
## Boolean operation result

There is `isSuccess` method that allows us to get the result of any operation as boolean value. In case of false it could be executed to long (5 sec by default). So it's resonable to specify custom timeout for some operations.

```kotlin
val isButtonDisplayed = byResId(R.id.button).isSuccess { withTimeout(2_000).isDisplayed() }
if (isButtonDisplayed) {
    //do some reasonable actions
}
```

## Extend framework with your own action and assertion

It's described in another page [here](../common/extension.md#ui-automator)
