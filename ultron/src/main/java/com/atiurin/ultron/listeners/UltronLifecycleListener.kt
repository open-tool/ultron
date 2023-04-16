package com.atiurin.ultron.listeners

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult

abstract class UltronLifecycleListener : LifecycleListener, AbstractListener(){
    override fun after(operationResult: OperationResult<Operation>) = Unit
    override fun afterFailure(operationResult: OperationResult<Operation>) = Unit
    override fun afterSuccess(operationResult: OperationResult<Operation>) = Unit
    override fun before(operation: Operation) = Unit
}
