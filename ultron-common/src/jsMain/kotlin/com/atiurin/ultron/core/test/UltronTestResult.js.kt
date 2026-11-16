package com.atiurin.ultron.core.test

import kotlin.js.Promise
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

/** The JS `Promise` the browser runner awaits, as `kotlinx.coroutines.test.TestResult` is on js. */
@JsName("Promise")
actual external class UltronTestResult {
    fun then(onFulfilled: (Unit) -> Unit, onRejected: (Throwable) -> Unit): UltronTestResult
}

internal actual fun isPendingResult(value: Any?): Boolean = value is Promise<*>

internal actual fun whenSettled(pending: Any, callback: (Throwable?) -> Unit) {
    pending.unsafeCast<Promise<Any?>>().then({ callback(null) }, { e -> callback(e) })
}

internal actual fun completedTestResult(): UltronTestResult = Promise.resolve(Unit).unsafeCast<UltronTestResult>()

internal actual fun pendingTestResult(register: (settle: (Throwable?) -> Unit) -> Unit): UltronTestResult =
    Promise { resolve, reject -> register { t -> if (t == null) resolve(Unit) else reject(t) } }.unsafeCast<UltronTestResult>()

@OptIn(DelicateCoroutinesApi::class)
internal actual fun startPending(work: suspend () -> Unit): Any? = GlobalScope.promise { work() }
