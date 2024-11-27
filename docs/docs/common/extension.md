---
sidebar_position: 3
---

# Ultron Extension

Ultron leverages the power of [Kotlin extension functions](https://kotlinlang.org/docs/extensions.html).

You can extend the framework by using its native approach along with your custom operations.

## Compose
***
To enhance the Compose part of the framework, follow these steps:
- Create an extension method for `UltronComposeSemanticsNodeInteraction`. This method should encapsulate the logic of the operation.
- Create `SemanticsMatcher` extension method to invoke the method with the operation logic.

Two methods facilitate this process:

- `perform`: This evaluates the operation and returns updated `UltronComposeSemanticsNodeInteraction` object.

```kotlin
fun UltronComposeSemanticsNodeInteraction.hasAnyChildren() = perform {
    Assert.assertTrue("SemanticsNode has any children", it.fetchSemanticsNode().children.isNotEmpty())
}

fun SemanticsMatcher.hasAnyChildren() = UltronComposeSemanticsNodeInteraction(this).hasAnyChildren()
```

- `execute`: This evaluates the operation and returns the operation's result.
```kotlin
fun UltronComposeSemanticsNodeInteraction.getWidth(): Int = execute {
    it.fetchSemanticsNode().size.width
}

fun SemanticsMatcher.getWidth(): Int = UltronComposeSemanticsNodeInteraction(this).getWidth()
```

### Customize operation info

You can provide additional information to the framework using `UltronComposeOperationParams` for both the `perform` and `execute` methods.

```kotlin
fun UltronComposeSemanticsNodeInteraction.getWidth(): Int = execute(
    UltronComposeOperationParams(
        operationName = "Get width of '${semanticsNodeInteraction.getDescription()}'",
        operationDescription = "Compose get width of '${semanticsNodeInteraction.getDescription()}' during $timeoutMs ms",
        operationType = CustomComposeOperationType.GET_WIDTH
    )
) {
    it.fetchSemanticsNode().size.width
}
```

## Espresso 
***
For Espresso operations, extend `UltronEspressoInteraction` class. There are 3 methods that help us: 

- `perform`: This evaluates the action and returns an updated  `UltronEspressoInteraction` object.

```kotlin
fun <T> UltronEspressoInteraction<T>.appendText(value: String) = perform { _, view ->
    val textView = (view as TextView)
    textView.text = "${textView.text}$value"
}
```

- `execute`: This evaluates the action and returns the result of the operation.
```kotlin
fun <T> UltronEspressoInteraction<T>.getText(): String = execute { _, view ->
    (view as TextView).text.toString()
}
```

- `assertMatches`: This evaluates the assertion and returns an updated `UltronEspressoInteraction` object.

```kotlin
fun <T> UltronEspressoInteraction<T>.assertChecked(expectedState: Boolean) = assertMatches { view ->
    // block returns Boolean defining whether assertion failed or succeded
    (view as CheckBox).isChecked == expectedState
}
```
To make your custom operation fully native, extend `Matcher<View>`, `ViewInteraction`, `DataInteraction`:

```kotlin
//support action for all Matcher<View>
fun Matcher<View>.appendText(text: String) = UltronEspressoInteraction(onView(this)).appendText(text)

//support action for all ViewInteractions
fun ViewInteraction.appendText(text: String) = UltronEspressoInteraction(this).appendText(text)

//support action for all DataInteractions
fun DataInteraction.appendText(text: String) =  UltronEspressoInteraction(this).appendText(text)
```

You are able to use this custom operation
```kotlin
withId(R.id.text_input).appendText("some text to append")
```

### Customize action info

You can provide additional information to the framework using  `UltronEspressoActionParams` for both the `perform` and `execute` methods.

```kotlin
fun <T> UltronEspressoInteraction<T>.getText(): String = execute(
    UltronEspressoActionParams(
        operationName = "GetText from TextView with '${getInteractionMatcher()}'",
        operationDescription = "${interaction.simpleClassName()} action '${CustomEspressoActionType.GET_TEXT}' of '${getInteractionMatcher()}' with root '${getInteractionRootMatcher()}' during ${getActionTimeout()} ms",
        operationType = CustomEspressoActionType.GET_TEXT,
        viewActionDescription = "getting text from TextView",
        viewActionConstraints = isAssignableFrom(TextView::class.java)
    )
) { _, view ->
    (view as TextView).text.toString()
}
```

### Customize assertion info

You can provide additional information to the framework using  `UltronEspressoAssertionParams` for the `assertChecked` method.

```kotlin
fun <T> UltronEspressoInteraction<T>.assertChecked(expectedState: Boolean) = assertMatches (
    UltronEspressoAssertionParams(
        operationName = "Assert CheckBox isChecked = '$expectedState'",
        operationDescription = "Assert CheckBox isChecked = '$expectedState' during $timeoutMs ms",
        operationType = EspressoAssertionType.IS_CHECKED,
    )
){ view ->
    (view as CheckBox).isChecked == expectedState
}
```

## Espresso Web
***

For Espresso Web operations, extend the `UltronWebElement` class.

```kotlin
// add action on wenView
fun UltronWebElement.appendText(text: String) = apply {
        executeOperation(
            getUltronWebActionOperation (
                webInteractionBlock = {
                    webInteractionBlock().perform(DriverAtoms.webKeys(text))
                },
                name = "WebElement(${locator.type} = '$value') appendText '$text'",
                description = "WebElement(${locator.type} = '$value') appendText '$text' during $timeoutMs ms"
            )
        )
    }
```

Use it like
```kotlin
id("text_input").appendText("some text")
```

In case you need to add an assertion, use `getUltronWebAssertionOperation()` instead of `getUltronWebActionOperation()`

```kotlin
// add assertion on webView
fun UltronWebElement.appendText(text: String) = apply {
        executeOperation(
            getUltronWebAssertionOperation (...)
        )
    }
```

## UI Automator
***

For UI Automator operations, extend either `UltronUiObject2` or `UltronUiObject` class.

```kotlin
//actually, UltronUiObject2 already has the same method addText
// this is just an example of how to extend UltronUiObject2
fun UltronUiObject2.appendText(appendText: String) = apply {
        executeAction(
            actionBlock = { uiObject2ProviderBlock()!!.text += appendText },
            name = "AppendText '$appendText' to $selectorDesc",
            description = "UiObject2 action '${UiAutomatorActionType.ADD_TEXT}' $selectorDesc appendText '$appendText' during $timeoutMs ms"
        )
    }
```
Use this new ability like:
```kotlin
object SomePage : Page<SomePage>() {
    private val search = byResId(R.id.search)  
    fun someUserStep(prefixText: String){
         search.addPrefixText(prefix)
    }
}
```
The same approach applies to adding custom assertions:

```kotlin
// actually it is not required to create custom  UltronOperationType, but could be useful later
enum class CustomUltronOperations : UltronOperationType {
    ASSERT_HAS_ANY_CHILD
}
// add extension function to UltronUiObject2 that calls `executeAssertion`
fun UltronUiObject2.assertHasAnyChild() = apply {
    executeAssertion(
        assertionBlock = { uiObject2ProviderBlock()!!.childCount > 0 },
        name = "Assert $selectorDesc has any child",
        type = CustomUltronOperations.ASSERT_HAS_ANY_CHILD,
        description = "UiObject2 assertion '${CustomUltronOperations.ASSERT_HAS_ANY_CHILD}' of $selectorDesc during $timeoutMs ms"
    )
}
```
Use this new ability like:
```kotlin
object SomePage : Page<SomePage>() {
    private val searchResult = byResId(R.id.search_result)
    fun someUserStep(prefixText: String){
        search.addPrefixText(prefix)
        searchResult.assertHasAnyChild()
    }
}
```



