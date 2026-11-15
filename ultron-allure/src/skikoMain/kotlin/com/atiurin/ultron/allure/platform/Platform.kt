package com.atiurin.ultron.allure.platform

/** Short name of the current target, used for the Allure `host` label. */
internal expect fun platformName(): String

/**
 * Id of the Kotlin target the test runs on (`desktop`, `ios`, `macos`, `js`, `wasmJs`), finer than [platformName],
 * which is `web` for both js and wasmJs. Goes into the `historyId` and the `target` parameter of every result.
 */
internal expect fun platformTarget(): String

/**
 * `true` when [throwable] is a test framework's skip: JUnit 4's assumption failure (`org.junit.AssumptionViolatedException`,
 * `org.junit.internal.AssumptionViolatedException`) or JUnit 5's `org.opentest4j.TestAbortedException`, or a subclass.
 * Told by class name, so the module needs no JUnit; `false` on Native and js/wasmJs, which have no such exceptions.
 */
internal expect fun isTestSkip(throwable: Throwable): Boolean

/**
 * Runs [handler] when the process ends normally, where the runtime offers such a hook: a JVM shutdown hook on
 * desktop, `atexit` on Kotlin/Native. On js/wasmJs nothing is registered: a browser page has no end-of-process
 * event the test runner lets through, see "Test names in the browser" in allure-multiplatform.md.
 */
internal expect fun registerProcessExitHandler(handler: () -> Unit)
