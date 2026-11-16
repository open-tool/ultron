package com.atiurin.ultron.allure.video

import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo

/** Where a frame sits on the video timeline, in milliseconds. */
class FrameTime(val ptsMs: Long, val durationMs: Int)

/** `pts(i)` is the sum of the delays before it, the duration of a frame is its own delay. */
fun frameTimeline(frames: List<ApngFrame>): List<FrameTime> {
    var pts = 0L
    return frames.map { frame -> FrameTime(pts, frame.delayMs).also { pts += frame.delayMs } }
}

/** Decoded pixels, 4 bytes each; [bgra] tells the channel order. */
class RawFrame(val width: Int, val height: Int, val pixels: ByteArray, val bgra: Boolean)

/** Width and height rounded up to even: H.264 chroma is subsampled by two. */
internal fun evenSize(size: Int): Int = size + size % 2

/** Pads to an even size by repeating the last column and row; returns [frame] itself when it is even already. */
fun padToEven(frame: RawFrame): RawFrame = fitTo(frame, evenSize(frame.width), evenSize(frame.height))

/**
 * Fits [frame] to [width] x [height]: pads with the edge pixels or crops. Frames of one test share a size
 * in practice (the scene is fixed); the first frame sets it and the rest are fitted to it.
 */
fun fitTo(frame: RawFrame, width: Int, height: Int): RawFrame {
    if (frame.width == width && frame.height == height) return frame
    val out = ByteArray(width * height * 4)
    for (y in 0 until height) {
        val sy = minOf(y, frame.height - 1)
        for (x in 0 until width) {
            val sx = minOf(x, frame.width - 1)
            val from = (sy * frame.width + sx) * 4
            frame.pixels.copyInto(out, (y * width + x) * 4, from, from + 4)
        }
    }
    return RawFrame(width, height, out, frame.bgra)
}

/** Decodes a PNG through skiko into BGRA pixels (the order is fixed explicitly, not left to the platform). */
fun decodePng(png: ByteArray): RawFrame {
    val image = Image.makeFromEncoded(png)
    val info = ImageInfo(image.width, image.height, ColorType.BGRA_8888, ColorAlphaType.PREMUL)
    val bitmap = Bitmap()
    check(bitmap.allocPixels(info)) { "cannot allocate ${image.width}x${image.height} bitmap" }
    check(image.readPixels(bitmap, 0, 0)) { "cannot read the pixels of a frame" }
    val pixels = bitmap.readPixels(info) ?: error("cannot read the bitmap pixels of a frame")
    return RawFrame(image.width, image.height, pixels, bgra = true)
}
