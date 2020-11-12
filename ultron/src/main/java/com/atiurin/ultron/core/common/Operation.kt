package com.atiurin.ultron.core.common

interface Operation {
    val name: String
    val description: String
    val type: OperationType
    val timeoutMs: Long
    fun execute() : OperationIterationResult
}
