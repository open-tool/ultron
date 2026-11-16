package com.atiurin.ultron.allure.screenrecord

import android.app.UiAutomation
import android.os.ParcelFileDescriptor
import android.os.Process
import androidx.test.platform.app.InstrumentationRegistry
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.screenshot.UiAutomationScreenshoter
import com.atiurin.ultron.utils.createCacheFile
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Records the device screen with the on-device `screenrecord` binary started through
 * [UiAutomation.executeShellCommand] (shell UID, like `adb shell`). One recording at a time.
 *
 * The stdout pipe is read until EOF on a background thread: closing it early makes the connection
 * kill the process with SIGKILL, which leaves an mp4 without `moov`.
 */
class ScreenRecorder(private val options: ScreenRecordOptions = ScreenRecordOptions()) {

    private class Session(val remotePath: String, val finished: CountDownLatch)

    private var session: Session? = null
    private val sequence = AtomicInteger()

    val isRecording: Boolean get() = session != null

    private val uiAutomation: UiAutomation
        get() = UiAutomationScreenshoter(100).getUiAutomation(InstrumentationRegistry.getInstrumentation())

    fun start(testName: String): Boolean {
        if (session != null) stopAndDiscard()
        val remotePath = ScreenRecordProcess.fileName(testName, sequence.incrementAndGet(), Process.myPid().toString())
        return runCatching {
            val pfd = uiAutomation.executeShellCommand(ScreenRecordProcess.command(options, remotePath))
            val finished = CountDownLatch(1)
            Thread({
                runCatching { ParcelFileDescriptor.AutoCloseInputStream(pfd).use { it.readBytes() } }
                finished.countDown()
            }, "ultron-screenrecord").apply { isDaemon = true }.start()
            session = Session(remotePath, finished)
            UltronLog.info("Screen recording started: $remotePath")
        }.onFailure { UltronLog.error("Screen recording failed to start: ${it.message}") }.isSuccess
    }

    /** Stops the recording and returns the mp4 copied into the app cache, or `null` when anything failed. */
    fun stop(): File? {
        val current = session ?: return null
        session = null
        return runCatching {
            signalStop(current)
            if (!current.finished.await(STOP_TIMEOUT_SEC, TimeUnit.SECONDS)) {
                UltronLog.warn("screenrecord did not exit within $STOP_TIMEOUT_SEC s; the file may be truncated")
            }
            val file = createCacheFile("ultron_video_", ".mp4")
            shellBytes("cat ${current.remotePath}").let { bytes -> file.writeBytes(bytes) }
            shellBytes("rm ${current.remotePath}")
            if (file.length() == 0L) {
                UltronLog.error("Screen recording ${current.remotePath} is empty (no screenrecord on this image?)")
                file.delete()
                null
            } else file
        }.onFailure { UltronLog.error("Screen recording failed to stop: ${it.message}") }.getOrNull()
    }

    fun stopAndDiscard() {
        stop()?.delete()
    }

    private fun signalStop(current: Session) {
        // SIGINT lets screenrecord finalise the mp4 (write moov). Pick our own process by file name so
        // that other recordings on the same device are left alone.
        val pid = ScreenRecordProcess.PS_COMMANDS.firstNotNullOfOrNull { ps ->
            ScreenRecordProcess.findPid(shellText(ps), current.remotePath)
        }
        if (pid != null) {
            shellBytes("kill -2 $pid")
        } else {
            UltronLog.warn("screenrecord for ${current.remotePath} not found in ps output; it may have hit --time-limit already")
        }
    }

    private fun shellBytes(command: String): ByteArray =
        ParcelFileDescriptor.AutoCloseInputStream(uiAutomation.executeShellCommand(command)).use { it.readBytes() }

    private fun shellText(command: String): String = shellBytes(command).toString(Charsets.UTF_8)

    companion object {
        private const val STOP_TIMEOUT_SEC = 10L
    }
}
