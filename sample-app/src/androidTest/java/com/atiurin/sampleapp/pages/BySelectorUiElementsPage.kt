package com.atiurin.sampleapp.pages

import com.atiurin.sampleapp.R
import com.atiurin.ultron.extensions.ByExt

class BySelectorUiElementsPage {
//    val button = By.res("com.atiurin.sampleapp","button1")
    val button = ByExt.resId(R.id.button1)
    val eventStatus = ByExt.resId(R.id.last_event_status)
    val radioVisibleButton = ByExt.resId(R.id.radio_visible)
    val radioInvisibleButton = ByExt.resId(R.id.radio_invisible)
    val radioGoneButton = ByExt.resId(R.id.radio_gone)
    val checkBoxClickable = ByExt.resId(R.id.checkbox_clickable)
    val checkBoxEnabled = ByExt.resId(R.id.checkbox_enable)
    val checkBoxSelected = ByExt.resId(R.id.checkbox_selected)
    val checkBoxFocusable = ByExt.resId(R.id.checkbox_focusable)
    val checkBoxJsEnabled = ByExt.resId(R.id.checkbox_js_enabled)
    val editTextContentDesc = ByExt.resId(R.id.et_contentDesc)
    val webView = ByExt.resId(R.id.webview)
    val appCompatTextView = ByExt.resId(R.id.app_compat_text)
    val imageView = ByExt.resId(R.id.image_view)
    val emptyImageView = ByExt.resId(R.id.empty_image_view)
}