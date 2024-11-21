package com.atiurin.ultron.core.test.context

interface UltronTestContextProvider {
    fun provide(): UltronTestContext
}