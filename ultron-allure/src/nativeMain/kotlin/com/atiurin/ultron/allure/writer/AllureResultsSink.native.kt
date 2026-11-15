package com.atiurin.ultron.allure.writer

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import okio.FileSystem
import okio.Path.Companion.toPath
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
internal actual fun platformEnv(name: String): String? = getenv(name)?.toKString()

internal actual fun platformResultsSink(directory: String): AllureResultsSink =
    FileSystemResultsSink(FileSystem.SYSTEM, directory.toPath())
