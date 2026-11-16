package com.atiurin.ultron.allure.lifecycle

import com.atiurin.ultron.allure.model.AllureAttachment
import com.atiurin.ultron.allure.model.AllureIds
import com.atiurin.ultron.allure.model.AllureLabel
import com.atiurin.ultron.allure.model.AllureParameter
import com.atiurin.ultron.allure.model.AllureStatus
import com.atiurin.ultron.allure.model.AllureStatusDetails
import com.atiurin.ultron.allure.model.AllureStep
import com.atiurin.ultron.allure.model.AllureTestResult
import com.atiurin.ultron.allure.platform.isTestSkip
import com.atiurin.ultron.allure.platform.platformName
import com.atiurin.ultron.allure.platform.platformTarget
import com.atiurin.ultron.allure.video.ApngEncoder
import com.atiurin.ultron.allure.video.ApngFrame
import com.atiurin.ultron.allure.video.EncodedVideo
import com.atiurin.ultron.allure.writer.AllureResultsWriter
import com.atiurin.ultron.annotations.InternalUltronApi
import com.atiurin.ultron.core.compose.UltronTestRunHooks
import com.atiurin.ultron.core.test.UltronTestListeners
import com.atiurin.ultron.log.UltronLog
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.atomicfu.locks.synchronized
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

/**
 * Tracks the current test and its step stack and writes the result when the test closes.
 * Everything here is best effort: a failure inside the reporter is logged and never thrown.
 * Tests run one at a time, on one thread; every method still takes the lifecycle's lock, because the process exit
 * handler (see [closeAtExit]) runs on a thread of its own while a test thread a timeout abandoned may still write.
 * The lock covers one call at a time, not a sequence of them: a caller that checks [currentTest] and then attaches to
 * it or closes it (the hook binding a late block to its own test, the exit handler attaching the video and the log
 * before it closes the test) can see another thread change the current test in between. A late block or `test { }`
 * that finds its own test closed does nothing; the narrow race left is a late callback that passes its check right
 * before another thread closes that test.
 *
 * Operation listeners and `step { }` find their test through [testOfCurrentRun], by the thread that runs them: the
 * operation pipeline (`OperationExecutor`) knows nothing about tests or blocks. The operations of a block or a
 * `test { }` that finishes late, abandoned by a JUnit timeout, are dropped rather than reported to the test in
 * progress. On js/wasmJs, which has no threads, no block can be told apart from another and the current test receives
 * them.
 */
