package com.atiurin.ultron.screenshot

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Looper
import android.view.View
import com.atiurin.ultron.exceptions.UltronException
import com.atiurin.ultron.file.MimeType
import com.atiurin.ultron.utils.ActivityUtil.getResumedActivity
import com.atiurin.ultron.utils.createCacheFile
import com.atiurin.ultron.utils.runOnUiThread
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.CountDownLatch

class ViewScreenshoter(private val quality: Int = 90) : Screenshoter {
    val mimeType = MimeType.JPEG

    @SuppressLint("SetWorldReadable")
    override fun takeFullScreenShot(file: File): ScreenshotResult {
        val activity = getResumedActivity() ?: throw UltronException("There is no resumed activity.")
        val bitmap = drawActivityBitmap(activity)
        writeBitmapToFile(file, bitmap)
        return ScreenshotResult(true, file, mimeType)
    }

    fun takeViewScreenshot(view: View): ScreenshotResult {
        val tempFile = createCacheFile()
        val bitmap = drawViewBitmap(view)
        writeBitmapToFile(tempFile, bitmap)
        return ScreenshotResult(true, tempFile, mimeType)
    }

    @SuppressLint("SetWorldReadable")
    private fun writeBitmapToFile(file: File, bitmap: Bitmap){
        BufferedOutputStream(FileOutputStream(file)).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            file.setReadable(true, false)
        }
        bitmap.recycle()
    }

    private fun drawActivityBitmap(activity: Activity): Bitmap = drawBitmap { activity.drawToBitmap() }
    private fun drawViewBitmap(view: View): Bitmap = drawBitmap { view.drawToBitmap() }

    private fun drawBitmap(block: () -> Bitmap): Bitmap{
        val result = runCatching {
            runOnUiThread {
                block()
            }
        }.onFailure { ex -> throw UltronException("Take screenshot failed due to ${ex.message}") }
        return result.getOrThrow()
    }

    private fun View.drawToBitmap(): Bitmap {
        if (this.width == 0 || this.height == 0) {
            throw UltronException(
                "Invalid ${this.javaClass.simpleName} size. It has zero height or width."
            )
        }
        val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        val bitmapHolder = Canvas(bitmap)
        this.draw(bitmapHolder)
        return bitmap
    }

    private fun Activity.drawToBitmap(): Bitmap = window.decorView.drawToBitmap()
}

