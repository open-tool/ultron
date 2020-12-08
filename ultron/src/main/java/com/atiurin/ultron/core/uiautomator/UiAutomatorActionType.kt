package com.atiurin.ultron.core.uiautomator

import com.atiurin.ultron.core.common.OperationType

enum class UiAutomatorActionType :
        OperationType {
    CLICK, LONG_CLICK, DOUBLE_CLICK,
    DRAG, FLING,
    TYPE_TEXT, REPLACE_TEXT, CLEAR_TEXT, PRESS_KEY, CLOSE_SOFT_KEYBOARD, GET_TEXT,
    GET_APPLICATION_PACKAGE,
    SWIPE_LEFT, SWIPE_RIGHT, SWIPE_UP, SWIPE_DOWN, SCROLL, CUSTOM
}