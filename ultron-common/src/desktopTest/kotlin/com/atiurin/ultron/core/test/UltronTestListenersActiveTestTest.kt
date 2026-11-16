package com.atiurin.ultron.core.test

import com.atiurin.ultron.annotations.InternalUltronApi
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/** `UltronTestListeners.activeTest` names the `test { }` in progress, for a listener registered from inside it. */
class UltronTestListenersActiveTestTest {
    class Scenario : UltronTest() {
        var seen: Pair<String, String?>? = null
        fun run(name: String?, fail: Boolean = false) = test(name = name) {
            go {
                seen = UltronTestListeners.activeTest?.let { it.testClass to it.testName }
                if (fail) throw AssertionError("boom")
            }
        }
    }

    class NestedScenario : UltronTest() {
        var seenAfterInner: String? = null
        fun run() = test(name = "outer") {
            go {
                test(name = "inner") { }
                seenAfterInner = UltronTestListeners.activeTest?.testName
            }
        }
    }

    class BodyScenario : UltronTest() {
        fun run(name: String, body: () -> Unit) = test(name = name) { go { body() } }
    }

    @AfterTest
    fun cleanup() = UltronTestListeners.clear()

    @Test
    fun `inside test the active test carries the class and the explicit name`() {
        val scenario = Scenario()
        scenario.run("named")
        assertEquals("com.atiurin.ultron.core.test.UltronTestListenersActiveTestTest.Scenario" to "named", scenario.seen)
        assertNull(UltronTestListeners.activeTest, "cleared when test { } returns")
    }

    @Test
    fun `an unnamed test has a null name and a failing one still clears the active test`() {
        val scenario = Scenario()
        assertFailsWith<AssertionError> { scenario.run(null, fail = true) }
        assertEquals("com.atiurin.ultron.core.test.UltronTestListenersActiveTestTest.Scenario" to null, scenario.seen)
        assertNull(UltronTestListeners.activeTest)
    }

    @Test
    fun `a test nested in another gives the outer one back as the active test when it returns`() {
        val scenario = NestedScenario()
        scenario.run()
        assertEquals("outer", scenario.seenAfterInner)
        assertNull(UltronTestListeners.activeTest)
    }

    /**
     * Test A hangs on its own thread (a JUnit timeout abandoned it) when test B starts on another one. B is not nested
     * in A: when B returns, no test is active, rather than A, which is still running but is nobody's enclosing test.
     */
    @Test
    fun `a test overlapping a hung one on another thread does not give the hung one back when it returns`() {
        val started = CountDownLatch(1)
        val release = CountDownLatch(1)
        val hung = thread(name = "hung test") {
            BodyScenario().run("hung") { started.countDown(); release.await(10, TimeUnit.SECONDS) }
        }
        try {
            assertTrue(started.await(10, TimeUnit.SECONDS), "precondition: the hung test is running")
            Scenario().run("next")
            assertNull(UltronTestListeners.activeTest, "the hung test is not the enclosing test of the next one")
        } finally {
            release.countDown()
            hung.join(10_000)
        }
    }

    /** A test that ended while a later one ran is never given back when the later one returns. */
    @Test
    fun `a test ending while a later one runs is not given back when the later one returns`() {
        val started = CountDownLatch(1)
        val release = CountDownLatch(1)
        val late = thread(name = "late test") {
            BodyScenario().run("late") { started.countDown(); release.await(10, TimeUnit.SECONDS) }
        }
        assertTrue(started.await(10, TimeUnit.SECONDS), "precondition: the late test is running")
        BodyScenario().run("next") { release.countDown(); late.join(10_000) }
        assertNull(UltronTestListeners.activeTest, "the late test already ended")
    }

    /** Each thread finds its own `test { }`, also while a test of another thread is the active one. */
    @OptIn(InternalUltronApi::class)
    @Test
    fun `the test on the current thread is the one that thread runs`() {
        val started = CountDownLatch(1)
        val release = CountDownLatch(1)
        var seenByLate: String? = null
        val late = thread(name = "late test") {
            BodyScenario().run("late") {
                started.countDown()
                release.await(10, TimeUnit.SECONDS)
                seenByLate = UltronTestListeners.testOnCurrentThread()?.testName
            }
        }
        assertTrue(started.await(10, TimeUnit.SECONDS), "precondition: the late test is running")
        var seenByNext: String? = null
        BodyScenario().run("next") {
            seenByNext = UltronTestListeners.testOnCurrentThread()?.testName
            release.countDown()
            late.join(10_000)
        }
        assertEquals("next", seenByNext)
        assertEquals("late", seenByLate)
        assertNull(UltronTestListeners.testOnCurrentThread(), "no test runs on this thread any more")
    }

    /**
     * js/wasmJs have no threads (`thread == null`); `test { }` is synchronous there, so a test still running when
     * another one starts is the one it was called from: the inner one is nested, not overlapping.
     */
    @Test
    fun `without threads a test started inside a running one is nested in it`() {
        val outer = UltronTestListeners.ActiveTest("Cls", "outer", outer = null, thread = null)
        val inner = UltronTestListeners.ActiveTest("Cls", "inner", outer = outer, thread = null)
        assertSame(outer, inner.enclosing)

        outer.ended = true
        assertNull(UltronTestListeners.ActiveTest("Cls", "after", outer = outer, thread = null).enclosing, "an ended test encloses nothing")
    }
}
