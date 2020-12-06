package com.atiurin.ultron.core.common

import com.atiurin.ultron.core.uiautomator.UiAutomatorOperationResult

class CheckOperationResultAnalyzer : OperationResultAnalyzer {
    override fun <Op : Operation, OpRes : OperationResult<Op>> analyze(operationResult: OpRes): Boolean {
        return operationResult.success
    }
}