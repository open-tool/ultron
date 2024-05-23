package com.atiurin.ultron.custom.espresso.matcher

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.test.espresso.matcher.BoundedDiagnosingMatcher
import com.atiurin.ultron.utils.getColorHex
import org.hamcrest.Description
import org.hamcrest.Matcher

/**
 * Returns a matcher that matches [android.widget.TextView] based on it's color.
 *
 *
 * **This API is currently in beta.**
 */
internal fun hasColor(colorResId: Int, textViewColorPropertyName: String = "", viewColorSelector: TextView.() -> Int): Matcher<View> {
  return object : BoundedDiagnosingMatcher<View, TextView>(TextView::class.java) {
    var context: Context? = null
    override fun matchesSafely(textView: TextView, mismatchDescription: Description): Boolean {
      val viewContext = textView.context
      context = viewContext
      val textViewColor = viewColorSelector.invoke(textView)
      val expectedColor: Int = if (Build.VERSION.SDK_INT <= 22) {
        viewContext.resources.getColor(colorResId)
      } else {
        viewContext.getColor(colorResId)
      }
      mismatchDescription
        .appendText("textView $textViewColorPropertyName was ")
        .appendText(getColorHex(textViewColor))
      return textViewColor == expectedColor
    }

    override fun describeMoreTo(description: Description) {
      description.appendText("textView $textViewColorPropertyName is color with ")
      if (context == null) {
        description.appendText("ID ").appendValue(colorResId)
      } else {
        val color = if (Build.VERSION.SDK_INT <= 22) context!!.resources.getColor(colorResId) else context!!.getColor(colorResId)
        description.appendText("value " + getColorHex(color))
      }
    }
  }
}

fun textViewHasCurrentTextColor(colorResId: Int) = hasColor(colorResId, "currentTextColor") { this.currentTextColor }
fun textViewHasHighlightColor(colorResId: Int) = hasColor(colorResId, "highlightColor") { this.highlightColor }
fun textViewHasShadowColor(colorResId: Int) = hasColor(colorResId, "shadowColor") { this.shadowColor }
fun textViewHasCurrentHintTextColor(colorResId: Int) = hasColor(colorResId, "currentHintTextColor") { this.currentHintTextColor }