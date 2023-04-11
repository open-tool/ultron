package com.atiurin.ultron.log

import java.io.File

interface UFileLogger : ULogger {
    fun getLogFile(): File
}