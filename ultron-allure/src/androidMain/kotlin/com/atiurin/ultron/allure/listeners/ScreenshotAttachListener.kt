package com.atiurin.ultron.allure.listeners

import com.atiurin.ultron.allure.config.AllureAttachStrategy
import com.atiurin.ultron.allure.screenshot.AllureScreenshot
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.listeners.UltronLifecycleListener

class ScreenshotAttachListener(val policies: Set<AllureAttachStrategy> = setOf(AllureAttachStrategy.OPERATION_FAILURE)) : UltronLifecycleListener() {
    val screenshot = AllureScreenshot()

    override fun afterFailure(operationResult: OperationResult<Operation>) {
        if (policies.contains(AllureAttachStrategy.OPERATION_FAILURE)) {
            screenshot.takeAndAttach("$prefix${operationResult.operation.name}")
        }
    }

    override fun afterSuccess(operationResult: OperationResult<Operation>) {
        if (policies.contains(AllureAttachStrategy.OPERATION_SUCCESS)) {
            screenshot.takeAndAttach("$prefix${operationResult.operation.name}")
        }
    }

    override fun after(operationResult: OperationResult<Operation>) {
        if (policies.contains(AllureAttachStrategy.OPERATION_FINISH)) {
            screenshot.takeAndAttach("$prefix${operationResult.operation.name}")
        }
    }
    companion object{
        private const val prefix = "screenshot_"
    }
}