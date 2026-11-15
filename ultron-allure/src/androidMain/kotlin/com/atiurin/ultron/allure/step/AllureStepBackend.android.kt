package com.atiurin.ultron.allure.step

import io.qameta.allure.kotlin.Allure

@PublishedApi
internal actual object AllureStepBackend {
    actual fun <T> step(description: String, block: () -> T): T = Allure.step(description) { block() }
}
