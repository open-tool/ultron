package com.atiurin.ultron.allure.attach

import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * mp4 and webm have no alpha channel: a scene without a background of its own turned black in the video, and dark
 * text on it invisible. Video frames are drawn over white first; what was opaque keeps its colour.
 */
class VideoFrameBackgroundTest {
    private val transparent = 0x00000000
    private val black = 0xFF000000.toInt()
    private val white = 0xFFFFFFFF.toInt()

    /** 2x1 image: a transparent pixel, then an opaque black one ("text"). */
    private fun sceneWithoutBackground(): Image {
        val bitmap = Bitmap()
        bitmap.allocPixels(ImageInfo(2, 1, ColorType.BGRA_8888, ColorAlphaType.PREMUL))
        bitmap.erase(transparent)
        bitmap.erase(black, org.jetbrains.skia.IRect.makeXYWH(1, 0, 1, 1))
        return Image.makeFromBitmap(bitmap)
    }

    private fun pixels(image: Image): List<Int> {
        val bitmap = Bitmap.makeFromImage(image)
        return listOf(bitmap.getColor(0, 0), bitmap.getColor(1, 0))
    }

    @Test
    fun transparentPixelsBecomeTheBackgroundAndOpaqueOnesStay() {
        val flattened = flattenOnto(sceneWithoutBackground(), ComposeScreenshotProvider.VIDEO_BACKGROUND)
        assertEquals(listOf(white, black), pixels(flattened))
    }

    @Test
    fun theVideoBackgroundIsOpaqueWhite() {
        assertEquals(white, ComposeScreenshotProvider.VIDEO_BACKGROUND)
    }
}
