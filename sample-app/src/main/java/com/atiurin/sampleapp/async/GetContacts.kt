package com.atiurin.sampleapp.async

import com.atiurin.sampleapp.data.entities.Contact
import com.atiurin.sampleapp.data.repositories.CONTACTS
import kotlinx.coroutines.delay

class GetContacts : UseCase<ArrayList<Contact>, UseCase.None>() {

    override suspend fun run(params: None): Either<Exception, ArrayList<Contact>> {
        return try {
            delay(500)
            val contacts = CONTACTS
            Success(contacts)
        } catch (e: Exception) {
            Failure(e)
        }
    }
}