package com.atiurin.ultron.allure.listeners

import com.atiurin.ultron.allure.lifecycle.AllureLifecycle
import com.atiurin.ultron.allure.model.AllureStatus
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.core.config.UltronCommonConfig
import com.atiurin.ultron.listeners.UltronLifecycleListener

/**
 * Turns every Ultron operation into an Allure step. Nesting follows the before/after call order. Each step goes to the
 * test of the `runUltronUiTest` block whose thread runs the operation (see `AllureLifecycle.testOfCurrentRun`): the
 * operation pipeline knows nothing about tests, the listener resolves it.
 */
class AllureOperationStepListener(private val lifecycle: AllureLifecycle) : UltronLifecycleListener() {

    private fun excluded(operation: Operation) =
        operation.type in UltronCommonConfig.operationsExcludedFromListeners

    override fun before(operation: Operation) {
        if (excluded(operation)) return
        lifecycle.testOfCurrentRun()?.let { lifecycle.startStep(it, operation.name) }
    }

    override fun afterSuccess(operationResult: OperationResult<Operation>) {
        if (excluded(operationResult.operation)) return
        lifecycle.testOfCurrentRun(open = false)?.let { lifecycle.updateStep(it, AllureStatus.PASSED) }
    }

    override fun afterFailure(operationResult: OperationResult<Operation>) {
        if (excluded(operationResult.operation)) return
        val test = lifecycle.testOfCurrentRun(open = false) ?: return
        val throwable = operationResult.operationIterationResult?.exception ?: operationResult.exceptions.lastOrNull()
        if (throwable != null) lifecycle.updateStep(test, AllureLifecycle.statusOf(throwable), AllureLifecycle.detailsOf(throwable))
        else lifecycle.updateStep(test, AllureStatus.BROKEN)
    }

    override fun after(operationResult: OperationResult<Operation>) {
        if (excluded(operationResult.operation)) return
        lifecycle.testOfCurrentRun(open = false)?.let { lifecycle.stopStep(it) }
    }
}
