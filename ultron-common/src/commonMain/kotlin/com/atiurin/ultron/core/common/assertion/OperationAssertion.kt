package com.atiurin.ultron.core.common.assertion

interface OperationAssertion {
    val name: String
    val block: () -> Unit
}

