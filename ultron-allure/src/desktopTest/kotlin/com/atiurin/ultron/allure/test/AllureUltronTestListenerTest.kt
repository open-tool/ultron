package com.atiurin.ultron.allure.test

import com.atiurin.ultron.allure.AllureTestRunHook
import com.atiurin.ultron.allure.attach.ArtifactProvider
import com.atiurin.ultron.allure.attach.TestFailureAttacher
import com.atiurin.ultron.allure.config.AllureAttachStrategy
import com.atiurin.ultron.allure.discovery.DiscoveredTest
import com.atiurin.ultron.allure.model.AllureStatus
import com.atiurin.ultron.allure.testutil.Harness
import com.atiurin.ultron.core.test.TestBlock
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * `UltronTest` owns the Allure test when a test runs through it: the test opens at `test { }`, the
 * `before`/`after` blocks become steps, and `runUltronUiTest` inside `go` joins the open test instead of
 * starting a second one.
 */
class AllureUltronTestListenerTest {
    private val h = Harness()
    private val hook = AllureTestRunHook(
        lifecycle = h.lifecycle,
        failureAttacher = TestFailureAttacher(h.lifecycle, emptyList(), emptySet(), emptySet()),
        logAttacher = null,
    )
    private val listener = AllureUltronTestListener(h.lifecycle, hook::closeTest)

    private fun block(block: TestBlock, throwable: Throwable? = null, body: () -> Unit = {}) {
        listener.beforeBlock("MyTest", block)
        body()
        listener.afterBlock("MyTest", block, throwable)
    }

    @Test
    fun `before and after blocks are steps and runUltronUiTest names the test opened by UltronTest`() {
        listener.beforeTest("MyTest", null)
        block(TestBlock.BEFORE) { h.clock.tick() }
        block(TestBlock.GO) {
            hook.beforeTest("my scenario")
            h.lifecycle.startStep("click"); h.lifecycle.stopStep()
            hook.afterTest(null)
        }
        block(TestBlock.AFTER)
        listener.afterTest("MyTest", null)

        val result = h.single()
        assertEquals("my scenario", result.name)
        assertEquals("MyTest.my scenario", result.fullName)
        assertEquals("MyTest", result.labels.single { it.name == "suite" }.value)
        assertEquals(listOf("before", "click", "after"), result.steps.map { it.name })
        assertEquals(AllureStatus.PASSED, result.status)
        assertTrue(result.labels.none { it.name == "ultron.testBoundary" && it.value == "implicit" })
    }

    @Test
    fun `a failure in the after block fails the test and the step`() {
        listener.beforeTest("MyTest", null)
        block(TestBlock.GO) {
            hook.beforeTest("scenario")
            hook.afterTest(null)
        }
        val boom = AssertionError("after failed")
        block(TestBlock.AFTER, boom)
        listener.afterTest("MyTest", boom)

        val result = h.single()
        assertEquals(AllureStatus.FAILED, result.status)
        assertEquals(AllureStatus.FAILED, result.steps.single { it.name == "after" }.status)
        assertEquals("after failed", result.statusDetails?.message)
    }

    @Test
    fun `without runUltronUiTest the test is still reported under the class`() {
        listener.beforeTest("MyTest", null)
        block(TestBlock.GO) { h.lifecycle.startStep("op"); h.lifecycle.stopStep() }
        listener.afterTest("MyTest", null)

        val result = h.single()
        assertEquals("MyTest", result.labels.single { it.name == "suite" }.value)
        assertEquals(listOf("op"), result.steps.map { it.name })
    }

    @Test
    fun `runUltronUiTest outside UltronTest keeps opening and closing its own test`() {
        hook.beforeTest("standalone")
        hook.afterTest(null)
        listener.beforeTest("MyTest", null)
        listener.afterTest("MyTest", null)
        assertEquals(listOf("standalone"), h.results().map { it.name }.filter { it == "standalone" })
        assertEquals(2, h.results().size)
    }

