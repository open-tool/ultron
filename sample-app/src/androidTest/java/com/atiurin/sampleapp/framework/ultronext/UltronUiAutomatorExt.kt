package com.atiurin.sampleapp.framework.ultronext

import com.atiurin.ultron.core.uiautomator.UiAutomatorActionType
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2

//actually, UltronUiObject2 already has the same method addText
// this is just an example of how to extend UltronUiObject2
fun UltronUiObject2.appendText(appendText: String) = apply {
    executeAction(
        actionBlock = { uiObject2ProviderBlock()!!.text += appendText },
        name = "AppendText '$appendText' to $selectorDesc",
        description = "UiObject2 action '${UiAutomatorActionType.ADD_TEXT}' $selectorDesc appendText '$appendText' during $timeoutMs ms"
    )
}