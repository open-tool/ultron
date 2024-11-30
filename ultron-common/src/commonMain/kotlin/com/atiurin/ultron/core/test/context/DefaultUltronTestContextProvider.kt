package com.atiurin.ultron.core.test.context

open class DefaultUltronTestContextProvider : UltronTestContextProvider {
    override fun provide(): UltronTestContext {
        return DefaultUltronTestContext()
    }
}