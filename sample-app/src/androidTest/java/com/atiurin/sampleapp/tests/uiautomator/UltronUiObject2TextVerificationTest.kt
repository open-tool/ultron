package com.atiurin.sampleapp.tests.uiautomator

import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.LoginActivity
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.core.uiautomator.uiobject.UltronUiObject
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2.Companion.byResId
import com.atiurin.ultron.testlifecycle.activity.UltronActivityRule
import org.junit.Assert
import org.junit.Test

/**
 * Text actions on UiObject2 succeed only when the node text becomes the expected value.
 * `et_username` / `et_password` carry a hint, which UiAutomator reports as the text of an empty field.
 */
class UltronUiObject2TextVerificationTest : BaseTest() {
    private val activityRule = UltronActivityRule(LoginActivity::class.java)
    private val userName = byResId(R.id.et_username)
    private val password = byResId(R.id.et_password)
    private val loginButton = byResId(R.id.login_button)

    init {
        ruleSequence.add(activityRule)
    }

    @Test
    fun addText_toEmptyFieldWithHint_givesExactlyTheText() {
        userName.addText("machine").hasText("machine")
    }

    @Test
    fun addText_toFieldWithText_appendsOnce() {
        userName.replaceText("123").addText("456").hasText("123456")
    }

    @Test
    fun addText_toFieldWhoseTextEqualsHint_treatsItAsEmpty() {
        // Documented limitation: text equal to the hint is indistinguishable from the hint itself.
        userName.replaceText("Enter user name").addText("x").hasText("x")
    }

    @Test
    fun clear_fieldWithHint_succeeds() {
        userName.replaceText("some text").clear()
    }

    @Test
    fun replaceText_toPasswordField_matchesByLength() {
        // et_password reports its text masked; the check relies on the node's isPassword flag and
        // compares the length, not the mask character.
        password.replaceText("secret")
    }

    @Test
    fun addText_toEmptyPasswordField_succeeds() {
        password.addText("secret")
    }

    @Test
    fun addText_toNonEmptyPasswordField_isRefused() {
        password.replaceText("secret")
        val error = runCatching { password.withTimeout(100).addText("1") }.exceptionOrNull()
        Assert.assertNotNull("appending to a non-empty password field must fail", error)
        Assert.assertTrue(error!!.message.orEmpty(), error.message.orEmpty().contains("password field"))
    }

    @Test
    fun legacySetText_setsTextLikeReplaceText() {
        userName.replaceText("old").legacySetText("new").hasText("new")
    }

    @Test
    fun legacyAddText_uiObject_toEmptyFieldWithHint_givesExactlyTheText() {
        UltronUiObject.uiResId(R.id.et_username).legacyAddText("machine")
        userName.hasText("machine")
    }

    @Test
    fun addText_toUneditableObject_failsNamingExpectedActualAndClass() {
        val error = runCatching { loginButton.withTimeout(100).addText("machine") }.exceptionOrNull()
        Assert.assertNotNull("addText on a button must fail", error)
        val message = error!!.message.orEmpty()
        Assert.assertTrue(message, message.contains("'machine'"))
        Assert.assertTrue(message, message.contains("android.widget.Button"))
    }
}
