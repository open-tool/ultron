package com.atiurin.sampleapp.framework

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.listeners.UltronLifecycleListener

class ScreenshotLifecycleListener : UltronLifecycleListener(){
    override fun before(operation: Operation) {
    }

    override fun afterFailure(operationResult: OperationResult<Operation>) {
        super.afterFailure(operationResult)
    }
}