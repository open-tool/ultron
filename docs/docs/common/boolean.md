---
sidebar_position: 4
---

# Boolean result

While using the **Ultron** framework you always can get the result of any operation as boolean value. 

```kotlin
object SomePage : Page<SomePage>{
    private val composeElement = hasTestTag("some_tag")
    private val espressoElement = withId(R.id.espressoId)
    private val espressoWebViewElement = xpath("some_xpath")
    private val uiautomatorElement = byResId(R.id.uiatomatorId)
}
```
All these elements have `isSuccess` method that allows us to get boolean result. 
In case of false it could be executed to long (5 sec by default). So it reasonable to specify custom timeout for some operations.
```kotlin
composeElement.isSuccess { withTimeout(1_000).assertIsDisplayed() }
espressoElement.isSuccess { withTimeout(2_000).isDisplayed() }
uiautomatorElement.isSuccess { withTimeout(2_000).isDisplayed() }
espressoWebViewElement.isSuccess { withTimeout(2_000).exists() }
```
