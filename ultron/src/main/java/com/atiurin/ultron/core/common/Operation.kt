package com.atiurin.ultron.core.common

interface Operation {
    val name: String
    val description: String
    val type: UltronOperationType
    val timeoutMs: Long
    fun execute() : OperationIterationResult
}
