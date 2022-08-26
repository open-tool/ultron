package com.atiurin.ultron.core.common

import android.os.SystemClock
import com.atiurin.ultron.extensions.isAssignedFrom

interface OperationExecutor<Op : Operation, OpRes : OperationResult<Op>> {
    val operation: Op
    val pollingTimeout: Long
    fun getAllowedExceptions(operation: Operation): List<Class<out Throwable>>
    fun generateResult(
        success: Boolean,
        exceptions: List<Throwable>,
        description: String,
        operationIterationResult: OperationIterationResult?
    ): OpRes

    fun getWrapperException(originalException: Throwable): Throwable {
        return originalException
    }

    fun execute(): OpRes {
        var success = true
        var description = ""
        val exceptions: MutableList<Throwable> = mutableListOf()
        val endTime = SystemClock.elapsedRealtime() + operation.timeoutMs
        var lastIteration: OperationIterationResult? = null
        try {
            do {
                val result = operation.execute()
                success = result.success
                if (!success) {
                    val error =
                        result.exception ?: UnknownError("Create an issue to ULTRON project")
                    if (error::class.java.isAssignedFrom(getAllowedExceptions(operation))) {
                        if (exceptions.find {
                                it.javaClass.simpleName == error.javaClass.simpleName &&
                                        it.message == error.message
                        } == null) {
                            exceptions.add(error)
                        }
                    } else {
                        description =
                            "Not allowed exception occurs - ${error.javaClass.simpleName}, message - ${error.message}, cause - ${error.cause}"
                        exceptions.add(error)
                        throw error
                    }
                }
                lastIteration = result
                if (!success) {
                    if (pollingTimeout > 0) SystemClock.sleep(pollingTimeout)
                }
            } while (SystemClock.elapsedRealtime() < endTime && !success)
        } catch (th: Throwable) {
            success = false // just make sure we will have correct action status
        }

        if (!success && exceptions.isNotEmpty()) {
            description +=
                """
                |Operation '${operation.name}' with type ${operation.type} during ${operation.timeoutMs} ms was failed. 
                |Operation description: ${operation.description}
                |
                """.trimMargin()
            if (exceptions.size > 1) {
                description += """
                |Errors were caught: 
                |${exceptions.map { "- '${it.javaClass.simpleName}', message: '${it.message}' cause: '${it.cause}'\n" }}
                |Last error message: '${exceptions.last().message}', cause: '${exceptions.last().cause}'
                """.trimMargin()
            } else {
                description += """
                |Error was caught '${exceptions.last().message}', cause: '${exceptions.last().cause}'
                """.trimMargin()
            }


        }
        return generateResult(
            success = success,
            exceptions = exceptions.map { getWrapperException(it) },
            description = description,
            operationIterationResult = lastIteration
        )
    }
}
