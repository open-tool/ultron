package com.atiurin.ultron.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

actual fun sleep(timeMs: Long) {
    runBlocking {
        delay(timeMs)
    }
}