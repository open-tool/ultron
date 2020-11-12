package com.atiurin.ultron.core.common

import com.atiurin.ultron.listeners.AbstractLifecycleListener
import com.atiurin.ultron.listeners.LogLifecycleListener

abstract class AbstractOperationLifecycle {
    private var listeners: MutableList<AbstractLifecycleListener> =
        mutableListOf(LogLifecycleListener())

    fun getListeners(): List<AbstractLifecycleListener>{
        return listeners
    }
    fun addListener(listener: AbstractLifecycleListener) {
        val exist = listeners.find { it.id == listener.id }
        exist?.let { listeners.remove(it) }
        listeners.add(listener)
    }

    fun clearListeners(){
        listeners.clear()
    }

    fun removeListener(listener: AbstractLifecycleListener){
        val exist = listeners.find { it.id == listener.id }
        if (exist != null) {
            listeners.remove(exist)
        }
    }
}