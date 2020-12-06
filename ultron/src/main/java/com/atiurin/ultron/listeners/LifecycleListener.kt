package com.atiurin.ultron.listeners

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult

internal interface LifecycleListener{
    /**
     * executed before any action or assertion
     */
    fun before(operation: Operation)
    /**
     * called when action or assertion has been executed successfully
     */
    fun afterSuccess(operationResult: OperationResult<Operation>)

    /**
     * called when action or assertion failed
     */
    fun  afterFailure(operationResult: OperationResult<Operation>)

    /**
     * called in any case of action or assertion result
     */
    fun after(operationResult: OperationResult<Operation>)
}