---
sidebar_position: 4
---

# Listeners

The framework has 2 types of listeners: UltronLifecycleListener & UltronRunListener

## UltronLifecycleListener

This one allows you to listen all stages of **Operation execution**. 

```kotlin
abstract class UltronLifecycleListener {
    /**
     * executed before any action or assertion
     */
    override fun before(operation: Operation) = Unit

    /**
     * called when action or assertion failed
     */
    override fun afterFailure(operationResult: OperationResult<Operation>) = Unit
    /**
     * called when action or assertion has been executed successfully
     */
    override fun afterSuccess(operationResult: OperationResult<Operation>) = Unit
    /**
     * called in any case of action or assertion result
     */
    override fun after(operationResult: OperationResult<Operation>) = Unit    
}
```
`Operation` object contains all info about operation (name, description, type, timeout)

`OperationResult` object contains all info about operation result (success, all exceptions that occured and exception that was thrown, description etc) and also has a reference to `Operation`.

All listener methods will be executed before an exception will be thrown. It gives you a guarantee that all exceptions in your tests will be processed  as you want.

### Log operation example

For instance, here is a listener that logs everything to Ultron log.
```kotlin
class LogLifecycleListener : UltronLifecycleListener() {
    override fun before(operation: Operation) {
        UltronLog.info("Start execution of ${operation.name}")
    }

    override fun afterSuccess(operationResult: OperationResult<Operation>) {
        UltronLog.info("Successfully executed ${operationResult.operation.name}")
    }

    override fun afterFailure(operationResult: OperationResult<Operation>) {
        UltronLog.error("Failed ${operationResult.operation.name} with description: \n" +
                "${operationResult.description} ")
    }
}
```

You can create you own custom listener in the same way.

```kotlin
class CustomLifecycleListener : UltronLifecycleListener() {...}
```

Add new listener for Ultron operations using `UltronCommonConfig.addListener()`.

```kotlin
abstract class BaseTest {
    companion object {
        @BeforeClass @JvmStatic
        fun configureUltron() {
            UltronCommonConfig.addListener(CustomLifecycleListener())
        }
    }
}
```

### Configuration

Basically we already know how to add new listener. But there are other options to configure Ultron listeners.

First of all Ultron by default already has [LogLifecycleListener](https://github.com/alex-tiurin/ultron/blob/master/ultron/src/main/java/com/atiurin/ultron/listeners/LogLifecycleListener.kt) that writes some usable info to logcat.

### Lifecycles

Ultron has 4 different lifecycles that watch for different operations.
- UltronEspressoOperationLifecycle
- UltronWebLifecycle (WebView operations)
- UltronUiAutomatorLifecycle
- UltronComposeOperationLifecycle

It is possible to add listener for any of these lifecycles.

`UltronUiAutomatorLifecycle.addListener(CustomLifecycleListener())`

In this case `CustomLifecycleListener` will be applied only for UI Automator operations.

### Exclude operation from listeners monitor

Ultron allows it to exclude operation from all listeners. This option is based on operation type.

For example, you've created a new operation

```kotlin
enum class CustomUltronOperations : UltronOperationType {
   ASSERT_HAS_ANY_CHILD
}
fun UltronUiObject2.assertHasAnyChild() = apply {
    executeAssertion(
            assertionBlock = { uiObject2ProviderBlock()!!.childCount > 0 },
            name = "Assert $selectorDesc has any child",
            type = CustomUltronOperations.ASSERT_HAS_ANY_CHILD,
            description = "UiObject2 assertion '${CustomUltronOperations.ASSERT_HAS_ANY_CHILD}' of $selectorDesc during $timeoutMs ms",
            timeoutMs = timeoutMs,
            resultHandler = resultHandler
    )
}
```
And you would like to exclude it from listeners for any reason no matter why.

Add single line to Ultron configuration function.

```kotlin
abstract class BaseTest {
    companion object {
        @BeforeClass @JvmStatic
        fun configureUltron() {
            ... 
            UltronCommonConfig.operationsExcludedFromListeners.add(CustomUltronOperations.ASSERT_HAS_ANY_CHILD)
        }
    }
}
```

## UltronRunListener

Allows you to add listener for Test Lifecycle. See [RunListener](https://github.com/open-tool/ultron/blob/master/ultron/src/main/java/com/atiurin/ultron/runner/RunListener.kt).

It is available in case you use `ultron-allure` and set `testInstrumentationRunner`.

```kotlin
testInstrumentationRunner = "com.atiurin.ultron.allure.UltronAllureTestRunner"
```

It could be used, for instance, to attach your custom application log to Allure Report.

```kotlin
class AppLogAttachRunListener() : UltronRunListener() {
    override fun testFailure(failure: Failure) {
        val logFile: File = AppLogProvider.provide()
        val fileName = AttachUtil.attachFile(
            name = "app_log_file",
            file = logFile,
            mimeType = MimeType.PLAIN_TEXT
        )
    }
}
```

Add custom RunListener to Allure config.
```kotlin
@BeforeClass @JvmStatic
fun configureUltron() {
    ...
    UltronAllureConfig.addRunListener(AppLogAttachRunListener())
}
```