class AllureLifecycle(
    writer: AllureResultsWriter,
    private val clock: () -> Long = defaultClock,
) {
    /**
     * Where results and attachments of the next test go. Replaced, not the lifecycle, when `UltronAllureConfig` is
     * applied again with another `resultsDirectory`: the open test and the registered listeners survive the re-apply.
     * A test writes its attachments and its result through the writer it started with ([TestState.writer]), so the
     * open test stays in one directory, its result next to its attachments; the new writer applies from the next test.
     */
    var writer: AllureResultsWriter = writer
        set(value) {
            field = value
            sinkAnnounced = false
        }

    class TestState internal constructor(
        name: String,
        fullName: String,
        val start: Long,
        implicit: Boolean,
        val labels: MutableList<AllureLabel>,
        /** The lifecycle's [AllureLifecycle.writer] when the test started: every file of the test goes through it. */
        internal val writer: AllureResultsWriter,
    ) {
        var name: String = name
            internal set
        var fullName: String = fullName
            internal set
        /** Opened by an operation or an unnamed `runUltronUiTest` rather than by a test; cleared when a `test { }` adopts it. */
        var implicit: Boolean = implicit
            internal set
        /** `true` when `runUltronUiTest` (the test run hook) opened the test, named or implicit. */
        var openedByHook: Boolean = false
            internal set
        /**
         * Opened by an operation, so nobody reports on it: it closes with the status of its steps when the next
         * test starts or the process exits. A test opened by `runUltronUiTest` has the hook to close it; still
         * open at those moments it is a test that never finished, and is written as broken.
         */
        val closesWithItsSteps: Boolean get() = implicit && !openedByHook
        /**
         * The `runUltronUiTest` block whose hook opened the test, `null` when no block was running (or no hook
         * opened it). Only a `test { }` of that very block adopts the test; a later one finds it stale.
         */
        var openedIn: UltronTestRunHooks.ActiveRun? = null
            internal set
        /**
         * The `runUltronUiTest` blocks whose hook callbacks address this test: the block whose hook opened it and the
         * blocks that joined it. A callback of any other block (one that finishes late, see `AllureTestRunHook`)
         * never touches it. Bound through [bindRun] and read through [testOf], under the lifecycle's lock: other
         * threads (a late block, the exit handler) read it.
         */
        internal val runs = mutableListOf<UltronTestRunHooks.ActiveRun>()
        /**
         * The `test { }` calls that opened, adopted or joined this test; the `afterTest` of any other one never touches
         * it. Bound through [bindUltronTest] and read through [testOf], under the lifecycle's lock, like [runs].
         */
        internal val ultronTests = mutableListOf<UltronTestListeners.ActiveTest>()
        /** Id of the component that opened the test and will close it; `null` for the default owner. */
        var owner: String? = null
            internal set
        /**
         * `true` when the name was given explicitly (`UltronTest.test(name)`, or `runUltronUiTest(testName)` for a
         * test the hook opened); later hooks, a nested `runUltronUiTest` included, must not rename it.
         */
        var explicitName: Boolean = false
            internal set
        /**
         * Id of the `UltronTest` listener whose `test { }` adopted this hook-opened test, `null` until one does;
         * [adoptedFinished] and [adoptedThrowable] record that `test { }` ending while the hook still has the test
         * open. Kept here rather than in the listener, which `UltronAllureConfig.apply` replaces mid-test.
         */
        var adoptedBy: String? = null
            internal set
        var adoptedFinished: Boolean = false
            internal set
        var adoptedThrowable: Throwable? = null
            internal set
        /** The failure whose artifacts (screenshot, hierarchy, failure frame) the test already has; taken once per failure. */
        var failureCaptured: Throwable? = null
            internal set
        internal val attachments = mutableListOf<AllureAttachment>()
        /** Added through [addParameter], in the order of their first value; written after `target`. */
        internal val parameters = linkedMapOf<String, String>()
        internal val steps = mutableListOf<AllureStep>()
        internal val openSteps = ArrayDeque<OpenStep>()
        /** Attachments an encoder is still producing; the result of the test waits for them, see [deferAttachment]. */
        internal val pending = mutableListOf<PendingAttachment>()
    }

    /**
     * An attachment of [test] that is not ready yet: the browser encodes video through Promises, and the test
     * ends before they resolve. [complete] writes the file and links it; the result of the test is written
     * once the last pending attachment of the test is settled. [fallback] are the frames [flushPendingAttachments]
     * turns into an APNG when the report is collected before the encoder finished.
     */
    class PendingAttachment internal constructor(
        private val lifecycle: AllureLifecycle,
        internal val test: TestState,
        val name: String,
        private val fallback: List<ApngFrame>,
    ) {
        internal var settled = false

        /** A no-op, logged, once the attachment was settled by the flush or by an earlier call. */
        fun complete(video: EncodedVideo) = lifecycle.settle(this, video.bytes, video.extension, video.mimeType)

        /** Settles the attachment without a file, when nothing could be produced; the test result is then written without it. */
        fun abandon() = lifecycle.settle(this, null, "", "")

        internal fun completeWithFallback() {
            val apng = runCatching { ApngEncoder.encode(fallback) }
                .onFailure { UltronLog.error("Allure: fallback APNG of pending '$name' failed: ${it.message}") }
                .getOrNull()
            if (apng == null) lifecycle.settle(this, null, "", "")
            else lifecycle.settle(this, apng, ApngEncoder.extension, ApngEncoder.mimeType)
        }
    }

    /** A result whose test is closed but whose attachments are not all ready. */
    private class DeferredResult(val test: TestState, var result: AllureTestResult)

    internal class OpenStep(val name: String, val start: Long) {
        var status: AllureStatus? = null
        var details: AllureStatusDetails? = null
        val steps = mutableListOf<AllureStep>()
        val attachments = mutableListOf<AllureAttachment>()
    }

    var currentTest: TestState? = null
        private set

    private var sinkAnnounced = false

    /**
     * Blocks whose test was closed while they still ran: a block abandoned by a JUnit timeout (closed as unfinished by
     * the next block's start), and also every block that closes its own test, as the hook does so in the block's
     * `afterTest`, before the block has ended. Their operations and steps, coming from their own thread after that,
     * are dropped. A block that has ended is removed from the list at the next close of a test, not right away.
     */
    private val orphanedRuns = mutableListOf<UltronTestRunHooks.ActiveRun>()

    /** The same for `test { }` calls (see [TestState.ultronTests]): a `test { }` abandoned by a JUnit timeout. */
    private val orphanedTests = mutableListOf<UltronTestListeners.ActiveTest>()

    /** Reentrant: a locked method calls others (`startTest` closes the previous test through `stopTest`). */
    private val lock = SynchronizedObject()

    private inline fun <T> locked(block: () -> T): T = synchronized(lock, block)

    fun startTest(
        name: String,
        fullName: String = name,
        labels: List<AllureLabel> = emptyList(),
        implicit: Boolean = false,
    ): Unit = locked {
        closeUnfinished(next = name)
        val all = (defaultLabels() + labels + if (implicit) listOf(AllureLabel(LABEL_BOUNDARY, "implicit")) else emptyList()).toMutableList()
        currentTest = TestState(name, fullName, clock(), implicit, all, writer)
        if (!sinkAnnounced) {
            UltronLog.info("Allure results go to ${writer.sink.description}")
            sinkAnnounced = true
        }
    }

    /**
     * Closes the test still open when the test [next] starts: with the status of its steps when an operation
     * opened it (see [TestState.closesWithItsSteps]), otherwise as broken. No-op when no test is open.
     */
    fun closeUnfinished(next: String): Unit = locked {
        val previous = currentTest ?: return@locked
        if (previous.closesWithItsSteps) {
            stopTest(null)
            return@locked
        }
        // A test that never reached its afterTest (a mocha timeout on js/wasmJs leaves the promise running, a hook
        // threw), the implicit test of an unnamed `runUltronUiTest` included: report it as broken, never as passed.
        UltronLog.warn("Allure: test '${previous.name}' was still open when '$next' started; closing it as broken")
        val details = AllureStatusDetails(message = "test did not finish before '$next' started")
        while (previous.openSteps.isNotEmpty()) stopStep(AllureStatus.BROKEN, details)
        writeAndClose(previous, AllureStatus.BROKEN, details)
    }

    /** Gives the open test its real name and extra labels, e.g. when the name becomes known after the start. */
    fun nameCurrentTest(name: String, fullName: String = name, labels: List<AllureLabel> = emptyList()): Unit = locked {
        val test = currentTest ?: return@locked
        test.name = name
        test.fullName = fullName
        labels.forEach { label -> test.labels.removeAll { it.name == label.name }; test.labels.add(label) }
    }

    fun ensureTestOpen(): TestState = locked {
        currentTest ?: run {
            startTest(IMPLICIT_TEST_NAME, implicit = true)
            currentTest!!
        }
    }

    /**
     * The test the operations, steps and attachments of the calling thread address: the current test, opening the
     * implicit one when none is open and [open] is set. `null` when the thread runs a `runUltronUiTest` block or a
     * `test { }` whose test is closed already (it finishes late, see [orphanedRuns] and [orphanedTests]): what it
     * reports then is dropped, it never reaches the test in progress. Operations outside any block or `test { }`, and
     * every operation on js/wasmJs, where there are no threads to tell them apart, address the current test.
     */
    fun testOfCurrentRun(open: Boolean = true): TestState? = locked {
        lateOnCurrentThread()?.let { late ->
            UltronLog.debug("Allure: '$late' reports after its test was closed; dropped")
            return@locked null
        }
        currentTest ?: if (open) ensureTestOpen() else null
    }

    /**
     * `true` when the calling thread runs a block or `test { }` whose test is closed already: see [testOfCurrentRun].
     * Logs nothing, so the log buffer may ask it for every line.
     */
    fun isCurrentRunLate(): Boolean = locked { lateOnCurrentThread() != null }

    /**
     * Called when [run] starts: `true`, and [run] is late itself from now on (its operations, steps, log lines, frames
     * and callbacks are dropped), when the calling thread runs a late block or `test { }` (see [isCurrentRunLate]). A
     * `runUltronUiTest` nested in a block whose test is closed belongs to that block, not to the test in progress.
     */
    internal fun orphanIfLate(run: UltronTestRunHooks.ActiveRun): Boolean = locked {
        val late = lateOnCurrentThread() != null
        if (late && run !in orphanedRuns) orphanedRuns += run
        late
    }

    /** The same for a `test { }` that starts (see [TestState.ultronTests]). */
    internal fun orphanIfLate(ultronTest: UltronTestListeners.ActiveTest): Boolean = locked {
        val late = lateOnCurrentThread() != null
        if (late && ultronTest !in orphanedTests) orphanedTests += ultronTest
        late
    }

    /** The name of the late block or `test { }` the calling thread runs, `null` when it runs none. */
    @OptIn(InternalUltronApi::class)
    private fun lateOnCurrentThread(): String? {
        val runs = generateSequence(UltronTestRunHooks.runOnCurrentThread()) { it.enclosing }.toList()
        val tests = generateSequence(UltronTestListeners.testOnCurrentThread()) { it.enclosing }.toList()
        val current = currentTest
        val bound = current != null && (runs.any { it in current.runs } || tests.any { it in current.ultronTests })
        if (bound || (runs.none { it in orphanedRuns } && tests.none { it in orphanedTests })) return null
        return runs.firstOrNull()?.testName ?: tests.firstOrNull()?.testName ?: IMPLICIT_TEST_NAME
    }

    /** Binds [run] to [test] (see [TestState.runs]); a no-op once [test] is closed. */
    internal fun bindRun(test: TestState, run: UltronTestRunHooks.ActiveRun): Unit = locked {
        if (test === currentTest && run !in test.runs) test.runs.add(run)
    }

    /** Binds [ultronTest] to [test] (see [TestState.ultronTests]); a no-op once [test] is closed. */
    internal fun bindUltronTest(test: TestState, ultronTest: UltronTestListeners.ActiveTest): Unit = locked {
        if (test === currentTest && ultronTest !in test.ultronTests) test.ultronTests.add(ultronTest)
    }

    /** The open test [run] is bound to; `null` once that test is closed. */
    internal fun testOf(run: UltronTestRunHooks.ActiveRun): TestState? = locked { currentTest?.takeIf { run in it.runs } }

    /** The open test [ultronTest] is bound to; `null` once that test is closed. */
    internal fun testOf(ultronTest: UltronTestListeners.ActiveTest): TestState? =
        locked { currentTest?.takeIf { ultronTest in it.ultronTests } }

    /** `true` when a `test { }` is bound to [test] (see [TestState.ultronTests]); `false` for a listener driven directly. */
    internal fun hasUltronTests(test: TestState): Boolean = locked { test.ultronTests.isNotEmpty() }

    /** The blocks bound to [test] that still run, the one that opened it first. */
    internal fun liveRunsOf(test: TestState): List<UltronTestRunHooks.ActiveRun> = locked { test.runs.filter { !it.ended } }

    /**
     * Adds the parameter [name] = [value] to the test of the calling thread's block or `test { }` (see
     * [testOfCurrentRun]), replacing an earlier value of [name]; dropped for a block that finishes late. The result
     * lists it after `target`, and its `historyId` includes it (see [AllureIds.historyId]). [PARAMETER_TARGET] is
     * reserved for the target and ignored.
     */
    fun addParameter(name: String, value: String): Unit = locked {
        if (name == PARAMETER_TARGET) {
            UltronLog.warn("Allure: the parameter '$PARAMETER_TARGET' names the target of the result; '$value' ignored")
            return@locked
        }
        testOfCurrentRun()?.parameters?.set(name, value)
    }

    fun startStep(name: String): Unit = locked { startStep(ensureTestOpen(), name) }

    /** Opens a step in [test]; a no-op once [test] is closed. */
    fun startStep(test: TestState, name: String): Unit = locked {
        if (test !== currentTest) return@locked
        test.openSteps.addLast(OpenStep(name, clock()))
    }

    fun updateStep(status: AllureStatus, details: AllureStatusDetails? = null): Unit = locked {
        currentTest?.let { updateStep(it, status, details) }
    }

    fun updateStep(test: TestState, status: AllureStatus, details: AllureStatusDetails? = null): Unit = locked {
        val step = test.openSteps.lastOrNull() ?: return@locked
        step.status = status
        step.details = details ?: step.details
    }

    fun stopStep(status: AllureStatus? = null, details: AllureStatusDetails? = null): Unit = locked {
        currentTest?.let { stopStep(it, status, details) }
    }

    fun stopStep(test: TestState, status: AllureStatus? = null, details: AllureStatusDetails? = null): Unit = locked {
        val open = test.openSteps.removeLastOrNull() ?: return@locked
        val finished = AllureStep(
            name = open.name,
            status = status ?: open.status ?: AllureStatus.PASSED,
            statusDetails = details ?: open.details,
            start = open.start,
            stop = clock(),
            steps = open.steps.toList(),
            attachments = open.attachments.toList(),
        )
        (test.openSteps.lastOrNull()?.steps ?: test.steps).add(finished)
    }

    fun stopStep(throwable: Throwable) = stopStep(statusOf(throwable), detailsOf(throwable))

    fun stopStep(test: TestState, throwable: Throwable) = stopStep(test, statusOf(throwable), detailsOf(throwable))

    /** Writes the attachment and links it to the current step, or to the test when no step is open. */
    fun attach(name: String, bytes: ByteArray, extension: String, type: String): Unit = locked {
        attach(ensureTestOpen(), name, bytes, extension, type)
    }

    /** Writes the attachment and links it to the open step of [test], or to [test]; a no-op once [test] is closed. */
    fun attach(test: TestState, name: String, bytes: ByteArray, extension: String, type: String): Unit = locked {
        if (test !== currentTest) return@locked
        val source = runCatching { test.writer.writeAttachment(bytes, extension) }
            .onFailure { UltronLog.error("Allure: failed to write attachment '$name': ${it.message}") }
            .getOrNull() ?: return@locked
        val attachment = AllureAttachment(name = name, source = source, type = type)
        (test.openSteps.lastOrNull()?.attachments ?: test.attachments).add(attachment)
    }

    fun attach(name: String, text: String, extension: String, type: String) =
        attach(name, text.encodeToByteArray(), extension, type)

    /** Writes the attachment and links it to the test itself, even while steps are still open. */
    fun attachToTest(name: String, bytes: ByteArray, extension: String, type: String): Unit = locked {
        val test = ensureTestOpen()
        val source = runCatching { test.writer.writeAttachment(bytes, extension) }
            .onFailure { UltronLog.error("Allure: failed to write attachment '$name': ${it.message}") }
            .getOrNull() ?: return@locked
        test.attachments.add(AllureAttachment(name = name, source = source, type = type))
    }

    fun attachToTest(name: String, text: String, extension: String, type: String) =
        attachToTest(name, text.encodeToByteArray(), extension, type)

    /**
     * Reserves a test-level attachment [name] of [test] to be completed later (see [PendingAttachment]). The test may
     * close meanwhile; its result is then written when the attachment is complete or flushed.
     */
    fun deferAttachment(test: TestState, name: String, fallback: List<ApngFrame>): PendingAttachment = locked {
        PendingAttachment(this, test, name, fallback).also { test.pending.add(it) }
    }

    private val deferredResults = mutableListOf<DeferredResult>()

    private fun settle(pending: PendingAttachment, bytes: ByteArray?, extension: String, type: String): Unit = locked {
        if (pending.settled) {
            UltronLog.warn("Allure: attachment '${pending.name}' completed after it was settled; ignored")
            return@locked
        }
        pending.settled = true
        val test = pending.test
        test.pending.remove(pending)
        val attachment = bytes?.let {
            runCatching { test.writer.writeAttachment(it, extension) }
                .onFailure { e -> UltronLog.error("Allure: failed to write attachment '${pending.name}': ${e.message}") }
                .getOrNull()
        }?.let { AllureAttachment(name = pending.name, source = it, type = type) }
        val deferred = deferredResults.firstOrNull { it.test === test }
        if (deferred == null) {
            // The test is still open: the attachment joins it like attachToTest would have.
            if (attachment != null) test.attachments.add(attachment)
            return@locked
        }
        if (attachment != null) deferred.result = deferred.result.copy(attachments = deferred.result.attachments + attachment)
        if (test.pending.isEmpty()) {
            deferredResults.remove(deferred)
            writeResult(deferred.result, test)
        }
    }

    /**
     * Settles every pending attachment now, as an APNG of its fallback frames, and writes the results that waited for
     * them. For whoever collects the results before an encoder finished (`UltronAllureResults.snapshot()`, the exit
     * handler): a test is never lost to a Promise that did not resolve in time. A later `complete` is ignored.
     */
    fun flushPendingAttachments(): Unit = locked {
        val all = (deferredResults.map { it.test } + listOfNotNull(currentTest)).distinct()
        all.flatMap { it.pending.toList() }.forEach { it.completeWithFallback() }
    }

    /** Settles the pending attachments of [test] alone as APNG of their fallback frames, see the form without a test. */
    internal fun flushPendingAttachments(test: TestState): Unit = locked { test.pending.toList().forEach { it.completeWithFallback() } }

    /** `true` while [test], closed already, has its result held back by a pending attachment (see [deferAttachment]). */
    internal fun isResultDeferred(test: TestState): Boolean = locked { deferredResults.any { it.test === test } }

    /** The closed test [run] is bound to whose result still waits for a pending attachment; `null` when there is none. */
    internal fun deferredTestOf(run: UltronTestRunHooks.ActiveRun): TestState? =
        locked { deferredResults.firstOrNull { run in it.test.runs }?.test }

    /**
     * Suspends until the result of [test] is written, its pending attachments settled: the browser `runUltronUiTest`
     * resolves only then, so the result of the last test is in the console before Karma closes the page. Waits at
     * most [timeout] of real time (on `Dispatchers.Default`: the virtual time of `runTest` would skip it); then the
     * pending attachments of [test] are settled as APNG and its result is written at once. Returns at once when the
     * result of [test] is not held back.
     */
    internal suspend fun awaitResult(test: TestState, timeout: Duration) {
        if (!isResultDeferred(test)) return
        val written = withContext(Dispatchers.Default) {
            withTimeoutOrNull(timeout) {
                while (isResultDeferred(test)) delay(RESULT_POLL_MS)
                true
            }
        }
        if (written == null) {
            UltronLog.warn("Allure: video of '${test.name}' not encoded within $timeout; attached as APNG")
            flushPendingAttachments(test)
        }
    }

    /**
     * Closes the current test, writes its result and returns it; `null` when no test is open. Without a
     * [throwable] the test passed, except an implicit test nobody reports on (opened by an operation, not by
     * `runUltronUiTest`): its status is the worst of its steps, `failed` over `broken` over `passed`.
     */
    fun stopTest(throwable: Throwable?): AllureTestResult? = locked {
        val test = currentTest ?: return@locked null
        while (test.openSteps.isNotEmpty()) {
            if (throwable != null) stopStep(throwable) else stopStep()
        }
        if (throwable != null) return@locked writeAndClose(test, statusOf(throwable), detailsOf(throwable))
        val worst = if (test.closesWithItsSteps) worstStep(test.steps) else null
        writeAndClose(test, worst?.status ?: AllureStatus.PASSED, worst?.statusDetails)
    }

    /**
     * Closes whatever test is still open when the process exits (see `UltronAllure`): the last implicit test
     * of a run, which no later test closes, or a test the process died inside (one opened by `runUltronUiTest`,
     * named or not), written as broken.
     */
    fun closeAtExit(): Unit = locked {
        closeOpenTestAtExit()
        flushPendingAttachments()
    }

    private fun closeOpenTestAtExit() {
        val test = currentTest ?: return
        if (test.closesWithItsSteps) {
            stopTest(null)
        } else {
            val details = AllureStatusDetails(message = "test did not finish before the process exited")
            while (test.openSteps.isNotEmpty()) stopStep(AllureStatus.BROKEN, details)
            writeAndClose(test, AllureStatus.BROKEN, details)
        }
    }

    private fun worstStep(steps: List<AllureStep>): AllureStep? =
        steps.flatMap { listOf(it) + (worstStep(it.steps)?.let(::listOf) ?: emptyList()) }
            .filter { it.status != AllureStatus.PASSED }
            .minByOrNull { it.status.ordinal }

    private fun writeAndClose(test: TestState, status: AllureStatus, details: AllureStatusDetails?): AllureTestResult {
        val result = AllureTestResult(
            uuid = AllureIds.uuid(),
            historyId = AllureIds.historyId(test.fullName, platformTarget(), test.parameters),
            fullName = test.fullName,
            name = test.name,
            status = status,
            statusDetails = details,
            start = test.start,
            stop = clock(),
            steps = test.steps.toList(),
            attachments = test.attachments.toList(),
            parameters = listOf(AllureParameter(PARAMETER_TARGET, platformTarget())) +
                test.parameters.map { (name, value) -> AllureParameter(name, value) },
            labels = test.labels.toList(),
        )
        currentTest = null
        orphanedRuns.removeAll { it.ended }
        orphanedRuns += test.runs.filter { !it.ended && it !in orphanedRuns }
        orphanedTests.removeAll { it.ended }
        orphanedTests += test.ultronTests.filter { !it.ended && it !in orphanedTests }
        if (test.pending.isEmpty()) writeResult(result, test)
        else deferredResults.add(DeferredResult(test, result))
        return result
    }

    private fun writeResult(result: AllureTestResult, test: TestState) {
        runCatching { test.writer.writeResult(result) }
            .onFailure { UltronLog.error("Allure: failed to write result for '${test.name}' to ${test.writer.sink.description}: ${it.message}") }
    }

    private fun defaultLabels() = listOf(
        AllureLabel("framework", "ultron"),
        AllureLabel("language", "kotlin"),
        AllureLabel("host", platformName()),
    )

    companion object {
        const val IMPLICIT_TEST_NAME = "unknown test"
        /** How long the end of a test waits for an asynchronous video (see [awaitResult]) before it falls back to APNG. */
        internal val PENDING_RESULT_TIMEOUT: Duration = 10.seconds
        const val LABEL_BOUNDARY = "ultron.testBoundary"
        /** The JVM test method of a test whose name was given explicitly through `UltronTest.test(name)`. */
        const val LABEL_TEST_METHOD = "ultron.testMethod"
        /** The parameter that names the target of a result (see `platformTarget`), also part of its `historyId`. */
        const val PARAMETER_TARGET = "target"
        private const val RESULT_POLL_MS = 20L

        @OptIn(ExperimentalTime::class)
        val defaultClock: () -> Long = { Clock.System.now().toEpochMilliseconds() }

        /**
         * `skipped` when the throwable or any of its causes is a test framework's skip (an assumption failure, see
         * [isTestSkip]); otherwise `failed` when one of them is an [AssertionError]: Ultron wraps a failed
         * assertion (`UltronWrapperException : AssertionError`) into `UltronOperationException : RuntimeException`;
         * otherwise `broken`.
         */
        fun statusOf(throwable: Throwable): AllureStatus {
            val chain = generateSequence(throwable) { it.cause.takeIf { c -> c !== it } }
            return when {
                chain.any(::isTestSkip) -> AllureStatus.SKIPPED
                chain.any { it is AssertionError } -> AllureStatus.FAILED
                else -> AllureStatus.BROKEN
            }
        }

        fun detailsOf(throwable: Throwable) = AllureStatusDetails(
            message = throwable.message ?: throwable::class.simpleName,
            trace = throwable.stackTraceToString(),
        )
    }
}
