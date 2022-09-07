package com.atiurin.sampleapp.compose

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.atiurin.sampleapp.activity.ComposeSecondActivity
import com.atiurin.sampleapp.data.entities.Contact

const val contactsListHeaderTag = "headerTestTag"
const val contactNameTestTag = "nameTestTag"
const val contactStatusTestTag = "statusTestTag"
const val contactsListContentDesc = "contacts list"
const val contactsListTestTag = "contactsListTestTag"
@ExperimentalMaterialApi
@ExperimentalUnitApi
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactsList(contacts: List<Contact>, context: Context, addStickyHeader: Boolean = true, testTagProvider: (Contact, Int) -> String) {
    var selectedItem = remember {
        mutableStateOf("")
    }
    Text(text = "Selected item = ${selectedItem.value}")
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.semantics {
            contentDescription = contactsListContentDesc
            testTag = contactsListTestTag
        }
    ) {
        if (addStickyHeader){
            stickyHeader (key = "header"){
                Text(text = "Lazy column header", modifier = Modifier.semantics { testTag = contactsListHeaderTag })
            }
        }
        itemsIndexed(contacts, key = { _, c -> c.name }) { index, contact ->
            Column(
                modifier = Modifier
                    .then(Modifier.clickable {
                        selectedItem.value = contact.name
                        val intent = Intent(context, ComposeSecondActivity::class.java)
                        intent.putExtra(ComposeSecondActivity.INTENT_CONTACT_ID, contact.id)
                        ContextCompat.startActivity(context, intent, null)
                    })
                    .then(Modifier.semantics {
                        testTag = testTagProvider.invoke(contact, index)
                    })
            ) {
                Row {
                    Image(
                        painter = painterResource(contact.avatar),
                        contentDescription = "avatar",
                        contentScale = ContentScale.Crop,            // crop the image if it's not a square
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)                       // clip to the circle shape
                            .border(2.dp, Color.Transparent, CircleShape)   // add a border (optional)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(contact.name, Modifier.semantics { testTag = contactNameTestTag }, fontSize = TextUnit(20f, TextUnitType.Sp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = contact.status, Modifier.semantics { testTag = contactStatusTestTag }, fontSize = TextUnit(16f, TextUnitType.Sp))
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color.Black)
            }
        }
    }
}

fun getContactItemTestTagById(contact: Contact) = "contactId=${contact.id}"
fun getContactItemTestTagByPosition(position: Int) = "position=$position"