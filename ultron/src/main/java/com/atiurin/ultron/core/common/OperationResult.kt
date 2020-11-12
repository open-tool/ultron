package com.atiurin.ultron.core.common

/**
 * Interface contains references to [Operation] and [OperationResultDescription]
 */
interface OperationResult {
    val operation: Operation
    val success: Boolean
    val exception: Throwable?
    var resultDescription: OperationResultDescription?
}