import androidx.compose.ui.test.ExperimentalTestApi
import com.atiurin.ultron.annotations.ExperimentalUltronApi
import com.atiurin.ultron.core.compose.runDesktopUltronUiTest
import com.atiurin.ultron.core.test.UltronTest
import kotlin.test.Test
import kotlin.test.assertFails

/**
 * Allure samples of the desktop target; the report screenshots on the "Allure for Compose Multiplatform" docs
 * page come from these scenarios. They live in an [UltronTest] so that `test(name)` names the result and the
 * class is the suite. The failing scenario runs under [assertFails]: the Gradle test stays green while the
 * Allure result is `failed` and carries the screenshot and the semantics tree of the moment of failure.
 */
@OptIn(ExperimentalTestApi::class, ExperimentalUltronApi::class)
class AllureDemoDesktopTest {
    class ContactsDesktopDemo : UltronTest() {
        override val beforeFirstTest = { AllureTestSetup.ensure() }

        fun contactsAreListed() = test(name = "contacts are listed after the greeting") {
            go {
                runDesktopUltronUiTest(width = 480, height = 800) {
                    setContent { App() }
                    ContactsScreen { greetAndCheckContacts() }
                }
            }
        }

        fun currentUserIsListed() = test(name = "current user appears in the contacts") {
            go {
                runDesktopUltronUiTest(width = 480, height = 800) {
                    setContent { App() }
                    ContactsScreen {
                        showGreeting()
                        assertCurrentUserListed()
                    }
                }
            }
        }
    }

    @Test
    fun contactsAreListed() = ContactsDesktopDemo().contactsAreListed()

    @Test
    fun failingScenarioIsReportedAsFailed() {
        assertFails { ContactsDesktopDemo().currentUserIsListed() }
    }
}
