package com.atiurin.ultron.core.espresso.resultanalyzer

import com.atiurin.ultron.core.common.OperationResult

class CheckOperationResultAnalyzer : EspressoOperationResultAnalyzer {
    override fun analyze(operationResult: OperationResult): Boolean {
        return operationResult.success
    }
}