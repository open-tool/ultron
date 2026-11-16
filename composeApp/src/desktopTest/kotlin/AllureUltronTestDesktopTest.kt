import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.allure.UltronAllure
import com.atiurin.ultron.allure.config.UltronAllureConfig
import com.atiurin.ultron.allure.model.AllureJson
import com.atiurin.ultron.allure.step.step
import com.atiurin.ultron.allure.model.AllureStatus
import com.atiurin.ultron.allure.model.AllureTestResult
import com.atiurin.ultron.allure.writer.AllureResultsLocation
import com.atiurin.ultron.annotations.ExperimentalUltronApi
import com.atiurin.ultron.core.compose.UltronTestRunHooks
import com.atiurin.ultron.core.compose.runUltronUiTest
import com.atiurin.ultron.core.test.UltronTest
import com.atiurin.ultron.extensions.assertTextEquals
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Allure through the `UltronTest` lifecycle: the config is applied once per class in `beforeFirstTest`, the
 * Allure test opens at `test { }`, `before`/`after` blocks are steps and the class is the suite.
 */
@OptIn(ExperimentalTestApi::class, ExperimentalUltronApi::class)
class AllureUltronTestDesktopTest {
    private val resultsDir = File(AllureResultsLocation.resolve(null))

    /** The test under test; its `test { }` runs inside a method of the outer class, which names the result. */
    class SampleScenario : UltronTest() {
        override val beforeFirstTest = { AllureTestSetup.ensure() }

        fun run() = test {
            before { }.go {
                runUltronUiTest {
                    setContent { Text("Hello", Modifier.testTag("text")) }
                    hasTestTag("text").assertTextEquals("Hello")
                }
            }.after { }
        }

        /** `test(name)` names the result; the `runUltronUiTest` name inside it is ignored. */
        fun runNamed() = test(name = "named scenario") {
            go {
                runUltronUiTest("ignored") {
                    setContent { Text("Hello", Modifier.testTag("text")) }
                    hasTestTag("text").assertTextEquals("Hello")
                }
            }
        }
    }

    /** Two `test { }` blocks in one `runUltronUiTest`, the browser form: each gets a result of its own. */
    class TwoTestsScenario : UltronTest() {
        override val beforeFirstTest = { AllureTestSetup.ensure() }

        fun run() = runUltronUiTest {
            setContent { Text("Hello", Modifier.testTag("text")) }
            test(name = "two tests: first") { go { hasTestTag("text").assertTextEquals("Hello") } }
            test(name = "two tests: second") { go { hasTestTag("text").assertTextEquals("Hello") } }
        }
    }

    /** `runUltronUiTest` nested in another one: the inner block joins the test of the outer, which alone closes it. */
    class NestedRunScenario : UltronTest() {
        override val beforeFirstTest = { AllureTestSetup.ensure() }

        /** The browser form with a `runUltronUiTest` in `go { }`: the adopted test is the one result. */
        fun runInAdoptedTest() = runUltronUiTest {
            test(name = "nested run: adopted") {
                go {
                    runUltronUiTest("nested run: inner") {
                        setContent { Text("Hello", Modifier.testTag("text")) }
                        hasTestTag("text").assertTextEquals("Hello")
                    }
                }
            }
        }
    }

    /** Nesting under Allure: each form gives one result, the outer one. */
    class NestingScenario : UltronTest() {
        override val beforeFirstTest = { AllureTestSetup.ensure() }

        /** `test { }` in a `runUltronUiTest` nested in another: adopts the test the outer block opened. */
        fun testInNestedRuns() = runUltronUiTest("nested runs: outer") {
            runUltronUiTest("nested runs: inner") {
                setContent { Text("Hello", Modifier.testTag("text")) }
                test(name = "nested runs: test") { go { hasTestTag("text").assertTextEquals("Hello") } }
            }
        }

        /** Two `test { }` in a `runUltronUiTest` nested in another: the second test is bound to both blocks too. */
        fun twoTestsInNestedRuns() = runUltronUiTest("reopen: outer") {
            runUltronUiTest("reopen: inner") {
                setContent { Text("Hello", Modifier.testTag("text")) }
                test(name = "reopen: first") { go { hasTestTag("text").assertTextEquals("Hello") } }
                test(name = "reopen: second") { go { hasTestTag("text").assertTextEquals("Hello") } }
            }
        }

        /** `test { }` in the `go { }` of another: joins it. */
        fun nestedTests() = test(name = "nested tests: outer") {
            go { test(name = "nested tests: inner") { go { } } }
        }

