package com.atiurin.sampleapp.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.atiurin.sampleapp.data.repositories.ContactRepositoty


class ComposeSecondActivity : ComponentActivity() {
    @ExperimentalUnitApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contactId = intent.getIntExtra(INTENT_CONTACT_ID, -1)
        val contact = ContactRepositoty.getContact(contactId)
        setContent {
            Column() {
                Text(contact.name, Modifier.semantics { testTag = "name" }, fontSize = TextUnit(16f, TextUnitType.Sp) )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = contact.status, Modifier.semantics { testTag = "status" })
            }
        }
    }

    companion object {
        const val INTENT_CONTACT_ID = "contactId"
    }
}