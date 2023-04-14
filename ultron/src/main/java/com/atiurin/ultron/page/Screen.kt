package com.atiurin.ultron.page

abstract class Screen<T> {
    inline operator fun <R> invoke(block: T.() -> R): R {
        return block.invoke(this as T)
    }
}