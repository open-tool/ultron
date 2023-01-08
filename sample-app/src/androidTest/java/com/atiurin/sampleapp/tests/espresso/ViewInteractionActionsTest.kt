package com.atiurin.sampleapp.tests.espresso


import android.os.SystemClock
import android.view.KeyEvent
import androidx.test.espresso.action.EspressoKey
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.framework.ultronext.appendText
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.framework.utils.TestDataUtils.getResourceString
import com.atiurin.sampleapp.pages.UiElementsPage
import com.atiurin.sampleapp.tests.UiElementsTest
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.core.espresso.UltronEspresso
import com.atiurin.ultron.custom.espresso.action.getContentDescription
import com.atiurin.ultron.custom.espresso.action.getDrawable
import com.atiurin.ultron.custom.espresso.action.getText
import com.atiurin.ultron.custom.espresso.assertion.hasAnyDrawable
import com.atiurin.ultron.custom.espresso.assertion.hasDrawable
import com.atiurin.ultron.extensions.*
import com.atiurin.ultron.listeners.executeWithoutListeners
import com.atiurin.ultron.utils.UltronLog
import com.atiurin.ultron.utils.getTargetString
import org.junit.Assert
import org.junit.Test
import kotlin.system.measureTimeMillis

class ViewInteractionActionsTest : UiElementsTest() {
    val page = UiElementsPage

    @Test
    fun isSuccess_notExistedElement_return_false() {
        val startTime = SystemClock.elapsedRealtime()
        val result = page.notExistElement.isSuccess { isDisplayed() }
        val endTime = SystemClock.elapsedRealtime()
        Assert.assertTrue(endTime - startTime >= UltronConfig.Espresso.ASSERTION_TIMEOUT)
        Assert.assertFalse(result)
    }

    @Test
    fun isSuccess_existedElement_return_true() {
        Assert.assertTrue(page.button.isSuccess { isDisplayed() })
    }

    @Test
    fun click_onClickable() {
        page.button.click()
        page.eventStatus.textContains(getTargetString(R.string.button_event_click))
    }

    @Test
    fun click_notExisted() {
        AssertUtils.assertException { page.notExistElement.withTimeout(100).click() }
    }

    @Test
    fun longClick_onLongClickable() {
        page.button.longClick()
        page.eventStatus.textContains(getTargetString(R.string.button_event_long_click))
    }

    @Test
    fun longClick_notExisted() {
        AssertUtils.assertException { page.notExistElement.withTimeout(100).longClick() }
    }

    @Test
    fun doubleClick_onClickable() {
        page.button.doubleClick()
        page.button.withTimeout(1000).isDisplayed()
        var success = false
        with(page.eventStatus) {
            textContains(getResourceString(R.string.button_event_click))
            success = isSuccess { withTimeout(3000).textContains("1") } || isSuccess { withTimeout(2000).textContains("2") }
        }
        Assert.assertTrue(success)
    }

    @Test
    fun doubleClick_notExisted() {
        AssertUtils.assertException { page.notExistElement.withTimeout(100).doubleClick() }
    }

    @Test
    fun typeText_onEditable() {
        val text1 = "begin"
        val text2 = "simple text"
        page.editTextContentDesc
            .replaceText(text1)
            .typeText(text2)
            .hasText("$text1$text2")
    }

    @Test
    fun typeText_onNotEditable() {
        AssertUtils.assertException { page.eventStatus.withTimeout(100).typeText("simple text") }
    }

    @Test
    fun typeText_notExisted() {
        AssertUtils.assertException { page.notExistElement.withTimeout(100).typeText("asd") }
    }

    @Test
    fun replaceText_onEditable() {
        val text = "simple text"
        page.editTextContentDesc.replaceText(text).hasText(text)
    }

    @Test
    fun replaceText_notExisted() {
        AssertUtils.assertException { page.notExistElement.withTimeout(100).replaceText("asd") }
    }

    @Test
    fun clearText_onEditable() {
        page.editTextContentDesc.clearText().hasText("")
    }

    @Test
    fun clearText_notExisted() {
        AssertUtils.assertException { page.notExistElement.withTimeout(100).clearText() }
    }

    @Test
    fun pressKey_onEditable() {
        val text = "simple text"
        val expectedText = text.substring(0, text.length - 1)
        page.editTextContentDesc
            .replaceText(text)
            .click()
            .pressKey(KeyEvent.KEYCODE_DEL)
            .hasText(expectedText)
    }

