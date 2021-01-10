package com.atiurin.sampleapp.tests.uiautomator

import androidx.test.rule.ActivityTestRule
import com.atiurin.sampleapp.activity.MainActivity
import com.atiurin.sampleapp.pages.UiObject2FriendsListPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.core.config.UltronConfig
import org.junit.BeforeClass
import org.junit.Test

class UltronUiObject2ScrollTest : BaseTest() {
    init {
        ruleSequence.addLast(ActivityTestRule(MainActivity::class.java))
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun speedUpAutomator() {
            UltronConfig.UiAutomator.speedUp()
        }
    }

    val page = UiObject2FriendsListPage

    @Test
    fun scrollToBottom() {
        for (i in 0..10) {
            if (page.bottomElement.isSuccess { withTimeout(100).isDisplayed() }) break
            page.list.scrollDown()
        }
        page.bottomElement.isDisplayed()
//        UiScrollable(UiSelector().resourceId(getTargetResourceName(R.id.recycler_friends))).flingToEnd(10)
    }

    @Test
    fun scrollToTop() {
        for (i in 0..10) {
            if (page.bottomElement.isSuccess { withTimeout(100).isDisplayed() }) break
            page.list.scrollDown()
        }
        page.bottomElement.isDisplayed()
        for (i in 0..10) {
            if (page.topElement.isSuccess { withTimeout(100).isDisplayed() }) break
            page.list.scrollUp()
        }
    }
}