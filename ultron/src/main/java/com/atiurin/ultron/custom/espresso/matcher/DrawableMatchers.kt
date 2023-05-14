package com.atiurin.ultron.custom.espresso.matcher

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.atiurin.ultron.extensions.isSameAs
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher


class ResourceDrawableMatcher(private val expectedId: Int = 0) : TypeSafeMatcher<View>() {
    private var resourceName: String? = null

    override fun matchesSafely(targetView: View): Boolean {
        if (targetView !is ImageView) {
            return false
        }
        val imageView: ImageView = targetView
        if (expectedId < 0) {
            return imageView.drawable != null
        }
        val resources: Resources = targetView.context.resources
        val expectedDrawable: Drawable = resources.getDrawable(expectedId)
        resourceName = resources.getResourceEntryName(expectedId)
        return imageView.drawable?.isSameAs(expectedDrawable) == true
    }


    override fun describeTo(description: Description) {
        if (expectedId < 0) {
            description.appendValue("has any drawable")
        } else {
            description.appendValue("has drawable from resource id: $expectedId")
        }
        if (resourceName != null) {
            description.appendText("[")
            description.appendText(resourceName)
            description.appendText("]")
        }
    }
}

class InstanceDrawableMatcher(private val drawable: Drawable) : TypeSafeMatcher<View>() {

    override fun matchesSafely(targetView: View): Boolean {
        if (targetView !is ImageView) {
            return false
        }
        return targetView.drawable?.isSameAs(drawable) == true
    }


    override fun describeTo(description: Description) {
        description.appendValue("with drawable instance $drawable")
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

/**
 * Matches view bitmap with drawable object
 * @param drawable the drawable object against which the matcher is evaluated
 * @return matchesSafely returns `true` if bitmaps are the same, otherwise `false`.
 */
fun withDrawableInstance(drawable: Drawable): Matcher<View> {
    return InstanceDrawableMatcher(drawable)
}