package com.atiurin.ultron.core.uiautomator

import com.atiurin.ultron.exceptions.UltronUiAutomatorException

/**
 * Checks the outcome of a UiAutomator text action: `ACTION_SET_TEXT` can be refused silently
 * (`UiObject2.setText` only logs it), so the node text is compared with the expected value.
 *
 * Two platform behaviours are taken into account:
 * - an empty field reports its hint as its text (`TextView.getTextForAccessibility`);
 * - a password field (`AccessibilityNodeInfo.isPassword`, set for a `PasswordTransformationMethod`)
 *   reports its displayed, i.e. masked, text. The mask character is not relied upon: for a password
 *   field only the length of the text is compared.
 */
internal object UiAutomatorTextCheck {

    /** The field text, or "" when [text] is the [hint] shown by an empty field. */
    fun textWithoutHint(text: String?, hint: String?): String {
        val current = text.orEmpty()
        return if (current.isNotEmpty() && current == hint) "" else current
    }

    /**
     * Throws [UltronUiAutomatorException] (retried by the operation executor until the timeout) when
     * [actual] is not [expected]. A password field matches by length, and an expected empty value
     * also matches the [hint] of the empty field.
     */
    fun verify(className: String?, expected: String, actual: String?, hint: String?, isPassword: Boolean) {
        val value = actual.orEmpty()
        val matches = value == expected ||
            (isPassword && value.length == expected.length) ||
            (expected.isEmpty() && value.isNotEmpty() && value == hint)
        if (!matches) {
            val shown = if (isPassword) "${value.length} character(s) of a password field" else "'$value'"
            throw UltronUiAutomatorException(
                "Text of $className was expected to become '$expected' but is $shown"
            )
        }
    }

    /**
     * Appending needs the current text. A non-empty password field reports it masked, so it cannot be
     * read back and appending is refused by name instead of writing the mask into the field.
     */
    fun requireReadable(className: String?, current: String, isPassword: Boolean) {
        if (isPassword && current.isNotEmpty()) {
            throw UltronUiAutomatorException(
                "Cannot append to password field $className: its current text cannot be read back; use replaceText instead"
            )
        }
    }
}
