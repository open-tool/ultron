package com.atiurin.ultron.core.common

import android.util.Log
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.config.UltronConfig.isListenersOn
import com.atiurin.ultron.listeners.UltronLifecycleListener
import com.atiurin.ultron.listeners.LogLifecycleListener
import kotlin.reflect.KClass

abstract class AbstractOperationLifecycle {
    private var listeners: MutableList<UltronLifecycleListener> =
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
        val isListen = executor.operation.type !in UltronConfig.operationsExcludedFromListeners && isListenersOn
        if (isListen) listeners.forEach { it.before(executor.operation) }
        val operationResult = operationProcessor.process(executor)
        if (isListen){
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

    fun getListeners(): List<UltronLifecycleListener> {
        return listeners
    }

    fun addListener(listener: UltronLifecycleListener) {
        val exist = listeners.find { it.id == listener.id }
        exist?.let { listeners.remove(it) }
        listeners.add(listener)
    }

    fun clearListeners() {
        listeners.clear()
    }

    fun removeListener(listenerId: String) {
        val exist = listeners.find { it.id == listenerId }
        if (exist != null) {
            listeners.remove(exist)
        }
    }

    fun <T: UltronLifecycleListener> removeListener(listenerClass: Class<T>) {
        val exist = listeners.find { it.id == listenerClass.name }
        if (exist != null) {
            listeners.remove(exist)
        }
    }
}