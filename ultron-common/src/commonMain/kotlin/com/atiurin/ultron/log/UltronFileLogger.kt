package com.atiurin.ultron.log


abstract class UltronFileLogger : ULogger() {
    abstract fun getLogFilePath(): String
    abstract fun clearFile()
}