package com.atiurin.ultron.core.common.resultanalyzer

interface SoftAssertionOperationResultAnalyzer : OperationResultAnalyzer {
    /**
     * Clears all previously caught exceptions, effectively resetting the internal state.
     * Use this method when starting a new set of assertions to ensure
     * that previous exceptions do not affect the current verification process.
     */
    fun clear()

    /**
     * Verifies whether any exceptions were caught during previous operations.
     * If there were caught exceptions, this method throws a general exception summarizing them.
     * Use this method at the end of your test or operation to ensure that all assertions passed.
     *
     * @throws Exception if one or more exceptions were previously caught.
     */
    fun verify()
}