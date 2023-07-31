package com.atiurin.ultron.allure.screenshot

import com.atiurin.ultron.allure.attachment.AttachUtil
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.screenshot.Screenshoter
import com.atiurin.ultron.screenshot.UiAutomationScreenshoter
import com.atiurin.ultron.screenshot.ViewScreenshoter
import com.atiurin.ultron.utils.createCacheFile
import io.qameta.allure.kotlin.Allure
import io.qameta.allure.kotlin.AllureLifecycle
import java.io.InputStream

class AllureScreenshot(private val quality: Int = 90) {
    private val mainScreenshoter: Screenshoter = UiAutomationScreenshoter(quality)
    private val backupScreenshoter: Screenshoter = ViewScreenshoter(quality)

    fun takeAndAttach(name: String = "screenshot"): Boolean {
        val tempFile = createCacheFile()
        var result = mainScreenshoter.takeFullScreenShot(tempFile)
        if (!result.isSuccess) {
            result = backupScreenshoter.takeFullScreenShot(tempFile)
            if (!result.isSuccess) UltronLog.error("Failed to take the screenshot ")
        }
        val fileName = AttachUtil.attachFile(
            name = "$name${result.mimeType.extension}",
            file = tempFile,
            mimeType = result.mimeType
        )
        UltronLog.info("SCREENSHOT file '$fileName' has attached to Allure report")
        return result.isSuccess
    }
}

