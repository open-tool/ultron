package com.atiurin.ultron.exceptions

class UltronOperationException: RuntimeException{
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}