package com.atiurin.ultron.listeners

import android.util.Log
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.config.UltronConfig.operationsExcludeFromLog

class LogLifecycleListener : AbstractLifecycleListener() {
    override fun before(operation: Operation) {
        if (operation.type in operationsExcludeFromLog) return
        Log.d(UltronConfig.LOGCAT_TAG, "Before execution of ${operation.name}.")
    }

    override fun afterSuccess(operationResult: OperationResult<Operation>) {
        if (operationResult.operation.type in operationsExcludeFromLog) return
        Log.d(UltronConfig.LOGCAT_TAG, "Successfully executed ${operationResult.operation.name}.")
    }

    override fun afterFailure(operationResult: OperationResult<Operation>) {
        if (operationResult.operation.type in operationsExcludeFromLog) return
        Log.d(UltronConfig.LOGCAT_TAG, "Failed description: ${operationResult.description}")
    }
}