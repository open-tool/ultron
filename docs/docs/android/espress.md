---
sidebar_position: 1
---

# Espresso

## How to use?

Simple espresso operation looks like this

```kotlin
onView(withId(R.id.send_button)).check(isDisplayed()).perform(click())
```
the same with **Ultron**

```kotlin
withId(R.id.send_button).isDisplayed().click()
```
Names of all Ultron operations are the same as espresso one. There are a lot of additional operations those simplifies test development.

```kotlin
//------ actions ------ 
click()
doubleClick()
longClick()
typeText(text: String)
replaceText(text: String)
clearText()
pressKey(keyCode: Int)
pressKey(key: EspressoKey)
closeSoftKeyboard()
swipeLeft()
swipeRight()
swipeUp()
swipeDown()
scrollTo()
perform(viewAction: ViewAction)          // execute custom espresso action as Ultron one
perform(params: UltronEspressoActionParams? = null, block: (uiController: UiController, view: View) -> Unit)
<T> execute(params: UltronEspressoActionParams? = null, block: (uiController: UiController, view: View) -> T): T

//------ get View property actions ------ 
getText() : String?
getContentDescription() : String?
getDrawable() : Drawable?

//------ assertions ------ 
exists()
doesNotExist()
isDisplayed()
isNotDisplayed()
isCompletelyDisplayed()
isDisplayingAtLeast(percentage: Int)
doesNotExist()
isEnabled()
isNotEnabled()
isSelected()
isNotSelected()
isClickable()
isNotClickable()
isChecked()
isNotChecked()
isFocusable()
isNotFocusable()
hasFocus()
isJavascriptEnabled()
hasText(text: String) 
hasText(resourceId: Int)
hasText(stringMatcher: Matcher<String>)
textContains(text: String)
hasContentDescription(text: String)
hasContentDescription(resourceId: Int)
hasContentDescription(charSequenceMatcher: Matcher<CharSequence>) 
contentDescriptionContains(text: String)
assertMatches(condition: Matcher<View>) // execute custom espresso assertion as Ultron one
hasDrawable(@DrawableRes resourceId: Int)
hasAnyDrawable()
hasCurrentTextColor(@ColorRes colorRes: Int)
hasCurrentHintTextColor(@ColorRes colorRes: Int)
hasShadowColor(@ColorRes colorRes: Int)
hasHighlightColor(@ColorRes colorRes: Int)
assertMatches(params: UltronEspressoAssertionParams? = null, block: (view: View) -> Boolean)
//------ general ------ 
withTimeout(timeoutMs: Long)                     // set custom timeout for operations
withResultHandler(resultHandlerBlock)            // set custom result handler and process operation result 
withAssertion(assertion: OperationAssertion)     // define custom assertion of action success
withAssertion(name: String = "", isListened: Boolean = false, block: () -> Unit)

//------ custom clicks ------
clickTopLeft(offsetX: Int, offsetY: Int)
clickTopCenter(offsetY: Int)
clickTopRight(offsetX: Int, offsetY: Int)
clickCenterRight(offsetX: Int)
clickBottomRight(offsetX: Int, offsetY: Int)
clickBottomCenter(offsetY: Int)
clickBottomLeft(offsetX: Int, offsetY: Int)
clickCenterLeft(offsetX: Int)

```

## Best practice

Specify page elements as properties of PageObject class.

```kotlin
object SomePage : Page<SomePage>() {
    private val button = withId(R.id.button1)
    private val eventStatus = withId(R.id.last_event_status)
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
withId(R.id.last_event_status).withTimeout(10_000).isDisplayed()
```
There are 2 ways of using custom timeout:
- Specify it for page property and it will be applied for all operations with this element
```kotlin
object SomePage : Page<SomePage>() {
    private val eventStatus = withId(R.id.last_event_status).withTimeout(10_000)
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

There is `isSuccess` method that allows us to get the result of any operation as boolean value. In case of false it could be executed to long (5 sec by default). So it reasonable to specify custom timeout for some operations.

```kotlin
val isButtonDisplayed = withId(R.id.button).isSuccess { withTimeout(2_000).isDisplayed() }
if (isButtonDisplayed) {
    //do some reasonable actions
}
```

## Dialog and popup

To execute any operation inside dialog or popup with espresso you have to specify correct root element
```kotlin
onView(withText("OK"))).inRoot(isDialog()).perform(click())
onView(withText("Cancel")).inRoot(isPlatformPopup()).perform(click())
```
Here is a point we need to put our minds on.

**Ultron extends not only `Matcher<View>` object but also `ViewInteraction` and `DataInteraction` objects**

`onView(withText("OK"))).inRoot(isDialog())` returns _ViewInteraction_. Therefore it's possible to use Ultron operations with dialogs.

So the best way would be a following

```kotlin
object DialogPage : Page<DialogPage>() {
    val okButton = onView(withText(R.string.ok_button))).inRoot(isDialog())
    val cancelButton = onView(withText(R.string.cancel_button))).inRoot(isDialog())
}
...
fun someUserStepInsideSomePage(){
    DialogPage.okButton.click()
    somePageElement.isDisplayed()
}
```
## Extend framework with your own ViewActions and ViewAssertions

Under the hood all espresso Ultron operations are described in `UltronEspressoInteraction` class. That is why you just need to extend this class using [kotlin extension function](https://kotlinlang.org/docs/extensions.html), e.g.
```kotlin
fun <T> UltronEspressoInteraction<T>.appendText(text: String) = apply {
    executeAction(
        operationBlock = getInteractionActionBlock(AppendTextAction(text)),
        name = "Append text '$text' to ${getInteractionMatcher()}",
        description = "${interaction!!::class.simpleName} APPEND_TEXT to ${getInteractionMatcher()} during $timeoutMs ms",
    )
}
```
`AppendTextAction` is a custom ViewAction, smth like that
```kotlin
class AppendTextAction(private val value: String) : ViewAction {
    override fun getConstraints() = allOf(isDisplayed(), isAssignableFrom(TextView::class.java))
    override fun perform(uiController: UiController, view: View) {
        (view as TextView).apply {
            this.text = "$text$value"
        }
        uiController.loopMainThreadUntilIdle()
    }
    ...
}
```

To make your custom operation 100% native for Ultron framework it's required to add 3 lines more

```kotlin
//support action for all Matcher<View>
fun Matcher<View>.appendText(text: String) = UltronEspressoInteraction(onView(this)).appendText(text)

//support action for all ViewInteractions
fun ViewInteraction.appendText(text: String) = UltronEspressoInteraction(this).appendText(text)

//support action for all DataInteractions
fun DataInteraction.appendText(text: String) =  UltronEspressoInteraction(this).appendText(text)
```
Finally you are able to use this custom operation
```kotlin
withId(R.id.text_input).appendText("some text to append")
```
View sample code [UltronEspressoExt](https://github.com/open-tool/ultron/blob/master/sample-app/src/androidTest/java/com/atiurin/sampleapp/framework/ultronext/UltronEspressoExt.kt)

## Get any property of any View

There are several build in methods that extends `Matcher<View>, ViewInteraction, DataInteraction`:
```kotlin
getText() : String?
getContentDescription() : String?
getDrawable() : Drawable?
```
And you are able to get any other property. There is an example how it could be done - [GetTextAction](https://github.com/alex-tiurin/ultron/blob/master/ultron/src/main/java/com/atiurin/ultron/custom/espresso/action/GetTextAction.kt)