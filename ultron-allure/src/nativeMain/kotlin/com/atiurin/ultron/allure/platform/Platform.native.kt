package com.atiurin.ultron.allure.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.staticCFunction
import platform.posix.atexit
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.OsFamily

@OptIn(ExperimentalNativeApi::class)
internal actual fun platformName(): String = Platform.osFamily.name.lowercase()

@OptIn(ExperimentalNativeApi::class)
internal actual fun platformTarget(): String = if (Platform.osFamily == OsFamily.MACOSX) "macos" else platformName()

internal actual fun isTestSkip(throwable: Throwable): Boolean = false

/** `atexit` takes a C function pointer, which cannot capture: the handler is kept here and read at exit. */
private var exitHandler: (() -> Unit)? = null

@OptIn(ExperimentalForeignApi::class)
internal actual fun registerProcessExitHandler(handler: () -> Unit) {
    exitHandler = handler
    atexit(staticCFunction<Unit> { exitHandler?.invoke() })
}
