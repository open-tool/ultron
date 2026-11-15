package com.atiurin.ultron.core.common

import com.atiurin.ultron.core.config.UltronCommonConfig
import com.atiurin.ultron.listeners.AbstractListenersContainer
import com.atiurin.ultron.listeners.UltronLifecycleListener
import com.atiurin.ultron.log.UltronLog

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

    /**
     * Executors call this between retry iterations; same listener set and exclusions as [execute]. Only the
     * [UltronLifecycleListener.betweenRetries] call is guarded: a listener that throws there is logged with its
     * stack trace and skipped, because the hook runs inside the retry loop, where an exception would abort the
     * operation being waited for. The guard is `runCatching`, so it swallows `Error`s too (an `OutOfMemoryError`
     * from a screenshot, an `AssertionError`), not only exceptions. The other listener callbacks are not guarded.
     */
    fun notifyBetweenRetries(operation: Operation) {
        if (operation.type in UltronCommonConfig.operationsExcludedFromListeners || !UltronCommonConfig.isListenersOn) return
        (getListeners() + UltronCommonConfig.getListeners()).forEach { listener ->
            runCatching { listener.betweenRetries(operation) }
                .onFailure { UltronLog.error("Listener '${listener.id}' failed between retries of '${operation.name}'", it) }
        }
    }

}