package com.atiurin.sampleapp.pages

import androidx.test.uiautomator.By
import com.atiurin.sampleapp.R
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2.Companion.by
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2.Companion.byResId

class UiObject2ElementsPage {
    val notExistedObject = by(By.res("com.atiurin.sampleapp","123123123123"))
    val button = byResId(R.id.button1)
    val eventStatus = byResId(R.id.last_event_status)
    val radioGroup = byResId(R.id.radio_group_visibility)
    val radioVisibleButton = byResId(R.id.radio_visible)
    val radioInvisibleButton = byResId(R.id.radio_invisible)
    val radioGoneButton = byResId(R.id.radio_gone)
    val checkBoxClickable = byResId(R.id.checkbox_clickable)
    val checkBoxEnabled = byResId(R.id.checkbox_enable)
    val checkBoxSelected = byResId(R.id.checkbox_selected)
    val checkBoxFocusable = byResId(R.id.checkbox_focusable)
    val checkBoxJsEnabled = byResId(R.id.checkbox_js_enabled)
    val editTextContentDesc = byResId(R.id.et_contentDesc)
    val webView = byResId(R.id.webview)
    val appCompatTextView = byResId(R.id.app_compat_text)
    val swipableImageView = byResId(R.id.image_view)
    val emptyImageView = byResId(R.id.empty_image_view)
}