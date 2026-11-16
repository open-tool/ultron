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
import com.atiurin.ultron.exceptions.UltronOperationException
import com.atiurin.ultron.extensions.assertTextEquals
import com.atiurin.ultron.extensions.withTimeout
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * The reverse nesting, `runUltronUiTest { test { } }` (still supported, no longer needed in the browser), with
 * `beforeFirstTest` applying the config:
 * `runUltronUiTest` opens the test under the configuration of the previous class, `beforeFirstTest` re-applies
 * the config inside that test. The re-apply must keep the open test, so that the failure of the first test of
 * the class still gets its artifacts and the `beforeFirstTest` log lines (see allure-multiplatform.md).
 */
@OptIn(ExperimentalTestApi::class, ExperimentalUltronApi::class)
class AllureReapplyDesktopTest {
    private val resultsDir = File(AllureResultsLocation.resolve(null))
    private val startedAt = System.currentTimeMillis()

    class ReapplyingScenario : UltronTest() {
        /** Same configuration as the rest of the suite, applied once more, from inside the open test. */
        override val beforeFirstTest = { configureAllure() }

        fun failInside() = runUltronUiTest {
            test {
                go {
                    setContent { Text("Hello", Modifier.testTag("text")) }
                    hasTestTag("text").withTimeout(300).assertTextEquals("Nope")
                }
            }
        }
    }

    private fun freshResultsNamed(name: String): List<AllureTestResult> =
        (resultsDir.listFiles() ?: emptyArray())
            .filter { it.name.endsWith("-result.json") }
            .map { AllureJson.decodeResult(it.readText()) }
            .filter { it.name == name && it.stop >= startedAt }

    @Test
    fun firstTestOfTheClassKeepsItsFailureArtifactsWhenBeforeFirstTestReappliesTheConfig() {
        AllureTestSetup.ensure() // the hook of the previous configuration is what opens the test
        assertFailsWith<UltronOperationException> { ReapplyingScenario().failInside() }

        val results = freshResultsNamed("firstTestOfTheClassKeepsItsFailureArtifactsWhenBeforeFirstTestReappliesTheConfig")
        assertEquals(1, results.size, "one result for the failed test, none dropped: ${results.map { it.fullName }}")
        val result = results.single()
        assertEquals(AllureStatus.FAILED, result.status)
        assertEquals("ReapplyingScenario", result.labels.single { it.name == "suite" }.value)
        val attachments = result.attachments.associateBy { it.name }
        assertTrue("test_failure_screenshot" in attachments && "test_failure_hierarchy" in attachments, attachments.keys.toString())
        assertTrue("video" in attachments, "video recorded from the re-apply on: ${attachments.keys}")
        val log = File(resultsDir, attachments.getValue("ultron_log.txt").source).readText()
        assertTrue("UltronAllureConfig applied" in log, "beforeFirstTest ran inside the first test, its lines are in its log:\n$log")
    }
}
