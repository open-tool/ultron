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
}
```

UI Automator comes from `ultron-common` (its android source set declares `api(Libs.uiautomator)`), so it is available transitively as well.

If you need another Espresso library in dependencies. It's better to use the same Espresso version as Ultron. 

Now - [Ultron Espresso verion](https://github.com/open-tool/ultron/blob/master/buildSrc/src/main/kotlin/Versions.kt) is `3.7.0`. 

## Allure Dependencies

The `com.atiurin:ultron-allure:<latest_version>` library is multiplatform. Its Android variant provides all Allure dependencies:

```kotlin
androidMain.dependencies {
    api(libs.allure.kotlin.android)   // io.qameta.allure:allure-kotlin-android:2.4.0
    api(libs.allure.kotlin.commons)
    api(libs.allure.kotlin.model)
    api(libs.allure.kotlin.junit4)
    api(libs.androidx.espresso.core)
}
```

The other targets (desktop, iOS, macOS, js, wasmJs) depend on `ultron-common`, `ultron-compose` and `compose-ui-test` and write Allure 2 result files themselves; the writer adds `kotlinx-serialization-json` on those targets only (`okio` and `kotlinx-datetime` come with `ultron-common` on every target). The Android variant has exactly the dependencies listed above, nothing from the Compose Multiplatform writer.

## Compose Dependencies

The `com.atiurin:ultron-compose:<latest_version>` library provides `androidx.compose.ui:ui-test-junit4`

```kotlin
dependencies {
    api(Libs.composeUiTest)
}
```
