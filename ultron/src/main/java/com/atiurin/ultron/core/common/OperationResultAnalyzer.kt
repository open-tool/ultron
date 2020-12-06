package com.atiurin.ultron.core.common

interface OperationResultAnalyzer{
    /**
     * @return success status of operation execution
     */
    fun <Op : Operation, OpRes : OperationResult<Op>> analyze(operationResult: OpRes): Boolean
}