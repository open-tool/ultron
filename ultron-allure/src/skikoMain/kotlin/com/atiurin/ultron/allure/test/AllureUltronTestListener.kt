package com.atiurin.ultron.allure.test

import com.atiurin.ultron.allure.discovery.DiscoveredTest
import com.atiurin.ultron.allure.discovery.discoverTestName
import com.atiurin.ultron.allure.lifecycle.AllureLifecycle
import com.atiurin.ultron.allure.log.AllureLogAttacher
import com.atiurin.ultron.allure.model.AllureLabel
import com.atiurin.ultron.allure.blockOfCurrentThread
import com.atiurin.ultron.annotations.InternalUltronApi
import com.atiurin.ultron.core.compose.UltronTestRunHooks
import com.atiurin.ultron.core.test.TestBlock
import com.atiurin.ultron.core.test.UltronTestListener
import com.atiurin.ultron.core.test.UltronTestAsync
import com.atiurin.ultron.core.test.UltronTestListeners
import com.atiurin.ultron.log.UltronLog

/**
 * Maps the `UltronTest` lifecycle onto Allure: `test { }` opens the test under the class's `suite` (simple
 * name) with `fullName = <qualified class>.<name>`,
 * `before { }` and `after { }` become steps, and a `runUltronUiTest` inside `go { }` joins the open test
 * through the test run hook instead of starting another one. The other nesting, `test { }` inside
 * `runUltronUiTest { }`, adopts the test the hook opened, also when the block is nested in the one that opened it:
 * the hook keeps closing it. A `test { }` in the `go { }` of another joins the outer one, which alone closes the
 * test; the inner one's failure reaches the report as far as it fails the outer one. `test { }` is the top-level
 * unit, one per `runUltronUiTest` block. Should a second `test { }` start in the same block anyway, it finds the
 * adopted test finished and still open: it is closed then (through [closeTest]) and the hook opens a new one
 * (through [reopenTest]) for the second `test { }` to adopt, so the two are written apart; unnamed ones still
 * share a `fullName`, and so a `historyId`, and show as retries of one test in Allure.
 *
 * Test name priority: the explicit `test(name = ...)` wins (the discovered JVM method, if any, is kept as the
 * `ultron.testMethod` label, and `runUltronUiTest(testName)` does not rename it); otherwise the JVM test method
 * name; otherwise [AllureLifecycle.IMPLICIT_TEST_NAME], which `runUltronUiTest(testName)` may later replace.
 *
 * @param closeTest attaches the per-test artifacts and writes the result; the hook's `closeTest` on Skiko targets.
 * @param failTest takes the failure artifacts (screenshot, hierarchy, failure frame) of the open test; the hook's
 * `onTestFailed`. Called when an adopted `test { }` fails, scene still alive: the hook only hears of a failure that
 * leaves `runUltronUiTest`, and the block may catch it. The hook closes the test as failed either way.
 * @param reopenTest opens a test in place of the one it is given, just closed, the way `runUltronUiTest` did for its
 * blocks that still run, bound to all of them; the hook's `reopen`. Called after [closeTest] when a second `test { }`
 * starts in the same block.
 * @param closeStaleTest closes, with its video and log, a test still open when this `test { }` starts its own: one
 * that never finished, or a hook-opened test of an earlier `runUltronUiTest` block, which is never adopted. The
 * hook's `closeStaleTest` on Skiko targets; without it the lifecycle closes the test, artifacts aside.
 *
 * `afterTest` and the block steps address the test bound to their own `test { }` at `beforeTest` (see
 * `TestState.ultronTests`): a `test { }` that finishes late, while a later one runs, finds its test closed and leaves
 * the later one alone. The forms without an `ActiveTest` (a listener driven directly) know no `test { }` and address
 * the current test.
 * @param discover looks up the running test (see `discoverTestName`); replaced in tests.
 * @param logAttacher told when a test starts here, so that lines logged before it (`beforeFirstTest`) are not in its log.
 * An adopted test was started, and its buffer cleared, by the hook; `beforeFirstTest` then runs inside it and is in its log.
 */
