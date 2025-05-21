package com.atiurin.ultron.core.common

data class DefaultElementInfo(override var name: String = "", override var meta: Any? = null) : ElementInfo {
    override fun copy(): DefaultElementInfo {
        return DefaultElementInfo(name, meta)
    }
}

