package com.atiurin.ultron.allure.writer

object AllureResultsLocation {
    const val ENV_VARIABLE = "ULTRON_ALLURE_RESULTS_DIR"
    const val DEFAULT = "build/allure-results"

    fun resolve(configured: String?, environment: String?, default: String = DEFAULT): String =
        configured?.takeIf { it.isNotBlank() }
            ?: environment?.takeIf { it.isNotBlank() }
            ?: default

    /** Resolution against the real environment of the current runtime. */
    fun resolve(configured: String?): String = resolve(configured, platformEnv(ENV_VARIABLE))
}
