package com.atiurin.sampleapp.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atiurin.sampleapp.data.entities.Contact

class ContactsViewModel : ViewModel(){
    val contacts: MutableLiveData<List<Contact>> by lazy {
        MutableLiveData()
    }
}