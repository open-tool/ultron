package com.atiurin.sampleapp.data.loaders

import com.atiurin.sampleapp.data.entities.Message
import com.atiurin.sampleapp.data.repositories.MESSAGES

open class MessageLoader{
    open fun load() : ArrayList<Message>{
        return MESSAGES
    }
}