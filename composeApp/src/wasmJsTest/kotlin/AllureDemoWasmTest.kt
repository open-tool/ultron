import androidx.compose.ui.test.ExperimentalTestApi
import com.atiurin.ultron.core.compose.runUltronUiTest
import kotlin.test.BeforeTest
import kotlin.test.Test

/**
 * Allure samples of the browser target; the report screenshots on the "Allure for Compose Multiplatform" docs
 * page come from these tests. They use the plain form, the name passed to `runUltronUiTest("...")`; the
 * `UltronTest` form `test { go { runUltronUiTest { } } }` works in the browser too (see
 * `UltronTestBrowserNestingTest`). The failing operation
 * is caught inside the test body, so the Karma run stays green while the step is `failed` and carries its
 * screenshot and semantics tree.
 */
@OptIn(ExperimentalTestApi::class)
class AllureDemoWasmTest {
    @BeforeTest
    fun setup() = AllureTestSetup.ensure()

    @Test
    fun contactsAreListed() = runUltronUiTest("contacts are listed after the greeting") {
        setContent { App() }
        ContactsScreen { greetAndCheckContacts() }
    }

    @Test
    fun currentUserIsListed() = runUltronUiTest("current user appears in the contacts") {
        setContent { App() }
        ContactsScreen {
            showGreeting()
            runCatching { assertCurrentUserListed() }
        }
    }
}
