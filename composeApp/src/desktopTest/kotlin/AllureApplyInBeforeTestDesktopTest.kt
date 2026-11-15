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
 * The browser form, `runUltronUiTest { test(name) { } }`, when the very first `UltronAllureConfig.apply` of the
 * run happens in the common `beforeTest`: that runs after the listeners' `beforeTest` already fired with no
 * listener registered. The hook registered by `apply` opens the test, and the listener must adopt it then and
 * there, otherwise the test keeps the discovered method name, has no suite and ignores `test(name)`.
 */
@OptIn(ExperimentalTestApi::class, ExperimentalUltronApi::class)
class AllureApplyInBeforeTestDesktopTest {
    private val resultsDir = File(AllureResultsLocation.resolve(null))
    private val startedAt = System.currentTimeMillis()

    class ApplyInBeforeTestScenario : UltronTest() {
        override val beforeTest = { configureAllure() }

        fun failInside() = runUltronUiTest {
            test(name = "applied in beforeTest") {
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
    fun theTestInProgressAdoptsTheHookTestOpenedByAFirstApplyFromTheCommonBeforeTest() {
        // As in a fresh process before the first `apply`: no hook, no UltronTest listener.
        UltronTestRunHooks.clear()
        UltronTestListeners.clear()
        assertFailsWith<UltronOperationException> { ApplyInBeforeTestScenario().failInside() }

        val results = freshResultsNamed("applied in beforeTest")
        assertEquals(1, results.size, "one result named by test(name): ${results.map { it.fullName }}")
        val result = results.single()
        assertEquals(AllureStatus.FAILED, result.status)
        assertEquals("ApplyInBeforeTestScenario", result.labels.single { it.name == "suite" }.value)
        assertEquals("ultronTest", result.labels.single { it.name == "ultron.testBoundary" }.value)
        assertTrue(result.fullName.endsWith("ApplyInBeforeTestScenario.applied in beforeTest"), result.fullName)
        val attachments = result.attachments.associateBy { it.name }
        assertTrue("test_failure_screenshot" in attachments && "test_failure_hierarchy" in attachments, attachments.keys.toString())
    }
}
