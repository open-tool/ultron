package com.atiurin.ultron.core.config

import com.atiurin.ultron.core.common.UltronOperationType
import com.atiurin.ultron.core.common.context.TestContext
import com.atiurin.ultron.core.common.resultanalyzer.OperationResultAnalyzer
import com.atiurin.ultron.core.common.resultanalyzer.UltronDefaultOperationResultAnalyzer
import com.atiurin.ultron.listeners.AbstractListenersContainer
import com.atiurin.ultron.listeners.UltronLifecycleListener

object UltronCommonConfig : AbstractListenersContainer<UltronLifecycleListener>() {
    val operationsExcludedFromListeners: MutableList<UltronOperationType> = mutableListOf()
    var operationTimeoutMs: Long = 5_000
    var isListenersOn = true
    var logDateFormat = "MM-dd HH:mm:ss.SSS"
    var logToFile: Boolean = true
    var resultAnalyzer: OperationResultAnalyzer = UltronDefaultOperationResultAnalyzer()
    var testContext = TestContext()

    fun wrapAnalyzerIfSoftAssertion(analyzer: OperationResultAnalyzer): OperationResultAnalyzer{
        return if (testContext.softAssertion) testContext.softAnalyzer.apply {
            originalAnalyzer = analyzer
        } else analyzer
    }

    class Defaults {
        companion object {
            const val OPERATION_TIMEOUT_MS = 5_000L
            const val POLLING_TIMEOUT_MS = 0L
        }
    }
}