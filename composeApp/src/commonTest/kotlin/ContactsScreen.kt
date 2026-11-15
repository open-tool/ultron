import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import com.atiurin.ultron.core.compose.list.composeList
import com.atiurin.ultron.core.common.options.TextContainsOption
import com.atiurin.ultron.core.compose.nodeinteraction.click
import com.atiurin.ultron.extensions.withName
import com.atiurin.ultron.page.Screen
import repositories.CURRENT_USER
import repositories.Contact
import repositories.ContactRepository

/**
 * Page object of the demo app for the Allure samples; the report screenshots on the "Allure for Compose
 * Multiplatform" docs page come from the tests that use it. Every element carries a `withName`, so the step
 * titles read the same on every platform: outside Android, Compose gives no description for a
 * `SemanticsMatcher`, and an unnamed element shows up in the report as a placeholder.
 */
object ContactsScreen : Screen<ContactsScreen>() {
    private val greetButton = hasText("Click me!").withName("Click me! button")
    private val greeting = hasTestTag("greeting").withName("Greeting")
    private val loadedLabel = hasText("Contacts loaded").withName("Contacts loaded label")
    private val contacts = composeList(hasTestTag("contactsListTestTag")).withDescription("Contacts list")

    private fun nameOf(contact: Contact) = hasText(contact.name).withName("Name of ${contact.name}")
    private fun statusOf(contact: Contact) = hasText(contact.status).withName("Status of ${contact.name}")

    fun showGreeting() {
        greetButton.click()
        greeting.assertIsDisplayed().assertTextContains("Hello", TextContainsOption(substring = true))
    }

    fun assertContactsLoaded() {
        loadedLabel.assertIsDisplayed()
        contacts.assertIsDisplayed()
    }

    fun assertContactVisible(contact: Contact) {
        nameOf(contact).assertIsDisplayed()
        statusOf(contact).assertIsDisplayed()
    }

    /** Fails on purpose: the current user is not among the contacts. Shows the failure artifacts in the report. */
    fun assertCurrentUserListed() {
        hasText(CURRENT_USER.name).withName("Name of ${CURRENT_USER.name}").withTimeout(1_000).assertIsDisplayed()
    }

    /** The passing scenario every platform's Allure sample runs. */
    fun greetAndCheckContacts() {
        showGreeting()
        assertContactsLoaded()
        assertContactVisible(ContactRepository.getFirst())
    }
}
