package com.atiurin.ultron.core.espresso.assertion

import com.atiurin.ultron.core.common.AbstractOperationLifecycle
import com.atiurin.ultron.core.espresso.EspressoExecutor
import com.atiurin.ultron.core.espresso.EspressoOperationResult


object ViewAssertionLifecycle : AbstractOperationLifecycle() {

    /**
     * @param executor contains all info for operation execution
     * @param resultHandler the point to access operation result by external analyzers
     * @return [EspressoOperationResult] of espresso assertion execution
     */
    fun assert(executor: EspressoExecutor, resultHandler: (EspressoOperationResult) -> Unit = {}): EspressoOperationResult {
        val assertion = executor.getOperation()
        val listeners = getListeners()
        listeners.forEach { it.before(assertion) }
        val operationResult = assertionProcessor.process(executor)
        if (operationResult.success) {
            listeners.forEach { it.afterSuccess(operationResult) }
        } else {
            listeners.forEach { it.afterFailure(operationResult) }
        }
        listeners.forEach { it.after(operationResult) }
        resultHandler(operationResult)
        return operationResult
    }

    //set your own implementation of ViewAssertionProcessor in case you would like to customise the behaviour
    var assertionProcessor = object : ViewAssertionProcessor {
        override fun process(executor: EspressoExecutor) : EspressoOperationResult {
            return executor.execute()
        }
    }

}