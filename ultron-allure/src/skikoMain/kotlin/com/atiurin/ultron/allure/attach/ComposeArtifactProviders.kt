package com.atiurin.ultron.allure.attach

import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SkikoComposeUiTest
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.printToString
import com.atiurin.ultron.core.compose.ComposeTestContainer
import com.atiurin.ultron.log.UltronLog
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.Surface

/**
 * PNG of the whole Compose scene through skia; `null` when the provider is not a Skiko test. With a [background]
 * (an ARGB color) the scene is drawn over it, so the PNG has no transparent pixels left.
 */
@OptIn(ExperimentalTestApi::class)
internal fun captureRootPng(provider: SemanticsNodeInteractionsProvider, background: Int? = null): ByteArray? {
    val test = provider as? SkikoComposeUiTest ?: return null
    val image = Image.makeFromBitmap(test.captureToImage().asSkiaBitmap())
    val png = if (background == null) image else flattenOnto(image, background)
    return png.encodeToData(EncodedImageFormat.PNG)?.bytes
}

/** [image] drawn over an opaque [background] (ARGB): what the image looks like where transparency is not shown. */
internal fun flattenOnto(image: Image, background: Int): Image {
    val surface = Surface.makeRasterN32Premul(image.width, image.height)
    surface.canvas.clear(background)
    surface.canvas.drawImage(image, 0f, 0f)
    return surface.makeImageSnapshot()
}

internal fun currentProvider(): SemanticsNodeInteractionsProvider? =
    if (ComposeTestContainer.isInitialized) ComposeTestContainer.getProvider() else null

/**
 * The scene as PNG. Screenshots keep the scene's own transparency (the report shows them on its page); the frames of
 * the video are taken with an opaque [background], because mp4 and webm have no alpha channel: a scene without a
 * background of its own would turn black there, and dark text on it invisible.
 */
class ComposeScreenshotProvider internal constructor(private val background: Int?) : ArtifactProvider {
    constructor() : this(null)

    override val kind = "screenshot"
    override val extension = "png"
    override val mimeType = "image/png"

    override fun produce(): ByteArray? {
        val provider = currentProvider() ?: return null
        return runCatching { captureRootPng(provider, background) }
            .onFailure { UltronLog.error("Allure: screenshot capture failed: ${it.message}") }
            .getOrNull()
            ?: run { UltronLog.error("Allure: screenshot is not available on this target"); null }
    }

    companion object {
        /** White, as the Allure report page behind the video player. */
        internal const val VIDEO_BACKGROUND: Int = 0xFFFFFFFF.toInt()

        /** The provider of video frames: the scene over [VIDEO_BACKGROUND]. */
        internal fun forVideo(): ComposeScreenshotProvider = ComposeScreenshotProvider(VIDEO_BACKGROUND)
    }
}

class ComposeHierarchyProvider : ArtifactProvider {
    override val kind = "hierarchy"
    override val extension = "txt"
    override val mimeType = "text/plain"

    @OptIn(ExperimentalTestApi::class)
    override fun produce(): ByteArray? {
        val provider = currentProvider() ?: return null
        return runCatching {
            provider.onAllNodes(isRoot(), useUnmergedTree = true)
                .fetchSemanticsNodes(atLeastOneRootRequired = false)
                .indices
                .map { provider.onAllNodes(isRoot(), useUnmergedTree = true)[it].printToString(maxDepth = Int.MAX_VALUE) }
                .joinToString("\n\n")
                .encodeToByteArray()
        }.onFailure { UltronLog.error("Allure: hierarchy dump failed: ${it.message}") }.getOrNull()
    }
}
