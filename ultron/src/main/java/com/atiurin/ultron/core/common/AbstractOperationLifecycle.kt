package com.atiurin.ultron.core.common

import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.listeners.AbstractLifecycleListener
import com.atiurin.ultron.listeners.LogLifecycleListener

abstract class AbstractOperationLifecycle {
    private var listeners: MutableList<AbstractLifecycleListener> =
        mutableListOf(LogLifecycleListener())

    //set your own implementation in case you would like to customise the behaviour
    open var operationProcessor : OperationProcessor = object : OperationProcessor{
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
        val listeners = getListeners()
        listeners.forEach { it.before(executor.operation) }
        val operationResult = operationProcessor.process(executor)
        if (operationResult.operation.type !in UltronConfig.operationsExcludedFromListeners){
            if (operationResult.success) {
                listeners.forEach { it.afterSuccess(operationResult as OperationResult<Operation>) }
            } else {
                listeners.forEach { it.afterFailure(operationResult as OperationResult<Operation>) }
            }
            listeners.forEach { it.after(operationResult as OperationResult<Operation>) }
        }
        resultHandler(operationResult)
        return operationResult
    }

    fun getListeners(): List<AbstractLifecycleListener> {
        return listeners
    }

    fun addListener(listener: AbstractLifecycleListener) {
        val exist = listeners.find { it.id == listener.id }
        exist?.let { listeners.remove(it) }
        listeners.add(listener)
    }

    fun clearListeners() {
        listeners.clear()
    }

    fun removeListener(listener: AbstractLifecycleListener) {
        val exist = listeners.find { it.id == listener.id }
        if (exist != null) {
            listeners.remove(exist)
        }
    }
}