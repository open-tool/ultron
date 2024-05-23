package com.atiurin.ultron.listeners

import com.atiurin.ultron.core.config.UltronCommonConfig


fun <T> executeWithoutListeners(block: () -> T): T {
    UltronCommonConfig.isListenersOn = false
    val result = block.invoke()
    UltronCommonConfig.isListenersOn = true
    return result
}

fun <T> executeWithListeners(block: () -> T): T {
    UltronCommonConfig.isListenersOn = true
    return block.invoke()
}

fun <T> executableWithoutListeners(block: () -> T): () -> T  = { executeWithoutListeners(block) }
fun <T> executableWithListeners(block: () -> T): () -> T  = { executeWithListeners(block) }

fun <T> (() -> T).setListenersState(value: Boolean): () -> T {
    return if(value) executableWithListeners(this)
    else executableWithoutListeners(this)
}
