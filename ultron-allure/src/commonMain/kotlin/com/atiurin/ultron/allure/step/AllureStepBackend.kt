package com.atiurin.ultron.allure.step

/** Where a manual [step] is reported on this platform: `allure-kotlin` on Android, Ultron's lifecycle elsewhere. */
@PublishedApi
internal expect object AllureStepBackend {
    fun <T> step(description: String, block: () -> T): T
}
