package com.atiurin.ultron.core.uiautomator

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.core.common.OperationResultDescription

class UiAutomatorOperationResult (
        override val operation: Operation,
        override val success: Boolean,
        override val exception: Throwable? = null,
        override var resultDescription: OperationResultDescription? = null
) : OperationResult