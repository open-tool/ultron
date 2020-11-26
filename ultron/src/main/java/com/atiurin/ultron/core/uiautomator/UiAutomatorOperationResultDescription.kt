package com.atiurin.ultron.core.uiautomator

import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.core.common.OperationResultDescription

class UiAutomatorOperationResultDescription(
        override val result: OperationResult,
        override val description: String
) : OperationResultDescription