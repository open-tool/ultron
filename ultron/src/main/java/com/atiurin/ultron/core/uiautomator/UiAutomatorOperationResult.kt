package com.atiurin.ultron.core.uiautomator

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult

class UiAutomatorOperationResult<T : Operation> (
        override val operation: T,
        override val success: Boolean,
        override val exception: Throwable? = null,
        override var description: String = ""
) : OperationResult<T>