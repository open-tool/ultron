import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import repositories.ContactRepository
import com.atiurin.ultron.core.common.options.TextContainsOption
import com.atiurin.ultron.core.compose.list.UltronComposeListItem
import com.atiurin.ultron.core.compose.list.composeList
import com.atiurin.ultron.core.compose.nodeinteraction.click
import com.atiurin.ultron.core.compose.runUltronUiTest
import com.atiurin.ultron.extensions.assertIsDisplayed
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.extensions.withAssertion
import com.atiurin.ultron.page.Screen
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class AppTest {
    @Test
    fun test() = runUltronUiTest {
        setContent {
            App()
        }
        hasText("Click me!").withAssertion(){
            hasTestTag("greeting")
                .assertIsDisplayed()
                .assertTextContains("Compose: Hello,", option = TextContainsOption(substring = true))
        }.click()
    }

    @Test
    fun testList() = runUltronUiTest {
        setContent {
            App()
        }
        val contact = ContactRepository.getFirst()
        composeList(hasTestTag("contactsListTestTag"))
            .assertIsDisplayed().assertNotEmpty()
            .firstVisibleItem().assertIsDisplayed()
        ListScreen {
            list.assertContentDescriptionEquals(contactsListContentDesc)
            list.getFirstVisibleItem<ListScreen.ListItem>().apply {
                name.assertIsDisplayed().assertTextContains(contact.name)
                status.assertIsDisplayed().assertTextContains(contact.status)
            }
        }
    }
}

object ListScreen : Screen<ListScreen>() {
    const val contactsListTestTag = "contactsListTestTag"
    const val contactsListContentDesc = "contactsListContentDesc"
    val list = composeList(hasTestTag(contactsListTestTag))

    class ListItem : UltronComposeListItem(){
        val name by child { hasTestTag("contactNameTestTag") }
        val status by child { hasTestTag("contactStatusTestTag") }
    }
}