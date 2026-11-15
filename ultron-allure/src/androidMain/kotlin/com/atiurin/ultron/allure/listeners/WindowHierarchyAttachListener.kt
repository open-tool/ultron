package com.atiurin.ultron.allure.listeners

import com.atiurin.ultron.allure.config.AllureAttachStrategy
import com.atiurin.ultron.allure.hierarchy.AllureHierarchyDumper
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.listeners.UltronLifecycleListener

class WindowHierarchyAttachListener(val policies: Set<AllureAttachStrategy> = setOf(AllureAttachStrategy.OPERATION_FAILURE)) : UltronLifecycleListener() {
    val dumper = AllureHierarchyDumper()

    override fun afterFailure(operationResult: OperationResult<Operation>) {
        if (policies.contains(AllureAttachStrategy.OPERATION_FAILURE)) {
            dumper.dumpAndAttach("$prefix${operationResult.operation.name}")
        }
    }

    override fun afterSuccess(operationResult: OperationResult<Operation>) {
        if (policies.contains(AllureAttachStrategy.OPERATION_SUCCESS)) {
            dumper.dumpAndAttach("$prefix${operationResult.operation.name}")
        }
    }

    override fun after(operationResult: OperationResult<Operation>) {
        if (policies.contains(AllureAttachStrategy.OPERATION_FINISH)) {
            dumper.dumpAndAttach("$prefix${operationResult.operation.name}")
        }
    }

    companion object{
        private const val prefix = "hierarchy_"
    }
}