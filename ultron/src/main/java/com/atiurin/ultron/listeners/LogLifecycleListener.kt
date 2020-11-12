package com.atiurin.ultron.listeners

import android.util.Log
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.core.config.EspressoPageObjectConfig

class LogLifecycleListener : AbstractLifecycleListener() {
    override fun before(operation: Operation) {
        Log.d(
            EspressoPageObjectConfig.LOGCAT_TAG,
            "Before execution of ${operation.name}."
        )
    }

    override fun afterSuccess(operationResult: OperationResult) {
        Log.d(
            EspressoPageObjectConfig.LOGCAT_TAG,
            "Successfully executed ${operationResult.operation.name}."
        )
    }

    override fun afterFailure(operationResult: OperationResult) {
        Log.d(
            EspressoPageObjectConfig.LOGCAT_TAG, "Failed execution of ${operationResult.operation.name}, " +
                    "description: ${operationResult.resultDescription?.description}"
        )
    }
}