    @Test
    fun `explicit test name wins over the discovered method name and keeps the method as a label`() {
        listener.beforeTest("MyTest", "named scenario")
        listener.afterTest("MyTest", null)

        val result = h.single()
        assertEquals("named scenario", result.name)
        assertEquals("MyTest.named scenario", result.fullName)
        assertEquals("MyTest", result.labels.single { it.name == "suite" }.value)
        assertEquals("ultronTest", result.labels.single { it.name == "ultron.testBoundary" }.value)
        assertEquals(
            "explicit test name wins over the discovered method name and keeps the method as a label",
            result.labels.single { it.name == "ultron.testMethod" }.value,
        )
    }

    @Test
    fun `without an explicit name the discovered method name is used and no testMethod label is added`() {
        listener.beforeTest("MyTest", null)
        listener.afterTest("MyTest", null)

        val result = h.single()
        assertEquals("without an explicit name the discovered method name is used and no testMethod label is added", result.name)
        assertTrue(result.labels.none { it.name == "ultron.testMethod" })
    }

    @Test
    fun `runUltronUiTest does not rename a test named explicitly through test(name)`() {
        listener.beforeTest("MyTest", "named scenario")
        block(TestBlock.GO) {
            hook.beforeTest("ignored")
            hook.afterTest(null)
        }
        listener.afterTest("MyTest", null)

        val result = h.single()
        assertEquals("named scenario", result.name)
        assertEquals("MyTest.named scenario", result.fullName)
    }

    private val screenshot = object : ArtifactProvider {
        override val kind = "screenshot"
        override val extension = "png"
        override val mimeType = "image/png"
        override fun produce(): ByteArray = byteArrayOf(1, 2, 3)
    }

    /** `runUltronUiTest { test { } }`: the only way to combine UltronTest with a browser test. */
    @Test
    fun `test nested inside runUltronUiTest adopts the open test instead of starting another`() {
        hook.beforeTest("outer")
        listener.beforeTest("com.example.MyTest", null)
        block(TestBlock.BEFORE)
        block(TestBlock.GO) { h.lifecycle.startStep("click"); h.lifecycle.stopStep() }
        block(TestBlock.AFTER)
        listener.afterTest("com.example.MyTest", null)
        hook.afterTest(null)

        val result = h.single()
        assertEquals("outer", result.name)
        assertEquals("com.example.MyTest.outer", result.fullName)
        assertEquals("MyTest", result.labels.single { it.name == "suite" }.value)
        assertEquals("ultronTest", result.labels.single { it.name == "ultron.testBoundary" }.value)
        assertEquals(listOf("before", "click", "after"), result.steps.map { it.name })
    }

    @Test
    fun `test(name) nested inside runUltronUiTest renames the open test`() {
        hook.beforeTest("outer")
        listener.beforeTest("MyTest", "named")
        listener.afterTest("MyTest", null)
        hook.afterTest(null)

        assertEquals("named", h.single().name)
        assertEquals("MyTest.named", h.single().fullName)
    }

    @Test
    fun `a failing nested test writes one failed result with the failure attachments`() {
        val attachingHook = AllureTestRunHook(
            lifecycle = h.lifecycle,
            failureAttacher = TestFailureAttacher(h.lifecycle, listOf(screenshot), setOf(AllureAttachStrategy.TEST_FAILURE), emptySet()),
            logAttacher = null,
        )
        val boom = AssertionError("boom")
        attachingHook.beforeTest("outer")
        listener.beforeTest("MyTest", null)
        block(TestBlock.GO, boom)
        listener.afterTest("MyTest", boom)
        attachingHook.onTestFailed(boom)
        attachingHook.afterTest(boom)

        val result = h.single()
        assertEquals(AllureStatus.FAILED, result.status)
        assertEquals(listOf("test_failure_screenshot"), result.attachments.map { it.name })
        assertTrue(h.results().none { it.name == "unknown test" })
    }

