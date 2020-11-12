package com.atiurin.sampleapp.idlingresources

import androidx.annotation.VisibleForTesting

open class Holder<out T>(private val constructor: () -> T) {

    @Volatile
    private var instance: T? = null

    @VisibleForTesting
    fun getInstanceFromTest(): T? {
        return when {
            instance != null -> instance
            else -> synchronized(this) {
                instance = constructor()
                instance
            }
        }
    }

    fun getInstanceFromApp(): T? {
        return instance
    }
}