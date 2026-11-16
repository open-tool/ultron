import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.allure.config.AllureAttachStrategy
import com.atiurin.ultron.allure.config.UltronAllureConfig
import com.atiurin.ultron.allure.model.AllureJson
import com.atiurin.ultron.allure.model.AllureStatus
import com.atiurin.ultron.allure.model.AllureTestResult
import com.atiurin.ultron.allure.step.step
import com.atiurin.ultron.allure.video.Mp4Info
import com.atiurin.ultron.allure.writer.AllureResultsLocation
import com.atiurin.ultron.core.compose.runUltronUiTest
import com.atiurin.ultron.extensions.assertTextEquals
import com.atiurin.ultron.extensions.withTimeout
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Block A is abandoned (a JUnit timeout left it running on its own thread) while block B runs, and goes on with
 * operations, a `step { }`, screenshots and video frames before it fails. The operation listeners address the test
 * of the block whose thread runs the operation: A's test was closed by B's start, so all of it is dropped and B's
 * report is B's alone.
 */
@OptIn(ExperimentalTestApi::class)
class AllureLateBlockOperationsDesktopTest {
    private val resultsDir = File(AllureResultsLocation.resolve(null))

    @BeforeTest
    fun configure() {
        AllureTestSetup.ensure()
        UltronAllureConfig.apply {
            addScreenshotPolicy = mutableSetOf(AllureAttachStrategy.OPERATION_FINISH)
            videoPolicy = mutableSetOf(AllureAttachStrategy.TEST_FINISH)
        }
    }

    @AfterTest
    fun restore() = UltronAllureConfig.apply {
        addScreenshotPolicy = mutableSetOf(AllureAttachStrategy.TEST_FAILURE, AllureAttachStrategy.OPERATION_FAILURE)
    }

    private fun freshResults(startedAt: Long): List<AllureTestResult> =
        (resultsDir.listFiles() ?: emptyArray())
            .filter { it.name.endsWith("-result.json") }
            .map { AllureJson.decodeResult(it.readText()) }
            .filter { it.stop >= startedAt }

    /** Samples of the mp4 video: one per distinct recorded frame. */
    private fun frameCount(result: AllureTestResult): Int {
        val video = File(resultsDir, result.attachments.single { it.name == "video" }.source).readBytes()
        return checkNotNull(Mp4Info.parse(video)) { "the video is an mp4" }.sampleCount
    }

    /** A late operation: B's scene has no such node, so it fails, after its step, screenshot and frames were taken. */
    private fun lateOperation() = runCatching { hasTestTag("late").withTimeout(300).assertTextEquals("late") }

    @Test
    fun operationsStepsScreenshotsAndFramesOfALateBlockNeverReachTheBlockInProgress() {
        val startedAt = System.currentTimeMillis()
        val aStarted = CountDownLatch(1)
        val releaseA = CountDownLatch(1)
        val aDone = CountDownLatch(1)
        val a = thread(name = "late run") {
            runCatching {
                runUltronUiTest("late ops: A") {
                    setContent { Text("A", Modifier.testTag("a")) }
                    hasTestTag("a").assertTextEquals("A")
                    aStarted.countDown()
                    releaseA.await(10, TimeUnit.SECONDS)
                    lateOperation()
                    step("late step") { lateOperation() }
                    throw AssertionError("A failed late")
                }
            }
            aDone.countDown()
        }
        assertTrue(aStarted.await(10, TimeUnit.SECONDS), "precondition: A is running")
        var label by mutableStateOf("B1")
        runUltronUiTest("late ops: B") {
            setContent { Text(label, Modifier.testTag("text")) }
            hasTestTag("text").assertTextEquals("B1")
            label = "while A runs late" // a screen of its own: a frame taken by A now would show in B's video
            releaseA.countDown()
            assertTrue(aDone.await(30, TimeUnit.SECONDS), "precondition: A finished while B runs")
            label = "B2"
            hasTestTag("text").assertTextEquals("B2")
        }
        a.join(10_000)

        val fresh = freshResults(startedAt)
        assertEquals(listOf("late ops: A", "late ops: B"), fresh.map { it.name }.sorted(), "one result per block, no implicit test")
        val b = fresh.single { it.name == "late ops: B" }
        assertEquals(AllureStatus.PASSED, b.status, b.statusDetails.toString())
        assertEquals(2, b.steps.size, b.steps.map { it.name }.toString())
        assertTrue(b.steps.none { "late" in it.name }, b.steps.map { it.name }.toString())
        assertEquals(2, b.steps.sumOf { it.attachments.size }, "one screenshot per own operation: ${b.steps.map { s -> s.attachments.map { it.name } }}")
        assertEquals(2, frameCount(b), "B's video has B's two screens only")
        val aResult = fresh.single { it.name == "late ops: A" }
        assertEquals(AllureStatus.BROKEN, aResult.status, "closed by B's start")
        assertEquals(1, aResult.steps.size, "A keeps the steps it had when B started")
    }

    @Test
    fun aStepOfALateBlockDoesNotLandInTheBlockInProgress() {
        val startedAt = System.currentTimeMillis()
        val aStarted = CountDownLatch(1)
        val releaseA = CountDownLatch(1)
        val aDone = CountDownLatch(1)
        val a = thread(name = "late run") {
            runCatching {
                runUltronUiTest("late step: A") {
                    aStarted.countDown()
                    releaseA.await(10, TimeUnit.SECONDS)
                    step("late step") { }
                }
            }
            aDone.countDown()
        }
        assertTrue(aStarted.await(10, TimeUnit.SECONDS), "precondition: A is running")
        runUltronUiTest("late step: B") {
            step("own step") {
                releaseA.countDown()
                assertTrue(aDone.await(10, TimeUnit.SECONDS), "precondition: A finished inside B's step")
            }
        }
        a.join(10_000)

        val b = freshResults(startedAt).single { it.name == "late step: B" }
        assertEquals(listOf("own step"), b.steps.map { it.name })
        assertTrue(b.steps.single().steps.isEmpty(), "the late step is not nested in B's open step")
        assertEquals(listOf("late step: A", "late step: B"), freshResults(startedAt).map { it.name }.sorted())
    }
}
