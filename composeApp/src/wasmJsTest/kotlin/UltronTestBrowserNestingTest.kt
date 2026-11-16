import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.allure.model.AllureJson
import com.atiurin.ultron.allure.model.AllureStatus
import com.atiurin.ultron.allure.writer.UltronAllureResults
import com.atiurin.ultron.core.compose.runUltronUiTest
import com.atiurin.ultron.core.test.UltronTest
import com.atiurin.ultron.extensions.assertTextEquals
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Browser: `test { go { runUltronUiTest { } } }` returns a promise that settles when the whole test has. A failure
 * inside the UI code rejects it, after `after { }` and `afterTest` have run, and the Allure result is the one of the
 * `test { }`. `a_failure…` returns the promise with the rejection handled, so that this suite stays green; `b_` checks what
 * happened (kotlin.test runs the tests of a class in declaration order on the browser targets).
 *
 * The same file lives in `jsTest`: the two targets share no test source set.
 */
@OptIn(ExperimentalTestApi::class)
class UltronTestBrowserNestingTest {
    private class Scenario : UltronTest() {
        override val afterTest = {
            events += "afterTest"
        }

        fun passingUi() = test(name = PASSING) {
            before { }.go {
                runUltronUiTest {
                    setContent { Text("Hello", Modifier.testTag("text")) }
                    hasTestTag("text").assertTextEquals("Hello")
                }
            }.after { }
        }

        fun failingUi() = test(name = FAILING) {
            before {
                events += "before"
            }.go {
                runUltronUiTest {
                    setContent { Text("Hello", Modifier.testTag("text")) }
                    hasTestTag("text").assertTextEquals("Hello")
                    events += "ui"
                    fail("failure inside runUltronUiTest")
                }
                events += "go returned"
            }.after {
                events += "after"
            }
        }
    }

    companion object {
        const val PASSING = "browser nesting: passing ui"
        const val FAILING = "browser nesting: failing ui"
        val events = mutableListOf<String>()
        var rejected = false
    }

    @BeforeTest
    fun setup() = AllureTestSetup.ensure()

    @Test
    fun a_passingUi() = Scenario().passingUi()

    @Test
    fun a_failureInsideUiRejectsTheTest() = Scenario().also { events.clear() }.failingUi().then({ }) {
        rejected = true
    }

    private fun resultsNamed(name: String) = UltronAllureResults.snapshot()
        .filterKeys { it.endsWith("-result.json") }
        .values
        .map { AllureJson.decodeResult(it.decodeToString()) }
        .filter { it.name == name }

    @Test
    fun b_checkFailureAndOrder() {
        assertTrue(rejected, "a failure inside runUltronUiTest fails the test")
        assertEquals(listOf("before", "go returned", "ui", "after", "afterTest"), events)

        val passed = resultsNamed(PASSING)
        assertEquals(1, passed.size, "one result for the test and the runUltronUiTest inside it")
        assertEquals(AllureStatus.PASSED, passed.single().status)
        val passedSteps = passed.single().steps.map { it.name }
        assertEquals("before", passedSteps.first(), passedSteps.toString())
        assertEquals("after", passedSteps.last(), passedSteps.toString())
        assertTrue(passedSteps.any { "AssertTextEquals" in it }, "operation step between the blocks: $passedSteps")

        val results = resultsNamed(FAILING)
        assertEquals(1, results.size, "one result for the test and the runUltronUiTest inside it")
        val result = results.single()
        assertEquals(AllureStatus.FAILED, result.status)
        assertTrue("failure inside runUltronUiTest" in result.statusDetails?.message.orEmpty(), "${result.statusDetails}")
        val steps = result.steps.map { it.name }
        assertTrue(steps.any { "AssertTextEquals" in it }, "the operation inside the UI code is a step: $steps")
        assertEquals("after", steps.last(), "the after step is recorded once the UI code has finished: $steps")
        assertTrue(result.attachments.isNotEmpty(), "failure artifacts taken while the scene was alive")
    }
}
