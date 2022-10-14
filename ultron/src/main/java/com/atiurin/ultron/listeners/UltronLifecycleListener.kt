package com.atiurin.ultron.listeners

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult

abstract class UltronLifecycleListener : LifecycleListener{
    var id: String
    constructor(id: String){
        this.id = id
    }
    constructor(){
        this.id = this::class.java.name
    }

    override fun after(operationResult: OperationResult<Operation>) = Unit

    override fun afterFailure(operationResult: OperationResult<Operation>) = Unit

    override fun afterSuccess(operationResult: OperationResult<Operation>) = Unit

    override fun before(operation: Operation) = Unit
}
