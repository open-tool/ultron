package com.atiurin.ultron.screenshot

import com.atiurin.ultron.file.MimeType
import java.io.File

data class ScreenshotResult(
    val isSuccess: Boolean,
    val file: File,
    val mimeType: MimeType = MimeType.JPEG
)
