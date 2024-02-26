package com.atiurin.sampleapp.framework

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.listeners.UltronLifecycleListener

class UltronCustomListener : UltronLifecycleListener() {
    override fun afterFailure(operationResult: OperationResult<Operation>) {
        operationResult.executionTimeMs
    }
}