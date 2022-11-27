package com.atiurin.ultron.core.common.assertion

open class DefaultOperationAssertion(override val name: String, override val block: () -> Unit) : OperationAssertion