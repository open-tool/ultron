package com.atiurin.ultron.allure.listeners

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.listeners.UltronLifecycleListener
import io.qameta.allure.kotlin.Allure
import io.qameta.allure.kotlin.model.Status
import io.qameta.allure.kotlin.model.StepResult
import io.qameta.allure.kotlin.util.ResultsUtils
import java.util.UUID

class DetailedOperationAllureListener : UltronLifecycleListener() {
    private val stepsMap = mutableMapOf<Operation, String>()

    override fun after(operationResult: OperationResult<Operation>) {
        Allure.lifecycle.stopStep(stepsMap.getUuid(operationResult.operation))
        stepsMap.remove(operationResult.operation)
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
        Allure.lifecycle.updateStep(stepsMap.getUuid(operationResult.operation)) {
            it.status = Status.PASSED
        }
    }

    override fun before(operation: Operation) {
        val uuid = UUID.randomUUID().toString()
        stepsMap[operation] = uuid
        val stepName = operation.name
        Allure.lifecycle.startStep(uuid, StepResult().apply {
            this.name = stepName
        })
    }

    private fun Map<Operation, String>.getUuid(operation: Operation): String {
        return this[operation] ?: throw UltronException("No Allure UUID defined for operation ${operation.name}")
    }
}