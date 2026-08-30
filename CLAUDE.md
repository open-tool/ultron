# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this repository is

Ultron — UI testing framework for **Android** (Espresso, UI Automator, Espresso Web) and **Compose Multiplatform** (Android, desktop/JVM, iOS, macOS, JS, wasmJs). Published to Maven Central as `com.atiurin:ultron-*`. Version lives in `gradle.properties` (`VERSION_NAME`).

Public docs: https://open-tool.github.io/ultron/ — the Docusaurus source is `docs/docs/` in this repo, so **doc changes belong in the same PR as the API change**.

## Modules

| Module | Type | Contents |
|---|---|---|
| `ultron-common` | KMP (android, desktop jvm, ios, macos, js, wasmJs) | Operation execution core, config, listeners, logging, `Page`/`Screen`, `UltronTest` context |
| `ultron-android` | Android library (JVM only) | Espresso, Espresso Web, UI Automator, RecyclerView support; `UltronConfig` |
| `ultron-compose` | KMP (same targets as common) | Compose semantics interactions, LazyList/`UltronComposeList`, `UltronComposeConfig`, `runUltronUiTest` |
| `ultron-allure` | Android library | Allure artifacts generation: screenshots, view hierarchy, steps, listeners, runner |
| `sample-app` | Android app | Demo app + `androidTest` suite — the real integration tests for `ultron-android`/`ultron-allure` |
| `composeApp` | KMP app | Demo app + multiplatform tests for `ultron-compose` |

Dependency direction: `ultron-android`, `ultron-compose`, `ultron-allure` all depend on `ultron-common` via `api`. Nothing depends on the sample apps.

Sources live in `src/commonMain|androidMain|jvmMain|nativeMain|jsWasmMain|shared` for KMP modules and `src/main/kotlin` (or `src/main/java` for allure) for the Android-only ones. The `shared` source dir is an extra hand-wired source set — check the module's `build.gradle.kts` before adding files there.

## Build & test commands

Gradle wrapper, JDK 17, `org.gradle.configuration-cache=true` is on.

```bash
./gradlew build                       # everything
./gradlew :ultron-compose:assemble    # single module

# What CI compiles (.github/workflows/ci-pipeline.yml, runs on macos):
./gradlew compileDebugKotlin compileDebugKotlinAndroid compileKotlinDesktop \
          compileKotlinIosArm64 compileKotlinIosSimulatorArm64 compileKotlinJs compileKotlinWasmJs

# Unit tests (JVM)
./gradlew :ultron-compose:test :ultron-android:test :ultron-allure:test
./gradlew :ultron-android:test --tests "*SomeTest*"          # single test class

# Multiplatform tests of composeApp
./gradlew :composeApp:desktopTest
./gradlew :composeApp:jsTest
./gradlew :composeApp:iosSimulatorArm64Test

# Instrumented tests (device/emulator required)
./prepare-emulator.sh                                        # disables animations, run first
./gradlew :sample-app:connectedDebugAndroidTest
./gradlew :composeApp:connectedDebugAndroidTest
./gradlew :sample-app:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.atiurin.sampleapp.tests.espresso.DemoEspressoTest
```

Docs site (`docs/`, Docusaurus 3, Node):

```bash
cd docs && npm install && npm start     # local preview
npm run build                           # what .github/workflows/docs.yml does
```

Dependency versions live in **two** places: `gradle/libs.versions.toml` (version catalog, used by KMP modules) and `buildSrc/src/main/kotlin/Versions.kt` (`Versions`/`Libs`/`Plugins` objects, used by the Android-only modules and sample-app). Adding a dependency usually means picking the convention the target module already uses.

## Core architecture

Everything a user calls — `hasText("x").click()`, `withId(R.id.b).isDisplayed()` — funnels into one execution pipeline in `ultron-common`:

