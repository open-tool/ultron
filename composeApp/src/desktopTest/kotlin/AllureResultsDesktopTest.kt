import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.allure.model.AllureIds
import com.atiurin.ultron.allure.model.AllureJson
import com.atiurin.ultron.allure.model.AllureStatus
import com.atiurin.ultron.allure.model.AllureTestResult
import com.atiurin.ultron.allure.UltronAllure
import com.atiurin.ultron.allure.config.UltronAllureConfig
import com.atiurin.ultron.allure.video.ApngInfo
import com.atiurin.ultron.allure.video.Mp4Info
import com.atiurin.ultron.allure.video.VideoFormat
import com.atiurin.ultron.allure.writer.AllureResultsLocation
import com.atiurin.ultron.core.compose.runUltronUiTest
import com.atiurin.ultron.exceptions.UltronOperationException
import com.atiurin.ultron.extensions.assertIsDisplayed
import com.atiurin.ultron.extensions.assertTextEquals
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.extensions.withTimeout
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Proves the whole chain on desktop against the real results directory. The directory is shared with
 * every other desktop test and never cleaned, so results are looked up by name, not counted.
 */
@OptIn(ExperimentalTestApi::class)
class AllureResultsDesktopTest {
    private val resultsDir = File(AllureResultsLocation.resolve(null))

    @BeforeTest
    fun setup() = AllureTestSetup.ensure()

    private val startedAt = System.currentTimeMillis()

    /** Newest result with this name written by this test: the directory keeps results of every previous run. */
    private fun latestResultNamed(name: String): AllureTestResult? =
        (resultsDir.listFiles() ?: emptyArray())
            .filter { it.name.endsWith("-result.json") }
            .map { AllureJson.decodeResult(it.readText()) }
            .filter { it.name == name && it.stop >= startedAt }
            .maxByOrNull { it.stop }

    private fun attachmentFile(source: String) = resultsDir.resolve(source)

    @Test
    fun passingTestWritesPassedResultNamedAfterTheMethod() {
        runUltronUiTest {
            setContent { Text("Hello", Modifier.testTag("text")) }
            hasTestTag("text").assertTextEquals("Hello")
        }

        val result = latestResultNamed("passingTestWritesPassedResultNamedAfterTheMethod")
        assertNotNull(result, "result file for this test in $resultsDir")
        assertEquals(AllureStatus.PASSED, result.status)
        assertEquals("AllureResultsDesktopTest.passingTestWritesPassedResultNamedAfterTheMethod", result.fullName)
        assertTrue(result.steps.isNotEmpty(), "operation steps recorded")
        assertEquals(AllureStatus.PASSED, result.steps.first().status)
    }

    /** Same-named tests of two classes must not share a historyId: the explicit name keeps the discovered class. */
    @Test
    fun explicitNameKeepsTheDiscoveredClassInFullName() {
        runUltronUiTest("allure explicit name sample") {
            setContent { Text("Hello", Modifier.testTag("text")) }
            hasTestTag("text").assertTextEquals("Hello")
        }

        val result = latestResultNamed("allure explicit name sample")
        assertNotNull(result, "result file for the explicitly named sample in $resultsDir")
        assertEquals("AllureResultsDesktopTest.allure explicit name sample", result.fullName)
        assertEquals(AllureIds.historyId(result.fullName, "desktop"), result.historyId)
        assertEquals(listOf("desktop"), result.parameters.filter { it.name == "target" }.map { it.value })
        assertEquals(listOf("AllureResultsDesktopTest"), result.labels.filter { it.name == "suite" }.map { it.value })
    }

    /** Invocations of a parameterized test: one test case, told apart by their parameters rather than by their names. */
    @Test
    fun parametersKeepInvocationsOfOneTestApart() {
        listOf("alice", "bob").forEach { user ->
            runUltronUiTest("allure parameter sample") {
                UltronAllure.parameter("user", user)
                setContent { Text("Hello $user", Modifier.testTag("text")) }
                hasTestTag("text").assertTextEquals("Hello $user")
            }
        }

        val results = (resultsDir.listFiles() ?: emptyArray())
            .filter { it.name.endsWith("-result.json") }
            .map { AllureJson.decodeResult(it.readText()) }
            .filter { it.name == "allure parameter sample" && it.stop >= startedAt }
            .sortedBy { it.stop }
        assertEquals(listOf("alice", "bob"), results.map { r -> r.parameters.single { it.name == "user" }.value })
        assertEquals(setOf("AllureResultsDesktopTest.allure parameter sample"), results.map { it.fullName }.toSet())
        assertEquals(2, results.map { it.historyId }.toSet().size, "one historyId per invocation")
        results.forEach { r ->
            assertEquals(AllureIds.historyId(r.fullName, "desktop", mapOf("user" to r.parameters.single { it.name == "user" }.value)), r.historyId)
            assertEquals(listOf("target", "user"), r.parameters.map { it.name })
        }
    }

