package com.atiurin.ultron.allure.step

import com.atiurin.ultron.log.UltronLogUtil.logTextBlock
import com.atiurin.ultron.log.UltronLogUtil.stepDelimiter

/** Manual step around a block of test code; operations inside become its child steps. */
inline fun <T> step(description: String, crossinline block: () -> T): T {
    logTextBlock("Begin STEP '$description'", delimiter = stepDelimiter)
    try {
        return AllureStepBackend.step(description) { block() }
    } finally {
        logTextBlock("End STEP '$description'", delimiter = stepDelimiter)
    }
}
