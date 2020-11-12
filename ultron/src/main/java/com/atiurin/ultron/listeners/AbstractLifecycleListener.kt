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

    override fun after(operationResult: OperationResult) = Unit

    override fun afterFailure(operationResult: OperationResult) = Unit

    override fun afterSuccess(operationResult: OperationResult) = Unit

    override fun before(operation: Operation) = Unit
}