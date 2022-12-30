package com.atiurin.sampleapp.framework.ultronext

import com.atiurin.ultron.core.common.UltronOperationType
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

enum class CustomUltronOperations : UltronOperationType {
    ADD_TEXT_PREFIX, ASSERT_HAS_ANY_CHILD
}
// add extension function to UltronUiObject2 that calls `executeAssertion`
fun UltronUiObject2.assertHasAnyChild() = apply {
    executeAssertion(
        assertionBlock = { uiObject2ProviderBlock()!!.childCount > 0 },
        name = "Assert $selectorDesc has any child",
        type = CustomUltronOperations.ASSERT_HAS_ANY_CHILD,
        description = "UiObject2 assertion '${CustomUltronOperations.ASSERT_HAS_ANY_CHILD}' of $selectorDesc during $timeoutMs ms"
    )
}
