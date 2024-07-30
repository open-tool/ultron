package com.atiurin.ultron.log

/**
 * Not implemented yet
 */
actual fun getFileLogger(): UltronFileLogger {
    return object : UltronFileLogger() {
        override fun getLogFilePath(): String = ""
        override fun clearFile() = Unit
        override fun info(message: String) = Unit
        override fun info(message: String, throwable: Throwable) = Unit
        override fun debug(message: String) = Unit
        override fun debug(message: String, throwable: Throwable) = Unit
        override fun warn(message: String) = Unit
        override fun warn(message: String, throwable: Throwable) = Unit
        override fun error(message: String) = Unit
        override fun error(message: String, throwable: Throwable) = Unit
    }
}