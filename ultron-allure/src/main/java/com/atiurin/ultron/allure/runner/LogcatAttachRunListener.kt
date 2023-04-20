package com.atiurin.ultron.allure.runner

import com.atiurin.ultron.allure.attachment.AttachUtil
import com.atiurin.ultron.file.MimeType
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.runner.UltronRunListener
import com.atiurin.ultron.utils.createCacheFile
import org.junit.runner.Description
import org.junit.runner.notification.Failure
import java.text.SimpleDateFormat
import java.util.*

class LogcatAttachRunListener : UltronRunListener() {
    private val sdf = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault())
    private lateinit var startTime: String

    override fun testStarted(description: Description) {
        startTime = sdf.format(Date())
    }

    override fun testFailure(failure: Failure) {
        val file = createCacheFile("logcat_", ".log")
        val command = prepareCommand(dumpTime = startTime)
        val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
        runCatching {
            file.outputStream().use { process.inputStream.copyTo(it) }
        }.onSuccess {
            UltronLog.info("Dump logcat to file")
            val fileName = AttachUtil.attachFile(file, MimeType.PLAIN_TEXT)
            UltronLog.info("LOGCAT file '$fileName' has attached to Allure report")
        }.onFailure {
            UltronLog.error("Failed to dump logcat ${it.message}")
        }
        process.destroy()
    }

    private fun prepareCommand(
        dumpTime: String,
        buffer: Buffer = Buffer.Default,
    ): String {
        return "logcat -b ${buffer.value} -d -t \"$dumpTime\""
    }

    /**
     * Logcat buffers
     * https://developer.android.com/studio/command-line/logcat#alternativeBuffers
     */
    enum class Buffer(val value: String) {
        Radio("radio"),     // View the buffer that contains radio/telephony related messages.
        Events("events"),   // View the interpreted binary system event buffer messages.
        Main("main"),       // View the main log buffer (default) does not contain system and crash log messages.
        System("system"),   // View the system log buffer (default).
        Crash("crash"),     // View the crash log buffer (default).
        All("all"),         // View all buffers.
        Default("default")  // Reports main, system, and crash buffers.
    }
}