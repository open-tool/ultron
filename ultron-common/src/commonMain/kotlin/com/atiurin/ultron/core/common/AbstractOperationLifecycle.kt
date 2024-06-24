package com.atiurin.ultron.core.common

import com.atiurin.ultron.core.config.UltronCommonConfig
import com.atiurin.ultron.listeners.AbstractListenersContainer
import com.atiurin.ultron.listeners.UltronLifecycleListener

abstract class AbstractOperationLifecycle : AbstractListenersContainer<UltronLifecycleListener>() {

    //set your own implementation in case you would like to customise the behaviour
    open var operationProcessor: OperationProcessor = object : OperationProcessor {
        override fun <Op : Operation, OpRes : OperationResult<Op>> process(executor: OperationExecutor<Op, OpRes>): OpRes {
            return executor.execute()
        }
    }

    /**
     * @param executor contains all info for operation execution
     * @param resultHandler the point to access operation result by external analyzers
     * @return [OperationResult] of operation execution
     */
    inline fun <Op : Operation, OpRes : OperationResult<Op>> execute(
        executor: OperationExecutor<Op, OpRes>,
        resultHandler: (OpRes) -> Unit = {}
    ): OpRes {
        val lifecycleListeners = getListeners() + UltronCommonConfig.getListeners()
        val isListen = executor.operation.type !in UltronCommonConfig.operationsExcludedFromListeners
                && UltronCommonConfig.isListenersOn
        if (isListen) lifecycleListeners.forEach { it.before(executor.operation) }
        val operationResult = operationProcessor.process(executor)
        if (isListen) {
            if (operationResult.success) {
                lifecycleListeners.forEach { it.afterSuccess(operationResult as OperationResult<Operation>) }
            } else {
                lifecycleListeners.forEach { it.afterFailure(operationResult as OperationResult<Operation>) }
            }
            lifecycleListeners.forEach { it.after(operationResult as OperationResult<Operation>) }
        }
        resultHandler(operationResult)
        return operationResult
    }

}