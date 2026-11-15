import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.allure.model.AllureJson
import com.atiurin.ultron.allure.model.AllureStatus
import com.atiurin.ultron.allure.model.AllureTestResult
import com.atiurin.ultron.allure.video.ApngInfo
import com.atiurin.ultron.allure.video.Mp4Info
import com.atiurin.ultron.allure.writer.UltronAllureResults
import com.atiurin.ultron.core.compose.runUltronUiTest
import com.atiurin.ultron.extensions.assertIsDisplayed
import com.atiurin.ultron.extensions.assertTextEquals
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.extensions.withTimeout
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Browser: no file system, so results stay in [UltronAllureResults] and are dumped to the console.
 *
 * On js/wasmJs `runUltronUiTest` returns a promise, so a result exists only after the test function
 * has completed: the two `a_` tests produce results and `b_checkCollectedResults` inspects them.
 * kotlin.test runs the tests of a class in declaration order on the browser targets.
 */
@OptIn(ExperimentalTestApi::class, kotlin.js.ExperimentalWasmJsInterop::class)
class AllureResultsWasmTest {
    @BeforeTest
    fun setup() = AllureTestSetup.ensure()

    private fun latestResultNamed(name: String): AllureTestResult? =
        UltronAllureResults.snapshot()
            .filterKeys { it.endsWith("-result.json") }
            .values
            .map { AllureJson.decodeResult(it.decodeToString()) }
            .filter { it.name == name }
            .maxByOrNull { it.stop }

    @Test
    fun a_passingTest() = runUltronUiTest("wasm allure sample") {
        setContent { Text("Hello", Modifier.testTag("text")) }
        hasTestTag("text").assertTextEquals("Hello")
    }

    @Test
    fun a_failingOperationCaughtInTest() = runUltronUiTest("wasm allure failing operation") {
        setContent { Text("Hello", Modifier.testTag("text")) }
        runCatching { hasTestTag("text").withTimeout(300).assertTextEquals("Nope") }
    }

    /** No explicit name: the kotlin.test name reaches Ultron through `karma-adapters/ultron-test-name-adapter.js`. */
    @Test
    fun a_unnamedTestIsNamedByTheKarmaAdapter() = runUltronUiTest {
        setContent { Text("Hello", Modifier.testTag("text")) }
        hasTestTag("text").assertTextEquals("Hello")
    }

    @Test
    fun a_videoSample() = runUltronUiTest("wasm video sample") {
        setContent { VideoSampleScreen() }
        repeat(VideoSample.STEPS) { step ->
            hasTestTag("inc").click()
            hasTestTag("count").assertTextEquals(VideoSample.titleAfter(step + 1))
            VideoSample.pause()
        }
        hasTestTag("done_${VideoSample.STEPS}").assertIsDisplayed()
    }

    @Test
    fun b_checkCollectedResults() = runUltronUiTest("wasm allure check") {
        val passing = latestResultNamed("wasm allure sample")
        assertNotNull(passing, "result in snapshot: ${UltronAllureResults.snapshot().keys}")
        assertEquals(AllureStatus.PASSED, passing.status)
        assertEquals("web", passing.labels.first { it.name == "host" }.value)
        assertTrue(passing.steps.isNotEmpty(), "operation steps recorded inside the async test body")
        assertEquals(AllureStatus.PASSED, passing.steps.first().status)

        val withFailedOp = latestResultNamed("wasm allure failing operation")
        assertNotNull(withFailedOp)
        assertEquals(AllureStatus.PASSED, withFailedOp.status)
        val failedStep = withFailedOp.steps.last()
        assertEquals(AllureStatus.FAILED, failedStep.status)
        val screenshot = failedStep.attachments.first { it.name.startsWith("screenshot_") }
        val png = UltronAllureResults.snapshot().getValue(screenshot.source)
        assertTrue(png.size > 8 && png[0] == 0x89.toByte() && png[1] == 'P'.code.toByte(), "PNG signature, ${png.size} bytes")
        assertTrue(failedStep.attachments.any { it.name.startsWith("hierarchy_") })

        val unnamed = latestResultNamed("a_unnamedTestIsNamedByTheKarmaAdapter")
        assertNotNull(unnamed, "kotlin.test name from the Karma adapter; results: ${UltronAllureResults.snapshot().keys}")
        assertEquals(AllureStatus.PASSED, unnamed.status)
        assertEquals("AllureResultsWasmTest", unnamed.labels.single { it.name == "suite" }.value, "class from the adapter's suite path")
        assertEquals("AllureResultsWasmTest.a_unnamedTestIsNamedByTheKarmaAdapter", unnamed.fullName)
        val implicit = latestResultNamed("unknown test")
        assertTrue(implicit == null || implicit.stop < unnamed.start, "no implicit result for the unnamed test")

        val videoSample = latestResultNamed("wasm video sample")
        assertNotNull(videoSample)
        val video = videoSample.attachments.single { it.name == "video" }
        // mp4 or webm when the browser encoder finished before the snapshot; APNG when it did not (the snapshot settles
        // a video still being encoded as APNG) or the browser has no encoder.
        val bytes = UltronAllureResults.snapshot().getValue(video.source)
        when (video.type) {
            "video/mp4" -> {
                val frames = Mp4Info.parse(bytes)?.sampleCount
                assertNotNull(frames, "attachment is an mp4")
                assertTrue(frames >= VideoSample.EXPECTED_MIN_FRAMES, "one frame per distinct screen, got $frames")
            }
            "video/webm" -> assertTrue(bytes.size > 4 && bytes[0] == 0x1A.toByte() && bytes[1] == 0x45.toByte(), "attachment is an EBML file")
            "image/png" -> {
                val frames = ApngInfo.frameCount(bytes)
                assertNotNull(frames, "attachment is an APNG")
                assertTrue(frames >= VideoSample.EXPECTED_MIN_FRAMES, "one frame per distinct screen, got $frames")
            }
            else -> fail("unexpected video type ${video.type}")
        }

        val lines = mutableListOf<String>()
        UltronAllureResults.dumpToConsole { lines += it }
        assertEquals(UltronAllureResults.snapshot().size, lines.size, "one line per file")
        assertTrue(lines.all { it.startsWith(UltronAllureResults.CONSOLE_PREFIX) })
        assertTrue(lines.any { it.startsWith("${UltronAllureResults.CONSOLE_PREFIX}${passing.uuid}-result.json ") })
    }

    /** Last by name: prints every result collected so far for the host (`collectWasmAllureResults`). */
    @Test
    fun z_dumpResultsToConsole() {
        UltronAllureResults.dumpToConsole()
    }
}
