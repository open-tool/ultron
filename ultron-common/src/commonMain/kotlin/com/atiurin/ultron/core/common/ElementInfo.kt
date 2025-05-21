package com.atiurin.ultron.core.common

interface ElementInfo {
    var name: String
    var meta: Any?

    fun copy(): ElementInfo
}

