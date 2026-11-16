package com.atiurin.ultron.allure.writer

internal actual fun platformEnv(name: String): String? = null

internal actual fun platformResultsSink(directory: String): AllureResultsSink = UltronAllureResults.sink
