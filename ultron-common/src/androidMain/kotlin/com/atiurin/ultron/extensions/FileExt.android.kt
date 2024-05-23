package com.atiurin.ultron.extensions

import com.atiurin.ultron.log.UltronLog
import java.io.File
import java.io.PrintWriter

fun File.clearContent() {
    PrintWriter(this).apply {
        print("")
        close()
    }
}

fun File.createDirectoryIfNotExists() {
    if (!exists()) {
        val result = mkdirs()
        if (!result) UltronLog.error("Unable to create directory '${this.absolutePath}'")
    }
}