package com.atiurin.ultron.core.common

/**
 * Interface contains references to [Operation]
 */
interface OperationResult<T : Operation> {
    val operation: T
    val success: Boolean
    val exceptions: List<Throwable>
    var description: String
    var operationIterationResult: OperationIterationResult?
    val executionTimeMs: Long
}