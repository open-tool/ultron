# Espresso page object

[![Download](https://api.bintray.com/packages/alex-tiurin/espresso-page-object/espressopageobject/images/download.svg)](https://bintray.com/alex-tiurin/espresso-page-object/espressopageobject/_latestVersion)
![Android CI](https://github.com/alex-tiurin/espresso-page-object/workflows/AndroidCI/badge.svg)

This library provides access to nice and simple DSL for **Espresso** framework. It allows you to write simple and stable UI tests for Android application.
Moreover, you don't need to learn any new classes or special syntax. All magic actions and assertions are provided from crunch.
Library can be easy customised and extended by advanced user. Wish you only stable tests!

## Russian README is [here](https://github.com/alex-tiurin/espresso-page-object/blob/master/README_RU.md)

## What are the benefits of using the library?

- Simple and presentative syntax
- Stability of all actions and assertions
- Full control under any action and assertion
- An architectural approach to writing tests

## A few words about syntax

The standard Espresso syntax is complex and not intuitive to understand. This is especially evident when interacting with the RecyclerView

Let's look at 2 examples:

_1. Click on simple button._

**Clear Espresso**

```kotlin
onView(withId(R.id.send_button)).perform(click())
```
**Espresso page object**
```kotlin
withId(R.id.send_button).click()
```

_2. Click on RecyclerView list item_

**Clear Espresso**

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
**Espresso page object**
```kotlin
withRecyclerView(withId(R.id.recycler_friends))
    .atItem(hasDescendant(withText("Janice")))
    .click()
```
### You can get the result of any operation as boolean value

```kotlin
val isButtonDisplayed = withId(R.id.button).isSuccess { isDisplayed() }
if (isButtonDisplayed) {
    //do some reasonable actions
}
```
### You can get the result of any operation and process it by yourself

```kotlin
val isButtonDisplayed = withId(R.id.button).click { result ->
    success = result.success
    exception = result.exception
    description = result.resultDescription
}
```

## Cool features

-  [How to interact with RecyclerView](https://github.com/alex-tiurin/espresso-page-object/blob/master/wiki/en/recyclerview.md)
-  [AdapterView and onData](https://github.com/alex-tiurin/espresso-page-object/blob/master/wiki/en/adapterview.md)
-  [How we reduce flakiness of all actions and assertions](https://github.com/alex-tiurin/espresso-page-object/blob/master/wiki/en/operations_stability.md)
-  [Lifecycle listener. Listen all operations and their results.](https://github.com/alex-tiurin/espresso-page-object/blob/master/wiki/en/lifecycle_listener.md)
-  [RuleSequence + SetUpTearDownRule. Full control under your tests](https://github.com/alex-tiurin/espresso-page-object/blob/master/wiki/en/rulesequence_setupterdownrule.md)

## 3 steps to write a test using espresso-page-object

I try to advocate the correct construction of the test framework architecture, the division of responsibilities between the layers and other correct things.

Therefore, I would like to recommend the following approach when your are using the library.

1. Create a PageObject class and specify screen UI elements `Matcher<View>`.

```kotlin
object ChatPage : Page<ChatPage>() {
    private val messagesList = withId(R.id.messages_list)
    private val clearHistoryBtn = withText("Clear history")
    private val inputMessageText = withId(R.id.message_input_text)
    private val sendMessageBtn = withId(R.id.send_button)
}
```
Some elements like chat title could be determined dynamically with application data.
In this case you need to add a method in PageObject class which will return `Matcher<View>` object.

```kotlin
object ChatPage : Page<ChatPage>() {
    private fun getTitle(title: String): Matcher<View> {
        return allOf(withId(R.id.toolbar_title), withText(title))
    }
}
```

It's recommended to make all PageObject classes as `object` and descendants of Page class.
In this case you will be able to use cool kotlin magic.

2. Describe user step methods in PageObject class.

```kotlin
object ChatPage : Page<ChatPage>() {
    fun sendMessage(text: String) = apply {
        inputMessageText.typeText(text)
        sendMessageBtn.click()
        this.getListItem(text).text
            .isDisplayed()
            .hasText(text)
    }

    fun clearHistory() = apply {
        openOptionsMenu()
        clearHistoryBtn.click()
    }
}
```
Full code sample [ChatPage.class](https://github.com/alex-tiurin/espresso-page-object/blob/master/app/src/androidTest/java/com/atiurin/espressopageobjectexample/pages/ChatPage.kt)

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

Full code sample [DemoEspressoTest](https://github.com/alex-tiurin/espresso-page-object/blob/master/app/src/androidTest/java/com/atiurin/espressopageobjectexample/tests/DemoEspressoTest.kt)

Use RuleSequence + SetUpTearDownRule to prepare test data.

In general, it all comes down to the fact that the architecture of your project will look like this.

![Architecture](https://github.com/alex-tiurin/espresso-page-object/blob/master/wiki/img/architecture.png)

## Add to your project
Gradle
```groovy
repositories {
    jcenter()
}

dependencies {
    androidTestImplementation 'com.atiurin.espresso:espressopageobject:0.1.19'
}
```
Maven
```
<dependency>
  <groupId>com.atiurin.espresso</groupId>
  <artifactId>espressopageobject</artifactId>
  <version>0.1.19</version>
  <type>pom</type>
</dependency>
```

Version 0.1.17 has a new internal structure and you need to make minor changes in your project to migrate on it.
Read more info [Migration to 0.1.17](https://github.com/alex-tiurin/espresso-page-object/wiki/Migration-to-0.1.17)

## AndroidX

It is required to use AndroidX libraries. You can get some problems with Android Support ones.
