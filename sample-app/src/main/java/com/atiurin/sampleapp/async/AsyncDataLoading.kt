package com.atiurin.sampleapp.async

import com.atiurin.sampleapp.MyApplication.CONTACTS_LOADING_TIMEOUT_MS
import kotlinx.coroutines.delay

class AsyncDataLoading(val delayMs: Long = CONTACTS_LOADING_TIMEOUT_MS) : UseCase<String, UseCase.None>() {

    override suspend fun run(params: None): Either<Exception, String> {
        return try {
            delay(delayMs)
            Success( "Loaded")
        } catch (e: Exception) {
            Failure(e)
        }
    }
}