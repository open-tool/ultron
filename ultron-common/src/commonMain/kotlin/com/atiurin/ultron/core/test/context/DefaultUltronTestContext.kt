package com.atiurin.ultron.core.test.context

import com.atiurin.ultron.core.common.resultanalyzer.DefaultSoftAssertionOperationResultAnalyzer
import com.atiurin.ultron.core.common.resultanalyzer.OperationResultAnalyzer

open class DefaultUltronTestContext : UltronTestContext {
    override var softAssertion: Boolean = false
    override val softAnalyzer = DefaultSoftAssertionOperationResultAnalyzer()

    override fun wrapAnalyzerIfSoftAssertion(analyzer: OperationResultAnalyzer): OperationResultAnalyzer {
        return if (softAssertion) softAnalyzer.apply {
            originalAnalyzer = analyzer
        } else analyzer
    }
}