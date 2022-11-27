package com.atiurin.ultron.core.common.assertion

import com.atiurin.ultron.listeners.setListenersState

class NoListenersOperationAssertion(override val name: String, override val block: () -> Unit) :
    DefaultOperationAssertion(name, block.setListenersState(false))