package com.atiurin.ultron.core.espressoweb.action

import com.atiurin.ultron.core.common.OperationType

enum class EspressoWebActionType :
    OperationType {
    WEB_CLICK,
    CLEAR_ELEMENT, WEB_KEYS,
    SCRIPT,
    SCROLL, CUSTOM
}