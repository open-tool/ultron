package com.atiurin.sampleapp.data.repositories

import com.atiurin.sampleapp.data.entities.Contact

object ContactRepository {
    fun loadContacts() {
        loader = { CONTACTS }
    }

    fun clear() {
        loader = { mutableListOf() }
    }

    fun getContact(id: Int): Contact {
        return contacts.find { it.id == id }!!
    }

    fun getFirst(): Contact {
        return contacts.first()
    }

    fun getLast(): Contact {
        return contacts.last()
    }

    fun all() = contacts.toList()

    private var loader: () -> MutableList<Contact> = { CONTACTS }
    private val contacts: MutableList<Contact>
        get() = loader()
}

interface ContactsLoader {
    fun getContacts(): () -> MutableList<Contact>
}

class DefaultContactsLoader : ContactsLoader {
    override fun getContacts(): () -> MutableList<Contact> = { CONTACTS }
}

