package com.atiurin.ultron.allure.video

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class VideoFramesTest {
    private fun png(width: Int, height: Int, argb: Int): ByteArray {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        for (x in 0 until width) for (y in 0 until height) image.setRGB(x, y, argb)
        return ByteArrayOutputStream().also { ImageIO.write(image, "png", it) }.toByteArray()
    }

    @Test
    fun timelineAccumulatesDelays() {
        val frames = listOf(500, 900, 1000).map { ApngFrame(ByteArray(0), it) }
        val timeline = frameTimeline(frames)
        assertEquals(listOf(0L, 500L, 1400L), timeline.map { it.ptsMs })
        assertEquals(listOf(500, 900, 1000), timeline.map { it.durationMs })
    }

    @Test
    fun padToEvenDuplicatesLastColumnAndRow() {
        // 3x3: pixel (x, y) holds x + 10 * y in its blue byte.
        val pixels = ByteArray(3 * 3 * 4)
        for (y in 0 until 3) for (x in 0 until 3) pixels[(y * 3 + x) * 4] = (x + 10 * y).toByte()
        val padded = padToEven(RawFrame(3, 3, pixels, bgra = true))
        assertEquals(4, padded.width)
        assertEquals(4, padded.height)
        fun at(x: Int, y: Int) = padded.pixels[(y * 4 + x) * 4].toInt()
        assertEquals(at(2, 1), at(3, 1))
        assertEquals(at(1, 2), at(1, 3))
        assertEquals(at(2, 2), at(3, 3))
        assertEquals(12, at(2, 1))
    }

    @Test
    fun padToEvenKeepsEvenFrameAsIs() {
        val frame = RawFrame(2, 2, ByteArray(16), bgra = true)
        assertSame(frame, padToEven(frame))
    }

    @Test
    fun oddFrameOfThePortraitSceneBecomesEven() {
        val padded = padToEven(RawFrame(375, 667, ByteArray(375 * 667 * 4), bgra = true))
        assertEquals(376 to 668, padded.width to padded.height)
    }

    @Test
    fun decodePngGivesBgraPixelsOfTheSize() {
        val frame = decodePng(png(3, 2, 0xFF102030.toInt()))
        assertEquals(3 to 2, frame.width to frame.height)
        assertEquals(3 * 2 * 4, frame.pixels.size)
        assertTrue(frame.bgra)
        assertEquals(0x30, frame.pixels[0].toInt() and 0xFF) // blue
        assertEquals(0x20, frame.pixels[1].toInt() and 0xFF) // green
        assertEquals(0x10, frame.pixels[2].toInt() and 0xFF) // red
    }
}
