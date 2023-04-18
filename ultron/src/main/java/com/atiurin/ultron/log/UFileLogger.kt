package com.atiurin.ultron.log

import java.io.File

abstract class UFileLogger : ULogger() {
    abstract fun getLogFile(): File
    abstract fun clearFile()
}