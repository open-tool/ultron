package com.atiurin.ultron.core.espressoweb.operation

import com.atiurin.ultron.core.common.UltronOperationType

enum class EspressoWebOperationType :
    UltronOperationType {
    //element
    WEB_CLICK, WEB_GET_TEXT, WEB_REPLACE_TEXT,
    WEB_CLEAR_ELEMENT, WEB_KEYS,
    WEB_SCROLL_INTO_VIEW, WEB_ASSERT_THAT,
    WEB_EXISTS, WEB_HAS_TEXT, WEB_CONTAINS_TEXT, WEB_HAS_ATTRIBUTE,
    //elements list
    WEB_FIND_MULTIPLE_ELEMENTS,
    //document
    WEB_VIEW_ASSERT_THAT, WEB_EVAL_JS_SCRIPT,
    WEB_SELECT_ACTIVE_ELEMENT, WEB_SELECT_FRAME_BY_INDEX, WEB_SELECT_FRAME_BY_ID_OR_NAME
}