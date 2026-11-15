package com.atiurin.ultron.core.compose

import com.atiurin.ultron.annotations.InternalUltronApi
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.utils.currentExecutionThread
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.CancellationException

/**
 * Observer of test boundaries opened by [runUltronUiTest]. Reporters (Allure) register one.
 * Hooks never affect the test: exceptions thrown by a hook are logged and swallowed.
 */
interface UltronTestRunHook {
    /** Stable id; registering a hook with the same id replaces the previous one. */
    val id: String get() = this::class.simpleName.orEmpty()

    /** [testName] is the explicit name passed to the entry point, or `null` when the test gave none. */
    fun beforeTest(testName: String?) {}

    /**
     * Called while the Compose scene is still alive, right after the test block threw [throwable] and
     * before [afterTest]. This is the place to capture screenshots or semantics dumps: by the time
     * [afterTest] runs, `runComposeUiTest` has already torn the scene down.
     */
    fun onTestFailed(throwable: Throwable) {}

    /** [throwable] is what the test block threw, or `null` when it passed. The scene is closed by now. */
    fun afterTest(throwable: Throwable?) {}

    /**
     * The forms `runUltronUiTest` calls, with the block the callback belongs to. A hook that keeps state per test
     * binds it to [run] here: a block that finishes late (abandoned by a JUnit or mocha timeout) still calls
     * [onTestFailed] and [afterTest] while a later block is in progress, and must not touch that block's test.
     * By default each delegates to the form without the run.
     */
    fun beforeTest(run: UltronTestRunHooks.ActiveRun) = beforeTest(run.testName)

    fun onTestFailed(run: UltronTestRunHooks.ActiveRun, throwable: Throwable) = onTestFailed(throwable)

    fun afterTest(run: UltronTestRunHooks.ActiveRun, throwable: Throwable?) = afterTest(throwable)

    /**
     * Called after [afterTest] of [run], before `runUltronUiTest` returns, still inside its `runComposeUiTest`: the
     * block ends only when this returns. For work [afterTest] started but could not finish synchronously (the browser
     * encodes video through Promises, and Karma closes the page once the last test's promise resolves). A hook must
     * bound the wait itself. Returns at once by default.
     */
    suspend fun awaitAfterTest(run: UltronTestRunHooks.ActiveRun) {}
}

object UltronTestRunHooks {
    /** A `runUltronUiTest` block in progress: [testName] is what its hooks received in `beforeTest`. */
    @OptIn(InternalUltronApi::class)
    class ActiveRun internal constructor(
        val testName: String?,
        outer: ActiveRun?,
        internal val thread: Any? = currentExecutionThread(),
    ) {
        /**
         * The block this one was called from, `runUltronUiTest` nested in the body of another on its thread;
         * `null` for a top-level block. A block that merely overlaps another (started on another thread while
         * that one hangs, or on js/wasmJs, where blocks interleave through their promises) is top-level too, and
         * never gives that one back.
         */
        val enclosing: ActiveRun? = outer?.takeIf { thread != null && it.thread == thread && !it.ended }

        /** `true` once the block has returned or thrown. */
        var ended: Boolean = false
            internal set
    }

    private val hooks = mutableListOf<UltronTestRunHook>()

    /** Blocks started and not ended yet, in start order; read from any thread, hence the lock. */
    private val liveRuns = mutableListOf<ActiveRun>()
    private val liveRunsLock = SynchronizedObject()

    /**
     * The `runUltronUiTest` block running right now, `null` between blocks. A reporter registered from inside
     * the block (a config applied in `beforeFirstTest`) reads it to open the test the hooks' `beforeTest` would
     * have opened had the reporter been registered at the start.
     */
    var activeRun: ActiveRun? = null
        private set

    /**
     * The innermost block in progress on the calling thread, `null` when the thread runs none, and always on
     * js/wasmJs, which has no threads to tell the blocks apart. Unlike [activeRun] it names the block an operation
     * really belongs to while blocks overlap: one abandoned by a JUnit timeout still runs on its own thread while a
     * later block runs on another.
     */
    @InternalUltronApi
    fun runOnCurrentThread(): ActiveRun? {
        val thread = currentExecutionThread() ?: return null
        return synchronized(liveRunsLock) { liveRuns.lastOrNull { it.thread == thread } }
    }

    fun register(hook: UltronTestRunHook) {
        hooks.removeAll { it.id == hook.id }
        hooks.add(hook)
    }

    fun unregister(id: String) {
        hooks.removeAll { it.id == id }
    }

    fun clear() = hooks.clear()

    /** The single registered hook, or `null`. Convenience for the common one-reporter case. */
    fun current(): UltronTestRunHook? = hooks.singleOrNull()

    fun all(): List<UltronTestRunHook> = hooks.toList()

    // Each fire* walks a copy: a hook may register or unregister (itself included) from its callback, as
    // UltronTestListeners allows, without a ConcurrentModificationException escaping runCatching.

    internal fun fireBefore(run: ActiveRun) = hooks.toList().forEach { hook ->
        runCatching { hook.beforeTest(run) }
            .onFailure { UltronLog.error("Test run hook '${hook.id}' failed in beforeTest: ${it.message}") }
    }

    internal fun fireFailed(run: ActiveRun, throwable: Throwable) = hooks.toList().forEach { hook ->
        runCatching { hook.onTestFailed(run, throwable) }
            .onFailure { UltronLog.error("Test run hook '${hook.id}' failed in onTestFailed: ${it.message}") }
    }

    internal fun fireAfter(run: ActiveRun, throwable: Throwable?) = hooks.toList().forEach { hook ->
        runCatching { hook.afterTest(run, throwable) }
            .onFailure { UltronLog.error("Test run hook '${hook.id}' failed in afterTest: ${it.message}") }
    }

    internal suspend fun fireAwait(run: ActiveRun) = hooks.toList().forEach { hook ->
        try {
            hook.awaitAfterTest(run)
        } catch (c: CancellationException) {
            throw c
        } catch (t: Throwable) {
            UltronLog.error("Test run hook '${hook.id}' failed in awaitAfterTest: ${t.message}")
        }
    }

    /**
     * Runs the test body between the hooks, inside the Compose scene. This must be called from the
     * `runComposeUiTest` lambda: on js/wasmJs that lambda is a promise, so hooks placed around the
     * `runComposeUiTest` call itself would fire before the body has run.
     */
    @OptIn(InternalUltronApi::class)
    internal suspend inline fun around(testName: String?, block: suspend () -> Unit) {
        // The block of this thread first: one nested in a block abandoned by a timeout is that block's, even while
        // another block is the active one.
        val run = ActiveRun(testName, outer = runOnCurrentThread() ?: activeRun)
        activeRun = run
        synchronized(liveRunsLock) { liveRuns.add(run) }
        fireBefore(run)
        var failure: Throwable? = null
        try {
            block()
        } catch (t: Throwable) {
            failure = t
            fireFailed(run, t)
            throw t
        } finally {
            fireAfter(run, failure)
            run.ended = true
            synchronized(liveRunsLock) { liveRuns.remove(run) }
            // Only this call's run is cleared: a nested `runUltronUiTest` gives the enclosing block its run back,
            // and a block that finishes late (a hung js/wasmJs promise) does not clear the run of the block in progress.
            // A run this one merely overlapped (abandoned by a JUnit or mocha timeout, ended meanwhile or still hung)
            // is never given back: no block is active then.
            if (activeRun === run) activeRun = run.enclosing
            fireAwait(run)
        }
    }
}
