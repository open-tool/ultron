package com.atiurin.ultron.core.test

import com.atiurin.ultron.annotations.InternalUltronApi

/**
 * What [UltronTest.test] returns. `Unit` on JVM, Android and Native, where the test runs to its end inside the call;
 * a `Promise` on js/wasmJs, settled once the test has finished, `after { }` and `afterTest` included. Return it from
 * the test function, `@Test fun login() = test { }`, so that the browser runner awaits a `runUltronUiTest` started
 * in one of the test's blocks: its body runs only after the call has returned.
 */
expect class UltronTestResult

/** `true` for a value the runtime settles later (a `Promise` on js/wasmJs); never on JVM, Android and Native. */
internal expect fun isPendingResult(value: Any?): Boolean

/** Calls [callback] once [pending] (see [isPendingResult]) has settled, with its failure or `null`. */
internal expect fun whenSettled(pending: Any, callback: (Throwable?) -> Unit)

/** The result of a test that has already finished without a failure. */
internal expect fun completedTestResult(): UltronTestResult

/** A result settled by the callback handed to [register]: with its throwable, or successfully for `null`. */
internal expect fun pendingTestResult(register: (settle: (Throwable?) -> Unit) -> Unit): UltronTestResult

/**
 * Starts [work] and returns what [whenSettled] can wait for; where nothing is ever pending, runs it to its end and
 * returns `null`.
 */
internal expect fun startPending(work: suspend () -> Unit): Any?

/**
 * Lets the blocks of [UltronTest.test] start asynchronous work the test waits for. On js/wasmJs `runUltronUiTest`
 * returns a promise whose body runs after the block that called it has returned; the block hands the promise to
 * [await], and the test runs its next block (and closes its report) only once the promise has settled. Everywhere
 * else nothing is ever pending and the test runs synchronously, as before.
 */
@InternalUltronApi
object UltronTestAsync {
    /** The pending results of the step running right now; `null` outside of a step. */
    private var collector: MutableList<Any>? = null

    /**
     * Makes the step of [UltronTest.test] running right now wait for [result] when it is pending (see
     * [isPendingResult]); a no-op for anything else, or outside of a test.
     */
    fun await(result: Any?) {
        if (isPendingResult(result)) collector?.add(result!!)
    }

    /**
     * Makes the step of [UltronTest.test] running right now wait for [work], started here; a no-op outside of a test.
     * For a listener whose `afterTest` leaves work behind (a browser video still being encoded): the test's promise
     * settles only when [work] has ended. The wait is the caller's to bound.
     */
    fun awaitWork(work: suspend () -> Unit) {
        val pending = collector ?: return
        startPending(work)?.let(pending::add)
    }

    /**
     * Runs [body], then calls [then] with its failure once it has returned and everything it handed to [await] has
     * settled: synchronously when nothing is pending. A failure of [body] wins over the failures of what it awaited.
     */
    internal fun step(body: () -> Unit, then: (Throwable?) -> Unit) {
        val outer = collector
        val pending = mutableListOf<Any>()
        collector = pending
        val failure = try {
            body()
            null
        } catch (t: Throwable) {
            t
        } finally {
            collector = outer
        }
        awaitAll(pending, failure, then)
    }

    private fun awaitAll(pending: List<Any>, failure: Throwable?, then: (Throwable?) -> Unit) {
        if (pending.isEmpty()) return then(failure)
        whenSettled(pending.first()) { t -> awaitAll(pending.drop(1), failure ?: t, then) }
    }

    /**
     * Runs [flow], which reports its end through `finish`. Finished synchronously, its failure is thrown here, as
     * a synchronous test does; otherwise the pending result settles with it.
     */
    internal fun result(flow: (finish: (Throwable?) -> Unit) -> Unit): UltronTestResult {
        var finished = false
        var failure: Throwable? = null
        var late: ((Throwable?) -> Unit)? = null
        flow { t ->
            val settle = late
            if (settle != null) settle(t) else {
                finished = true
                failure = t
            }
        }
        if (finished) {
            failure?.let { throw it }
            return completedTestResult()
        }
        return pendingTestResult { settle -> late = settle }
    }
}
