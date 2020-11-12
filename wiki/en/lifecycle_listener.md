# Lifecycle Listeners

There is an interface to listen all actions and assertions in your test.
It provides 4 methods to catch that's happening while operation execution.

```kotlin
internal interface LifecycleListener{
    /**
     * executed before any action or assertion
     */
    fun before(operation: Operation)
    /**
     * called when action or assertion has been executed successfully
     */
    fun afterSuccess(operationResult: OperationResult)

    /**
     * called when action or assertion failed
     */
    fun afterFailure(operationResult: OperationResult)

    /**
     * called in any case of action or assertion result
     */
    fun after(operationResult: OperationResult)
}
```
*Operation* and *OperationResult* are interfaces. They provide an access to all necessary info about executed operation and it result.

To use `LifecycleListener` interface you need to create a child class of `AbstractLifecycleListener`.
For example, let's create screenshot listener which will make a screenshot in case of action or assertion failure.
```kotlin
class ScreenshotLifecycleListener : AbstractLifecycleListener(){
    override fun afterFailure(description: Description, throwable: Throwable) {
        takeScreenshot(description.type.toString()) // takeScreenshot() isn't implemented
    }
}
```
To add new listener to lifecycle:
```kotlin
companion object {
    @BeforeClass @JvmStatic
    fun beforeClass() {
        val listener = ScreenshotLifecycleListener()
        ViewActionLifecycle.addListener(listener)
        ViewAssertionLifecycle.addListener(listener)
    }
}
```

There is an implementation of a `LogLifecycleListener` and it has been
added to ViewActionLifecycle and ViewAssertionLifecycle listeners by
default.

If you want to drop the list of listeners use:
```kotlin
ViewActionLifecycle.clearListeners()
ViewAssertionLifecycle.clearListeners()
```

*Note that heavy listeners could slow down your tests speed!*