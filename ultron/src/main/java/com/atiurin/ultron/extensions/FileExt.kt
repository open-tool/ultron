package com.atiurin.ultron.extensions

import java.io.File
import java.io.PrintWriter

fun File.clearContent() {
    PrintWriter(this).apply {
        print("")
        close()
    }
}