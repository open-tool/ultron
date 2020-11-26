package com.atiurin.ultron.core.uiautomator

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationExecutor
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.core.espresso.EspressoOperationResult

interface UiAutomatorExecutor : OperationExecutor {
    fun getAllowedExceptions(operation: Operation): List<Class<out Throwable>>
}