package com.atiurin.sampleapp.tests.uiautomator

import com.atiurin.sampleapp.tests.UiElementsTest
import com.atiurin.ultron.file.MimeType
import com.atiurin.ultron.screenshot.UiAutomationScreenshoter
import com.atiurin.ultron.utils.createCacheFile
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class UiAutomationScreenshotTest : UiElementsTest() {
    private var screenshotFile: File? = null

    @After
    fun cleanup() {
        screenshotFile?.delete()
    }

    @Test
    fun uiAutomationScreenshoterCapturesNonEmptyJpegFile() {
        val file = createCacheFile("uiautomation_screenshot_", MimeType.JPEG.extension)
        screenshotFile = file

        val result = UiAutomationScreenshoter(quality = 90).takeFullScreenShot(file)

        assertTrue("UiAutomation screenshot should succeed", result.isSuccess)
        assertEquals(file, result.file)
        assertEquals(MimeType.JPEG, result.mimeType)
        assertTrue("Screenshot file should exist", file.exists())
        assertTrue("Screenshot file should not be empty", file.length() > 0L)
        assertArrayEquals(
            "Screenshot should be encoded as JPEG",
            byteArrayOf(0xFF.toByte(), 0xD8.toByte()),
            file.readBytes().take(2).toByteArray()
        )
    }
}
