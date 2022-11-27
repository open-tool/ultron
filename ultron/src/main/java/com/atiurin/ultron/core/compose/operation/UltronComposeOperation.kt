package com.atiurin.ultron.core.compose.operation

import com.atiurin.ultron.core.common.*
import com.atiurin.ultron.core.common.assertion.DefaultOperationAssertion
import com.atiurin.ultron.core.common.assertion.OperationAssertion

class UltronComposeOperation(
    val operationBlock: () -> Unit,
    override val name: String,
    override val type: UltronOperationType,
    override val description: String,
    override val timeoutMs: Long,
    override val assertion: OperationAssertion = DefaultOperationAssertion(""){}
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