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

### Android application instrumented UI tests
```kotlin
dependencies {
    androidTestImplementation("com.atiurin:ultron-compose:<latest_version>")
    androidTestImplementation("com.atiurin:ultron-android:<latest_version>")
    androidTestImplementation("com.atiurin:ultron-allure:<latest_version>")
}
```

### Compose Multiplatform UI tests

```kotlin
kotlin {
    sourceSets {
         commonTest.dependencies {
            implementation("com.atiurin:ultron-compose:<latest_version>")
        }
    }
}
```
Since Multiplatform support in alpha state it's possible to have some problems with `commonTest` usage.

In this case you can specify dependencies in relevant part.
```kotlin
kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant {
            ...
            dependencies {
                implementation("com.atiurin:ultron-compose:<latest_version>")
            }
        }
    }
    sourceSets {
        val desktopTest by getting {
            dependencies {
                implementation("com.atiurin:ultron-compose:<latest_version>")
            }
        }
    }
}
```