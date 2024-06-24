package com.atiurin.ultron.screenshot

import java.io.File

interface Screenshoter {
    fun takeFullScreenShot(file: File): ScreenshotResult
}