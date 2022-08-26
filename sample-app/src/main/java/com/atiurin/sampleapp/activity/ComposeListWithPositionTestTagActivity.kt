package com.atiurin.sampleapp.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.lifecycle.Observer
import com.atiurin.sampleapp.async.GetContacts
import com.atiurin.sampleapp.async.UseCase
import com.atiurin.sampleapp.compose.ContactsList
import com.atiurin.sampleapp.compose.LoadingAnimation
import com.atiurin.sampleapp.compose.getContactItemTestTagByPosition
import com.atiurin.sampleapp.data.entities.Contact
import com.atiurin.sampleapp.data.repositories.ContactRepositoty
import com.atiurin.sampleapp.data.viewmodel.ContactsViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class ComposeListWithPositionTestTagActivity: ComponentActivity() {
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
                        contacts = ContactRepositoty.all(), this@ComposeListWithPositionTestTagActivity, false
                    ) { _, position -> getContactItemTestTagByPosition(position) }
                }
            }
        }
        model.contacts.observe(this, contactsObserver)
        GlobalScope.async {
            GetContacts()(
                UseCase.None,
                onSuccess = { model.contacts.value = it },
                onFailure = { Toast.makeText(this@ComposeListWithPositionTestTagActivity, "Failed to load contacts", Toast.LENGTH_LONG).show() }
            )
        }
    }
}


