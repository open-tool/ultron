package com.atiurin.ultron.core.common

/**
 * Interface contains references to [Operation]
 */
interface OperationResult<T : Operation> {
    val operation: T
    val success: Boolean
    val exception: Throwable?
    var description: String
    var operationIterationResult: OperationIterationResult?
}