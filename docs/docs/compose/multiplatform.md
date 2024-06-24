---
sidebar_position: 1
---

# Multiplatform

> Multiplatform support is in Alpha state.

Compose Multiplatform provides robust tools for building and testing UI components across various platforms. One significant aspect of this is the ability to write and run common tests for your UI elements ([official doc sample](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html#write-and-run-common-tests)).

### `runComposeUiTest` vs `runUltronUiTest`

With standart Compose Testing framework you have to use `runComposeUiTest` method to interact with UI elements.

Here is simplified basic test sample with Compose Multiplatform. Typically it's placed in common app module, like `composeApp/src/commonTest/kotlin`

```kotlin
class ComposeExampleTest {
    @Test
    fun myTest() = runComposeUiTest {
        setContent {
            // reasonable UI content
        }
        onNode(hasTestTag("text")).assertTextEquals("Hello")
        onNode(hasTestTag("button")).performClick()
        onNode(hasTestTag("text")).assertTextEquals("Compose")
    }
}
```

Usage of `runUltronUiTest` function simplifies the interaction syntax.  

```kotlin
class UltronExampleTest {
    @Test
    fun myTest() = runUltronUiTest {
        setContent {
            // reasonable UI content
        }
        hasTestTag("text").assertTextEquals("Hello")
        hasTestTag("button").click()
        hasTestTag("text").assertTextEquals("Compose")
    }
}
```
More over it makes interactions more reliable and stable. 

Additionally, it becomes possible to call these interactions **EVERYWHERE** you want, e.g. in **Page Objects**

### Compose Page Object

Everyone knows that **Page Object** pattern is a good pattern. But how to use it for multiplatform tests?

While `runComposeUiTest` provides the context for interaction with UI elements, like calling `onNodeWithTag()`, moving this logic into a Page Object or any other class/method can lead to issues, as these don’t have direct access to the testing API. This is because the testing API is provided by an object called `SemanticsNodeInteractionProvider`, which needs to be passed into each object to call the testing API.

Here’s an example of a modified test using the Page Object pattern:

```kotlin
class PageObjectMultiplatformTest {
    @Test
    fun myTest() = runComposeUiTest {
        setContent {
            // reasonable UI content
        }
        ExamplePage(provider = this).someStep()
    }
}

class ExamplePage(val provider: SemanticsNodeInteractionsProvider){
    fun someStep(){
        provider.onNodeWithTag("text").assertTextEquals("Hello")
        provider.onNodeWithTag("button").performClick()
        provider.onNodeWithTag("text").assertTextEquals("Compose")
    }
}
```

### Ultron Page Object

Ultron eliminates the need to pass `SemanticsNodeInteractionProvider` into each object. You only need to replace the `runComposeUiTest` method with `runUltronUiTest`.

```kotlin
class UltronMultiplatformTest {
    @Test
    fun myTest() = runUltronUiTest {
        setContent {
            // reasonable UI content
        }

        UltronPage.someStep()
    }
}

object UltronPage {
    fun someStep(){
        hasTestTag("text").assertTextEquals("Hello")
        hasTestTag("button").click()
        hasTestTag("text").assertTextEquals("Compose")
    }
}
```