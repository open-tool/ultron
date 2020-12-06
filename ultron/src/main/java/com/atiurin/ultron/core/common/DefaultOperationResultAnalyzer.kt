package com.atiurin.ultron.core.common

class DefaultOperationResultAnalyzer: OperationResultAnalyzer {
    override fun <Op : Operation, OpRes : OperationResult<Op>> analyze(operationResult: OpRes): Boolean {
        if (!operationResult.success && operationResult.exception != null) {
            throw operationResult.exception!!
        }
        return operationResult.success
    }
}