package com.atiurin.sampleapp.pages

import androidx.test.uiautomator.By
import com.atiurin.sampleapp.R
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2.Companion.by
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2.Companion.resId

class UiObject2ElementsPage {
    val notExistedObject = by(By.res("com.atiurin.sampleapp","123123123123"))
    val button = resId(R.id.button1)
    val eventStatus = resId(R.id.last_event_status)
    val radioGroup = resId(R.id.radio_group_visibility)
    val radioVisibleButton = resId(R.id.radio_visible)
    val radioInvisibleButton = resId(R.id.radio_invisible)
    val radioGoneButton = resId(R.id.radio_gone)
    val checkBoxClickable = resId(R.id.checkbox_clickable)
    val checkBoxEnabled = resId(R.id.checkbox_enable)
    val checkBoxSelected = resId(R.id.checkbox_selected)
    val checkBoxFocusable = resId(R.id.checkbox_focusable)
    val checkBoxJsEnabled = resId(R.id.checkbox_js_enabled)
    val checkBoxUnavailable = resId(R.id.checkbox_unavailable)
    val editTextContentDesc = resId(R.id.et_contentDesc)
    val webView = resId(R.id.webview)
    val appCompatTextView = resId(R.id.app_compat_text)
    val imageView = resId(R.id.image_view)
    val emptyImageView = resId(R.id.empty_image_view)
}