    /**
     * `runUltronUiTest { test { } }` without a name on a target without test-name discovery (iOS, macOS, a
     * browser without the Karma adapter): the hook opens the implicit `unknown test`. Here the hook runs on a
     * thread with no `@Test` method on its stack, which is what those targets look like to it.
     */
    @Test
    fun `test nested inside an unnamed runUltronUiTest adopts the implicit test the hook opened`() {
        val attachingHook = AllureTestRunHook(
            lifecycle = h.lifecycle,
            failureAttacher = TestFailureAttacher(h.lifecycle, listOf(screenshot), setOf(AllureAttachStrategy.TEST_FAILURE), emptySet()),
            logAttacher = null,
        )
        thread { attachingHook.beforeTest(null) }.join()
        assertTrue(h.lifecycle.currentTest!!.implicit, "precondition: the hook opened the implicit test")

        val boom = AssertionError("boom")
        listener.beforeTest("com.example.MyTest", null)
        block(TestBlock.GO, boom) { h.lifecycle.startStep("click"); h.lifecycle.stopStep(boom) }
        listener.afterTest("com.example.MyTest", boom)
        attachingHook.onTestFailed(boom)
        attachingHook.afterTest(boom)

        val result = h.single()
        assertEquals("test nested inside an unnamed runUltronUiTest adopts the implicit test the hook opened", result.name)
        assertEquals("com.example.MyTest.${result.name}", result.fullName)
        assertEquals("MyTest", result.labels.single { it.name == "suite" }.value)
        assertEquals("ultronTest", result.labels.single { it.name == "ultron.testBoundary" }.value)
        assertEquals(listOf("click"), result.steps.map { it.name })
        assertEquals(AllureStatus.FAILED, result.status)
        assertEquals(listOf("test_failure_screenshot"), result.attachments.map { it.name })
    }

    @Test
    fun `test(name) nested inside an unnamed runUltronUiTest names the implicit test`() {
        thread { hook.beforeTest(null) }.join()
        listener.beforeTest("MyTest", "named")
        listener.afterTest("MyTest", null)
        hook.afterTest(null)

        val result = h.single()
        assertEquals("named", result.name)
        assertEquals("MyTest.named", result.fullName)
        assertTrue(result.labels.none { it.value == "implicit" })
    }

    /** Two `test { }` blocks in one `runUltronUiTest`: each gets a result of its own, the second never merges into the first. */
    @Test
    fun `a second test nested in the same runUltronUiTest closes the first and gets its own result`() {
        val listener = AllureUltronTestListener(h.lifecycle, hook::closeTest, reopenTest = { hook.beforeTest(null) })
        hook.beforeTest("outer")
        listener.beforeTest("MyTest", "first")
        block(TestBlock.GO) { h.lifecycle.startStep("first step"); h.lifecycle.stopStep() }
        listener.afterTest("MyTest", null)
        listener.beforeTest("MyTest", "second")
        assertEquals(listOf("first"), h.results().map { it.name }, "the first test is written when the second starts")
        block(TestBlock.GO) { h.lifecycle.startStep("second step"); h.lifecycle.stopStep() }
        listener.afterTest("MyTest", null)
        assertEquals(1, h.results().size, "the second test is still open: the hook closes it with the artifacts")
        hook.afterTest(null)

        val (first, second) = h.results()
        assertEquals("MyTest.first", first.fullName)
        assertEquals(listOf("first step"), first.steps.map { it.name })
        assertEquals("MyTest.second", second.fullName)
        assertEquals(listOf("second step"), second.steps.map { it.name })
        assertEquals("ultronTest", second.labels.single { it.name == "ultron.testBoundary" }.value)
    }

    /**
     * `UltronAllureConfig.apply` from the common `beforeTest` or from `go { }` re-registers the listener: a new
     * instance must still know that the open test was adopted and finished, so the next `test { }` does not merge.
     */
    @Test
    fun `a listener re-registered between two nested tests still closes the first before the second`() {
        val first = AllureUltronTestListener(h.lifecycle, hook::closeTest, reopenTest = { hook.beforeTest(null) })
        hook.beforeTest("outer")
        first.beforeTest("MyTest", "first")
        first.afterTest("MyTest", null)

        val second = AllureUltronTestListener(h.lifecycle, hook::closeTest, reopenTest = { hook.beforeTest(null) })
        second.beforeTest("MyTest", "second")
        assertEquals(listOf("MyTest.first"), h.results().map { it.fullName }, "the first test is written when the second starts")
        second.afterTest("MyTest", null)
        hook.afterTest(null)

        assertEquals(listOf("MyTest.first", "MyTest.second"), h.results().map { it.fullName })
    }

