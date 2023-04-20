package com.atiurin.ultron.core.compose.config

import com.atiurin.ultron.core.compose.operation.ComposeOperationResult
import com.atiurin.ultron.core.compose.operation.UltronComposeOperation
import com.atiurin.ultron.core.common.*
import com.atiurin.ultron.core.compose.operation.UltronComposeOperationLifecycle
import com.atiurin.ultron.exceptions.*
import com.atiurin.ultron.listeners.LogLifecycleListener
import com.atiurin.ultron.listeners.UltronLifecycleListener
import com.atiurin.ultron.log.UltronLog

object UltronComposeConfig {
    const val DEFAULT_LAZY_COLUMN_OPERATIONS_TIMEOUT = 10_000L
    const val DEFAULT_OPERATION_TIMEOUT = 5_000L

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
        AssertionError::class.java,
        UltronWrapperException::class.java,
        UltronAssertionException::class.java,
        UltronException::class.java,
    )

    fun addListener(listener: UltronLifecycleListener){
        UltronLog.info("Add UltronComposeOperationLifecycle listener ${listener.javaClass.simpleName}")
        UltronComposeOperationLifecycle.addListener(listener)
    }

    private fun modify() {
        addListener(LogLifecycleListener())
        UltronLog.info("UltronComposeConfig applied with params $params}")
    }

    fun applyRecommended(){
        params = UltronComposeConfigParams()
        modify()
    }

    fun apply(block: UltronComposeConfigParams.() -> Unit) {
        params.block()
        modify()
    }
}