1. **`Operation`** (`core/common/Operation.kt`) — name, description, type, `timeoutMs`, an optional `OperationAssertion`, `ElementInfo`, and `execute(): OperationIterationResult`.
2. **`OperationExecutor`** (`core/common/OperationExecutor.kt`) — the stability engine. It retries `operation.execute()` in a loop until the timeout expires, sleeping `pollingTimeout` between iterations, but **only for allowed exceptions**; a non-allowed exception aborts immediately. After a successful operation it runs the assertion block and can re-enter the retry loop. It also builds the human-readable step description used by logs and Allure.
3. **`OperationResultAnalyzer`** (`core/common/resultanalyzer/`) — decides what a failed result means: throw, or accumulate as soft assertion (`SoftAssertionOperationResultAnalyzer`, wired through `UltronTestContext`).
4. **`AbstractOperationLifecycle`** / **listeners** (`core/common/AbstractOperationLifecycle.kt`, `listeners/`) — each module has its own lifecycle object (`UltronEspressoOperationLifecycle`, `UltronComposeOperationLifecycle`) that notifies registered `UltronLifecycleListener`s before/after each operation. This is the extension point `ultron-allure` hooks into to produce steps, screenshots and hierarchy dumps.

Each framework binding supplies its own `Operation` + `OperationExecutor` + `OperationResult` triple: `core/espresso/`, `core/espressoweb/`, `core/uiautomator/` in `ultron-android`, `core/compose/operation/` in `ultron-compose`.

### Public API is extension functions

There is no fluent builder to maintain — the user-facing surface is Kotlin extension functions over `Matcher<View>` / `SemanticsMatcher`, which construct the interaction wrapper and delegate to it:

- Compose: extend `UltronComposeSemanticsNodeInteraction` using `perform { }` (returns the interaction) or `execute { }` (returns a value), then add a `SemanticsMatcher.myOp()` shortcut. Optional `UltronComposeOperationParams` customizes the operation name/description/type shown in reports.
- Espresso: extend `UltronEspressoInteraction<T>` with `perform`, `execute` or `assertMatches`, then add the `Matcher<View>.myOp()` shortcut in `extensions/MatcherViewExt.kt` style.

**When adding a new operation, follow this shape** — do not bypass `perform`/`execute`, otherwise the operation loses retry, logging, listener and Allure integration. See `docs/docs/common/extension.md`.

### Configuration

Per-module config objects, all applied from a test's `@BeforeClass`: `UltronCommonConfig` (common timeouts, logging, listeners), `UltronConfig` (+ nested `Espresso`, `Espresso.ViewActionConfig`, `Espresso.ViewAssertionConfig`, `WebInteractionOperationConfig`, `UiAutomator`), `UltronComposeConfig`, `UltronAllureConfig`. Each has `applyRecommended()`. Config carries `allowedExceptions`, `isExceptionAllowed`, timeouts, and the result analyzer — i.e. it is what parameterizes the executor described above.

### Test lifecycle

`UltronTest` (see `docs/docs/common/ultrontest.md`) provides `beforeFirstTest` / `beforeTest` / `afterTest` plus a per-test `test { before { }.go { }.after { } }` DSL with `suppressCommonBefore/After`. Compose Multiplatform tests enter through `runUltronUiTest { }` (`ultron-compose/.../UltronUiTest.kt`), which initializes `ComposeTestContainer` with the platform `ComposeTestEnvironment` — that container is how common code reaches the current `ComposeUiTest` without passing a rule around.

## Release process

Read `RELEASE_PROCESS.md` before touching anything release-related. Key constraints:

- Releases are prepared with `scripts/prepare-ultron-release.sh <version>` from a clean `master` (`--dry-run` to preview). It edits **only** `gradle.properties` and `docs/docs/release-notes.md`.
- A release PR must come from branch `release/<version>`, target `master`, and change only those two files — `release_pr_guard.yml` enforces this.
- Merging the release PR is the authorization: CI tags the merge commit, publishes to Maven Central, creates the GitHub Release and posts to Telegram. Never create or move release tags by hand.
- The generated TODO highlight in the release notes must be replaced with human-written text before merge.

`scripts/test-release-tools.sh` and `scripts/test-release-workflows.sh` cover the release scripting itself.
