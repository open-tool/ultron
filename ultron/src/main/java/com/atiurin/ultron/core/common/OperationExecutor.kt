package com.atiurin.ultron.core.common

import android.os.SystemClock
import com.atiurin.ultron.exceptions.UltronAssertionBlockException
import com.atiurin.ultron.extensions.isAssignedFrom
import kotlin.system.measureTimeMillis

interface OperationExecutor<Op : Operation, OpRes : OperationResult<Op>> {
    val operation: Op
    val pollingTimeout: Long
    val descriptor: ResultDescriptor

    fun getAllowedExceptions(operation: Operation): List<Class<out Throwable>>
    fun generateResult(
        success: Boolean,
        exceptions: List<Throwable>,
        description: String,
        lastOperationIterationResult: OperationIterationResult?,
        executionTimeMs: Long
    ): OpRes

    fun getWrapperException(originalException: Throwable): Throwable

    fun execute(): OpRes {
        return descriptor.nestedOperation { execWithAssertion(operation.timeoutMs, null, true) }
    }

    private tailrec fun execWithAssertion(operationDuration: Long, previousResult: OpRes?, isFirstIteration: Boolean = false): OpRes {
        var result = execOperation(operationDuration, previousResult)
        if (result.success) {
            result = execAssertion(result)
            if (!result.success && (operationDuration > result.executionTimeMs || isFirstIteration)) {
                return execWithAssertion(operationDuration, result)
            }
        }
        return result
    }

    fun execOperation(operationDuration: Long, previousResult: OpRes?): OpRes {
        var isSuccess: Boolean
        val description = StringBuilder()
        descriptor.appendLine(description, "------ Operation ${operation.name} ------")
        descriptor.increaseLevel()
        val exceptions = mutableListOf<Throwable>()
        val endTime = SystemClock.elapsedRealtime() + operationDuration
        var lastIteration: OperationIterationResult? = null
        val execTime = measureTimeMillis {
            try {
                do {
                    val result = operation.execute()
                    isSuccess = result.success
                    if (!isSuccess) {
                        val error = result.exception ?: UnknownError("Create an issue to ULTRON project")
                        if (error::class.java.isAssignedFrom(getAllowedExceptions(operation))) {
                            if (!exceptions.any { it.javaClass.simpleName == error.javaClass.simpleName && it.message == error.message }) {
                                exceptions.add(error)
                            }
                        } else {
                            descriptor.appendLine(description,"Not allowed exception occurs - ${error.javaClass.simpleName}, cause - ${error.cause}")
                            exceptions.add(error)
                            throw error
                        }
                    }
                    lastIteration = result
                    if (!isSuccess) {
                        if (pollingTimeout > 0) SystemClock.sleep(pollingTimeout)
                    }
                } while (SystemClock.elapsedRealtime() < endTime && !isSuccess)
            } catch (th: Throwable) {
                isSuccess = false // just make sure we will have correct action status
            }
        }
        val operationExceptions = exceptions.map { getWrapperException(it) }.distinctBy { it.javaClass.name + it.message }
        descriptor.describeResult(description, isSuccess, execTime, operationExceptions)
        descriptor.decreaseLevel()
        descriptor.append(description,"------ End of operation '${operation.name}' ------")
        val operationResult = generateResult(
            success = isSuccess,
            exceptions = operationExceptions,
            description = description.toString(),
            lastOperationIterationResult = lastIteration,
            executionTimeMs = execTime
        )
        return mergeResults(previousResult, operationResult)
    }

    fun execAssertion(previousResult: OpRes): OpRes {
        var isSuccess = true
        val description = StringBuilder()
        descriptor.appendLine(description, "\n------ Assertion block '${operation.assertion.name}' ------")
        descriptor.increaseLevel()
        val exceptions: MutableList<Throwable> = mutableListOf()
        val assertionExecTime = measureTimeMillis {
            try {
                operation.assertion.block.invoke()
                descriptor.appendLine(description,"Result = PASSED!")
            } catch (ex: Throwable) {
                isSuccess = false
                val originalException = getWrapperException(ex)
                exceptions.add(
                    UltronAssertionBlockException(
                        """
                        |Exception in assertion block '${operation.assertion.name}' of operation '${operation.name}'. 
                        |${"exception: ${originalException::class.java.canonicalName}".prefixTab()}
                        |${"message: ${originalException.message}".prefixTabForAllLines()}
                    """.trimMargin()
                    )
                )
            }
        }
        descriptor.describeResult(description, isSuccess, assertionExecTime, exceptions)
        descriptor.decreaseLevel()
        descriptor.appendLine(description,"------ End of assertion block '${operation.assertion.name}' ------")
        val assertionResult = generateResult(
            success = isSuccess,
            exceptions = exceptions,
            description = description.toString(),
            lastOperationIterationResult = null,
            executionTimeMs = assertionExecTime
        )
        return mergeResults(previousResult, assertionResult)
    }

    private fun mergeResults(previousResult: OpRes?, currentResult: OpRes): OpRes {
        return generateResult(
            success = currentResult.success,
            exceptions = mutableListOf<Throwable>().apply {
                previousResult?.let { addAll(it.exceptions) }
                addAll(currentResult.exceptions)
            },
            description = previousResult?.let { it.description + "\n" + currentResult.description } ?: currentResult.description,
            lastOperationIterationResult = currentResult.operationIterationResult ?: previousResult?.operationIterationResult,
            executionTimeMs = (previousResult?.executionTimeMs ?: 0) + currentResult.executionTimeMs
        )
    }
}


