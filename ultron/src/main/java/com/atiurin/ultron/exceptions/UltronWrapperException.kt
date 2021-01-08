package com.atiurin.ultron.exceptions

class UltronWrapperException: RuntimeException{
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable)
            : super("""
                |$message
                |Original error: ${cause::class.simpleName}[${cause.message}${if (cause.cause != null)  ", cause ${cause.cause}" else ""}]
                """.trimMargin(), cause)
}