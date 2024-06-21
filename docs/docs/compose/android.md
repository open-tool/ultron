---
sidebar_position: 2
---

# Android

## Android Compose testing API

Typical Android test looks smth like this:

```kotlin
class ComposeContentTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    @Test
    fun myTest() {
        composeTestRule.setContent { .. } 
        composeTestRule.onNode(hasTestTag("Continue")).performClick()
        composeTestRule.onNodeWithText("Welcome").assertIsDisplayed()
    }
}
```
You can read more aboit it in [official documentation](https://developer.android.com/jetpack/compose/testing)

So, all compose testing APIs are provided by `composeTestRule`. It's definitely uncomfortable. Moreover, in case your UI loading takes some time, e.g. in integration test, an assertion or an action fails.

If you need to launch an Activity it's required to use another factory method to create Compose TestRule - `createUltronComposeRule<A>`

```kotlin
class ActivityComposeTest {
    @get:Rule
    val composeTestRule = createUltronComposeRule<YourActivity>()
    @Test
    fun myTest() {
        composeTestRule.onNode(hasTestTag("Continue")).performClick()
        composeTestRule.onNodeWithText("Welcome").assertIsDisplayed()
    }
}
```

_**Ultron**_ framework solves all these problems and do a lot more.

## Ultron Compose

Just create compose rule using Ultron static method

```kotlin
@get:Rule
val composeTestRule = createDefaultUltronComposeRule()
```
After that you're able to perform stable compose operations in **ANY** class. Just create a `SemanticsMatcher`(like `hasTestTag("smth")`) and call an operation on it. e.g.
```kotlin
hasTestTag("Continue").click()
hasText("Welcome").assertIsDisplayed()
```

`SemanticsMatcher` object is used in Android Compose testing framework to find a target node to interact with.

To launch an Activity use `createUltronComposeRule<A>`

```kotlin
@get:Rule
val composeTestRule = createUltronComposeRule<YourActivity>()
```
