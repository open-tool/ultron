package com.atiurin.ultron.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

fun Drawable.isSameAs(
    expected: Drawable
): Boolean {
    val stateA = expected.constantState
    val stateB = this.constantState
    // If the constant state is identical, they are using the same drawable resource.
    // However, the opposite is not necessarily true.
    val areBitmapsSame = this.getBitmap().isSameAs(expected.getBitmap())
    val areStatesSame = stateA != null && stateB != null && stateA == stateB
    return (areStatesSame || areBitmapsSame)
}

fun Drawable.getBitmap(): Bitmap {
    val result: Bitmap
    if (this is BitmapDrawable) {
        result = this.bitmap
    } else {
        var width = this.intrinsicWidth
        var height = this.intrinsicHeight
        // Some drawables have no intrinsic width - e.g. solid colours.
        if (width <= 0) {
            width = 1
        }
        if (height <= 0) {
            height = 1
        }
        result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        this.setBounds(0, 0, canvas.width, canvas.height)
        this.draw(canvas)
    }
    return result
}
