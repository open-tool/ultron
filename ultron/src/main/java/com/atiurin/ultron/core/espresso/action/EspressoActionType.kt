package com.atiurin.ultron.core.espresso.action

import com.atiurin.ultron.core.common.UltronOperationType

enum class EspressoActionType :
    UltronOperationType {
    CLICK, LONG_CLICK, DOUBLE_CLICK,
    TYPE_TEXT, REPLACE_TEXT, CLEAR_TEXT, PRESS_KEY,
    SWIPE_LEFT, SWIPE_RIGHT, SWIPE_UP, SWIPE_DOWN, SCROLL, CUSTOM,
    CLOSE_SOFT_KEYBOARD, PRESS_BACK, OPEN_ACTION_BAR_OVERFLOW_OR_OPTION_MENU, OPEN_CONTEXTUAL_ACTION_MODE_OVERFLOW_MENU
}