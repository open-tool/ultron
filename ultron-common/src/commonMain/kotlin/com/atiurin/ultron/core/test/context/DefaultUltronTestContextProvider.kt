package com.atiurin.ultron.core.test.context

class DefaultUltronTestContextProvider : UltronTestContextProvider {
    override fun provide(): UltronTestContext {
        return DefaultUltronTestContext()
    }
}