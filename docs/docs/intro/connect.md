---
sidebar_position: 2
---

# Connect to project

The framework has three libraries that could be added as dependencies.

- `com.atiurin:ultron-compose` - could be used both for Android application and Compose Multiplatform UI tests
- `com.atiurin:ultron-android` - native Android UI tests based on Espresso(including web part) and UI Automator
- `com.atiurin:ultron-allure` - Allure report support for Android application UI tests 

You need **mavenCentral** repository.

```kotlin
repositories {
    mavenCentral()
}
```

For Android application instrumented UI tests
```kotlin
dependencies {
    androidTestImplementation("com.atiurin:ultron-compose:<latest_version>")
    androidTestImplementation("com.atiurin:ultron-android:<latest_version>")
    androidTestImplementation("com.atiurin:ultron-allure:<latest_version>")
}
```

For Compose Multiplatform UI tests
```kotlin
kotlin {
    sourceSets {
         commonTest.dependencies {
            implementation("com.atiurin:ultron-compose:<latest_version>")
        }
    }
}
```