    @Test
    fun failingTestWritesFailedResultWithStepsAndAttachments() {
        assertFailsWith<UltronOperationException> {
            runUltronUiTest("allure failing sample") {
                setContent { Text("Hello", Modifier.testTag("text")) }
                hasTestTag("text").withTimeout(300).assertTextEquals("Nope")
            }
        }

        val result = latestResultNamed("allure failing sample")
        assertNotNull(result, "result file for the failing sample in $resultsDir")
        assertEquals(AllureStatus.FAILED, result.status)
        assertTrue(result.statusDetails?.message.orEmpty().isNotBlank(), "status message")
        assertTrue(result.statusDetails?.trace.orEmpty().isNotBlank(), "status trace")

        val failedStep = result.steps.last()
        assertEquals(AllureStatus.FAILED, failedStep.status)
        assertEquals(
            setOf("screenshot_${failedStep.name}", "hierarchy_${failedStep.name}"),
            failedStep.attachments.map { it.name }.toSet()
        )

        val testLevel = result.attachments.associateBy { it.name }
        assertTrue("test_failure_screenshot" in testLevel && "test_failure_hierarchy" in testLevel && "ultron_log.txt" in testLevel, testLevel.keys.toString())
        assertEquals("image/png", testLevel.getValue("test_failure_screenshot").type)
        (failedStep.attachments + result.attachments).forEach {
            val file = attachmentFile(it.source)
            assertTrue(file.isFile && file.length() > 0, "attachment ${it.name} at $file")
        }
        val png = attachmentFile(testLevel.getValue("test_failure_screenshot").source).readBytes()
        assertEquals(0x89.toByte(), png[0]); assertEquals('P'.code.toByte(), png[1])
        assertTrue(attachmentFile(testLevel.getValue("test_failure_hierarchy").source).readText().contains("Text"), "hierarchy mentions the Text node")
    }

    @Test
    fun videoSampleAttachesMp4WithOneSamplePerDistinctScreen() {
        runUltronUiTest("desktop video sample") {
            setContent { VideoSampleScreen() }
            repeat(VideoSample.STEPS) { step ->
                hasTestTag("inc").click()
                hasTestTag("count").assertTextEquals(VideoSample.titleAfter(step + 1))
                VideoSample.pause()
            }
            hasTestTag("done_${VideoSample.STEPS}").assertIsDisplayed()
        }

        val result = latestResultNamed("desktop video sample")
        assertNotNull(result, "result for the video sample in $resultsDir")
        assertEquals(AllureStatus.PASSED, result.status)
        val video = result.attachments.single { it.name == "video" }
        assertEquals("video/mp4", video.type)
        val info = Mp4Info.parse(attachmentFile(video.source).readBytes())
        assertNotNull(info, "attachment is an mp4")
        assertEquals("avc1", info.codec)
        assertTrue(info.sampleCount >= VideoSample.EXPECTED_MIN_FRAMES, "one sample per distinct screen, got ${info.sampleCount}")
        assertTrue(result.steps.all { step -> step.attachments.none { it.name == "video" } }, "video is a test-level attachment")
    }

    @Test
    fun videoFormatApngKeepsTheAnimatedPng() {
        UltronAllureConfig.apply { videoFormat = VideoFormat.APNG }
        try {
            runUltronUiTest("desktop apng video sample") {
                setContent { VideoSampleScreen() }
                repeat(VideoSample.STEPS) { step ->
                    hasTestTag("inc").click()
                    hasTestTag("count").assertTextEquals(VideoSample.titleAfter(step + 1))
                    VideoSample.pause()
                }
                hasTestTag("done_${VideoSample.STEPS}").assertIsDisplayed()
            }
        } finally {
            UltronAllureConfig.apply { videoFormat = VideoFormat.AUTO }
        }

        val result = latestResultNamed("desktop apng video sample")
        assertNotNull(result, "result for the apng sample in $resultsDir")
        val video = result.attachments.single { it.name == "video" }
        assertEquals("image/png", video.type)
        val frames = ApngInfo.frameCount(attachmentFile(video.source).readBytes())
        assertNotNull(frames, "attachment is an APNG")
        assertTrue(frames >= VideoSample.EXPECTED_MIN_FRAMES, "one frame per distinct screen, got $frames")
    }

    @Test
    fun waitingInsideOneOperationStillYieldsIntermediateFrames() {
        val started = System.currentTimeMillis()
        runUltronUiTest("desktop waiting video sample") {
            setContent { ElapsedSecondsScreen() }
            hasTestTag("elapsed").withTimeout(10_000).assertTextEquals("Elapsed: ${VideoSample.WAIT_SECONDS} s")
        }
        val elapsed = System.currentTimeMillis() - started
        println("waiting sample: ${elapsed} ms for a ${VideoSample.WAIT_SECONDS * 1000} ms wait")

        val result = latestResultNamed("desktop waiting video sample")
        assertNotNull(result)
        assertEquals(AllureStatus.PASSED, result.status)
        val bytes = attachmentFile(result.attachments.single { it.name == "video" }.source).readBytes()
        val frames = Mp4Info.parse(bytes)?.sampleCount
        assertNotNull(frames, "attachment is an mp4")
        // 3 s of waiting at one retry frame per 500 ms: "0 s", "1 s", "2 s", "3 s" plus the final operation frame
        assertTrue(frames >= VideoSample.WAIT_SECONDS + 1, "intermediate frames while one operation waits, got $frames")
        // At most one retry frame per 500 ms plus the operation frame: more would mean the interval is not honoured.
        assertTrue(frames <= VideoSample.WAIT_SECONDS * 2 + 2, "no more than one frame per retry interval, got $frames")
        // Wall-clock bound kept loose on purpose: a loaded CI machine is not a regression, a run twice as long is.
        assertTrue(elapsed < VideoSample.WAIT_SECONDS * 1000 * 2 + 5000, "recording overhead stays bounded: $elapsed ms")
    }

    @Test
    fun passingTestWithStaticScreenStillGetsAVideoOfOneFrame() {
        runUltronUiTest("desktop static video sample") {
            setContent { Text("Hello", Modifier.testTag("text")) }
            hasTestTag("text").assertTextEquals("Hello")
            hasTestTag("text").assertTextEquals("Hello")
        }
        val result = latestResultNamed("desktop static video sample")
        assertNotNull(result)
        val bytes = attachmentFile(result.attachments.single { it.name == "video" }.source).readBytes()
        assertEquals(1, Mp4Info.parse(bytes)?.sampleCount, "identical frames are merged into one sample")
    }
}
