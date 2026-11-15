import androidx.compose.ui.test.ExperimentalTestApi
import com.atiurin.ultron.annotations.ExperimentalUltronApi
import com.atiurin.ultron.core.compose.runUltronUiTest
import com.atiurin.ultron.core.test.UltronTest
import kotlin.test.Test
import kotlin.test.assertFails

/**
 * Allure samples of the iOS simulator; the report screenshots on the "Allure for Compose Multiplatform" docs
 * page come from these scenarios. Same shape as the desktop sample: `test(name)` names the result (there is no
 * stack discovery on iOS), the class is the suite, and the failing scenario runs under [assertFails].
 */
@OptIn(ExperimentalTestApi::class, ExperimentalUltronApi::class)
class AllureDemoIosTest {
    class ContactsIosDemo : UltronTest() {
        override val beforeFirstTest = { AllureTestSetup.ensure() }

        fun contactsAreListed() = test(name = "contacts are listed after the greeting") {
            go {
                runUltronUiTest {
                    setContent { App() }
                    ContactsScreen { greetAndCheckContacts() }
                }
            }
        }

        fun currentUserIsListed() = test(name = "current user appears in the contacts") {
            go {
                runUltronUiTest {
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
    fun contactsAreListed() = ContactsIosDemo().contactsAreListed()

    @Test
    fun failingScenarioIsReportedAsFailed() {
        assertFails { ContactsIosDemo().currentUserIsListed() }
    }
}
