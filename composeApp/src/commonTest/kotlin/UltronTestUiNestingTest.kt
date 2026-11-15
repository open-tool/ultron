import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.core.compose.runUltronUiTest
import com.atiurin.ultron.core.test.UltronTest
import com.atiurin.ultron.extensions.assertTextEquals
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * One nesting for every target: `test { go { runUltronUiTest { } } }`, returned from the test function. In the
 * browser `runUltronUiTest` is a promise whose body runs after `go { }` has returned; `test { }` waits for it there,
 * so `after { }` and the common `afterTest` see the UI code done, as on JVM and Native.
 */
@OptIn(ExperimentalTestApi::class)
class UltronTestUiNestingTest : UltronTest() {
    private val events = mutableListOf<String>()

    @BeforeTest
    fun setup() = AllureTestSetup.ensure()

    override val beforeTest = {
        events += "beforeTest"
    }

    // An assertion here fails the test: nothing failed before it.
    override val afterTest = {
        assertEquals(listOf("beforeTest", "before", "ui", "after"), events, "the UI code runs between before and after")
    }

    @Test
    fun uiRunsBetweenBeforeAndAfter() = test(name = "ui nested in go") {
        before {
            events += "before"
        }.go {
            runUltronUiTest {
                setContent { Text("Hello", Modifier.testTag("text")) }
                hasTestTag("text").assertTextEquals("Hello")
                events += "ui"
            }
        }.after {
            events += "after"
        }
    }
}
