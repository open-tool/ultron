package com.atiurin.ultron.exceptions

import android.annotation.SuppressLint

class UltronOperationException : RuntimeException {
    constructor(message: String) : super(message)
    @SuppressLint("NewApi")
    constructor(message: String, cause: Throwable) : super(message, cause)
}