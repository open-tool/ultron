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
import com.atiurin.ultron.core.compose.UltronTestRunHooks
import com.atiurin.ultron.core.compose.runUltronUiTest
import com.atiurin.ultron.core.test.UltronTest
import com.atiurin.ultron.core.test.UltronTestListeners
import com.atiurin.ultron.exceptions.UltronOperationException
import com.atiurin.ultron.extensions.assertTextEquals
import com.atiurin.ultron.extensions.withTimeout
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * The browser form, `runUltronUiTest { test { } }`, when the very first `UltronAllureConfig.apply` of the run
 * happens in `beforeFirstTest`: no hook is registered when `runUltronUiTest` starts, so nothing opens the test
 * there. The hook registered by `apply` must open it then and there, otherwise the first test of the run has no
 * failure artifacts and no video (every later test has them, as the hook is in place by then).
 */
@OptIn(ExperimentalTestApi::class, ExperimentalUltronApi::class)
class AllureFirstApplyInsideRunDesktopTest {
    private val resultsDir = File(AllureResultsLocation.resolve(null))
    private val startedAt = System.currentTimeMillis()

    class FirstApplyScenario : UltronTest() {
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
    fun firstTestOfTheRunGetsItsFailureArtifactsWhenTheConfigIsFirstAppliedInsideRunUltronUiTest() {
        // As in a fresh process before the first `apply`: no hook, no UltronTest listener.
        UltronTestRunHooks.clear()
        UltronTestListeners.clear()
        assertFailsWith<UltronOperationException> { FirstApplyScenario().failInside() }

        val results = freshResultsNamed("firstTestOfTheRunGetsItsFailureArtifactsWhenTheConfigIsFirstAppliedInsideRunUltronUiTest")
        assertEquals(1, results.size, "one result for the failed test: ${results.map { it.fullName }}")
        val result = results.single()
        assertEquals(AllureStatus.FAILED, result.status)
        assertEquals("FirstApplyScenario", result.labels.single { it.name == "suite" }.value)
        val attachments = result.attachments.associateBy { it.name }
        assertTrue("test_failure_screenshot" in attachments && "test_failure_hierarchy" in attachments, attachments.keys.toString())
        assertTrue("video" in attachments, "video recorded from the apply on: ${attachments.keys}")
    }
}
