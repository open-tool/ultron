package com.atiurin.sampleapp.framework.ultronext

import com.atiurin.ultron.core.common.UltronOperationType
import com.atiurin.ultron.core.uiautomator.UiAutomatorActionType
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2
enum class CustomUltronOperations : UltronOperationType {
    ADD_TEXT_PREFIX, ASSERT_HAS_ANY_CHILD
}
//actually, UltronUiObject2 already has the same method addText
// this is just an example of how to extend UltronUiObject2
fun UltronUiObject2.appendText(appendText: String) = apply {
    executeAction(
        actionBlock = { uiObject2ProviderBlock()!!.text += appendText },
        name = "AppendText '$appendText' to $selectorDesc",
        type = UiAutomatorActionType.ADD_TEXT,
        description = "UiObject2 action '${UiAutomatorActionType.ADD_TEXT}' $selectorDesc appendText '$appendText' during $timeoutMs ms",
        timeoutMs = timeoutMs,
        resultHandler = resultHandler
    )
}