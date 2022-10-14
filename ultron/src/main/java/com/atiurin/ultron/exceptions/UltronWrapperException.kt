package com.atiurin.ultron.exceptions

class UltronWrapperException: RuntimeException{
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable)
            : super("""
                |$message
                |Original error: ${cause::class.simpleName}[${if (cause is UltronOperationException || cause is UltronWrapperException)  "" else "${cause.message}"}]
                """.trimMargin(), cause)
}