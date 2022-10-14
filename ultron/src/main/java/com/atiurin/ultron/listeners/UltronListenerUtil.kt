package com.atiurin.ultron.listeners

import com.atiurin.ultron.core.config.UltronConfig

fun <T> executeWithoutListeners(block: () -> T): T {
    UltronConfig.isListenersOn = false
    val result = block.invoke()
    UltronConfig.isListenersOn = true
    return result
}

fun <T> executeWithListeners(block: () -> T): T {
    UltronConfig.isListenersOn = true
    return block.invoke()
}

fun <T> executableWithoutListeners(block: () -> T): () -> T  = { executeWithoutListeners(block) }
fun <T> executableWithListeners(block: () -> T): () -> T  = { executeWithListeners(block) }

fun <T> (() -> T).setListenersState(value: Boolean): () -> T {
    return if(value) executableWithListeners(this)
    else executableWithoutListeners(this)
}
