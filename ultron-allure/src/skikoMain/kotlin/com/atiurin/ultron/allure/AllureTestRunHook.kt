package com.atiurin.ultron.allure

import com.atiurin.ultron.allure.attach.TestFailureAttacher
import com.atiurin.ultron.allure.discovery.discoverTestName
import com.atiurin.ultron.allure.lifecycle.AllureLifecycle
import com.atiurin.ultron.allure.log.AllureLogAttacher
import com.atiurin.ultron.allure.model.AllureLabel
import com.atiurin.ultron.allure.test.AllureUltronTestListener
import com.atiurin.ultron.allure.video.VideoAttacher
import com.atiurin.ultron.annotations.InternalUltronApi
import com.atiurin.ultron.core.compose.UltronTestRunHook
import com.atiurin.ultron.core.compose.UltronTestRunHooks
import com.atiurin.ultron.core.test.UltronTestListeners
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.utils.currentExecutionThread

/**
 * Opens and closes the Allure test around each `runUltronUiTest` block. When the block runs inside
 * `UltronTest.test { }`, the test is already open (owned by [AllureUltronTestListener]); the hook then only
 * names it, and not even that when the test was named explicitly through `UltronTest.test(name = ...)`.
 * The other way round, `test { }` inside the block, adopts the test this hook opened (see the listener).
 * A `runUltronUiTest` nested in the body of another (see `UltronTestRunHooks.ActiveRun.enclosing`) joins the
 * test of the enclosing block the same way, and leaves closing it to that block.
 *
 * Every callback of a block addresses the test bound to that block at its `beforeTest` (see `TestState.runs`).
 * A block that finishes late, abandoned by a JUnit or mocha timeout while a later block runs, finds its test
 * closed already (the later block's start closed it as unfinished): its failure artifacts and its close are
 * dropped, the later block's test is never touched. The forms without a run (a hook driven directly, not by
 * `runUltronUiTest`) know no block and address the current test.
 */
