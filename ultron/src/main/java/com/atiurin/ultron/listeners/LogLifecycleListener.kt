package com.atiurin.ultron.listeners

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.utils.UltronLog.info

class LogLifecycleListener : UltronLifecycleListener() {
    override fun before(operation: Operation) {
        info("-------- Before execution of ${operation.name} --------")
    }

    override fun afterSuccess(operationResult: OperationResult<Operation>) {
        info("-------- Successfully executed ${operationResult.operation.name} --------")
    }

    override fun afterFailure(operationResult: OperationResult<Operation>) {
        info("-------- Failed ${operationResult.operation.name} with description: -------- \n" +
                "${operationResult.description} ")
    }
}