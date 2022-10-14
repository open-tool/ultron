package com.atiurin.ultron.core.common

import com.atiurin.ultron.core.common.assertion.OperationAssertion

interface Operation {
    val name: String
    val description: String
    val type: UltronOperationType
    val timeoutMs: Long
    val assertion: OperationAssertion
    fun execute() : OperationIterationResult
}
