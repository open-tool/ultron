<p align="center">
<img src="https://user-images.githubusercontent.com/12834123/252489846-db6cb0f8-6b28-4ae4-bceb-8b5907f1d59f.png#gh-light-mode-only" width=600>
<img src="https://user-images.githubusercontent.com/12834123/252498170-61e5a440-c2b5-42ea-8bfb-91ee12248422.png#gh-dark-mode-only" width=600>
</p>

<div align="center">

[![Maven Central][maven-badge]][maven]
[![CI][ci-badge]][ci]
[![Documentation][documentation-badge]][documentation]
[![Telegram][telegram-badge]][telegram]
[![License][license-badge]][license]

</div>

Ultron is a UI testing framework for **Android** and **Compose Multiplatform**. It is built on top of Espresso, Espresso Web, UI Automator and the Compose UI testing API, and gives you a syntax you actually want to write — with stability, logging and reporting built into every action and assertion.

**Targets:** Android · iOS · Desktop (JVM) · Web (JS & wasmJs) · macOS

## Why Ultron

- **Stable by design** — every operation retries itself until it succeeds or the timeout expires, and only for the exceptions you allow
- **Concise syntax** — the operation names you already know, without the ceremony around them
- **[Page Object][docs-page] and [UI Blocks][docs-uiblock]** — describe elements inside their parent block instead of the whole screen
- **[Lists without pain][docs-lazylist]** — RecyclerView and Compose LazyList items addressed by matcher, index or position, scrolling handled for you
- **[Allure report][docs-allure] out of the box** — steps, screenshots and view hierarchy attached automatically (Android only)
- **[Test lifecycle control][docs-ultrontest]** — preconditions per test class *and* per single test, plus soft assertions
- **[Extendable][docs-extension]** — add your own operations as Kotlin extensions and they inherit retries, logging and reporting

## A few words about syntax

#### Compose

_Compose UI testing API_

```kotlin
composeTestRule.onNode(hasTestTag("Continue")).performClick()
composeTestRule.onNodeWithText("Welcome").assertIsDisplayed()
```

_Ultron_

```kotlin
hasTestTag("Continue").click()
hasText("Welcome").assertIsDisplayed()
```

Note the missing `composeTestRule`: Ultron operations work in **any** class, so Page Objects need no test rule passed around. See the [Compose doc][docs-compose].

#### Espresso

_Espresso_

```kotlin
onView(withId(R.id.send_button)).check(matches(isDisplayed())).perform(click())
```

_Ultron_

```kotlin
withId(R.id.send_button).isDisplayed().click()
```

See the [Espresso doc][docs-espresso].

#### Lists

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

The same applies to Compose lists — see [RecyclerView][docs-recyclerview] and [Compose LazyList][docs-lazylist].

More comparisons, including Espresso Web and UI Automator, are on the [project page][documentation].

## Add Ultron to your project

The framework has three libraries — take the ones you need:

| Artifact | What for |
|---|---|
| `com.atiurin:ultron-compose` | Compose UI tests, both for Android apps and Compose Multiplatform |
| `com.atiurin:ultron-android` | Native Android UI tests: Espresso, Espresso Web, UI Automator |
| `com.atiurin:ultron-allure` | Allure report artifacts for Android UI tests |

All of them are published to Maven Central — the latest version is on the badge above.

Android instrumented tests:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    androidTestImplementation("com.atiurin:ultron-compose:$ultronVersion")
    androidTestImplementation("com.atiurin:ultron-android:$ultronVersion")
    androidTestImplementation("com.atiurin:ultron-allure:$ultronVersion")
}
```

Compose Multiplatform tests:

```kotlin
kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation("com.atiurin:ultron-compose:$ultronVersion")
        }
    }
}
```

Read the [dependencies management doc][docs-dependencies] for details.

## Your first test

**Compose Multiplatform** — replace `runComposeUiTest` with `runUltronUiTest` and interact with `SemanticsMatcher` directly:

```kotlin
class ExampleTest {
    @Test
    fun myTest() = runUltronUiTest {
        setContent {
            App()
        }
        hasTestTag("text").assertTextEquals("Hello")
        hasTestTag("button").click()
        hasTestTag("text").assertTextEquals("Compose")
    }
}
```

**Android** — describe the screen once, then read your tests like scenarios:

```kotlin
object ChatPage : Page<ChatPage>() {
    private val messageInput = withId(R.id.message_input_text)
    private val sendButton = withId(R.id.send_button)

