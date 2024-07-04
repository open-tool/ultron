import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.core.compose.list.UltronComposeListItem
import com.atiurin.ultron.core.compose.list.composeList
import com.atiurin.ultron.core.compose.runUltronUiTest
import com.atiurin.ultron.page.Screen
import repositories.ContactRepository
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ListTest {

    @Test
    fun testList() = runUltronUiTest {
        setContent {
            App()
        }
        composeList(hasTestTag("contactsListTestTag"))
            .assertIsDisplayed().assertNotEmpty()
            .firstVisibleItem().assertIsDisplayed()
    }

    @Test
    fun testListItemChildElements() = runUltronUiTest {
        setContent {
            App()
        }
        val contact = ContactRepository.getFirst()
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
    val list = composeList(
        listMatcher = hasTestTag(contactsListTestTag),
        initBlock = {
            registerItem { ListItem() }
        }
    )

    class ListItem : UltronComposeListItem() {
        val name by child { hasTestTag("contactNameTestTag") }
        val status by child { hasTestTag("contactStatusTestTag") }
    }
}