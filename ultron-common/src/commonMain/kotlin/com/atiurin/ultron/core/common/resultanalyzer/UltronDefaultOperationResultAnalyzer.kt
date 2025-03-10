package com.atiurin.ultron.core.common.resultanalyzer

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.core.common.assertion.isEmptyAssertion
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.exceptions.UltronOperationException

open class UltronDefaultOperationResultAnalyzer : OperationResultAnalyzer {
    override fun <Op : Operation, OpRes : OperationResult<Op>> analyze(operationResult: OpRes): Boolean {
        if (!operationResult.success) {
            val exceptionToThrow = operationResult.exceptions.lastOrNull()
                ?: UltronException(
                    """Unknown exception occurs during operation '${operationResult.operation.name}'. 
                | ! Operation result is '${operationResult.success}'.
                | ! Operation result description: ${operationResult.description}
                |"""".trimMargin()
                )
            val delimiter = "----------------------------------------------------------------------------------------"
            val assertion = if (!operationResult.operation.assertion.isEmptyAssertion()) "\nwith assertion block '${operationResult.operation.assertion.name}'" else ""
            throw UltronOperationException(
                message = """Failed '${operationResult.operation.name}' during ${operationResult.executionTimeMs}
                | > $delimiter
                | ! Begin of operation '${operationResult.operation.name}'$assertion
                | ! $delimiter
                | ! ${operationResult.description.lines().joinToString("\n !")}
                | ! $delimiter
                | ! End of operation '${operationResult.operation.name}'$assertion      
                | > $delimiter
                """.trimMargin(), exceptionToThrow
            )
        }
        return operationResult.success
    }
}