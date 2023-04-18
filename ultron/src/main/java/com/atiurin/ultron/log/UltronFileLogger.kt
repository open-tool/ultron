package com.atiurin.ultron.log

import android.os.Build
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.extensions.clearContent
import com.atiurin.ultron.utils.createCacheFile
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class UltronFileLogger: UFileLogger() {
    private var file: File = createCacheFile("ultron_", ".log")

    override fun getLogFile(): File  = file
    override fun clearFile() = file.clearContent()

    override fun info(message: String) = append(LogLevel.I, message)
    override fun info(message: String, throwable: Throwable) = append(LogLevel.I, message)
    override fun debug(message: String) = append(LogLevel.D, message)
    override fun debug(message: String, throwable: Throwable) = append(LogLevel.D, message)
    override fun warn(message: String) = append(LogLevel.W, message)
    override fun warn(message: String, throwable: Throwable) = append(LogLevel.W, message)
    override fun error(message: String) = append(LogLevel.E, message)
    override fun error(message: String, throwable: Throwable) = append(LogLevel.E, message)

    private fun append(level: LogLevel, text: String, throwable: Throwable? = null) {
        val throwableText = if (throwable == null) "" else "${throwable.message}, cause ${throwable.cause}\n"
        file.appendLine("${getTime()} $level: $text\n$throwableText")
    }

    private fun File.appendLine(text: String) = this.appendText(text)

    private fun getTime(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            current.format(DateTimeFormatter.ofPattern(UltronConfig.Log.dateFormat))
        } else {
            SimpleDateFormat(UltronConfig.Log.dateFormat, Locale.getDefault()).format(Date())
        }
    }

}