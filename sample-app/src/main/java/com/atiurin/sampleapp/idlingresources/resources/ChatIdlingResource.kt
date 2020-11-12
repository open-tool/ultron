package com.atiurin.sampleapp.idlingresources.resources

import com.atiurin.sampleapp.idlingresources.AbstractIdlingResource
import com.atiurin.sampleapp.idlingresources.Holder

class ChatIdlingResource : AbstractIdlingResource(){
    companion object : Holder<ChatIdlingResource>(::ChatIdlingResource)
}