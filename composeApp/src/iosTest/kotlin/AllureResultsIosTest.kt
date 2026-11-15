import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.allure.lifecycle.AllureLifecycle
import com.atiurin.ultron.allure.model.AllureJson
import com.atiurin.ultron.allure.model.AllureStatus
import com.atiurin.ultron.allure.model.AllureTestResult
import com.atiurin.ultron.allure.video.ApngInfo
import com.atiurin.ultron.allure.video.Mp4Info
import com.atiurin.ultron.allure.writer.AllureResultsLocation
import com.atiurin.ultron.core.compose.runUltronUiTest
import com.atiurin.ultron.core.test.UltronTest
import com.atiurin.ultron.exceptions.UltronOperationException
import com.atiurin.ultron.extensions.assertIsDisplayed
import com.atiurin.ultron.extensions.assertTextEquals
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.extensions.withTimeout
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * iOS simulator: results land on the host in `SIMCTL_CHILD_ULTRON_ALLURE_RESULTS_DIR` (see
 * `composeApp/build.gradle.kts`). The directory is shared and never cleaned, so results are looked up
 * by name and only results written after the test started count.
 */
@OptIn(ExperimentalTestApi::class)
class AllureResultsIosTest {
    private val fs = FileSystem.SYSTEM
    private val resultsDir: Path = AllureResultsLocation.resolve(null).toPath()

    @BeforeTest
    fun setup() = AllureTestSetup.ensure()

    @OptIn(ExperimentalTime::class)
    private val startedAt = Clock.System.now().toEpochMilliseconds()

    /** Newest result with this name written by this test, never one of an earlier run. */
    private fun latestResultNamed(name: String): AllureTestResult? =
        fs.list(resultsDir)
            .filter { it.name.endsWith("-result.json") }
            .map { AllureJson.decodeResult(fs.read(it) { readUtf8() }) }
            .filter { it.name == name && it.stop >= startedAt }
            .maxByOrNull { it.stop }

    /** No stack discovery on iOS: without `test(name)` this result would be `unknown test`. */
    class NamedIosScenario : UltronTest() {
        fun run() = test(name = "ios named scenario") {
            go {
                runUltronUiTest("ignored on ios") {
                    setContent { Text("Hello", Modifier.testTag("text")) }
                    hasTestTag("text").assertTextEquals("Hello")
                }
            }
        }
    }

    private fun attachmentBytes(source: String): ByteArray = fs.read(resultsDir / source) { readByteArray() }

    @Test
    fun namedFailingTestWritesFailedResultWithArtifactsOnTheHost() {
        assertFailsWith<UltronOperationException> {
            runUltronUiTest("ios allure sample") {
                setContent { Text("Hello", Modifier.testTag("text")) }
                hasTestTag("text").withTimeout(300).assertTextEquals("Nope")
            }
        }

        val result = latestResultNamed("ios allure sample")
        assertNotNull(result, "result for 'ios allure sample' in $resultsDir")
        assertEquals(AllureStatus.FAILED, result.status)
        assertTrue(result.steps.isNotEmpty(), "operation steps recorded")
        assertEquals(AllureStatus.FAILED, result.steps.last().status)

        val byName = result.attachments.associateBy { it.name }
        assertTrue("test_failure_screenshot" in byName && "test_failure_hierarchy" in byName, byName.keys.toString())
        val png = attachmentBytes(byName.getValue("test_failure_screenshot").source)
        assertTrue(png.isNotEmpty() && png[0] == 0x89.toByte() && png[1] == 'P'.code.toByte(), "PNG signature")
        assertTrue(attachmentBytes(byName.getValue("test_failure_hierarchy").source).decodeToString().contains("Text"))
    }

    @Test
    fun unnamedTestFallsBackToUnknownTestWithImplicitLabel() {
        runUltronUiTest {
            setContent { Text("Hello", Modifier.testTag("text")) }
            hasTestTag("text").assertTextEquals("Hello")
        }

        val result = latestResultNamed(AllureLifecycle.IMPLICIT_TEST_NAME)
        assertNotNull(result, "implicit result in $resultsDir")
        assertEquals(AllureStatus.PASSED, result.status)
        assertEquals("implicit", result.labels.firstOrNull { it.name == AllureLifecycle.LABEL_BOUNDARY }?.value)
    }

    @Test
    fun videoSampleAttachesAnimatedPngOnTheHost() {
        runUltronUiTest("ios video sample") {
            setContent { VideoSampleScreen() }
            repeat(VideoSample.STEPS) { step ->
                hasTestTag("inc").click()
                hasTestTag("count").assertTextEquals(VideoSample.titleAfter(step + 1))
                VideoSample.pause()
            }
            hasTestTag("done_${VideoSample.STEPS}").assertIsDisplayed()
        }

        val result = latestResultNamed("ios video sample")
        assertNotNull(result, "result for the video sample in $resultsDir")
        assertEquals(AllureStatus.PASSED, result.status)
        val video = result.attachments.single { it.name == "video" }
        // mp4 from AVAssetWriter; APNG when VideoToolbox is unavailable (CI without a GPU session) and the encoder fell back.
        val bytes = attachmentBytes(video.source)
        val frames = when (video.type) {
            "video/mp4" -> Mp4Info.parse(bytes)?.sampleCount.also { assertNotNull(it, "attachment is an mp4") }
            "image/png" -> ApngInfo.frameCount(bytes).also { assertNotNull(it, "attachment is an APNG") }
            else -> error("unexpected video type ${video.type}")
        }
        assertTrue(frames!! >= VideoSample.EXPECTED_MIN_FRAMES, "one frame per distinct screen, got $frames")
    }

    @Test
    fun ultronTestNameNamesTheResultOnIos() {
        NamedIosScenario().run()

        val result = latestResultNamed("ios named scenario")
        assertNotNull(result, "result for 'ios named scenario' in $resultsDir")
        assertEquals(AllureStatus.PASSED, result.status)
        assertEquals("AllureResultsIosTest.NamedIosScenario.ios named scenario", result.fullName)
        assertEquals("NamedIosScenario", result.labels.single { it.name == "suite" }.value)
        assertEquals("ultronTest", result.labels.single { it.name == AllureLifecycle.LABEL_BOUNDARY }.value)
        assertTrue(result.labels.none { it.name == AllureLifecycle.LABEL_TEST_METHOD }, "no JVM method on iOS")
    }
}
