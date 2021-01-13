package com.atiurin.ultron.core.espresso.action

import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions
import com.atiurin.ultron.core.common.DefaultOperationIterationResult
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationIterationResult
import com.atiurin.ultron.core.common.OperationType

class EspressoOperation(
    val operationBlock: () -> Unit,
    override val name: String,
    override val type: OperationType,
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