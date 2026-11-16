package com.atiurin.ultron.allure.screenshot

import com.atiurin.ultron.allure.attachment.AttachUtil
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.screenshot.Screenshoter
import com.atiurin.ultron.screenshot.ScreenshotResult
import com.atiurin.ultron.screenshot.UiAutomationScreenshoter
import com.atiurin.ultron.screenshot.ViewScreenshoter
import com.atiurin.ultron.utils.createCacheFile
import java.io.File

class AllureScreenshot(private val quality: Int = 90) {
    private val mainScreenshoter: Screenshoter = UiAutomationScreenshoter(quality)
    private val backupScreenshoter: Screenshoter = ViewScreenshoter(quality)

    fun takeAndAttach(name: String = "screenshot"): Boolean {
        val tempFile = createCacheFile()
        var result = takeScreenshotSafely(mainScreenshoter, tempFile)
        if (!result.isSuccess) {
            result = takeScreenshotSafely(backupScreenshoter, tempFile)
            if (!result.isSuccess) {
                UltronLog.error("Failed to take the screenshot")
                return false
            }
        }
        val fileName = AttachUtil.attachFile(
            name = "$name${result.mimeType.extension}",
            file = tempFile,
            mimeType = result.mimeType
        )
        UltronLog.info("SCREENSHOT file '$fileName' has attached to Allure report")
        return result.isSuccess
    }

    private fun takeScreenshotSafely(screenshoter: Screenshoter, file: File): ScreenshotResult {
        return runCatching {
            screenshoter.takeFullScreenShot(file)
        }.onFailure {
            UltronLog.error("Failed to take screenshot due to ${it.message}")
        }.getOrElse {
            ScreenshotResult(false, file)
        }
    }
}
