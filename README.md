# Ultron

[ ![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.atiurin/ultron-compose/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.atiurin/ultron-compose)
![Multiplatform CI](https://github.com/open-tool/ultron/workflows/MultiplatformCI/badge.svg)

Ultron is the simplest framework to develop UI tests for **Android** & **Compose Multiplatform**.

It's constructed upon the Espresso, UI Automator and Compose UI testing frameworks. Ultron introduces a range of remarkable new features. Furthermore, Ultron puts you in complete control of your tests!

You don't need to learn any new classes or special syntax. All magic actions and assertions are provided from crunch. Ultron can be easially customised and extended. Wish you exclusively stable tests!

<p align="center">
<img src="https://user-images.githubusercontent.com/12834123/252489846-db6cb0f8-6b28-4ae4-bceb-8b5907f1d59f.png#gh-light-mode-only" width=600>
<img src="https://user-images.githubusercontent.com/12834123/252498170-61e5a440-c2b5-42ea-8bfb-91ee12248422.png#gh-dark-mode-only" width=600>
</p>

## [Documentation](https://open-tool.github.io/ultron/) | [Releases](https://github.com/open-tool/ultron/releases) | [Telegram](https://t.me/ultron_framework)

## What are the benefits of using the framework?

- Page/Screen Object pattern support
- Exceptional simplification for [**Compose UI tests**](https://open-tool.github.io/ultron/docs/compose/index)
- Out-of-the-box generation of [**Allure report**](https://open-tool.github.io/ultron/docs/common/allure) (Now, for Android UI tests only)
- A straightforward and expressive syntax
- Ensured **Stability** for all actions and assertions
- Complete control over every action and assertion
- Incredible interaction with lists: [**RecyclerView**](./android/recyclerview.md) and [**Compose LazyList**](https://open-tool.github.io/ultron/docs/compose/lazylist).
- An **Architectural** approach to developing UI tests (search "Best practice")
- An incredible mechanism for setups and teardowns (You can even set up preconditions for a single test within a test class, without affecting the others)
- [The ability to effortlessly extend the framework with your own operations](https://open-tool.github.io/ultron/docs/common/extension)
- Accelerated UI Automator operations
- Ability to monitor each stage of operation execution with [Listeners](https://open-tool.github.io/ultron/docs/common/listeners)
- [Custom operation assertions](https://open-tool.github.io/ultron/docs/common/customassertion)

***
### Documentation
The framework offers an excellent [documentation](https://open-tool.github.io/ultron/docs/) that addresses the majority of significant usage scenarios.

### A few words about syntax

The standard syntax provided by Google is intricate and not intuitive. This is especially evident when dealing with **LazyList** and **RecyclerView** interactions.

Let's explore some examples:

#### 1. Simple compose operation (refer to the doc [here](https://open-tool.github.io/ultron/docs/compose/index))

_Compose framework_

```kotlin
composeTestRule.onNode(hasTestTag("Continue")).performClick()
composeTestRule.onNodeWithText("Welcome").assertIsDisplayed()
```
_Ultron_

```kotlin
hasTestTag("Continue").click()
hasText("Welcome").assertIsDisplayed()
```

#### 2. Compose list operation (refer to the [doc](https://open-tool.github.io/ultron/docs/compose/lazylist))

_Compose framework_

```kotlin
val itemMatcher = hasText(contact.name)
composeRule
    .onNodeWithTag(contactsListTestTag)
    .performScrollToNode(itemMatcher)
    .onChildren()
    .filterToOne(itemMatcher)
    .assertTextContains(contact.name)
```

_Ultron_

```kotlin
composeList(hasTestTag(contactsListTestTag))
    .item(hasText(contact.name))
    .assertTextContains(contact.name)
```
#### 3. Simple Espresso assertion and action.

_Espresso_

```kotlin
onView(withId(R.id.send_button)).check(isDisplayed()).perform(click())
```
_Ultron_

```kotlin
withId(R.id.send_button).isDisplayed().click()
```
This presents a cleaner approach. Ultron's operation names mirror Espresso's, while also providing additional operations.

Refer to the [doc](https://open-tool.github.io/ultron/docs/android/espress) for further details.

#### 4. Action on RecyclerView list item

_Espresso_

```kotlin
onView(withId(R.id.recycler_friends))
    .perform(
        RecyclerViewActions
            .actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant(withText("Janice")),
                click()
            )
        )
```
_Ultron_

```kotlin
withRecyclerView(R.id.recycler_friends)
    .item(hasDescendant(withText("Janice")))
    .click()
```

Explore the [doc](https://open-tool.github.io/ultron/docs/android/espress) to unveil Ultron's magic with RecyclerView interactions.

#### 5. Espresso WebView operations

_Espresso_

```kotlin
onWebView()
    .withElement(findElement(Locator.ID, "text_input"))
    .perform(webKeys(newTitle))
    .withElement(findElement(Locator.ID, "button1"))
    .perform(webClick())
    .withElement(findElement(Locator.ID, "title"))
    .check(webMatches(getText(), containsString(newTitle)))
```

_Ultron_

```kotlin
id("text_input").webKeys(newTitle)
id("button1").webClick()
id("title").hasText(newTitle)
```

Refer to the [doc](https://open-tool.github.io/ultron/docs/android/webview) for more details.

#### 6. UI Automator operations

_UI Automator_

```kotlin
val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
device
    .findObject(By.res("com.atiurin.sampleapp:id", "button1"))
    .click()
```

_Ultron_

```kotlin
byResId(R.id.button1).click() 
```
Refer to the [doc](https://open-tool.github.io/ultron/docs/android/uiautomator)
***
### Acquiring the result of any operation as Boolean value

```kotlin
val isButtonDisplayed = withId(R.id.button).isSuccess { isDisplayed() }
if (isButtonDisplayed) {
    //do some reasonable actions
}
```
***
### Why are all Ultron actions and assertions more stable?

The framework captures a list of specified exceptions and attempts to repeat the operation during a timeout period (default is 5 seconds). Of course, you have the ability to customize the list of handled exceptions. You can also set a custom timeout for any operation.

```kotlin
withId(R.id.result).withTimeout(10_000).hasText("Passed")
```
***
## 3 steps to develop a test using Ultron

We advocate for a proper test framework architecture, division of responsibilities between layers, and other best practices. Therefore, when using Ultron, we recommend the following approach:

1. Create a Page Object and specify screen UI elements as `Matcher<View>` objects.

```kotlin
object ChatPage : Page<ChatPage>() {
    private val messagesList = withId(R.id.messages_list)
    private val clearHistoryBtn = withText("Clear history")
    private val inputMessageText = withId(R.id.message_input_text)
    private val sendMessageBtn = withId(R.id.send_button)
}
```

It's recommended to make all Page Objects as `object` and descendants of Page class.
This allows for the utilization of convenient Kotlin features. It also helps you to keep Page Objects stateless.

2. Describe user step methods in Page Object.

```kotlin
object ChatPage : Page<ChatPage>() {
    fun sendMessage(text: String) = apply {
        inputMessageText.typeText(text)
        sendMessageBtn.click()
        getMessageListItem(text).text
             .isDisplayed()
             .hasText(text)
    }

    fun clearHistory() = apply {
        openContextualActionModeOverflowMenu()
        clearHistoryBtn.click()
    }
}
```
Refer to the full code sample [ChatPage.class](https://github.com/open-tool/ultron/blob/master/sample-app/src/androidTest/java/com/atiurin/sampleapp/pages/ChatPage.kt)

3. Call user steps in test

```kotlin
    @Test
    fun friendsItemCheck(){
        FriendsListPage {
            assertName("Janice")
            assertStatus("Janice","Oh. My. God")
        }
    }
    @Test
    fun sendMessage(){
        FriendsListPage.openChat("Janice")
        ChatPage {
            clearHistory()
            sendMessage("test message")
        }
    }
```
Refer to the full code sample [DemoEspressoTest.class](https://github.com/open-tool/ultron/blob/master/sample-app/src/androidTest/java/com/atiurin/sampleapp/tests/espresso/DemoEspressoTest.kt)

In essence, your project's architecture will look like this:

[acrchitecture](https://github.com/open-tool/ultron/assets/12834123/b0882d34-a18d-4f1f-959b-f75796d11036)

***
## Allure report

Ultron has built in support to generate artifacts for Allure reports. Just apply the recommended configuration and set testIntrumentationRunner.

For the complete guide, refer to the [Allure description](https://open-tool.github.io/ultron/docs/common/allure)

```kotlin
@BeforeClass @JvmStatic
fun setConfig() {
    UltronConfig.applyRecommended()
    UltronAllureConfig.applyRecommended()
    UltronComposeConfig.applyRecommended() 
}
```
![allure](https://github.com/open-tool/ultron/assets/12834123/c05c813a-ece6-45e6-a04f-e1c92b82ffb1)

![allure compose](https://github.com/open-tool/ultron/assets/12834123/1f751f3d-fc58-4874-a850-acd9181bfb70)

## Add Ultron to your project

Gradle
```groovy
repositories {
    mavenCentral()
}

dependencies {
    androidTestImplementation 'com.atiurin:ultron-android:<latest_version>'
    androidTestImplementation 'com.atiurin:ultron-allure:<latest_version>'
    androidTestImplementation 'com.atiurin:ultron-compose:<latest_version>'
}
```
Please, read [gradle dependencies management](https://open-tool.github.io/ultron/docs/intro/dependencies) doc.
