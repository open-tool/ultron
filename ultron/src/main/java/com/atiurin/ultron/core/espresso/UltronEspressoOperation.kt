package com.atiurin.ultron.core.espresso

import com.atiurin.ultron.core.common.DefaultOperationIterationResult
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.UltronOperationType

/**
 * @param operationBlock represent an action or assertion block, for example
 * operationBlock = { onView(matcher).perform(viewAction) }
 *
 * if [operationBlock] doesn't throw any exception the operation executes successful
 * @param type specifies the type of operation to be executed.
 * Use one of [com.atiurin.ultron.core.espresso.action.EspressoActionType], [com.atiurin.ultron.core.espresso.assertion.EspressoAssertionType]
 */
class UltronEspressoOperation(
    val operationBlock: () -> Unit,
    override val name: String,
    override val type: UltronOperationType,
    override val description: String,
    override val timeoutMs: Long
) : Operation {
    override fun execute(): OperationIterationResult {
        var success = true
        var exception: Throwable? = null
        try {
            operationBlock()
        }catch (error: Throwable){
            success = false
            exception = error
        }
        return DefaultOperationIterationResult(success, exception)
    }
}