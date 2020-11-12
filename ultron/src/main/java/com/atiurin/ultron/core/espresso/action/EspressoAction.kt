package com.atiurin.ultron.core.espresso.action

import androidx.test.espresso.ViewAction
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationType

interface EspressoAction : Operation {
    val viewAction: ViewAction
    override val name: String
    override val type: OperationType
    override val description: String
    override val timeoutMs: Long
}