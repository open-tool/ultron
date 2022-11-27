package com.atiurin.ultron.exceptions

class UltronWrapperException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable)
            : super(
        "$message${
            if (cause is UltronWrapperException || cause is UltronOperationException) ""
            else "\nOriginal error ${cause::class.qualifiedName}: ${cause.message}"
        }"
    )
}