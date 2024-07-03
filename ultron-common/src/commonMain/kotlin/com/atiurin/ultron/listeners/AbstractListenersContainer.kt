package com.atiurin.ultron.listeners

import kotlin.reflect.KClass

abstract class AbstractListenersContainer<T: AbstractListener> {
    private var listeners: MutableList<T> = mutableListOf()

    open fun getListeners(): List<T> {
        return listeners
    }

    fun addListener(listener: T) {
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

    fun <T : AbstractListener> removeListener(listenerClass: KClass<T>) {
        val exist = listeners.find { it.id == listenerClass.simpleName }
        if (exist != null) {
            listeners.remove(exist)
        }
    }
}