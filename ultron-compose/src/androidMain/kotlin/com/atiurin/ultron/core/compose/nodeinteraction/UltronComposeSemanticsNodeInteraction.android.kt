package com.atiurin.ultron.core.compose.nodeinteraction

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.semantics.AccessibilityAction
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.captureToImage
import com.atiurin.ultron.core.compose.operation.ComposeOperationType
import com.atiurin.ultron.extensions.requireSemantics
import com.atiurin.ultron.utils.runOnUiThread
import java.util.concurrent.atomic.AtomicReference

@RequiresApi(Build.VERSION_CODES.O)
fun UltronComposeSemanticsNodeInteraction.captureToImage(): ImageBitmap {
    val image = AtomicReference<ImageBitmap>()
    executeOperation(
        operationBlock = { image.set(semanticsNodeInteraction.captureToImage()) },
        name = "CaptureImage for '${elementInfo.name}'",
        type = ComposeOperationType.CAPTURE_IMAGE,
        description = "Compose captureToImage for '${elementInfo.name}' during $timeoutMs ms",
    )
    return image.get()
}

fun <T : Function<Boolean>, R> SemanticsNodeInteraction.performSemanticsActionWithResult(
    key: SemanticsPropertyKey<AccessibilityAction<T>>, invocation: (T) -> R?
): R? {
    val node = fetchSemanticsNode("Failed to perform ${key.name} action.")
    requireSemantics(node, key) {
        "Failed to perform action ${key.name}"
    }
    return runOnUiThread {
        node.config[key].action?.let(invocation)
    }
}