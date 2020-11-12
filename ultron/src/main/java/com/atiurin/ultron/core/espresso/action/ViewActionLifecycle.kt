package com.atiurin.ultron.core.espresso.action

import com.atiurin.ultron.core.common.AbstractOperationLifecycle
import com.atiurin.ultron.core.espresso.EspressoExecutor
import com.atiurin.ultron.core.espresso.EspressoOperationResult

object ViewActionLifecycle : AbstractOperationLifecycle() {

    /**
     * @param executor contains all info for operation execution
     * @param resultHandler the point to access operation result by external analyzers
     * @return [EspressoOperationResult] of espresso action execution
     */
    fun execute(executor: EspressoExecutor, resultHandler: (EspressoOperationResult) -> Unit = {}): EspressoOperationResult {
        val action = executor.getOperation()
        val listeners = getListeners()
        listeners.forEach { it.before(action) }
        val operationResult = actionProcessor.process(executor)
        if (operationResult.success) {
            listeners.forEach { it.afterSuccess(operationResult) }
        } else {
            listeners.forEach { it.afterFailure(operationResult) }
        }
        listeners.forEach { it.after(operationResult) }
        resultHandler(operationResult)
        return operationResult
    }

    //set your own implementation of ViewActionProcessor in case you would like to customise the behaviour
    var actionProcessor: ViewActionProcessor = object : ViewActionProcessor {
        override fun process(executor: EspressoExecutor): EspressoOperationResult {
            return executor.execute()
        }
    }
}