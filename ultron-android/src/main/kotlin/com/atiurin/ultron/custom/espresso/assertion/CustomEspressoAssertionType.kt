package com.atiurin.ultron.custom.espresso.assertion

import com.atiurin.ultron.core.common.UltronOperationType

enum class CustomEspressoAssertionType : UltronOperationType {
    HAS_DRAWABLE, HAS_ANY_DRAWABLE,
    HAS_CURRENT_TEXT_COLOR, HAS_CURRENT_HINT_TEXT_COLOR, HAS_HIGHLIGHT_COLOR, HAS_SHADOW_COLOR
}
