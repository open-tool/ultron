---
sidebar_position: 3
---

# Dependencies Management

Ultron provides all the required dependencies in a transitive manner. That means you don't need to specify the Espresso or UI Automator library in your dependencies section in most cases.

You can find all Ultron dependencies in [Versions.kt](https://github.com/open-tool/ultron/blob/master/buildSrc/src/main/kotlin/Versions.kt).

## Android Dependencies

The `com.atiurin:ultron-android:<latest_version>` library provides:

```kotlin
dependencies {
    api(Libs.espressoCore)
    api(Libs.espressoContrib)
    api(Libs.espressoWeb)
    api(Libs.accessibility)
    api(Libs.hamcrestCore)
    api(Libs.uiautomator)
}
```

If you need another Espresso library in dependencies. It's better to use the same Espresso version as Ultron. 

Now - [Ultron Espresso verion](https://github.com/open-tool/ultron/blob/1c81014f4cbea97b2f24128831a13e601936ef57/buildSrc/src/main/kotlin/Versions.kt#L9) is `3.4.0`. 
We don't update to `3.5.1` because it brings only a set of problems and nothing improve.

## Allure Dependencies

The `com.atiurin:ultron-allure:<latest_version>` library provides all Allure dependencies.

```kotlin
dependencies {
    api(Libs.allureAndroid)
    api(Libs.allureCommon)
    api(Libs.allureModel)
    api(Libs.allureJunit4)
    api(Libs.espressoCore)
}
```

## Compose Dependencies

The `com.atiurin:ultron-compose:<latest_version>` library provides `androidx.compose.ui:ui-test-junit4`

```kotlin
dependencies {
    api(Libs.composeUiTest)
}
```
