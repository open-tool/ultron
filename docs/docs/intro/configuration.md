---
sidebar_position: 4
---

# Configuration

Each library of the framework has it's own config onject. 

- `UltronComposeConfig` - ultron-compose
- `UltronConfig` - ultron-android
- `UltronAllureConfig` - ultron-allure
- `UltronCommonConfig` - inside each library

You can use recommended configuration and just apply it in **BaseTest** class ([sample](https://github.com/open-tool/ultron/blob/master/sample-app/src/androidTest/java/com/atiurin/sampleapp/tests/BaseTest.kt#L29)) :

```kotlin
@BeforeClass
@JvmStatic
fun config() {
    UltronConfig.applyRecommended()
    UltronAllureConfig.applyRecommended()
    UltronComposeConfig.applyRecommended()
}

```

### UltronComposeConfig
***
Manages configurations for Compose part of the framework

```kotlin
UltronComposeConfig.apply {
    operationTimeoutMs = 10_000
    lazyColumnOperationTimeoutMs = 15_000
    operationPollingTimeoutMs = 100
    lazyColumnItemSearchLimit = 100
    useUnmergedTree = true //set up this value as a default for all SemanticNodeInteractions
}
```

### UltronCommonConfig
***
Provides an ability to config common parameters for your testing framework. 

```kotlin
UltronCommonConfig.apply {
    logToFile = true
    operationTimeoutMs = 10_000
    logDateFormat = "MM-dd HH:mm:ss.SSS"
}
```

It also gives an API to add/remove operations listeners

```kotlin
UltronCommonConfig.addListener(CustomListener())
```

### UltronConfig 
***
`UltronConfig` object is responsible for configuring and managing settings related to the Espresso, EspressoWeb, and UiAutomator. 

You can set custom main settings using `apply` method.

```kotlin
UltronConfig.apply {
    accelerateUiAutomator = true
    operationTimeoutMs = 10_000
}
```

- `UltronConfig.Espresso` nested Object:

Manages configurations specific to the Espresso part of the framework.
Provides settings related to timeouts, view matchers, result analyzers, and action/assertion configurations.

```kotlin
UltronConfig.Espresso.RECYCLER_VIEW_LOAD_TIMEOUT = 20_000
UltronConfig.Espresso.RECYCLER_VIEW_OPERATIONS_TIMEOUT = 10_000
UltronConfig.Espresso.RECYCLER_VIEW_ITEM_SEARCH_LIMIT = 100
UltronConfig.Espresso.INCLUDE_VIEW_HIERARCHY_TO_EXCEPTION = true // false by default
UltronConfig.Espresso.setResultAnalyzer { operationResult ->
    // set custom operations result analyzer 
}
```

- `UltronConfig.Espresso.ViewActionConfig` and `UltronConfig.Espresso.ViewAssertionConfig` nested Objects:

Manage configurations for Espresso view actions and view assertions, respectively.
Provide settings for allowed exceptions and result handlers.

```kotlin
UltronConfig.Espresso.ViewActionConfig.allowedExceptions.add(CustomViewException::class.java)
UltronConfig.Espresso.ViewAssertionConfig.allowedExceptions.add(CustomViewException::class.java)
```

- `UltronConfig.Espresso.WebInteractionOperationConfig` nested Object:

Manages configurations for Espresso web interaction operations.
Provides settings for allowed exceptions and result handlers.

```kotlin
UltronConfig.Espresso.WebInteractionOperationConfig.allowedExceptions.add(CustomJSException::class.java)
```

- `UltronConfig.UiAutomator` nested Object:

Manages configurations specific to the UiAutomator part of the framework.
Provides settings related to timeouts, result analyzers, and UiDevice configurations.

```kotlin
UltronConfig.UiAutomator.OPERATION_TIMEOUT = 15_000
val device = UltronConfig.UiAutomator.uiDevice
UltronConfig.UiAutomator.UiObject2Config.allowedExceptions.add(CustomViewException::class.java)
```

- `UltronConfig.UiAutomator.UiObjectConfig` and `UltronConfig.UiAutomator.UiObject2Config` nested Objects:

Manage configurations for UiAutomator operations using UiSelector and BySelector, respectively.
Provide settings for allowed exceptions and result handlers.

### UltronAllureConfig
***

Help us to configure Allure report.

```kotlin
UltronAllureConfig.apply {
    addScreenshotPolicy =  mutableSetOf(
        AllureAttachStrategy.TEST_FAILURE,
        AllureAttachStrategy.OPERATION_FAILURE,
        AllureAttachStrategy.OPERATION_SUCCESS
    )
    addHierarchyPolicy = mutableSetOf(
        AllureAttachStrategy.TEST_FAILURE
    )
    attachLogcat = false
    attachUltronLog = true
    addConditionsToReport = true
    detailedAllureReport = true
}
```

It also allow us to add or remove RunListener. 

```kotlin
UltronAllureConfig.addRunListener(LogcatAttachRunListener())
UltronAllureConfig.removeRunListener(LogcatAttachRunListener::class.java)
```

