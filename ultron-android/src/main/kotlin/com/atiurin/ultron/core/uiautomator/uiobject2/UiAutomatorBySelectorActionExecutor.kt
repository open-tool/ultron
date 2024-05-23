package com.atiurin.ultron.core.uiautomator.uiobject2

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperation
import com.atiurin.ultron.core.uiautomator.UiAutomatorOperationExecutor
import kotlin.reflect.KClass

class UiAutomatorBySelectorActionExecutor(
    action: UiAutomatorBySelectorAction
) : UiAutomatorOperationExecutor<UiAutomatorOperation>(action) {
    override fun getAllowedExceptions(operation: Operation): List<KClass<out Throwable>> {
        return UltronConfig.UiAutomator.UiObject2Config.allowedExceptions.map { it.kotlin }
    }
}