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
    fun <Op: Operation, OpRes : OperationResult<Op>> afterSuccess(operationResult: OpRes)

    /**
     * called when action or assertion failed
     */
    fun <Op: Operation, OpRes : OperationResult<Op>> afterFailure(operationResult: OpRes)

    /**
     * called in any case of action or assertion result
     */
    fun <Op: Operation, OpRes : OperationResult<Op>> after(operationResult: OpRes)
}