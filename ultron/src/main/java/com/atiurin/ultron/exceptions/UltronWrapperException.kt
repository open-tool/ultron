package com.atiurin.ultron.exceptions

class UltronWrapperException(override val message: String, override val cause: Throwable)
    : RuntimeException("$message, original: ${cause.message}, ${cause.cause}")