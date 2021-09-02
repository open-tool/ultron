package com.atiurin.sampleapp.framework.ultronext

import com.atiurin.ultron.core.common.UltronOperationType
import com.atiurin.ultron.core.uiautomator.UiAutomatorActionType
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2

// this is just an example of how to extend UltronUiObject2
fun UltronUiObject2.addPrefixText(prefix: String) = apply {
    executeAction(
        actionBlock = {
            val initialText = uiObject2ProviderBlock()!!.text
            uiObject2ProviderBlock()!!.text = prefix + initialText
        },
        name = "Add text prefix '$prefix' to $selectorDesc",
        type = CustomUltronOperations.ADD_TEXT_PREFIX,
        description = "UiObject2 action '${CustomUltronOperations.ADD_TEXT_PREFIX}' '$prefix' to $selectorDesc during $timeoutMs ms",
        timeoutMs = timeoutMs,
        resultHandler = resultHandler
    )
}

fun UltronUiObject2.assertHasAnyChild() = apply {
    executeAssertion(
        assertionBlock = { uiObject2ProviderBlock()!!.childCount > 0 },
        name = "Assert $selectorDesc has any child",
        type = CustomUltronOperations.ASSERT_HAS_ANY_CHILD,
        description = "UiObject2 assertion '${CustomUltronOperations.ASSERT_HAS_ANY_CHILD}' of $selectorDesc during $timeoutMs ms",
        timeoutMs = timeoutMs,
        resultHandler = resultHandler
    )
}

enum class CustomUltronOperations : UltronOperationType {
    ADD_TEXT_PREFIX, ASSERT_HAS_ANY_CHILD
}