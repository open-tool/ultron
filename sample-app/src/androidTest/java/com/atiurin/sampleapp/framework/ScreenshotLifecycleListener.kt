package com.atiurin.sampleapp.framework

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.core.espresso.action.EspressoActionType
import com.atiurin.ultron.listeners.AbstractLifecycleListener

class ScreenshotLifecycleListener : AbstractLifecycleListener(){
    override fun before(operation: Operation) {
    }

    override fun after(operationResult: OperationResult<Operation>) {
        operationResult.operation
    }
}