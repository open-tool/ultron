package com.atiurin.ultron.allure.writer

import okio.FileSystem
import okio.Path.Companion.toPath

internal actual fun platformEnv(name: String): String? = System.getenv(name)

internal actual fun platformResultsSink(directory: String): AllureResultsSink =
    FileSystemResultsSink(FileSystem.SYSTEM, directory.toPath())
