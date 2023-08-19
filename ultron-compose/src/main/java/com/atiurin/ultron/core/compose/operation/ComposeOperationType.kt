package com.atiurin.ultron.core.compose.operation

import com.atiurin.ultron.core.common.UltronOperationType

enum class ComposeOperationType : UltronOperationType {
    CLICK, LONG_CLICK, DOUBLE_CLICK,
    TEXT_INPUT, REPLACE_TEXT, CLEAR_TEXT, PRESS_KEY, TEXT_INPUT_SELECTION, COPY_TEXT, PASTE_TEXT, CUT_TEXT, SET_TEXT,
    GET_TEXT, GET_SEMANTICS_NODE, GET_SEMANTICS_CONFIG_PROPERTY,
    IME_ACTION, COLLAPSE, EXPAND, DISMISS, SET_PROGRESS, SET_SELECTION,
    SWIPE_LEFT, SWIPE_RIGHT, SWIPE_UP, SWIPE_DOWN,
    SCROLL_TO, SCROLL_TO_INDEX, SCROLL_TO_KEY, SCROLL_TO_NODE,
    CUSTOM, MOUSE_INPUT, SEMANTIC_ACTION,
    IS_DISPLAYED, IS_NOT_DISPLAYED,
    DOES_NOT_EXIST, EXISTS,
    IS_ENABLED, IS_NOT_ENABLED,
    IS_SELECTED, IS_NOT_SELECTED, IS_SELECTABLE,
    IS_TOGGLEABLE,
    IS_FOCUSED, IS_NOT_FOCUSED,
    IS_ON, IS_OFF,
    HAS_CLICK_ACTION, HAS_NO_CLICK_ACTION,
    HEIGHT_IS_AT_LEAST, HEIGHT_IS_EQUAL_TO, WIDTH_IS_AT_LEAST, WIDTH_IS_EQUAL_TO,
    TEXT_EQUALS, CONTAINS_TEXT,
    HAS_CONTENT_DESCRIPTION, CONTENT_DESCRIPTION_CONTAINS_TEXT,
    VALUE_EQUALS,
    PROGRESS_BAR_RANGE_EQUALS,
    ASSERT_MATCHES,
    CAPTURE_IMAGE
}