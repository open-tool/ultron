package com.atiurin.ultron.allure.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object AllureJson {
    val format: Json = Json {
        encodeDefaults = true
        explicitNulls = false
    }

    inline fun <reified T> encode(value: T): String = format.encodeToString(value)

    /** Reads a `*-result.json` back; handy for tests that verify a results directory. */
    fun decodeResult(text: String): AllureTestResult = format.decodeFromString(AllureTestResult.serializer(), text)
}