    fun sendMessage(text: String) = apply {
        messageInput.typeText(text)
        sendButton.click()
    }
}

class ChatTest {
    @Test
    fun sendMessage() {
        FriendsListPage.openChat("Janice")
        ChatPage.sendMessage("test message")
    }
}
```

Full samples live in [`sample-app`][sample-app] (Android) and [`composeApp`][compose-app] (multiplatform) — they are the framework's own integration tests.

## Why are Ultron tests more stable?

Ultron doesn't execute an operation once and hope for the best. It repeats the operation during a timeout (5 seconds by default) while the failure is in the list of allowed exceptions — an unexpected failure still fails fast.

```kotlin
// waits and retries until the text appears
withId(R.id.result).hasText("Passed")

// a slow screen? ask for more time on this operation only
withId(R.id.result).withTimeout(10_000).hasText("Passed")

// any operation as a Boolean, no try/catch needed
val isButtonDisplayed = withId(R.id.button).isSuccess { isDisplayed() }
```

You can also assert that an action actually had an effect and repeat it otherwise — see [custom assertions][docs-customassertion].

## Documentation

The framework offers an extensive [documentation][documentation] that addresses the majority of usage scenarios. Good places to start:

- [Connect to project][docs-connect] and [Configuration][docs-configuration]
- [Compose][docs-compose] · [Espresso][docs-espresso] · [UI Automator][docs-uiautomator] · [WebView][docs-webview]
- [UltronTest lifecycle][docs-ultrontest] · [UI Blocks][docs-uiblock] · [Listeners][docs-listeners] · [Allure][docs-allure]

Questions and feedback are welcome in the [Telegram chat][telegram].

<!--
Link References
-->

[telegram-badge]:https://img.shields.io/badge/Chat-Telegram-0088CC?style=for-the-badge
[documentation-badge]:https://img.shields.io/badge/Documentation-233a60?style=for-the-badge
[maven-badge]:https://img.shields.io/maven-central/v/com.atiurin/ultron-compose?style=for-the-badge&label=Maven%20Central&color=233a60
[ci-badge]:https://img.shields.io/github/actions/workflow/status/open-tool/ultron/ci-pipeline.yml?branch=master&style=for-the-badge&label=CI
[license-badge]:https://img.shields.io/github/license/open-tool/ultron?style=for-the-badge&color=233a60

[telegram]:https://t.me/ultron_framework
[documentation]:https://open-tool.github.io/ultron/
[maven]:https://central.sonatype.com/search?q=g:com.atiurin
[ci]:https://github.com/open-tool/ultron/actions/workflows/ci-pipeline.yml
[license]:https://github.com/open-tool/ultron/blob/master/LICENSE

[docs-connect]:https://open-tool.github.io/ultron/docs/intro/connect
[docs-dependencies]:https://open-tool.github.io/ultron/docs/intro/dependencies
[docs-configuration]:https://open-tool.github.io/ultron/docs/intro/configuration
[docs-compose]:https://open-tool.github.io/ultron/docs/compose/
[docs-lazylist]:https://open-tool.github.io/ultron/docs/compose/lazylist
[docs-espresso]:https://open-tool.github.io/ultron/docs/android/espress
[docs-recyclerview]:https://open-tool.github.io/ultron/docs/android/recyclerview
[docs-uiautomator]:https://open-tool.github.io/ultron/docs/android/uiautomator
[docs-webview]:https://open-tool.github.io/ultron/docs/android/webview
[docs-allure]:https://open-tool.github.io/ultron/docs/common/allure
[docs-ultrontest]:https://open-tool.github.io/ultron/docs/common/ultrontest
[docs-uiblock]:https://open-tool.github.io/ultron/docs/common/uiblock
[docs-listeners]:https://open-tool.github.io/ultron/docs/common/listeners
[docs-extension]:https://open-tool.github.io/ultron/docs/common/extension
[docs-customassertion]:https://open-tool.github.io/ultron/docs/common/customassertion
[docs-page]:https://open-tool.github.io/ultron/docs/

[sample-app]:https://github.com/open-tool/ultron/tree/master/sample-app
[compose-app]:https://github.com/open-tool/ultron/tree/master/composeApp
