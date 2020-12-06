package com.atiurin.ultron.listeners

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult

abstract class AbstractLifecycleListener : LifecycleListener{
    var id: String
    constructor(id: String){
        this.id = id
    }
    constructor(){
        this.id = this::class.java.name
    }

    override fun <Op: Operation, OpRes : OperationResult<Op>> after(operationResult: OpRes) = Unit

    override fun <Op: Operation, OpRes : OperationResult<Op>> afterFailure(operationResult: OpRes) = Unit

    override fun <Op: Operation, OpRes : OperationResult<Op>> afterSuccess(operationResult: OpRes) = Unit

    override fun before(operation: Operation) = Unit
}