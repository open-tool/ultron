package com.atiurin.sampleapp.data.entities

data class Message(val authorId: Int,
                   val receiverId: Int,
                   val text: String)