package com.atiurin.ultron.allure.step

import com.atiurin.ultron.log.UltronLogUtil.logTextBlock
import com.atiurin.ultron.log.UltronLogUtil.stepDelimiter
import io.qameta.allure.kotlin.Allure

inline fun <T> step (description: String, crossinline block: () -> T): T {
    logTextBlock("Begin STEP '$description'", delimiter = stepDelimiter)
    val result = Allure.step(description) {
        block()
    }
    logTextBlock("End STEP '$description'", delimiter = stepDelimiter)
    return result
}