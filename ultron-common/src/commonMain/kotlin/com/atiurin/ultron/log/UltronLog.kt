package com.atiurin.ultron.log

expect fun getFileLogger(): UltronFileLogger

object UltronLog {
    val fileLogger by lazy { getFileLogger() }

    private val loggers = mutableSetOf<ULogger>()

    fun addLogger(logger: ULogger) {
        removeLogger(logger)
        loggers.add(logger)
    }

    fun removeLogger(logger: ULogger){
        removeLogger(logger.id)
    }

    fun removeLogger(id: String){
        val exist = loggers.find { it.id == id }
        exist?.let { loggers.remove(it) }
    }

    fun clearLoggers() = loggers.clear()

    fun info(message: String) = loggers.forEach { it.info(message) }
    fun info(message: String, throwable: Throwable) = loggers.forEach { it.info(message, throwable) }
    fun debug(message: String) = loggers.forEach { it.debug(message) }
    fun debug(message: String, throwable: Throwable) = loggers.forEach { it.debug(message, throwable) }
    fun warn(message: String) = loggers.forEach { it.warn(message) }
    fun warn(message: String, throwable: Throwable) = loggers.forEach { it.warn(message, throwable) }
    fun error(message: String) = loggers.forEach { it.error(message) }
    fun error(message: String, throwable: Throwable) = loggers.forEach { it.error(message, throwable) }

    fun log(level: LogLevel, message: String) = when (level) {
        LogLevel.I -> info(message)
        LogLevel.D -> debug(message)
        LogLevel.W -> warn(message)
        LogLevel.E -> error(message)
    }
}