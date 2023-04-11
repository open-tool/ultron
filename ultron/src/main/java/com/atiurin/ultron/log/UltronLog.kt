package com.atiurin.ultron.log

import kotlin.system.measureTimeMillis

object UltronLog {
    val fileLogger by lazy { UltronFileLogger() }
    val loggers = mutableListOf<ULogger>().apply { add(UltronLogcatLogger()) }

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

    fun <R> time(desc: String, block: () -> R): R {
        val result: R
        val duration = measureTimeMillis {
            result = block()
        }
        debug("$desc duration $duration ms")
        return result
    }
}