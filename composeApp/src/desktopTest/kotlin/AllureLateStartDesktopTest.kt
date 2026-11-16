import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.allure.model.AllureJson
import com.atiurin.ultron.allure.model.AllureStatus
import com.atiurin.ultron.allure.model.AllureTestResult
import com.atiurin.ultron.allure.writer.AllureResultsLocation
import com.atiurin.ultron.annotations.ExperimentalUltronApi
import com.atiurin.ultron.core.compose.runUltronUiTest
import com.atiurin.ultron.core.test.UltronTest
import com.atiurin.ultron.extensions.assertTextEquals
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Block A is abandoned (a JUnit timeout left it running on its own thread), block B closes A's test at its start,
 * and then A goes on and STARTS something: a `test { }` or a nested `runUltronUiTest`. That start belongs to A,
 * whose test is closed: it must neither adopt, rename, fail or close B's test nor open a test of its own.
 */
@OptIn(ExperimentalTestApi::class, ExperimentalUltronApi::class)
class AllureLateStartDesktopTest {
    private val resultsDir = File(AllureResultsLocation.resolve(null))

    class LateScenario : UltronTest() {
        fun lateTest(name: String) = test(name = name) {
            go { throw AssertionError("A failed late") }
        }

        /** A `test { }` that hangs in `go { }` until released, then fails: abandoned by a JUnit timeout. */
        fun hangingTest(name: String, started: CountDownLatch, release: CountDownLatch) = test(name = name) {
            go {
                started.countDown()
                release.await(10, TimeUnit.SECONDS)
                throw AssertionError("A failed late")
            }
        }
    }

    @BeforeTest
    fun configure() = AllureTestSetup.ensure()

    private fun freshResults(startedAt: Long): List<AllureTestResult> =
        (resultsDir.listFiles() ?: emptyArray())
            .filter { it.name.endsWith("-result.json") }
            .map { AllureJson.decodeResult(it.readText()) }
            .filter { it.stop >= startedAt }

    /** Runs block A on its own thread; it blocks until released, then runs [late]. */
    private fun lateBlock(name: String, started: CountDownLatch, release: CountDownLatch, late: () -> Unit) =
        thread(name = "late run") {
            runCatching {
                runUltronUiTest(name) {
                    setContent { Text("A", Modifier.testTag("a")) }
                    started.countDown()
                    release.await(10, TimeUnit.SECONDS)
                    late()
                }
            }
        }

    /**
     * Block B: an operation of its own, then it releases A and waits for A to end. No operation after that: a scene
     * A's nested `runUltronUiTest` set up on its thread keeps B's `waitForIdle` from ever returning.
     */
    private fun blockInProgress(name: String, releaseA: CountDownLatch, a: Thread) = runUltronUiTest(name) {
        setContent { Text("Hello", Modifier.testTag("text")) }
        hasTestTag("text").assertTextEquals("Hello")
        releaseA.countDown()
        a.join(10_000)
    }

    private fun assertUntouched(b: AllureTestResult, name: String) {
        assertEquals(name, b.name)
        assertEquals(AllureStatus.PASSED, b.status, b.statusDetails.toString())
        assertTrue(b.labels.none { it.value == "ultronTest" }, "not adopted by A's test { }: ${b.labels}")
        assertEquals(1, b.steps.size, b.steps.map { it.name }.toString())
        assertTrue("AssertTextEquals" in b.steps.single().name, b.steps.single().name)
        assertTrue(b.attachments.none { it.name.startsWith("test_failure_") }, b.attachments.map { it.name }.toString())
    }

