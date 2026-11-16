package com.atiurin.ultron.allure.step

import com.atiurin.ultron.allure.lifecycle.AllureLifecycle
import com.atiurin.ultron.allure.model.AllureStatus
import com.atiurin.ultron.log.UltronLogUtil.logTextBlock
import com.atiurin.ultron.log.UltronLogUtil.stepDelimiter

/**
 * Same as [step], reported to an explicit Ultron [lifecycle] instead of the platform default. The step goes to the test
 * of the `runUltronUiTest` block whose thread runs it; a block whose test is closed already runs [block] unreported.
 */
inline fun <T> step(description: String, lifecycle: AllureLifecycle, crossinline block: () -> T): T {
    logTextBlock("Begin STEP '$description'", delimiter = stepDelimiter)
    val test = lifecycle.testOfCurrentRun()
    test?.let { lifecycle.startStep(it, description) }
    try {
        val result = block()
        test?.let { lifecycle.stopStep(it, AllureStatus.PASSED) }
        return result
    } catch (throwable: Throwable) {
        test?.let { lifecycle.stopStep(it, throwable) }
        throw throwable
    } finally {
        logTextBlock("End STEP '$description'", delimiter = stepDelimiter)
    }
}
