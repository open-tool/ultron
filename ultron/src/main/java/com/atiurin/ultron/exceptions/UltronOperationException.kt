package com.atiurin.ultron.exceptions

class UltronOperationException: RuntimeException{
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable)
            : super("""
                |$message
                |Original error: ${cause::class.simpleName}[${if (cause is UltronOperationException || cause is UltronWrapperException)  "" else "${cause.message}"}]
                """.trimMargin(), cause)
//    |Original error: ${cause::class.simpleName}[${cause.message}${if (cause.cause != null)  ", cause ${cause.cause}" else ""}]
}