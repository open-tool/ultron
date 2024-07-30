---
sidebar_position: 6
---

# Custom assertions

Our applications are not perfect. It's often happens, that some action has no result. Mostly, this
problem connected with bad app design and test device freeze.

All Ultron operations (Espresso, Web, UiAutomator and Compose) has an ability to be asserted by
custom logic.

For example, you need to assert that some element appears after click. If it's not you need to
repeat the click action.

You can do it like:

```kotlin
button.withAssertion("Assert smth is displayed") {
    title.isDisplayed()
}.click()
```

`"Assert smth is displayed"` - is the name of assertion an you will see it in case of exception.

You can skip it and write shorter:
```kotlin
button.withAssertion {
    title.isDisplayed()
}.click()
```

By default all Ultron operations inside assertion block are not logged in logcat, but don't worry!
You will see it result in case of exception.

If you want to have everything in logcat, use `isListened` param

```kotlin
button.withAssertion(isListened = true) { .. }
```

### Few words about timeouts

**Please note: it is really important to understand the timeouts of operation and assertion.**

* `withAssertion {..}` may double the time of failure. This happens because an operation is executed
  at least twice. And the assertion block is also executed twice. It's required to make sure that the
  failure is a proper failure.

* In case an operation is executed successfully but an assertion fails you may have several interactions of the operation and the assertion.

* You may restrict the assertion time by using Ultron `withTimeout()` method, e.g.

```kotlin
button.withAssertion {
    title.withTimeout(3_000L).isDisplayed()
}.click()
```

* You still can extend operation timeout
```kotlin
button.withTimeout(10_000L).withAssertion {
    title.withTimeout(2_000L).isDisplayed()
}.click()
```



