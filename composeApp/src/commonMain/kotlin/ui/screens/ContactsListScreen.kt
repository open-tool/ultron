package ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import repositories.ContactRepository
import kotlinx.coroutines.async
import repositories.Contact

@Composable
fun ContactsListScreen() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        val scope = rememberCoroutineScope()
        var contactItems by remember { mutableStateOf(emptyList<Contact>()) }
        var text by remember { mutableStateOf("Loading ...") }

        scope.async {
            contactItems = loadContacts()
            text = "Contacts loaded"
        }

        Text(text)
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.semantics {
                contentDescription = "contactsListContentDesc"
                testTag = "contactsListTestTag"
            }
        ) {
            items(contactItems) { contact -> ContactItem(contact) }
        }
    }
}

@Composable
fun ContactItem(contact: Contact) {
    Box(modifier = Modifier.testTag("contactItem=${contact.id}")) {
        Column {
            Row {
                Column {
                    Text(contact.name, Modifier.semantics { testTag = "contactNameTestTag" }, fontSize = TextUnit(20f, TextUnitType.Sp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = contact.status, Modifier.semantics { testTag = "contactStatusTestTag" }, fontSize = TextUnit(16f, TextUnitType.Sp))
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.Black)
    }

}

suspend fun loadContacts(): List<Contact> {
//    delay(1000)
    return ContactRepository.all()
}