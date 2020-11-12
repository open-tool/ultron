package com.atiurin.ultron.custom.espresso.matcher

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher


class ResourceDrawableMatcher(private val expectedId: Int = 0) : TypeSafeMatcher<View>() {
    private var resourceName: String? = null

    override fun matchesSafely(targetView: View): Boolean {
        if (targetView !is ImageView) {
            return false
        }
        val imageView: ImageView = targetView as ImageView
        if (expectedId < 0) {
            return imageView.drawable != null
        }
        val resources: Resources = targetView.context.resources
        val expectedDrawable: Drawable = resources.getDrawable(expectedId)
        resourceName = resources.getResourceEntryName(expectedId)
        return areDrawablesIdentical(imageView.drawable, expectedDrawable)
    }


    override fun describeTo(description: Description) {
        if (expectedId < 0) {
            description.appendValue("has any drawable")
        } else {
            description.appendValue("with drawable from resource id: $expectedId")
        }
        if (resourceName != null) {
            description.appendText("[")
            description.appendText(resourceName)
            description.appendText("]")
        }
    }

    private fun areDrawablesIdentical(
        expected: Drawable,
        actual: Drawable
    ): Boolean {
        val stateA = expected.constantState
        val stateB = actual.constantState
        // If the constant state is identical, they are using the same drawable resource.
        // However, the opposite is not necessarily true.
        return (stateA != null && stateB != null && stateA == stateB
                || getBitmap(expected).sameAs(getBitmap(actual)))
    }

    private fun getBitmap(drawable: Drawable): Bitmap {
        val result: Bitmap
        if (drawable is BitmapDrawable) {
            result = drawable.bitmap
        } else {
            var width = drawable.intrinsicWidth
            var height = drawable.intrinsicHeight
            // Some drawables have no intrinsic width - e.g. solid colours.
            if (width <= 0) {
                width = 1
            }
            if (height <= 0) {
                height = 1
            }
            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(result)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
        return result
    }
}

/**
 * Matches view bitmap with resource object
 * @param resourceId the object id against which the matcher is evaluated
 * @return matchesSafely returns `true` if bitmaps are the same, otherwise `false`.
 */
fun withDrawable(resourceId: Int): Matcher<View> {
    return ResourceDrawableMatcher(resourceId)
}

/**
 * Matches view has any drawable or not
 * @return matchesSafely returns `true` if view has any drawable, otherwise `false`.
 */
fun hasAnyDrawable(): Matcher<View> {
    return ResourceDrawableMatcher(-1)
}