        /** The same inside `runUltronUiTest`, where the outer `test { }` adopted the block's test. */
        fun nestedTestsInRun() = runUltronUiTest {
            setContent { Text("Hello", Modifier.testTag("text")) }
            test(name = "nested adopted: outer") {
                go { test(name = "nested adopted: inner") { go { hasTestTag("text").assertTextEquals("Hello") } } }
            }
        }
    }

    /** A plain `test { }` (no `runUltronUiTest`) started while a test of an earlier, finished block is still open. */
    class AfterStaleBlockScenario : UltronTest() {
        fun run() = test(name = "after a stale block") { go { } }
    }

    /** Two `test { }` calls on two threads: the first is abandoned while the second runs, and fails late. */
    class LateTestScenario : UltronTest() {
        override val beforeFirstTest = { AllureTestSetup.ensure() }

        fun runLate(started: CountDownLatch, release: CountDownLatch) = test(name = "late test: A") {
            go {
                started.countDown()
                release.await(10, TimeUnit.SECONDS)
                throw AssertionError("A failed late")
            }.after { step("late step of A") { } }
        }

        fun runNext(release: CountDownLatch, late: Thread) = test(name = "late test: B") {
            go {
                release.countDown()
                late.join(10_000)
            }
        }
    }

    private fun freshResults(startedAt: Long): List<AllureTestResult> =
        (resultsDir.listFiles() ?: emptyArray())
            .filter { it.name.endsWith("-result.json") }
            .map { AllureJson.decodeResult(it.readText()) }
            .filter { it.stop >= startedAt }

    private fun latestResultNamed(name: String): AllureTestResult? =
        (resultsDir.listFiles() ?: emptyArray())
            .filter { it.name.endsWith("-result.json") }
            .map { AllureJson.decodeResult(it.readText()) }
            .filter { it.name == name }
            .maxByOrNull { it.stop }

    @Test
    fun ultronTestBlocksBecomeStepsAndTheClassIsTheSuite() {
        SampleScenario().run()

        val result = latestResultNamed("ultronTestBlocksBecomeStepsAndTheClassIsTheSuite")
        assertNotNull(result, "result of the UltronTest sample in $resultsDir")
        assertEquals(AllureStatus.PASSED, result.status)
        assertEquals("SampleScenario", result.labels.single { it.name == "suite" }.value)
        assertEquals("AllureUltronTestDesktopTest.SampleScenario.ultronTestBlocksBecomeStepsAndTheClassIsTheSuite", result.fullName)
        assertEquals("ultronTest", result.labels.single { it.name == "ultron.testBoundary" }.value)
        val names = result.steps.map { it.name }
        assertEquals("before", names.first(), names.toString())
        assertEquals("after", names.last(), names.toString())
        assertTrue(names.any { it.contains("AssertTextEquals") }, "operation step between the blocks: $names")
    }

    @Test
    fun twoTestsInOneRunUltronUiTestAreTwoResults() {
        val startedAt = System.currentTimeMillis()
        TwoTestsScenario().run()

        val first = assertNotNull(latestResultNamed("two tests: first"))
        val second = assertNotNull(latestResultNamed("two tests: second"))
        assertTrue(first.stop >= startedAt && second.stop >= startedAt, "both written by this run")
        assertEquals("TwoTestsScenario", first.labels.single { it.name == "suite" }.value)
        assertEquals("TwoTestsScenario", second.labels.single { it.name == "suite" }.value)
        // `before` and `after` are steps of every `test { }`; one operation step each, nothing of the other test.
        assertEquals(1, first.steps.count { "AssertTextEquals" in it.name }, "the first test keeps only its own operation: ${first.steps.map { it.name }}")
        assertEquals(1, second.steps.count { "AssertTextEquals" in it.name }, "the second test keeps only its own operation: ${second.steps.map { it.name }}")
        assertTrue("video" in first.attachments.map { it.name } && "video" in second.attachments.map { it.name }, "each test has its video")
    }

    @Test
    fun runUltronUiTestNestedInTheGoBlockOfAnAdoptedTestJoinsIt() {
        val startedAt = System.currentTimeMillis()
        NestedRunScenario().runInAdoptedTest()

        val fresh = freshResults(startedAt)
        assertEquals(listOf("nested run: adopted"), fresh.map { it.name }, "one result, the outer test is not closed as broken")
        val result = fresh.single()
        assertEquals(AllureStatus.PASSED, result.status)
        assertEquals("NestedRunScenario", result.labels.single { it.name == "suite" }.value)
        assertTrue(result.steps.any { "AssertTextEquals" in it.name }, "the inner block's operation is a step of the joined test: ${result.steps.map { it.name }}")
    }