class AllureTestRunHook(
    private val lifecycle: AllureLifecycle,
    private val failureAttacher: TestFailureAttacher,
    private val logAttacher: AllureLogAttacher?,
    private val videoAttacher: VideoAttacher? = null,
) : UltronTestRunHook {
    override val id: String = "AllureTestRunHook"

    override fun beforeTest(run: UltronTestRunHooks.ActiveRun) {
        if (lifecycle.orphanIfLate(run)) {
            // Nested in a block whose test is closed (abandoned by a JUnit timeout): never join, close or open a test.
            val name = run.testName ?: AllureLifecycle.IMPLICIT_TEST_NAME
            UltronLog.debug("Allure: '$name' starts after its test was closed; nothing to report")
            return
        }
        start(run, run.testName)
    }

    override fun beforeTest(testName: String?) = start(null, testName)

    /** Opens the test of [run] (or joins the one it runs in), named [testName]; also reopens a test, see the listener. */
    internal fun start(run: UltronTestRunHooks.ActiveRun?, testName: String?) {
        // Discovered even with an explicit name: its class qualifies the name in `fullName`, and so in `historyId`.
        val discovered = discoverTestName()
        val name = testName ?: discovered?.name
        val joined = lifecycle.currentTest?.takeIf { joins(it, run) }
        if (joined != null) {
            // Opened by UltronTest (see AllureUltronTestListener) or by the enclosing block: name it unless
            // test(name) or the enclosing runUltronUiTest(testName) already did, do not start a second test.
            if (name != null && !joined.explicitName) {
                // Keep the qualifier of the open test's fullName (`<qualified class>.`), replace the name only.
                lifecycle.nameCurrentTest(name, joined.fullName.removeSuffix(joined.name) + name)
            }
            run?.let { lifecycle.bindRun(joined, it) }
        } else {
            closeStaleTest(next = name ?: AllureLifecycle.IMPLICIT_TEST_NAME)
            if (name == null) {
                lifecycle.startTest(AllureLifecycle.IMPLICIT_TEST_NAME, implicit = true)
            } else {
                // `suite` is the simple class name; `fullName` keeps the package, the bare name where no class is known.
                val className = discovered?.className
                val labels = className?.let { listOf(AllureLabel("suite", it.substringAfterLast('.'))) } ?: emptyList()
                lifecycle.startTest(name = name, fullName = className?.let { "$it.$name" } ?: name, labels = labels)
            }
            lifecycle.currentTest?.let { test ->
                test.openedByHook = true
                test.explicitName = testName != null
                test.openedIn = run ?: blockOfCurrentThread()
                run?.let { lifecycle.bindRun(test, it) }
            }
            logAttacher?.onTestStarted()
        }
        videoAttacher?.onTestStarted()
    }

    /**
     * Opens a test in place of [closed], just closed while its blocks still run (see `AllureUltronTestListener`): as the
     * first of those blocks, the one that opened [closed], did at its start, and bound to the others, which joined it.
     * With none of them left running it keeps the name `runUltronUiTest` gave [closed].
     */
    internal fun reopen(closed: AllureLifecycle.TestState) {
        val runs = lifecycle.liveRunsOf(closed)
        val opener = runs.firstOrNull()
        start(opener, opener?.testName ?: closed.openedIn?.testName)
        lifecycle.currentTest?.let { test -> runs.drop(1).forEach { lifecycle.bindRun(test, it) } }
    }

    /**
     * The open test is UltronTest's, or the enclosing block's: [run] joins it instead of opening its own. A test with
     * an owner is joined only from inside its own `test { }`, on that test's thread: a block on another thread (the
     * next test, started while a `test { }` abandoned by a JUnit timeout still runs) closes it as unfinished and opens
     * its own. On js/wasmJs, which has no threads, the block is always the open `test { }`'s; a test whose listener was
     * driven directly, with no `test { }` known, is joined like the current test.
     */
    private fun joins(test: AllureLifecycle.TestState, run: UltronTestRunHooks.ActiveRun?): Boolean {
        if (test.owner != null) return inUltronTestOf(test)
        if (run == null) return blockOfCurrentThread()?.enclosing != null
        return run.enclosing?.let(lifecycle::testOf) === test
    }

    @OptIn(InternalUltronApi::class)
    private fun inUltronTestOf(test: AllureLifecycle.TestState): Boolean {
        if (currentExecutionThread() == null || !lifecycle.hasUltronTests(test)) return true
        return generateSequence(UltronTestListeners.testOnCurrentThread()) { it.enclosing }.any { lifecycle.testOf(it) === test }
    }

    /** The test [run] addresses: the one bound to it, still open; with no run known, the current test. */
    private fun testOf(run: UltronTestRunHooks.ActiveRun?): AllureLifecycle.TestState? =
        if (run == null) lifecycle.currentTest else lifecycle.testOf(run)

    override fun onTestFailed(run: UltronTestRunHooks.ActiveRun, throwable: Throwable) = fail(run, throwable)

    /**
     * Artifacts need the live scene, so they are taken here rather than in [afterTest]. Also called by the
     * `UltronTest` listener when an adopted `test { }` fails, as the block may catch that failure; the block
     * failing with it afterwards does not take them twice.
     */
    override fun onTestFailed(throwable: Throwable) = fail(null, throwable)

    private fun fail(run: UltronTestRunHooks.ActiveRun?, throwable: Throwable) {
        // Closed already (by someone else, or by a later block when this one finished late): never open an implicit
        // test for the artifacts, never give them to another block's test.
        val test = testOf(run) ?: return
        if (test.failureCaptured === throwable) return
        test.failureCaptured = throwable
        failureAttacher.onTestFinished(throwable)
        videoAttacher?.onTestFailed()
    }

    override fun afterTest(run: UltronTestRunHooks.ActiveRun, throwable: Throwable?) {
        val test = testOf(run)
        if (test == null) {
            UltronLog.info("Allure: '${run.testName ?: AllureLifecycle.IMPLICIT_TEST_NAME}' finished after its test was closed; nothing to report")
            return
        }
        if (test.owner != null) return // UltronTest closes it, video included, see closeTest
        if (run.enclosing != null) return // the enclosing block closes the test it joined
        closeTest(throwable)
    }

    /**
     * Holds the end of [run] until the result of the test it closed is written: in the browser the video is encoded
     * asynchronously and the result waits for it, and Karma closes the page once the last test's promise resolves.
     * Bounded by `AllureLifecycle.PENDING_RESULT_TIMEOUT`; past it the video is attached as APNG. Returns at once on desktop and
     * Native, whose encoders finish inside `afterTest`.
     */
    override suspend fun awaitAfterTest(run: UltronTestRunHooks.ActiveRun) {
        val test = lifecycle.deferredTestOf(run) ?: return
        lifecycle.awaitResult(test, AllureLifecycle.PENDING_RESULT_TIMEOUT)
    }

    override fun afterTest(throwable: Throwable?) {
        if (lifecycle.currentTest?.owner != null) return
        if (UltronTestRunHooks.activeRun?.enclosing != null) return
        closeTest(throwable)
    }

    /**
     * Closes the test still open when the test [next] starts, with its video and log: as broken unless an
     * operation opened it (see TestState.closesWithItsSteps). No-op when no test is open.
     */
    fun closeStaleTest(next: String) {
        if (!attachUnfinished("test did not finish before '$next' started")) return
        lifecycle.closeUnfinished(next)
    }

    /** Closes the test still open when the process exits (see `AllureLifecycle.closeAtExit`), with its video and log. */
    fun closeAtExit() {
        if (!attachUnfinished("test did not finish before the process exited")) return
        lifecycle.closeAtExit()
    }

    /**
     * Gives the open test its video and log before it is closed unfinished; `false` when no test is open. Best
     * effort: an attachment that throws (frames or log lines added by a thread a timeout abandoned) is logged and
     * skipped, the test is still closed.
     */
    private fun attachUnfinished(reason: String): Boolean {
        val test = lifecycle.currentTest ?: return false
        runCatching { videoAttacher?.onTestFinished(if (test.closesWithItsSteps) null else IllegalStateException(reason)) }
            .onFailure { UltronLog.error("Allure: video of unfinished test '${test.name}' failed: ${it.message}") }
        runCatching { logAttacher?.onTestFinished() }
            .onFailure { UltronLog.error("Allure: log of unfinished test '${test.name}' failed: ${it.message}") }
        return true
    }

    /**
     * Attaches the video and the log and writes the result; called by the owner of the test. No-op when no test
     * is open. A test adopted by a `test { }` that failed fails with that failure even when the block caught it.
     */
    fun closeTest(throwable: Throwable?) {
        val test = lifecycle.currentTest
        if (test == null) {
            videoAttacher?.onTestFinished(throwable) // stops a recording that lost its test
            return
        }
        val failure = throwable ?: test.adoptedThrowable.takeIf { test.adoptedFinished }
        videoAttacher?.onTestFinished(failure)
        logAttacher?.onTestFinished()
        lifecycle.stopTest(failure)
    }
}

/**
 * The `runUltronUiTest` block of the calling thread; the active block where threads are not told apart (js/wasmJs).
 * Not the active block alone: one abandoned by a timeout still runs on its own thread while another is active.
 */
@OptIn(InternalUltronApi::class)
internal fun blockOfCurrentThread(): UltronTestRunHooks.ActiveRun? =
    UltronTestRunHooks.runOnCurrentThread() ?: UltronTestRunHooks.activeRun
