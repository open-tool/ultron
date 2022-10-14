package com.atiurin.ultron.core.common.assertion

class EmptyOperationAssertion : OperationAssertion {
    override val name: String
        get() = ""
    override val block: () -> Unit
        get() = {}
}

fun OperationAssertion.isEmptyAssertion(): Boolean = this is EmptyOperationAssertion