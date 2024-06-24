package com.atiurin.ultron.custom.espresso.action

import com.atiurin.ultron.core.common.UltronOperationType

enum class CustomEspressoActionType : UltronOperationType {
    GET_TEXT, GET_CONTENT_DESCRIPTION, GET_DRAWABLE, GET_VIEW, GET_VIEW_FORCIBLY,
    PERFORM_ON_VIEW, PERFORM_ON_VIEW_FORCIBLY
}