    @Test
    fun runUltronUiTestNestedInAnotherKeepsTheOuterTestAndName() {
        configureAllure()
        val startedAt = System.currentTimeMillis()
        runUltronUiTest("nested plain: outer") {
            runUltronUiTest("nested plain: inner") {
                setContent { Text("Hello", Modifier.testTag("text")) }
                hasTestTag("text").assertTextEquals("Hello")
            }
        }

        val fresh = freshResults(startedAt)
        assertEquals(listOf("nested plain: outer"), fresh.map { it.name })
        assertEquals(AllureStatus.PASSED, fresh.single().status)
        assertTrue(fresh.single().steps.any { "AssertTextEquals" in it.name })
    }

    /**
     * A test opened by an earlier `runUltronUiTest` that never reached its `afterTest` (here the hook is removed
     * inside the block) is not adopted by a later `test { }`: it is closed as broken with its video and log.
     */
    @Test
    fun aTestLeftOpenByAnEarlierBlockIsClosedAsBrokenNotAdopted() {
        configureAllure()
        val startedAt = System.currentTimeMillis()
        val hook = UltronTestRunHooks.all().single { it.id == "AllureTestRunHook" }
        try {
            runUltronUiTest("stale block") {
                setContent { Text("Hello", Modifier.testTag("text")) }
                hasTestTag("text").assertTextEquals("Hello")
                UltronTestRunHooks.unregister(hook.id)
            }
        } finally {
            UltronTestRunHooks.register(hook)
        }
        AfterStaleBlockScenario().run()

        val fresh = freshResults(startedAt).associateBy { it.name }
        assertEquals(setOf("stale block", "after a stale block"), fresh.keys)
        val stale = fresh.getValue("stale block")
        assertEquals(AllureStatus.BROKEN, stale.status)
        assertTrue("did not finish" in stale.statusDetails?.message.orEmpty(), stale.statusDetails.toString())
        assertTrue(stale.steps.any { "AssertTextEquals" in it.name }, stale.steps.map { it.name }.toString())
        val attachments = stale.attachments.map { it.name }
        assertTrue("video" in attachments && "ultron_log.txt" in attachments, attachments.toString())
        val next = fresh.getValue("after a stale block")
        assertEquals(AllureStatus.PASSED, next.status)
        assertTrue(next.steps.none { "AssertTextEquals" in it.name }, next.steps.map { it.name }.toString())
    }

    @Test
    fun explicitTestNameWinsOverMethodAndRunUltronUiTestName() {
        val startedAt = System.currentTimeMillis()
        SampleScenario().runNamed()

        val result = latestResultNamed("named scenario")
        assertNotNull(result, "result of the named UltronTest sample in $resultsDir")
        assertTrue(result.stop >= startedAt, "fresh result, not one from an earlier run")
        assertEquals(AllureStatus.PASSED, result.status)
        assertEquals("AllureUltronTestDesktopTest.SampleScenario.named scenario", result.fullName)
        assertEquals("SampleScenario", result.labels.single { it.name == "suite" }.value)
        assertEquals(
            "explicitTestNameWinsOverMethodAndRunUltronUiTestName",
            result.labels.single { it.name == "ultron.testMethod" }.value,
        )
        val ignored = latestResultNamed("ignored")
        assertTrue(ignored == null || ignored.stop < startedAt, "runUltronUiTest(\"ignored\") must not name a result")
    }

    /**
     * Run A is abandoned (a JUnit timeout left it running on its own thread) when run B starts, and fails while B
     * runs. A's late callbacks address A's test, already closed by B's start: B is one result, passed, with its own
     * steps and without A's failure artifacts.
     */
    @Test
    fun aBlockFailingLateDoesNotTouchTheTestOfTheBlockInProgress() {
        configureAllure()
        val startedAt = System.currentTimeMillis()
        val aStarted = CountDownLatch(1)
        val releaseA = CountDownLatch(1)
        val a = thread(name = "late run") {
            runCatching {
                runUltronUiTest("late block: A") {
                    setContent { Text("A", Modifier.testTag("a")) }
                    aStarted.countDown()
                    releaseA.await(10, TimeUnit.SECONDS)
                    UltronAllure.captureVideoFrame("late frame of A")
                    throw AssertionError("A failed late")
                }
            }
        }
        assertTrue(aStarted.await(10, TimeUnit.SECONDS), "precondition: A is running")
        var framesOfB: List<String>? = null
        runUltronUiTest("late block: B") {
            setContent { Text("Hello", Modifier.testTag("text")) }
            releaseA.countDown()
            a.join(10_000)
            framesOfB = UltronAllureConfig.videoAttacher?.recorder?.frames()?.map { it.label }
            hasTestTag("text").assertTextEquals("Hello")
        }
        assertNotNull(framesOfB, "precondition: B is recorded")
        assertTrue("late frame of A" !in framesOfB!!, framesOfB.toString())

        val fresh = freshResults(startedAt)
        assertEquals(listOf("late block: A", "late block: B"), fresh.map { it.name }.sorted(), "one result per block")
        val b = fresh.single { it.name == "late block: B" }
        assertEquals(AllureStatus.PASSED, b.status, b.statusDetails.toString())
        assertTrue(b.steps.any { "AssertTextEquals" in it.name }, b.steps.map { it.name }.toString())
        assertTrue(b.attachments.none { it.name.startsWith("test_failure_") }, b.attachments.map { it.name }.toString())
        assertEquals(AllureStatus.BROKEN, fresh.single { it.name == "late block: A" }.status)
    }