    @Test
    fun aTestStartedByALateBlockLeavesTheTestInProgressAlone() {
        val startedAt = System.currentTimeMillis()
        val scenario = LateScenario()
        val aStarted = CountDownLatch(1)
        val releaseA = CountDownLatch(1)
        val a = lateBlock("late test start: A", aStarted, releaseA) { scenario.lateTest("late test start: A's test") }
        assertTrue(aStarted.await(10, TimeUnit.SECONDS), "precondition: A is running")
        blockInProgress("late test start: B", releaseA, a)

        val fresh = freshResults(startedAt)
        assertEquals(listOf("late test start: A", "late test start: B"), fresh.map { it.name }.sorted(), "no result of A's late test { }")
        assertUntouched(fresh.single { it.name == "late test start: B" }, "late test start: B")
        assertEquals(AllureStatus.BROKEN, fresh.single { it.name == "late test start: A" }.status, "closed by B's start")
    }

    @Test
    fun aRunNestedInALateBlockLeavesTheTestInProgressAlone() {
        val startedAt = System.currentTimeMillis()
        val aStarted = CountDownLatch(1)
        val releaseA = CountDownLatch(1)
        val a = lateBlock("late run start: A", aStarted, releaseA) {
            runUltronUiTest("late run start: inner") { throw AssertionError("A failed late") }
        }
        assertTrue(aStarted.await(10, TimeUnit.SECONDS), "precondition: A is running")
        blockInProgress("late run start: B", releaseA, a)

        val fresh = freshResults(startedAt)
        assertEquals(listOf("late run start: A", "late run start: B"), fresh.map { it.name }.sorted(), "no result of A's nested block")
        assertUntouched(fresh.single { it.name == "late run start: B" }, "late run start: B")
    }

    @Test
    fun aTestStartedByALateBlockAfterTheNextOneEndedOpensNoTest() {
        val startedAt = System.currentTimeMillis()
        val scenario = LateScenario()
        val aStarted = CountDownLatch(1)
        val releaseA = CountDownLatch(1)
        val a = lateBlock("late after: A", aStarted, releaseA) { scenario.lateTest("late after: A's test") }
        assertTrue(aStarted.await(10, TimeUnit.SECONDS), "precondition: A is running")
        runUltronUiTest("late after: B") {
            setContent { Text("Hello", Modifier.testTag("text")) }
            hasTestTag("text").assertTextEquals("Hello")
        }
        releaseA.countDown()
        a.join(10_000)

        val fresh = freshResults(startedAt)
        assertEquals(listOf("late after: A", "late after: B"), fresh.map { it.name }.sorted(), "no result of A's late test { }")
        assertUntouched(fresh.single { it.name == "late after: B" }, "late after: B")
    }

    /**
     * Test A, a `test { }` with an owner, hangs on its thread; the runner starts B, a plain `runUltronUiTest`, on
     * another. B must not join A (its thread runs no `test { }` of A): A is closed as broken at B's start, B gets a
     * result of its own with its operation, and A's late failure and close leave B alone.
     */
    @Test
    fun aBlockOnAnotherThreadDoesNotJoinAHangingUltronTest() {
        val startedAt = System.currentTimeMillis()
        val scenario = LateScenario()
        val aStarted = CountDownLatch(1)
        val releaseA = CountDownLatch(1)
        val a = thread(name = "hanging test") {
            runCatching { scenario.hangingTest("owned late: A", aStarted, releaseA) }
        }
        assertTrue(aStarted.await(10, TimeUnit.SECONDS), "precondition: A is running")
        blockInProgress("owned late: B", releaseA, a)

        val fresh = freshResults(startedAt)
        assertEquals(listOf("owned late: A", "owned late: B"), fresh.map { it.name }.sorted(), "one result each")
        assertUntouched(fresh.single { it.name == "owned late: B" }, "owned late: B")
        val resultA = fresh.single { it.name == "owned late: A" }
        assertEquals(AllureStatus.BROKEN, resultA.status, "closed by B's start: ${resultA.statusDetails}")
        assertTrue(resultA.steps.none { "AssertTextEquals" in it.name }, "B's operation not in A: ${resultA.steps.map { it.name }}")
    }
}
