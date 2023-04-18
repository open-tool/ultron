package com.atiurin.ultron.allure.listeners

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.listeners.UltronLifecycleListener
import io.qameta.allure.kotlin.Allure
import io.qameta.allure.kotlin.model.Status
import io.qameta.allure.kotlin.model.StepResult
import io.qameta.allure.kotlin.util.ResultsUtils
import java.util.*

class DetailedOperationAllureListener : UltronLifecycleListener() {
    lateinit var stepName: String
    lateinit var uuid: String
    override fun after(operationResult: OperationResult<Operation>) {
        Allure.lifecycle.stopStep(uuid)
    }

    override fun afterFailure(operationResult: OperationResult<Operation>) {
        val exception = operationResult.operationIterationResult?.exception
        Allure.lifecycle.updateStep {
            with(it) {
                status = ResultsUtils.getStatus(exception) ?: Status.BROKEN
                statusDetails = ResultsUtils.getStatusDetails(exception)
            }
        }
    }

    override fun afterSuccess(operationResult: OperationResult<Operation>) {
        Allure.lifecycle.updateStep(uuid) { it.status = Status.PASSED }
    }

    override fun before(operation: Operation) {
        uuid = UUID.randomUUID().toString()
        stepName = operation.name
        Allure.lifecycle.startStep(uuid, StepResult().apply {
            this.name = stepName
        })
    }
}