package com.atiurin.ultron.core.common

class DefaultOperationResultAnalyzer: OperationResultAnalyzer {
    override fun <Op : Operation, OpRes : OperationResult<Op>> analyze(operationResult: OpRes): Boolean {
        val exceptionToThrow = operationResult.exception
            ?: UnknownError("""Unknown exception occurs during operation '${operationResult.operation.name}'. 
                |Operation result is '${operationResult.success}'.
                |Operation result description: ${operationResult.description}
                |"""".trimMargin())
        if (!operationResult.success && operationResult.exception != null) {
            throw operationResult.exception!!
        }
        return operationResult.success
    }
}