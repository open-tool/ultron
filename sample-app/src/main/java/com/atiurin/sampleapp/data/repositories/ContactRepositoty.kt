package com.atiurin.sampleapp.data.repositories

import com.atiurin.sampleapp.data.entities.Contact
object ContactRepositoty {

    fun getContact(id: Int) : Contact{
        return contacts.find { it.id == id }!!
    }

    fun getFirst(): Contact {
        return contacts.first()
    }
    fun getLast() : Contact{
        return contacts.last()
    }
    fun all() = contacts.toList()

    private val contacts = CONTACTS
}