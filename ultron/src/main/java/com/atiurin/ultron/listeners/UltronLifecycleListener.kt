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
    /**
     * executed before any action or assertion
     * @param operationResult contains reference
     */
    override fun after(operationResult: OperationResult<Operation>) = Unit
    /**
     * called when action or assertion failed
     */
    override fun afterFailure(operationResult: OperationResult<Operation>) = Unit
    /**
     * called when action or assertion has been executed successfully
     */
    override fun afterSuccess(operationResult: OperationResult<Operation>) = Unit
    /**
     * called in any case of action or assertion result
     */
    override fun before(operation: Operation) = Unit
}