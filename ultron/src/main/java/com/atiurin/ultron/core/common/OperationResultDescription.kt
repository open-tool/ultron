package com.atiurin.ultron.core.common

/**
 * Interface contains references to [OperationResult]
 * it also contains text description of the result
 */
interface OperationResultDescription {
    val result: OperationResult
    val description: String
}
