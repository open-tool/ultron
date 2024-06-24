package com.atiurin.ultron.extensions

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.SemanticsMatcher
import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeSemanticsNodeInteraction
import com.atiurin.ultron.core.compose.nodeinteraction.captureToImage

@RequiresApi(Build.VERSION_CODES.O)
fun SemanticsMatcher.captureToImage(): ImageBitmap = UltronComposeSemanticsNodeInteraction(this).captureToImage()