    /** The `test { }` form of the late block: A's late `afterTest` must not close B with A's failure. */
    @Test
    fun aTestFinishingLateDoesNotCloseTheTestInProgress() {
        configureAllure()
        val startedAt = System.currentTimeMillis()
        val scenario = LateTestScenario()
        val aStarted = CountDownLatch(1)
        val releaseA = CountDownLatch(1)
        val a = thread(name = "late test") { runCatching { scenario.runLate(aStarted, releaseA) } }
        assertTrue(aStarted.await(10, TimeUnit.SECONDS), "precondition: A is running")
        scenario.runNext(releaseA, a)

        val fresh = freshResults(startedAt)
        assertEquals(listOf("late test: A", "late test: B"), fresh.map { it.name }.sorted(), "one result per test")
        val b = fresh.single { it.name == "late test: B" }
        assertEquals(AllureStatus.PASSED, b.status, b.statusDetails.toString())
        assertEquals(listOf("before", "after"), b.steps.map { it.name }, "B's own blocks only, nothing of A's late after { }")
        val bLog = b.attachments.singleOrNull { it.name == "ultron_log.txt" }?.let { File(resultsDir, it.source).readText() }.orEmpty()
        assertTrue("late step of A" !in bLog && "late test: A" !in bLog, "A's late lines are not in B's log:\n$bLog")
        assertEquals(AllureStatus.BROKEN, fresh.single { it.name == "late test: A" }.status)
    }

    @Test
    fun aTestInARunNestedInAnotherAdoptsTheTestOfTheOuterRun() {
        val startedAt = System.currentTimeMillis()
        NestingScenario().testInNestedRuns()

        val fresh = freshResults(startedAt)
        assertEquals(listOf("nested runs: test"), fresh.map { it.name }, "one result, the outer test is not closed as broken")
        assertEquals(AllureStatus.PASSED, fresh.single().status, fresh.single().statusDetails.toString())
        assertTrue(fresh.single().steps.any { "AssertTextEquals" in it.name })
    }

    /** The inner block addresses the reopened test of the second `test { }`: it is not told its test was closed. */
    @Test
    fun aSecondTestInNestedRunsIsBoundToBothRuns() {
        val startedAt = System.currentTimeMillis()
        NestingScenario().twoTestsInNestedRuns()

        val fresh = freshResults(startedAt)
        assertEquals(listOf("reopen: first", "reopen: second"), fresh.map { it.name }.sorted())
        val second = fresh.single { it.name == "reopen: second" }
        assertEquals(AllureStatus.PASSED, second.status, second.statusDetails.toString())
        val log = second.attachments.singleOrNull { it.name == "ultron_log.txt" }?.let { File(resultsDir, it.source).readText() }.orEmpty()
        assertTrue("finished after its test was closed" !in log, log)
    }

    @Test
    fun aTestNestedInTheGoBlockOfAnotherJoinsIt() {
        val startedAt = System.currentTimeMillis()
        NestingScenario().nestedTests()

        val fresh = freshResults(startedAt)
        assertEquals(listOf("nested tests: outer"), fresh.map { it.name }, "one result, the outer test is not closed as broken")
        assertEquals(AllureStatus.PASSED, fresh.single().status, fresh.single().statusDetails.toString())
    }

    @Test
    fun aTestNestedInAnAdoptedTestJoinsIt() {
        val startedAt = System.currentTimeMillis()
        NestingScenario().nestedTestsInRun()

        val fresh = freshResults(startedAt)
        assertEquals(listOf("nested adopted: outer"), fresh.map { it.name }, "one result, the outer test is not closed as broken")
        assertEquals(AllureStatus.PASSED, fresh.single().status, fresh.single().statusDetails.toString())
        assertTrue(fresh.single().steps.any { "AssertTextEquals" in it.name })
    }
}
