package com.atiurin.ultron.core.common.resultanalyzer

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult

interface OperationResultAnalyzer {
    /**
     * @return success status of operation execution
     */
    fun <Op : Operation, OpRes : OperationResult<Op>> analyze(operationResult: OpRes): Boolean
}