    /** The first `test { }` failed but user code caught it inside the block: closed when the second starts, with its failure artifacts. */
    @Test
    fun `a caught failure of the first nested test still gets the failure attachments when the second starts`() {
        val attachingHook = AllureTestRunHook(
            lifecycle = h.lifecycle,
            failureAttacher = TestFailureAttacher(h.lifecycle, listOf(screenshot), setOf(AllureAttachStrategy.TEST_FAILURE), emptySet()),
            logAttacher = null,
        )
        val listener = AllureUltronTestListener(
            h.lifecycle, attachingHook::closeTest, failTest = attachingHook::onTestFailed, reopenTest = { attachingHook.beforeTest(null) },
        )
        val boom = AssertionError("boom")
        attachingHook.beforeTest("outer")
        listener.beforeTest("MyTest", "first")
        block(TestBlock.GO, boom)
        listener.afterTest("MyTest", boom)
        listener.beforeTest("MyTest", "second")
        listener.afterTest("MyTest", null)
        attachingHook.afterTest(null)

        val (first, second) = h.results()
        assertEquals(AllureStatus.FAILED, first.status)
        assertEquals(listOf("test_failure_screenshot"), first.attachments.map { it.name })
        assertEquals(AllureStatus.PASSED, second.status)
        assertTrue(second.attachments.isEmpty(), second.attachments.toString())
    }

    /** The only `test { }` of the block failed and the block caught the failure: the test still fails, with its artifacts, once. */
    @Test
    fun `a caught failure of the only nested test fails it with the failure attachments`() {
        val attachingHook = AllureTestRunHook(
            lifecycle = h.lifecycle,
            failureAttacher = TestFailureAttacher(h.lifecycle, listOf(screenshot), setOf(AllureAttachStrategy.TEST_FAILURE), emptySet()),
            logAttacher = null,
        )
        val listener = AllureUltronTestListener(h.lifecycle, attachingHook::closeTest, failTest = attachingHook::onTestFailed)
        val boom = AssertionError("boom")
        attachingHook.beforeTest("outer")
        listener.beforeTest("MyTest", "only")
        block(TestBlock.GO, boom)
        listener.afterTest("MyTest", boom)
        attachingHook.afterTest(null)

        val result = h.single()
        assertEquals(AllureStatus.FAILED, result.status)
        assertEquals("boom", result.statusDetails?.message)
        assertEquals(listOf("test_failure_screenshot"), result.attachments.map { it.name })
    }

    /** Not caught: the block fails with the same throwable, and the artifacts the listener took are not taken again. */
    @Test
    fun `an uncaught failure of the nested test is attached once`() {
        val attachingHook = AllureTestRunHook(
            lifecycle = h.lifecycle,
            failureAttacher = TestFailureAttacher(h.lifecycle, listOf(screenshot), setOf(AllureAttachStrategy.TEST_FAILURE), emptySet()),
            logAttacher = null,
        )
        val listener = AllureUltronTestListener(h.lifecycle, attachingHook::closeTest, failTest = attachingHook::onTestFailed)
        val boom = AssertionError("boom")
        attachingHook.beforeTest("outer")
        listener.beforeTest("MyTest", "only")
        listener.afterTest("MyTest", boom)
        attachingHook.onTestFailed(boom)
        attachingHook.afterTest(boom)

        assertEquals(listOf("test_failure_screenshot"), h.single().attachments.map { it.name })
    }

    @Test
    fun `the hook never opens an implicit test after its test was closed by someone else`() {
        hook.beforeTest("outer")
        h.lifecycle.stopTest(null)
        hook.onTestFailed(AssertionError("late"))
        hook.afterTest(AssertionError("late"))

        assertEquals(listOf("outer"), h.results().map { it.name })
    }

