package com.atiurin.ultron.core.common

class UltronDefaultOperationResultAnalyzer: OperationResultAnalyzer {
    override fun <Op : Operation, OpRes : OperationResult<Op>> analyze(operationResult: OpRes): Boolean {
        if (!operationResult.success) {
            val exceptionToThrow = operationResult.exceptions.lastOrNull()
                ?: UnknownError("""Unknown exception occurs during operation '${operationResult.operation.name}'. 
                |Operation result is '${operationResult.success}'.
                |Operation result description: ${operationResult.description}
                |"""".trimMargin())
            throw exceptionToThrow
        }
        return operationResult.success
    }
}