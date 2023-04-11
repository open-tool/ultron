package com.atiurin.ultron.log

interface ULogger {
    fun info(message: String): Any
    fun info(message: String, throwable: Throwable): Any
    fun debug(message: String): Any
    fun debug(message: String, throwable: Throwable): Any
    fun warn(message: String): Any
    fun warn(message: String, throwable: Throwable): Any
    fun error(message: String): Any
    fun error(message: String, throwable: Throwable): Any
}