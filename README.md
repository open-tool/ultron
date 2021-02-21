# Ultron

[ ![Download](https://api.bintray.com/packages/alex-tiurin/android/ultron/images/download.svg) ](https://bintray.com/alex-tiurin/android/ultron/_latestVersion)
![Android CI](https://github.com/alex-tiurin/ultron/workflows/AndroidCI/badge.svg)

Ultron is an easiest framework to develop Android UI tests. It makes your tests stable, short and understandable.
It's based on Espresso and UI Automator and it provides a lot of new great features.
Ultron also gives you a full control under your tests!

Moreover, you don't need to learn any new classes or special syntax. All magic actions and assertions are provided from crunch.
Ultron can be easially customised and extended. Wish you only stable tests!

![logo](https://user-images.githubusercontent.com/12834123/108596249-d02e9580-7394-11eb-991b-781178d51a72.png)


## What are the benefits of using the framework?

- Simple and presentative syntax
- Stability of all actions and assertions
- Full control under any action or assertion
- An architectural approach to writing tests
- Amazing mechanism of setups and teardowns (You even can setup preconditions for single test in test class. It won't affect the others)

### A few words about syntax

The standard Espresso syntax is complex and not intuitive to understand. This is especially evident when interacting with the RecyclerView

Let's look at 2 examples:

1. Simple assertion and action.

_Clear Espresso_

```kotlin
onView(withId(R.id.send_button)).check(isDisplayed()).perform(click())
```
_Ultron_

```kotlin
withId(R.id.send_button).isDisplayed().click()
```
It looks better. Names of all Ultron operations are the same as Espresso one. It also provide a list of additional operations.

2. Action on RecyclerView list item

_Clear Espresso_

```kotlin
onView(withId(R.id.recycler_friends))
    .perform(
        RecyclerViewActions
            .scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText("Janice")))
    )
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
### You can get the result of any operation as boolean value

```kotlin
val isButtonDisplayed = withId(R.id.button).isSuccess { isDisplayed() }
if (isButtonDisplayed) {
    //do some reasonable actions
}
```
### Why all Ultron actions and assertions are much more stable?

The framework catches a list of specified exceptions and tries to repeat operation during timeout (5 sec by default). Ofcourse, you are able to customise the list of processed exceptions. It is also available to specify custom timeout for any operation. 

```kotlin
withId(R.id.result).withTimeout(10_000).hasText("Passed")
```

## 3 steps to develop a test using Ultron

I try to advocate the correct construction of the test framework architecture, the division of responsibilities between the layers and other proper things.

Therefore, I would like to recommend the following approach when your are using Ultron.

1. Create a PageObject class and specify screen UI elements as `Matcher<View>` objects.

```kotlin
object ChatPage : Page<ChatPage>() {
    private val messagesList = withId(R.id.messages_list)
    private val clearHistoryBtn = withText("Clear history")
    private val inputMessageText = withId(R.id.message_input_text)
    private val sendMessageBtn = withId(R.id.send_button)
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
        this.getMessageListItem(text).text
             .isDisplayed()
             .hasText(text)
    }

    fun clearHistory() = apply {
        openContextualActionModeOverflowMenu()
        clearHistoryBtn.click()
    }
}
```
Full code sample [ChatPage.class](https://github.com/alex-tiurin/ultron/blob/master/sample-app/src/androidTest/java/com/atiurin/sampleapp/pages/ChatPage.kt)

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
Full code sample [DemoEspressoTest.class](https://github.com/alex-tiurin/ultron/blob/master/sample-app/src/androidTest/java/com/atiurin/sampleapp/tests/espresso/DemoEspressoTest.kt)

In general, it all comes down to the fact that the architecture of your project will look like this.

![Architecture](https://github.com/alex-tiurin/espresso-page-object/blob/master/wiki/img/architecture.png)

## Add to your project
Gradle
```groovy
repositories {
    jcenter()
}

dependencies {
    androidTestImplementation 'com.atiurin:ultron:<latest_version>'
}
```

## AndroidX

It is required to use AndroidX libraries. You can get some problems with Android Support ones.