    @Test
    fun pressKey_notExisted() {
        AssertUtils.assertException { page.notExistElement.withTimeout(100).pressKey(KeyEvent.KEYCODE_DEL) }
    }

    @Test
    fun pressEspressoKey_onEditable() {
        val text = "simple text"
        val expectedText = text.substring(0, text.length - 1)
        page.editTextContentDesc
            .replaceText(text)
            .click()
            .pressKey(EspressoKey.Builder().withKeyCode(KeyEvent.KEYCODE_DEL).build())
            .hasText(expectedText)
    }

    @Test
    fun pressEspressoKey_notExisted() {
        AssertUtils.assertException { page.notExistElement.withTimeout(100).pressKey(EspressoKey.Builder().withKeyCode(KeyEvent.KEYCODE_DEL).build()) }
    }

    @Test
    fun closeSoftKeyboard_whenItOpened() {
        page.editTextContentDesc.click()
        SystemClock.sleep(500)
        page.editTextContentDesc.closeSoftKeyboard()
    }

    @Test
    fun preformCustomClick_onClickable() {
        page.button.perform(click())
        page.eventStatus.textContains(getResourceString(R.string.button_event_click))
    }

    @Test
    fun performCustom_notExisted() {
        AssertUtils.assertException { page.notExistElement.withTimeout(100).perform(click()) }
    }

    @Test
    fun closeSoftKeyboardTest() {
        page.editTextContentDesc.click()
        UltronEspresso.closeSoftKeyboard()
        page.imageView.isDisplayed()
    }

    @Test
    fun customVisibilityAction() {
        val text = "appended"
        page.editTextContentDesc.appendText(text)
            .hasText(getTargetString(R.string.button_default_content_desc) + text)
        page.button.appendText(text)
    }

    @Test
    fun getTextActionTest_textExist() {
        val text = page.appCompatTextView.getText()
        Assert.assertEquals(getTargetString(R.string.app_compat_text), text)
    }

    @Test
    fun getTextActionTest_noTextInView() {
        AssertUtils.assertException { page.imageView.withTimeout(100).getText() }
    }

    @Test
    fun getDrawable_drawableExist() {
        Assert.assertNotNull(page.imageView.getDrawable())
    }

    @Test
    fun hasDrawableTest() {
        page.imageView.hasDrawable(R.drawable.ic_account)
    }

    @Test
    fun hasDrawable_wrongResourceId() {
        AssertUtils.assertException {
            page.imageView.withTimeout(1000).hasDrawable(R.drawable.chandler)
        }
    }

    @Test
    fun drawableCompare() {
        val actDr = page.imageView.getDrawable()
        val actDr2 = page.imageView2.getDrawable()
        Assert.assertTrue(actDr!!.isSameAs(actDr2!!))
    }

    @Test
    fun hasAnyDrawable_noDrawable() {
        AssertUtils.assertException { page.emptyNotClickableImageView.withTimeout(1000).hasAnyDrawable() }
    }

    @Test
    fun hasAnyDrawable_imageHasDrawable() {
        page.imageView.hasAnyDrawable()
    }

    @Test
    fun getContentDesc_descIsNull() {
        Assert.assertEquals(null, page.imageView.getContentDescription())
    }

    @Test
    fun getContentDesc_descNotNull() {
        Assert.assertEquals(getTargetString(R.string.button_default_content_desc), page.button.getContentDescription())
    }

    @Test
    fun customAssertionTest() {
        val text = "some text"
        val execTime = measureTimeMillis {
            page.editTextContentDesc.withAssertion("demo name") {
                page.editTextContentDesc.hasText(text)
            }.replaceText(text)
        }
        Assert.assertTrue(execTime < UltronConfig.Espresso.ACTION_TIMEOUT)
    }

    @Test
    fun withAssertion_failedAssertion() {
        AssertUtils.assertException {
            page.editTextContentDesc.withTimeout(1000).withAssertion {
                withText("asd23213 12312").withTimeout(500).isDisplayed()
            }.typeText("1")
        }
    }

    @Test
    fun withAssertion_failedAssertion_timeout() {
        val operationTime = 1000L
        val execTime = measureTimeMillis {
            page.editTextContentDesc.isSuccess {
                withTimeout(operationTime).withAssertion {
                    withText("asd23213 12312").withTimeout(100).isDisplayed()
                }.typeText("1")
            }
        }
        Assert.assertTrue(execTime > operationTime)
    }
}
