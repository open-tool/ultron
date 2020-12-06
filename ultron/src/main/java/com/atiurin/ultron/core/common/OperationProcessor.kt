package com.atiurin.ultron.core.common

interface OperationProcessor {
    fun <Op : Operation, OpRes : OperationResult<Op>> process(executor: OperationExecutor<Op, OpRes>) : OpRes
}