package com.atiurin.ultron.screenshot

import android.annotation.SuppressLint
import android.app.Instrumentation
import android.app.UiAutomation
import android.graphics.Bitmap
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.Configurator
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.file.MimeType
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.log.UltronLog.debug
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class UiAutomationScreenshoter(private val quality: Int): Screenshoter {
    val mimeType = MimeType.JPEG

    @SuppressLint("NewApi")
    override fun takeFullScreenShot(file: File): ScreenshotResult {
        val screenshot: Bitmap =
            getUiAutomation(InstrumentationRegistry.getInstrumentation()).takeScreenshot()
                ?: return ScreenshotResult(false, file)
        var bos: BufferedOutputStream? = null
        try {
            bos = BufferedOutputStream(FileOutputStream(file))
            screenshot.compress(Bitmap.CompressFormat.JPEG, quality, bos)
            bos.flush()
        } catch (ioe: IOException) {
            UltronLog.error("failed to save screen shot to file")
            return ScreenshotResult(false, file, mimeType)
        } finally {
            if (bos != null) {
                try {
                    bos.close()
                } catch (ioe: IOException) {
                    // Ignore
                }
            }
            screenshot.recycle()
        }
        return ScreenshotResult(true, file, mimeType)
    }

    /**
     * Code from the source code of UiDevice + improvements
     */
    fun getUiAutomation(instrumentation: Instrumentation): UiAutomation {
        val flags = Configurator.getInstance().uiAutomationFlags
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N && !isReleaseBuild) {
            instrumentation.getUiAutomation(flags)
        } else {
            // Custom flags not supported prior to N.
            if (flags != DEFAULT_UIAUTOMATION_FLAGS) {
                debug("UiAutomation flags not supported prior to N - ignoring.")
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                instrumentation.uiAutomation
            } else {
                throw UltronException("Screenshot not supported for SDK lower ${Build.VERSION_CODES.JELLY_BEAN_MR2}")
            }
        }
    }

    companion object {
        const val DEFAULT_UIAUTOMATION_FLAGS = 0
        val isReleaseBuild = "REL" == Build.VERSION.CODENAME
    }

}