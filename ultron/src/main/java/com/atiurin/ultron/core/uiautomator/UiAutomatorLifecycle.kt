package com.atiurin.ultron.core.uiautomator

import com.atiurin.ultron.core.common.AbstractOperationLifecycle
import com.atiurin.ultron.core.common.OperationExecutor
import com.atiurin.ultron.core.common.OperationProcessor
import com.atiurin.ultron.core.common.OperationResult

object UiAutomatorLifecycle : AbstractOperationLifecycle() {

    /**
     * @param executor contains all info for operation execution
     * @param resultHandler the point to access operation result by external analyzers
     * @return [OperationResult] of UiAutomator action execution
     */
    fun execute(executor: OperationExecutor, resultHandler: (OperationResult) -> Unit = {}): OperationResult {
        val action = executor.getOperation()
        val listeners = getListeners()
        listeners.forEach { it.before(action) }
        val operationResult = processor.process(executor)
        if (operationResult.success) {
            listeners.forEach { it.afterSuccess(operationResult) }
        } else {
            listeners.forEach { it.afterFailure(operationResult) }
        }
        listeners.forEach { it.after(operationResult) }
        resultHandler(operationResult)
        return operationResult
    }

    var processor: OperationProcessor = object : OperationProcessor {
        override fun process(executor: OperationExecutor): OperationResult {
            return executor.execute()
        }
    }
}