class AllureUltronTestListener(
    private val lifecycle: AllureLifecycle,
    private val closeTest: (Throwable?) -> Unit,
    private val logAttacher: AllureLogAttacher? = null,
    private val failTest: (Throwable) -> Unit = {},
    private val reopenTest: (AllureLifecycle.TestState) -> Unit = {},
    private val closeStaleTest: (next: String) -> Unit = {},
    private val discover: () -> DiscoveredTest? = ::discoverTestName,
) : UltronTestListener {
    override val id: String = ID

    override fun beforeTest(test: UltronTestListeners.ActiveTest) {
        if (lifecycle.orphanIfLate(test)) {
            // Started in a block or `test { }` whose test is closed (abandoned by a JUnit timeout): the test in
            // progress belongs to another block, never adopt, close or rename it, and open none.
            val name = test.testName ?: AllureLifecycle.IMPLICIT_TEST_NAME
            UltronLog.debug("Allure: '$name' starts after its test was closed; nothing to report")
            return
        }
        start(test, test.testClass, test.testName)
    }

    override fun beforeTest(testClass: String, testName: String?) = start(null, testClass, testName)

    private fun start(test: UltronTestListeners.ActiveTest?, testClass: String, testName: String?) {
        val discovered = discover()
        val qualifier = qualifiedClassOf(testClass, discovered)
        val labels = buildList {
            // `suite` stays readable (simple name); `fullName`, and so `historyId`, carry the qualified class.
            add(AllureLabel("suite", testClass.substringAfterLast('.')))
            add(AllureLabel(AllureLifecycle.LABEL_BOUNDARY, "ultronTest"))
            if (testName != null && discovered != null && discovered.name != testName) {
                add(AllureLabel(AllureLifecycle.LABEL_TEST_METHOD, discovered.name))
            }
        }
        val enclosing = test?.enclosing
        enclosing?.let(lifecycle::testOf)?.let { joined ->
            // `test { }` in the `go { }` of another: one test, the outer one; its blocks are steps of it.
            lifecycle.bindUltronTest(joined, test!!)
            return
        }
        lifecycle.currentTest?.takeIf { it.adoptedBy == ID && it.adoptedFinished }?.let { previous ->
            // A second `test { }` in the same `runUltronUiTest`: write the first one now, with its video and
            // log, and let the hook open a fresh test for this one, as it did at the start of the block.
            closeTest(previous.adoptedThrowable)
            reopenTest(previous)
        }
        val open = lifecycle.currentTest?.takeIf {
            it.openedByHook && it.owner == null && it.adoptedBy == null && it.openedIn.isBlockOf(blockOfCurrentThread())
        }
        if (open != null) {
            // `runUltronUiTest { test { } }` (the browser form): the hook opened the test and will close it with
            // the failure artifacts. Adopt it: name it from `test(name)` when given, label it, start nothing.
            // Without a name on a target without discovery the hook opened the implicit `unknown test`: it
            // becomes this test, named like a test opened here would be.
            val name = testName ?: open.name.takeUnless { open.implicit } ?: discovered?.name ?: AllureLifecycle.IMPLICIT_TEST_NAME
            lifecycle.nameCurrentTest(name, "$qualifier.$name", labels)
            open.implicit = false
            open.explicitName = testName != null
            open.adoptedBy = ID
            test?.let { lifecycle.bindUltronTest(open, it) }
            return
        }
        val name = testName ?: discovered?.name ?: AllureLifecycle.IMPLICIT_TEST_NAME
        closeStaleTest(name)
        lifecycle.startTest(name = name, fullName = "$qualifier.$name", labels = labels)
        lifecycle.currentTest?.let { opened ->
            opened.owner = ID
            opened.explicitName = testName != null
            test?.let { lifecycle.bindUltronTest(opened, it) }
        }
        logAttacher?.onTestStarted()
    }

    override fun beforeBlock(test: UltronTestListeners.ActiveTest, block: TestBlock) {
        val name = stepNameOf(block) ?: return
        ownTest(test)?.let { lifecycle.startStep(it, name) }
    }

    override fun afterBlock(test: UltronTestListeners.ActiveTest, block: TestBlock, throwable: Throwable?) {
        if (stepNameOf(block) == null) return
        val own = ownTest(test) ?: return
        if (throwable == null) lifecycle.stopStep(own) else lifecycle.stopStep(own, throwable)
    }

    override fun beforeBlock(testClass: String, block: TestBlock) {
        stepNameOf(block)?.let { lifecycle.startStep(it) }
    }

    override fun afterBlock(testClass: String, block: TestBlock, throwable: Throwable?) {
        if (stepNameOf(block) == null) return
        if (throwable == null) lifecycle.stopStep() else lifecycle.stopStep(throwable)
    }

    /** The test [test] opened, adopted or joined, still open; `null` once it was closed (the `test { }` finishes late). */
    private fun ownTest(test: UltronTestListeners.ActiveTest) = lifecycle.testOf(test)

    override fun afterTest(test: UltronTestListeners.ActiveTest, throwable: Throwable?) {
        // Closed already (a later `test { }` started while this one was still running): never touch the later one.
        val own = ownTest(test) ?: return
        if (test.enclosing?.let(lifecycle::testOf) === own) return // joined: the outer `test { }` closes it
        finish(own, throwable)
    }

    /** `true` for [run] itself or a block [run] is nested in: `runUltronUiTest { runUltronUiTest { test { } } }` adopts too. */
    private fun UltronTestRunHooks.ActiveRun?.isBlockOf(run: UltronTestRunHooks.ActiveRun?): Boolean =
        this === run || (this != null && generateSequence(run) { it.enclosing }.any { it === this })

    override fun afterTest(testClass: String, throwable: Throwable?) = finish(lifecycle.currentTest, throwable)

    @OptIn(InternalUltronApi::class)
    private fun finish(current: AllureLifecycle.TestState?, throwable: Throwable?) {
        if (current?.owner != ID) {
            if (current?.adoptedBy == ID) {
                current.adoptedFinished = true
                current.adoptedThrowable = throwable
                throwable?.let(failTest)
            }
            return
        }
        closeTest(throwable)
        // The browser encodes the video asynchronously and the result waits for it: `test { }` settles its promise
        // only once the result is written, so the last test's result is in the console before Karma closes the page.
        if (current != null && lifecycle.isResultDeferred(current)) {
            UltronTestAsync.awaitWork { lifecycle.awaitResult(current, AllureLifecycle.PENDING_RESULT_TIMEOUT) }
        }
    }

    /**
     * The class that qualifies `fullName`: [testClass], or the discovered class when [testClass] is a simple name and
     * the discovered one is the same class with its package. On js/wasmJs `UltronTest` knows only the simple class
     * name, the Karma adapter (see `discoverTestName`) the qualified one: without it `auth.LoginTest` and
     * `admin.LoginTest` would share a `fullName`, and so a `historyId`.
     */
    private fun qualifiedClassOf(testClass: String, discovered: DiscoveredTest?): String {
        val className = discovered?.className ?: return testClass
        val qualified = '.' !in testClass && '.' in className && className.substringAfterLast('.') == testClass
        return if (qualified) className else testClass
    }

    private fun stepNameOf(block: TestBlock) = when (block) {
        TestBlock.BEFORE -> "before"
        TestBlock.AFTER -> "after"
        TestBlock.GO -> null
    }

    companion object {
        const val ID = "AllureUltronTestListener"
    }
}
