---
sidebar_position: 2
---

# Connect to project

The framework has three libraries that could be added as dependencies.

- `com.atiurin:ultron-compose` - could be used both for Android application and Compose Multiplatform UI tests
- `com.atiurin:ultron-android` - native Android UI tests based on Espresso(including web part) and UI Automator
- `com.atiurin:ultron-allure` - Allure report support for Android application UI tests and for Compose Multiplatform tests (desktop, iOS, macOS, js, wasmJs); see [Allure for Compose Multiplatform](../common/allure-multiplatform.md)

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

:::note `ultron-allure` is a Kotlin Multiplatform artifact
Since 2.7.0, when it gained Compose Multiplatform support, `com.atiurin:ultron-allure` is published like `ultron-common` and `ultron-compose`: a root module with Gradle module metadata, and the Android AAR in the `com.atiurin:ultron-allure-android` artifact. Gradle consumers need no change, the declaration above resolves the Android variant. Notations that address the file directly, such as `com.atiurin:ultron-allure:<latest_version>@aar`, and build tools that read only the POM (Maven, Bazel `rules_jvm_external`) must reference `com.atiurin:ultron-allure-android:<latest_version>` instead.
:::

### Upgrading from 2.6.x to 2.7.0

**New in 2.7.0:** [Allure for Compose Multiplatform](../common/allure-multiplatform.md) — the same `ultron-allure` module now reports desktop, iOS, macOS, js and wasmJs tests.

Code compiled against 2.6.x links without a rebuild. What can change for an existing Android project:

- **Artifact layout.** `com.atiurin:ultron-allure` is now a Kotlin Multiplatform root module, the AAR is `com.atiurin:ultron-allure-android` (see the note above). Gradle dependencies need no change; `@aar` notations, Maven and Bazel do.
- **`UltronTest.test`** got an optional `name` parameter in third position. `test { }`, `test(true) { }` and `test(true, false) { }` compile as before; a lambda passed positionally inside the parentheses, `test(false, false, { })`, does not — use a trailing lambda. Java callers use `test(false, false, null, block)`.
- **A failing `beforeTest`** now fails the test without running its blocks, and `afterTest` still runs. Before, the exception escaped before `afterTest`. An exception of `afterTest` is dropped when the test has already failed.
- **`beforeFirstTest`** runs once per class identified by its qualified name, so two classes with the same simple name in different packages each run it. See [UltronTest](../common/ultrontest.md).
- **`AllureAttachStrategy.TEST_FINISH`** is a new value. An exhaustive `when` over `AllureAttachStrategy` needs a branch for it.

### Compose Multiplatform UI tests

```kotlin
kotlin {
    sourceSets {
         commonTest.dependencies {
            implementation("com.atiurin:ultron-compose:<latest_version>")
            implementation("com.atiurin:ultron-allure:<latest_version>") // optional, 2.7.0+: Allure reports, see Allure for Compose Multiplatform
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