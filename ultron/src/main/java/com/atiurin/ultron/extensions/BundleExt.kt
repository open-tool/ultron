package com.atiurin.ultron.extensions

import android.os.Bundle

fun Bundle.putArguments(key: String, vararg values: CharSequence) {
    val arguments: String = listOfNotNull(
        getCharSequence(key),
        *values
    ).joinToString(separator = ",")
    putCharSequence(key, arguments)
}