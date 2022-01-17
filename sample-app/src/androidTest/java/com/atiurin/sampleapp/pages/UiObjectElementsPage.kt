package com.atiurin.sampleapp.pages

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiSelector
import com.atiurin.sampleapp.R
import com.atiurin.ultron.core.uiautomator.uiobject.UltronUiObject.Companion.ui
import com.atiurin.ultron.core.uiautomator.uiobject.UltronUiObject.Companion.uiResId
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2.Companion.by
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2.Companion.byResId
import com.atiurin.ultron.utils.getTargetResourceName

class UiObjectElementsPage {
    val notExistedObject = uiResId(R.id.send_button)
    val button = uiResId(R.id.button1)
    val eventStatus = uiResId(R.id.last_event_status)
    val radioGroup = uiResId(R.id.radio_group_visibility)
    val radioVisibleButton = uiResId(R.id.radio_visible)
    val radioInvisibleButton = uiResId(R.id.radio_invisible)
    val radioGoneButton = uiResId(R.id.radio_gone)
    val checkBoxClickable = uiResId(R.id.checkbox_clickable)
    val checkBoxEnabled = uiResId(R.id.checkbox_enable)
    val checkBoxSelected = uiResId(R.id.checkbox_selected)
    val checkBoxFocusable = uiResId(R.id.checkbox_focusable)
    val checkBoxJsEnabled = uiResId(R.id.checkbox_js_enabled)
    val editTextContentDesc = uiResId(R.id.et_contentDesc)
    val webView = uiResId(R.id.webview)
    val appCompatTextView = uiResId(R.id.app_compat_text)
    val swipableImageView = uiResId(R.id.image_view)
    val emptyImageView = uiResId(R.id.empty_image_view)
}