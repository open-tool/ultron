package com.atiurin.ultron.core.test

import com.atiurin.ultron.annotations.InternalUltronApi
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.utils.currentExecutionThread
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

/** The three blocks of [TestMethod]: `before { }`, `go { }` and `after { }`. */
enum class TestBlock { BEFORE, GO, AFTER }

/**
 * Observes the lifecycle of tests written with [UltronTest]: the `test { }` call and its blocks.
 * Reporters (for example the Allure module) register one through [UltronTestListeners]. Every method is
 * optional; a listener that throws is logged and never fails the test.
 */
interface UltronTestListener {
    val id: String

    /**
     * `test { }` entered; [testClass] is the qualified name of the [UltronTest] subclass where the platform
     * provides one (JVM, Native) and its simple name on js/wasmJs, [testName] the `name` given to
     * [UltronTest.test], or `null` when none was given. The same [testClass] is passed to every other method.
     */
    fun beforeTest(testClass: String, testName: String?) {}

    /** `test { }` finished, after the common `afterTest`; [throwable] is what the test is about to throw. */
    fun afterTest(testClass: String, throwable: Throwable?) {}

    fun beforeBlock(testClass: String, block: TestBlock) {}

    fun afterBlock(testClass: String, block: TestBlock, throwable: Throwable?) {}

    /**
     * The forms [UltronTest] calls, with the `test { }` the event belongs to. A listener that keeps state per test
     * binds it to [test] here: a `test { }` that finishes late (abandoned by a JUnit or mocha timeout) still calls
     * [afterBlock] and [afterTest] while a later one is in progress, and must not touch that one's state.
     * By default each delegates to the form without the test.
     */
    fun beforeTest(test: UltronTestListeners.ActiveTest) = beforeTest(test.testClass, test.testName)

    fun afterTest(test: UltronTestListeners.ActiveTest, throwable: Throwable?) = afterTest(test.testClass, throwable)

    fun beforeBlock(test: UltronTestListeners.ActiveTest, block: TestBlock) = beforeBlock(test.testClass, block)

    fun afterBlock(test: UltronTestListeners.ActiveTest, block: TestBlock, throwable: Throwable?) =
        afterBlock(test.testClass, block, throwable)
}

object UltronTestListeners {
    /** A `test { }` in progress: [testClass] and [testName] as the listeners' `beforeTest` received them. */
    @OptIn(InternalUltronApi::class)
    class ActiveTest internal constructor(
        val testClass: String,
        val testName: String?,
        outer: ActiveTest?,
        internal val thread: Any? = currentExecutionThread(),
    ) {
        /**
         * The `test { }` this one was called from, nested in its `go { }` on its thread; `null` for a top-level test.
         * A test that merely overlaps another (started on another thread while that one hangs) is top-level too, and
         * never gives that one back. js/wasmJs have no threads, and need none while the runner awaits each test (the
         * promise [UltronTest.test] returns there): a test still running when another starts is the one that called it.
         * A test abandoned by a mocha timeout, its `runUltronUiTest` hung, is taken for the enclosing one of the next.
         */
        val enclosing: ActiveTest? = outer?.takeIf { it.thread == thread && !it.ended }

        /** `true` once the test has returned or thrown. */
        var ended: Boolean = false
            internal set
    }

    private val listeners = mutableListOf<UltronTestListener>()

    /** Tests started and not ended yet, in start order; read from any thread, hence the lock. */
    private val liveTests = mutableListOf<ActiveTest>()
    private val liveTestsLock = SynchronizedObject()

    /**
     * The `test { }` running right now, `null` between tests. A listener registered from inside it (a reporter
     * configured in the common `beforeTest` or in `go { }`) reads it to catch up on the `beforeTest` it missed.
     */
    var activeTest: ActiveTest? = null
        private set

    /**
     * The innermost `test { }` in progress on the calling thread, `null` when the thread runs none, and always on
     * js/wasmJs, which has no threads to tell the tests apart. Unlike [activeTest] it names the test an operation
     * really belongs to while tests overlap: one abandoned by a JUnit timeout still runs on its own thread while a
     * later test runs on another.
     */
    @InternalUltronApi
    fun testOnCurrentThread(): ActiveTest? {
        val thread = currentExecutionThread() ?: return null
        return synchronized(liveTestsLock) { liveTests.lastOrNull { it.thread == thread } }
    }

    /** Registers [listener], replacing an earlier one with the same [UltronTestListener.id]. */
    fun register(listener: UltronTestListener) {
        listeners.removeAll { it.id == listener.id }
        listeners.add(listener)
    }

    fun unregister(id: String) {
        listeners.removeAll { it.id == id }
    }

    fun clear() = listeners.clear()

    fun all(): List<UltronTestListener> = listeners.toList()

    /** Returns the test now active, to be handed back to [fireAfterTest]. */
    @OptIn(InternalUltronApi::class)
    internal fun fireBeforeTest(testClass: String, testName: String?): ActiveTest {
        // The test of this thread first: one nested in a test abandoned by a timeout is that test's, even while
        // another test is the active one.
        val test = ActiveTest(testClass, testName, outer = testOnCurrentThread() ?: activeTest)
        activeTest = test
        synchronized(liveTestsLock) { liveTests.add(test) }
        each("beforeTest") { it.beforeTest(test) }
        return test
    }

    internal fun fireAfterTest(test: ActiveTest, throwable: Throwable?) {
        each("afterTest") { it.afterTest(test, throwable) }
        test.ended = true
        synchronized(liveTestsLock) { liveTests.remove(test) }
        // A `test { }` nested in another gives the enclosing one back; only this test's entry is cleared. A test it
        // merely overlapped is never given back, ended or hung: no test is active then.
        if (activeTest === test) activeTest = test.enclosing
    }
    internal fun fireBeforeBlock(test: ActiveTest, block: TestBlock) = each("beforeBlock") { it.beforeBlock(test, block) }
    internal fun fireAfterBlock(test: ActiveTest, block: TestBlock, throwable: Throwable?) =
        each("afterBlock") { it.afterBlock(test, block, throwable) }

    private inline fun each(event: String, action: (UltronTestListener) -> Unit) = listeners.toList().forEach { listener ->
        runCatching { action(listener) }
            .onFailure { UltronLog.error("UltronTest listener '${listener.id}' failed in $event: ${it.message}") }
    }
}
