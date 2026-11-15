package com.atiurin.ultron.core.test

import kotlin.js.Promise
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

/** The JS `Promise` the browser runner awaits, as `kotlinx.coroutines.test.TestResult` is on wasmJs. */
@JsName("Promise")
actual external class UltronTestResult : JsAny {
    fun then(onFulfilled: (JsAny?) -> Unit, onRejected: (JsAny) -> Unit): UltronTestResult
}

internal actual fun isPendingResult(value: Any?): Boolean = value is Promise<*>

internal actual fun whenSettled(pending: Any, callback: (Throwable?) -> Unit) {
    (pending as Promise<*>).then(
        onFulfilled = { callback(null); null },
        onRejected = { e -> callback(e.toThrowable()); null },
    )
}

/** A Kotlin throwable comes back as itself (it crossed JS as a reference); anything else is a JS error. */
private fun JsAny.toThrowable(): Throwable = (this as Any) as? Throwable ?: IllegalStateException(toString())

internal actual fun completedTestResult(): UltronTestResult = Promise.resolve(null as JsAny?).unsafeCast<UltronTestResult>()

internal actual fun pendingTestResult(register: (settle: (Throwable?) -> Unit) -> Unit): UltronTestResult =
    Promise<JsAny?> { resolve, reject -> register { t -> if (t == null) resolve(null) else reject(t.toJsReference()) } }
        .unsafeCast<UltronTestResult>()

@OptIn(DelicateCoroutinesApi::class)
internal actual fun startPending(work: suspend () -> Unit): Any? = GlobalScope.promise<JsAny?> {
    work()
    null
}
