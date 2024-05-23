package com.atiurin.ultron.listeners

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.log.UltronLog

class LogLifecycleListener : UltronLifecycleListener() {
    override fun before(operation: Operation) {
        UltronLog.info("Start execution of ${operation.name}")
        UltronLog.info("Element info: ${operation.elementInfo}")
    }

    override fun afterSuccess(operationResult: OperationResult<Operation>) {
        UltronLog.info("Successfully executed ${operationResult.operation.name}")
    }

    override fun afterFailure(operationResult: OperationResult<Operation>) {
        UltronLog.error("Failed ${operationResult.operation.name}.  with description: \n" +
                "${operationResult.description} ")
    }
}