    @Test
    fun `a qualified test class gives a qualified fullName and keeps the suite readable`() {
        listener.beforeTest("com.example.login.MyTest", "scenario")
        listener.afterTest("com.example.login.MyTest", null)

        val result = h.single()
        assertEquals("com.example.login.MyTest.scenario", result.fullName)
        assertEquals("MyTest", result.labels.single { it.name == "suite" }.value)
    }

    @Test
    fun `runUltronUiTest naming a test keeps the qualified class in fullName`() {
        listener.beforeTest("com.example.MyTest", null)
        block(TestBlock.GO) {
            hook.beforeTest("from runUltronUiTest")
            hook.afterTest(null)
        }
        listener.afterTest("com.example.MyTest", null)

        val result = h.single()
        assertEquals("from runUltronUiTest", result.name)
        assertEquals("com.example.MyTest.from runUltronUiTest", result.fullName)
        assertEquals("MyTest", result.labels.single { it.name == "suite" }.value)
    }

    @Test
    fun `runUltronUiTest still names a test opened without an explicit name`() {
        listener.beforeTest("MyTest", null)
        block(TestBlock.GO) {
            hook.beforeTest("from runUltronUiTest")
            hook.afterTest(null)
        }
        listener.afterTest("MyTest", null)

        val result = h.single()
        assertEquals("from runUltronUiTest", result.name)
        assertEquals("MyTest.from runUltronUiTest", result.fullName)
    }

    /** js/wasmJs: `UltronTest` passes the simple class name, the Karma adapter's discovery the qualified one. */
    @Test
    fun `a simple test class takes the package of the discovered class with the same simple name`() {
        val listener = AllureUltronTestListener(h.lifecycle, hook::closeTest, discover = { DiscoveredTest("login", "auth.LoginTest") })
        listener.beforeTest("LoginTest", "login")
        listener.afterTest("LoginTest", null)

        val result = h.single()
        assertEquals("auth.LoginTest.login", result.fullName)
        assertEquals("LoginTest", result.labels.single { it.name == "suite" }.value)
    }

    @Test
    fun `an adopted test takes the package of the discovered class with the same simple name`() {
        val listener = AllureUltronTestListener(h.lifecycle, hook::closeTest, discover = { DiscoveredTest("login", "auth.LoginTest") })
        hook.beforeTest("outer")
        listener.beforeTest("LoginTest", "login")
        listener.afterTest("LoginTest", null)
        hook.afterTest(null)

        assertEquals("auth.LoginTest.login", h.single().fullName)
    }

    @Test
    fun `a simple test class stays unqualified without a discovered class of the same simple name`() {
        val none = AllureUltronTestListener(h.lifecycle, hook::closeTest, discover = { null })
        none.beforeTest("LoginTest", "login")
        none.afterTest("LoginTest", null)
        val other = AllureUltronTestListener(h.lifecycle, hook::closeTest, discover = { DiscoveredTest("login", "auth.OtherTest") })
        other.beforeTest("LoginTest", "login")
        other.afterTest("LoginTest", null)

        assertEquals(listOf("LoginTest.login", "LoginTest.login"), h.results().map { it.fullName })
    }

    @Test
    fun `an assumption failure in UltronTest skips the test without failing its step`() {
        val skip = org.junit.AssumptionViolatedException("not here")
        listener.beforeTest("MyTest", "scenario")
        block(TestBlock.BEFORE, skip)
        listener.afterTest("MyTest", skip)

        val result = h.single()
        assertEquals(AllureStatus.SKIPPED, result.status)
        assertEquals(AllureStatus.SKIPPED, result.steps.single { it.name == "before" }.status)
    }

    @Test
    fun `an assumption failure in runUltronUiTest skips the test`() {
        hook.beforeTest("scenario")
        h.lifecycle.startStep("go")
        hook.afterTest(org.junit.AssumptionViolatedException("not here"))

        val result = h.single()
        assertEquals(AllureStatus.SKIPPED, result.status)
        assertEquals(AllureStatus.SKIPPED, result.steps.single().status)
    }
}
