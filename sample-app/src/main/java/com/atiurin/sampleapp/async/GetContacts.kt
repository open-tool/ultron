package com.atiurin.sampleapp.async

import com.atiurin.sampleapp.MyApplication.CONTACTS_LOADING_TIMEOUT_MS
import com.atiurin.sampleapp.data.entities.Contact
import com.atiurin.sampleapp.data.repositories.CONTACTS
import kotlinx.coroutines.delay

class GetContacts(val delayMs: Long = CONTACTS_LOADING_TIMEOUT_MS) : UseCase<ArrayList<Contact>, UseCase.None>() {

    override suspend fun run(params: None): Either<Exception, ArrayList<Contact>> {
        return try {
            delay(delayMs)
            val contacts = CONTACTS
            Success(contacts)
        } catch (e: Exception) {
            Failure(e)
        }
    }
}