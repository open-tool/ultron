package com.atiurin.ultron.core.common.resultanalyzer

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult

class CheckOperationResultAnalyzer : OperationResultAnalyzer {
    override fun <Op : Operation, OpRes : OperationResult<Op>> analyze(operationResult: OpRes): Boolean {
        return operationResult.success
    }
}