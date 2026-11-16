package com.atiurin.ultron.core.test

import kotlinx.coroutines.runBlocking

/** The test has finished when `test { }` returns: `Unit`, which keeps `test` a `void` method on the JVM. */
actual typealias UltronTestResult = Unit

internal actual fun isPendingResult(value: Any?): Boolean = false

internal actual fun whenSettled(pending: Any, callback: (Throwable?) -> Unit): Unit =
    error("Nothing is ever pending on this platform")

internal actual fun completedTestResult(): UltronTestResult = Unit

internal actual fun pendingTestResult(register: (settle: (Throwable?) -> Unit) -> Unit): UltronTestResult =
    error("Nothing is ever pending on this platform")

internal actual fun startPending(work: suspend () -> Unit): Any? {
    runBlocking { work() }
    return null
}
