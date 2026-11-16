package com.atiurin.ultron.allure.listeners

import com.atiurin.ultron.allure.attach.ArtifactProvider
import com.atiurin.ultron.allure.config.AllureAttachStrategy
import com.atiurin.ultron.allure.config.allows
import com.atiurin.ultron.allure.lifecycle.AllureLifecycle
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.listeners.UltronLifecycleListener

/**
 * Attaches one artifact kind to the current step according to the operation-level policy, in the test of the
 * `runUltronUiTest` block whose thread runs the operation; nothing is captured for a block whose test is closed.
 */
class AttachOnOperationListener(
    private val lifecycle: AllureLifecycle,
    private val provider: ArtifactProvider,
    private val policy: Set<AllureAttachStrategy>,
) : UltronLifecycleListener() {
    init { id = "${AttachOnOperationListener::class.simpleName}_${provider.kind}" }

    override fun afterFailure(operationResult: OperationResult<Operation>) =
        attachIf(AllureAttachStrategy.OPERATION_FAILURE, operationResult.operation)

    override fun afterSuccess(operationResult: OperationResult<Operation>) =
        attachIf(AllureAttachStrategy.OPERATION_SUCCESS, operationResult.operation)

    override fun after(operationResult: OperationResult<Operation>) =
        attachIf(AllureAttachStrategy.OPERATION_FINISH, operationResult.operation)

    private fun attachIf(strategy: AllureAttachStrategy, operation: Operation) {
        if (!policy.allows(strategy)) return
        val test = lifecycle.testOfCurrentRun() ?: return
        val bytes = provider.produce() ?: return
        lifecycle.attach(test, "${provider.kind}_${operation.name}", bytes, provider.extension, provider.mimeType)
    }
}
