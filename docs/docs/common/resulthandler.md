---
sidebar_position: 7
---

# Result handler

**Ultron** allows you to process the result of any operation in your own custom way. It provides full info to do that.

Let's loot at the example

```kotlin
object SomePage : Page<SomePage>{
    private val espressoElement = withId(R.id.espressoId)
    private val espressoWebViewElement = xpath("some_xpath")
    private val uiautomatorElement = byResId(R.id.uiatomatorId)
    private val composeElement = hasTestTag("some_tag")
}
```
Now, we want to catch the result of operation and do smth reasonable. There is a method that opens the door - `withResultHandler`
```kotlin
espressoElement.withResultHandler { operationResult ->
    // smth that make sense
}
```
What it gives to us?

![resultHandler](https://user-images.githubusercontent.com/12834123/113351564-bc872f00-9343-11eb-925a-432dbc191b32.png)

**_A little explanation in case you would like to be more familiar with **Ultron** framework_**

There is an entity which we call `ResultHandler`. By default all Ultron operations has the same `ResultHandler`. 
It catches the result of operation and asks `OperationResultAnalyzer` to analyze the result. 
In case `operationResult.success` is `false` the result analyzer throws catched exception.

## How to use?
There are 2 ways of using custom ResultHandler:
- Specify it for page property and it will be applied for all operations with this element
```kotlin
object SomePage : Page<SomePage>() {
    private val eventStatus = withId(R.id.last_event_status).withResultHandler { operationResult ->
        // smth that make sense
    }
}
```
- Specify it inside special step there the element operation should be processed in different way.
This ResultHandler will be applied only once for single operation.

```kotlin
object SomePage : Page<SomePage>() {
    fun someSpecificUserStep(expectedEventText: String){
         eventStatus.withResultHandler { operationResult ->
             // smth that make sense
         }.hasText(expectedEventText)
    }
}
```