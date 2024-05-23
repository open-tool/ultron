package com.atiurin.ultron.core.common

class CheckOperationResultAnalyzer : OperationResultAnalyzer {
    override fun <Op : Operation, OpRes : OperationResult<Op>> analyze(operationResult: OpRes): Boolean {
        return operationResult.success
    }
}