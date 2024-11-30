package com.atiurin.ultron.core.common.resultanalyzer

import com.atiurin.ultron.core.common.Operation
import com.atiurin.ultron.core.common.OperationResult
import com.atiurin.ultron.exceptions.UltronOperationException

class DefaultSoftAssertionOperationResultAnalyzer : SoftAssertionOperationResultAnalyzer {
    private val caughtExceptions = mutableListOf<Throwable>()
    lateinit var originalAnalyzer: OperationResultAnalyzer

    override fun <Op : Operation, OpRes : OperationResult<Op>> analyze(operationResult: OpRes): Boolean {
        return kotlin.runCatching {
            originalAnalyzer.analyze(operationResult)
        }.onFailure { ex ->
            caughtExceptions.add(ex)
        }.isSuccess
    }

    override fun clear(){
        caughtExceptions.clear()
    }

    override fun verify(){
        val message = StringBuilder()
        if (caughtExceptions.isNotEmpty()){
            val delimiter = "========================================================================================"
            caughtExceptions.forEach { ex ->
                message.appendLine(ex.message)
                message.appendLine(delimiter)
            }
            clear()
            throw UltronOperationException("SOFT ASSERTION FAILED. Details:\n$message")
        }
    }
}