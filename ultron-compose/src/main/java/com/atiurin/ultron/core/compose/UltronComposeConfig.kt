package com.atiurin.ultron.core.compose

import com.atiurin.ultron.core.compose.operation.ComposeOperationResult
import com.atiurin.ultron.core.compose.operation.UltronComposeOperation
import com.atiurin.ultron.core.common.*
import com.atiurin.ultron.core.compose.operation.UltronComposeOperationLifecycle
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.config.UltronConfigParams
import com.atiurin.ultron.exceptions.*
import com.atiurin.ultron.listeners.LogLifecycleListener
import com.atiurin.ultron.listeners.UltronLifecycleListener
import com.atiurin.ultron.log.UltronLog

object UltronComposeConfig {
    const val DEFAULT_LAZY_COLUMN_OPERATIONS_TIMEOUT = 10_000L
    const val DEFAULT_OPERATION_TIMEOUT = 5_000L

    private var params: UltronComposeConfigParams = UltronComposeConfigParams()
    fun getParams() = params

    var COMPOSE_OPERATION_POLLING_TIMEOUT = 0L //ms
    var LAZY_COLUMN_OPERATIONS_TIMEOUT = DEFAULT_LAZY_COLUMN_OPERATIONS_TIMEOUT
    var LAZY_COLUMN_ITEM_SEARCH_LIMIT = -1
    var OPERATION_TIMEOUT = DEFAULT_OPERATION_TIMEOUT

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
        OPERATION_TIMEOUT = params.operationTimeoutMs
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

data class UltronComposeConfigParams(
    var operationTimeoutMs: Long = UltronConfig.DEFAULT_OPERATION_TIMEOUT_MS
)
