package com.atiurin.sampleapp.idlingresources.resources

import com.atiurin.sampleapp.idlingresources.AbstractIdlingResource
import com.atiurin.sampleapp.idlingresources.Holder

class ContactsIdlingResource : AbstractIdlingResource(){
    companion object : Holder<ContactsIdlingResource>(::ContactsIdlingResource)
}
