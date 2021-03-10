package com.atiurin.ultron.listeners

import android.util.Log
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult

class TimeListener : UltronLifecycleListener() {
    var time = 0L

    override fun before(operation: Operation) {
        time = System.currentTimeMillis()
    }

    override fun after(operationResult: OperationResult<Operation>) {
        val execTime = System.currentTimeMillis() - time
        val mes= "${operationResult.operation.name} time = $execTime"
        if (execTime > 5_000) {
            Log.e("UltronTime", mes)
        } else Log.d("UltronTime", mes)
        time = 0
    }
}