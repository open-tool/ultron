package com.atiurin.ultron.allure.step

import com.atiurin.ultron.allure.UltronAllure
import com.atiurin.ultron.allure.model.AllureStatus

@PublishedApi
internal actual object AllureStepBackend {
    actual fun <T> step(description: String, block: () -> T): T {
        val lifecycle = UltronAllure.lifecycle
        // The test of the block whose thread runs the step; `null` for a block finishing late: the step is dropped.
        val test = lifecycle.testOfCurrentRun()
        test?.let { lifecycle.startStep(it, description) }
        try {
            return block().also { test?.let { lifecycle.stopStep(it, AllureStatus.PASSED) } }
        } catch (throwable: Throwable) {
            test?.let { lifecycle.stopStep(it, throwable) }
            throw throwable
        }
    }
}
