package com.atiurin.sampleapp.pages

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.atiurin.ultron.page.Page
import com.atiurin.sampleapp.R

object UiElementsPage : Page<UiElementsPage>() {
    val notExistElement = withText("Some not existed text element")
    val button = withId(R.id.button1)
    val eventStatus = withId(R.id.last_event_status)
    val radioVisibleButton = withId(R.id.radio_visible)
    val radioInvisibleButton = withId(R.id.radio_invisible)
    val radioGoneButton = withId(R.id.radio_gone)
    val checkBoxClickable = withId(R.id.checkbox_clickable)
    val checkBoxEnabled = withId(R.id.checkbox_enable)
    val checkBoxSelected = withId(R.id.checkbox_selected)
    val checkBoxFocusable = withId(R.id.checkbox_focusable)
    val checkBoxJsEnabled = withId(R.id.checkbox_js_enabled)
    val editTextContentDesc = withId(R.id.et_contentDesc)
    val webView = withId(R.id.webview)
    val appCompatTextView = withId(R.id.app_compat_text)
    val imageView = withId(R.id.image_view)
    val imageView2 = withId(R.id.image_view2)
    val emptyNotClickableImageView = withId(R.id.empty_image_view)
    val dialogButtonOk = onView(withText("OK")).inRoot(isDialog())
    val popupButtonCancel = onView(withText("Cancel")).inRoot(isPlatformPopup())
    val hiddenButton = withId(R.id.exist_hidden_button)
}