package com.atiurin.ultron.extensions

import android.graphics.Bitmap
import java.nio.ByteBuffer
import java.util.*

fun Bitmap.isSameAs(expected: Bitmap): Boolean {
    val buffer1 = ByteBuffer.allocate(this.height * this.rowBytes);
    this.copyPixelsToBuffer(buffer1)

    val buffer2 = ByteBuffer.allocate(expected.height * expected.rowBytes);
    expected.copyPixelsToBuffer(buffer2)
    return Arrays.equals(buffer1.array(), buffer2.array())
}