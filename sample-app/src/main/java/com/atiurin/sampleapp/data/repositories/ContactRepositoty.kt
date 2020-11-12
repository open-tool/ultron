package com.atiurin.sampleapp.data.repositories

import com.atiurin.sampleapp.data.entities.Contact

object ContactRepositoty {
//    fun getAll(adapter: ContactAdapter) {
//////        ContactsIdlingResource.getInstanceFromApp()?.setIdleState(false)
////        Handler().postDelayed({
////            adapter.updateData(contacts)
////            adapter.notifyDataSetChanged()
//////            ContactsIdlingResource.getInstanceFromApp()?.setIdleState(true)
////        }, 200)
////    }

    fun getContact(id: Int) : Contact{
        return contacts.find { it.id == id }!!
    }

    private val contacts = CONTACTS
}