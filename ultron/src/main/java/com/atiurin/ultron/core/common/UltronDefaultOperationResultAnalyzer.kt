package com.atiurin.ultron.core.common

import com.atiurin.ultron.core.common.assertion.isEmptyAssertion
import com.atiurin.ultron.exceptions.UltronOperationException

class UltronDefaultOperationResultAnalyzer : OperationResultAnalyzer {
    override fun <Op : Operation, OpRes : OperationResult<Op>> analyze(operationResult: OpRes): Boolean {
        if (!operationResult.success) {
            val exceptionToThrow = operationResult.exceptions.lastOrNull()
                ?: UnknownError(
                    """Unknown exception occurs during operation '${operationResult.operation.name}'. 
                |Operation result is '${operationResult.success}'.
                |Operation result description: ${operationResult.description}
                |"""".trimMargin()
                )
            val delimiter = "========================================================================================"
            val assertion = if (!operationResult.operation.assertion.isEmptyAssertion()) "\nwith assertion block '${operationResult.operation.assertion.name}'" else ""
            throw UltronOperationException(
                message = """Failed '${operationResult.operation.name}' during ${operationResult.executionTimeMs}
                |$delimiter
                |Begin of operation '${operationResult.operation.name}'$assertion
                |$delimiter
                |${operationResult.description}
                |$delimiter
                |End of operation '${operationResult.operation.name}'$assertion      
                |$delimiter
                """.trimMargin(), exceptionToThrow
            )
        }
        return operationResult.success
    }
}