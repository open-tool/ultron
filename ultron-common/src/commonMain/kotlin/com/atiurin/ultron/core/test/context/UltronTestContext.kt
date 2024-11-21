package com.atiurin.ultron.core.test.context

import com.atiurin.ultron.core.common.resultanalyzer.OperationResultAnalyzer
import com.atiurin.ultron.core.common.resultanalyzer.SoftAssertionOperationResultAnalyzer

interface UltronTestContext {
    var softAssertion: Boolean
    val softAnalyzer: SoftAssertionOperationResultAnalyzer

    fun wrapAnalyzerIfSoftAssertion(analyzer: OperationResultAnalyzer): OperationResultAnalyzer
}
