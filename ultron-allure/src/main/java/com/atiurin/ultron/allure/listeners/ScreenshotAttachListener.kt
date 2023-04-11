package com.atiurin.ultron.allure.listeners

import com.atiurin.ultron.allure.config.AllureAttachStrategy
import com.atiurin.ultron.allure.screenshot.AllureScreenshot
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.listeners.UltronLifecycleListener

class ScreenshotAttachListener(val policy: AllureAttachStrategy) : UltronLifecycleListener() {
    val screenshot = AllureScreenshot()

    override fun afterFailure(operationResult: OperationResult<Operation>) {
        if (policy == AllureAttachStrategy.FAILURE) {
            screenshot.takeAndAttach(operationResult.operation.name)
        }
    }

    override fun afterSuccess(operationResult: OperationResult<Operation>) {
        if (policy == AllureAttachStrategy.SUCCESS) {
            screenshot.takeAndAttach(operationResult.operation.name)
        }
    }

    override fun after(operationResult: OperationResult<Operation>) {
        if (policy == AllureAttachStrategy.FINISH) {
            screenshot.takeAndAttach(operationResult.operation.name)
        }
    }
}