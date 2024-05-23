package com.atiurin.ultron.core.common

interface OperationIterationResult {
    val success: Boolean
    val exception: Throwable?
}