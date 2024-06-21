package com.atiurin.ultron.core.compose.config

import com.atiurin.ultron.core.config.UltronCommonConfig
import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.core.common.OperationResultAnalyzer
import com.atiurin.ultron.core.common.UltronDefaultOperationResultAnalyzer
import com.atiurin.ultron.core.compose.operation.ComposeOperationResult
import com.atiurin.ultron.core.compose.operation.ComposeOperationType
import com.atiurin.ultron.core.compose.operation.UltronComposeOperation
import com.atiurin.ultron.core.compose.operation.UltronComposeOperationLifecycle
import com.atiurin.ultron.exceptions.UltronAssertionException
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.exceptions.UltronWrapperException
import com.atiurin.ultron.extensions.simpleClassName
import com.atiurin.ultron.listeners.LogLifecycleListener
import com.atiurin.ultron.listeners.UltronLifecycleListener
import com.atiurin.ultron.log.UltronLog

object UltronComposeConfig {
    init {
        UltronCommonConfig.operationsExcludedFromListeners.addAll(
            listOf(ComposeOperationType.GET_LIST_ITEM, ComposeOperationType.GET_LIST_ITEM_CHILD)
        )
        UltronCommonConfig.addListener(LogLifecycleListener())
    }
    const val DEFAULT_LAZY_COLUMN_OPERATIONS_TIMEOUT = 10_000L
    @Deprecated(
        message = "Default moved to UltronCommonConfig.Defaults",
        replaceWith = ReplaceWith(expression = "UltronCommonConfig.Defaults.OPERATION_TIMEOUT_MS")
    )
    const val DEFAULT_OPERATION_TIMEOUT = UltronCommonConfig.Defaults.OPERATION_TIMEOUT_MS

    var params: UltronComposeConfigParams = UltronComposeConfigParams()

    @Deprecated("Use [UltronComposeConfig.params.operationPollingTimeoutMs]")
    var COMPOSE_OPERATION_POLLING_TIMEOUT = params.operationPollingTimeoutMs

    @Deprecated("Use [UltronComposeConfig.params.lazyColumnOperationTimeoutMs]")
    var LAZY_COLUMN_OPERATIONS_TIMEOUT = params.lazyColumnOperationTimeoutMs

    @Deprecated("Use [UltronComposeConfig.params.lazyColumnItemSearchLimit]")
    var LAZY_COLUMN_ITEM_SEARCH_LIMIT = params.lazyColumnItemSearchLimit

    @Deprecated("Use [UltronComposeConfig.params.operationTimeoutMs]")
    var OPERATION_TIMEOUT = params.operationTimeoutMs

    var resultAnalyzer: OperationResultAnalyzer = UltronDefaultOperationResultAnalyzer()

    inline fun setResultAnalyzer(crossinline block: (OperationResult<Operation>) -> Boolean) {
        resultAnalyzer = object : OperationResultAnalyzer {
            override fun <Op : Operation, OpRes : OperationResult<Op>> analyze(
                operationResult: OpRes,
            ): Boolean {
                return block(operationResult as OperationResult<Operation>)
            }
        }
    }

    val resultHandler: (ComposeOperationResult<UltronComposeOperation>) -> Unit = {
        resultAnalyzer.analyze(it)
    }

    var allowedExceptions = mutableListOf(
        AssertionError::class,
        UltronWrapperException::class,
        UltronAssertionException::class,
        UltronException::class,
    )

    @Deprecated(
        message = "Listeners storage moved to UltronCommonConfig",
        replaceWith = ReplaceWith(expression = "UltronCommonConfig.addListener(Listener)")
    )
    fun addListener(listener: UltronLifecycleListener) {
        UltronLog.info("Add UltronComposeOperationLifecycle listener ${listener.simpleClassName()}")
        UltronCommonConfig.addListener(listener)
    }

    fun applyRecommended() {
        params = UltronComposeConfigParams()
    }

    fun apply(block: UltronComposeConfigParams.() -> Unit) {
        params.block()
        UltronLog.info("UltronComposeConfig applied with params $params}")
    }
}
