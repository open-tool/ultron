package com.atiurin.sampleapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.atiurin.sampleapp.async.GetContacts
import com.atiurin.sampleapp.async.UseCase
import com.atiurin.sampleapp.compose.ContactsList
import com.atiurin.sampleapp.compose.LoadingAnimation
import com.atiurin.sampleapp.compose.getContactItemTestTagById
import com.atiurin.sampleapp.data.entities.Contact
import com.atiurin.sampleapp.data.repositories.ContactRepositoty
import com.atiurin.sampleapp.data.viewmodel.ContactsViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class ComposeListActivity : ComponentActivity() {
    val model: ContactsViewModel by viewModels()

    @ExperimentalMaterialApi
    @ExperimentalUnitApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoadingAnimation()
            }
        }
        val contactsObserver = Observer<List<Contact>> {
            setContent {
                Column {
                    ContactsList(
                        contacts = ContactRepositoty.all(), this@ComposeListActivity
                    ) { contact, _ -> getContactItemTestTagById(contact) }
                }
            }
        }
        model.contacts.observe(this, contactsObserver)
        GlobalScope.async {
            GetContacts(0)(
                UseCase.None,
                onSuccess = { model.contacts.value = it },
                onFailure = { Toast.makeText(this@ComposeListActivity, "Failed to load contacts", Toast.LENGTH_LONG).show() }
            )
        }
    }
}


