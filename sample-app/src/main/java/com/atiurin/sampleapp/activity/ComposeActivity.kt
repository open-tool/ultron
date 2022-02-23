package com.atiurin.sampleapp.activity

import android.os.Bundle
import android.preference.PreferenceActivity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.atiurin.sampleapp.data.entities.Contact
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.unit.dp
import com.atiurin.sampleapp.data.repositories.ContactRepositoty

class ComposeActivity : ComponentActivity() {
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactsList(contacts = ContactRepositoty.all())
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactsList(contacts: List<Contact>) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        stickyHeader {
            PreferenceActivity.Header()
        }
        items(contacts) { contact ->
            Text(contact.name)
            Text(text = contact.status)
        }
    }
}