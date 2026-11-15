package com.atiurin.ultron.allure.config

/**
 * Allure reporting entry point for every Ultron target.
 *
 * The common surface is what every platform understands: `params`, [applyRecommended] and [apply].
 * Each platform adds its own members to the `actual`:
 * - **Android** (instrumented tests, `allure-kotlin` pipeline): `setAllureResultsDirectory`, `addRunListener`,
 *   `removeRunListener`, `setAllureConditionExecutor`, `setAllureConditionsExecutorWrapper`; params
 *   `attachLogcat`, `addConditionsToReport`, `recordVideoPolicy`, `screenRecordOptions`.
 * - **Desktop, iOS, macOS, js, wasmJs** (Compose Multiplatform tests, Ultron's own Allure 2 writer):
 *   `videoAttacher`; params `resultsDirectory`, `videoPolicy`, `videoLimits`.
 */
expect object UltronAllureConfig {
    var params: AllureConfigParams

    /** Resets [params] to the defaults and registers everything; earlier customisations are discarded. */
    fun applyRecommended()

    /** Changes the current [params] and re-registers; the receiver is the platform's params class. */
    fun apply(block: AllureConfigParams.() -> Unit)
}

/** Parameters shared by every target; the platform `actual`s add their own (see [UltronAllureConfig]). */
expect class AllureConfigParams() {
    var addScreenshotPolicy: MutableSet<AllureAttachStrategy>
    var addHierarchyPolicy: MutableSet<AllureAttachStrategy>
    var attachUltronLog: Boolean
    var detailedAllureReport: Boolean
}
