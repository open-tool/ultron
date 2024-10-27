package com.atiurin.ultron.core.common.context

import com.atiurin.ultron.core.common.resultanalyzer.SoftAssertionOperationResultAnalyzer

class TestContext {
    var softAssertion: Boolean = false
    val softAnalyzer = SoftAssertionOperationResultAnalyzer()
}