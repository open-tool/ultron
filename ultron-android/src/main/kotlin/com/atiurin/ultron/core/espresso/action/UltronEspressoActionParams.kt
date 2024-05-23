package com.atiurin.ultron.core.espresso.action

import android.view.View
import com.atiurin.ultron.core.common.CommonOperationType
import com.atiurin.ultron.core.common.UltronOperationType
import org.hamcrest.Matcher
import org.hamcrest.Matchers

data class UltronEspressoActionParams(
    val operationName: String,
    val operationDescription: String,
    val operationType: UltronOperationType = CommonOperationType.DEFAULT,
    val viewActionConstraints: Matcher<View> = Matchers.any(View::class.java),
    val viewActionDescription: String = "Anonymous ViewAction: specify params in perform/execute method to provide custom info about this action"
)
