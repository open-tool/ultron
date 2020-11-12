package com.atiurin.ultron.core.espresso.resultanalyzer

import com.atiurin.ultron.core.common.OperationResult

class DefaultOperationResultAnalyzer : EspressoOperationResultAnalyzer {
    override fun analyze(operationResult: OperationResult): Boolean {
        if (!operationResult.success && operationResult.exception != null) {
            throw operationResult.exception!!
        }
        return operationResult.success
    }
}