package com.atiurin.ultron.allure.platform

internal actual fun platformName(): String = "web"

internal actual fun isTestSkip(throwable: Throwable): Boolean = false

/** No end-of-process hook in the browser: the last implicit test of a run is not written there. */
internal actual fun registerProcessExitHandler(handler: () -> Unit) = Unit
