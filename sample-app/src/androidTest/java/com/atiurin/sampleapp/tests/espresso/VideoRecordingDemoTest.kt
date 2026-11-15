package com.atiurin.sampleapp.tests.espresso

import com.atiurin.sampleapp.activity.MainActivity
import com.atiurin.sampleapp.pages.ChatPage
import com.atiurin.sampleapp.pages.FriendsListPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.allure.config.AllureAttachStrategy
import com.atiurin.ultron.allure.config.UltronAllureConfig
import com.atiurin.ultron.core.espresso.UltronEspresso
import com.atiurin.ultron.testlifecycle.activity.UltronActivityRule
import org.junit.BeforeClass
import org.junit.Test

/**
 * A several-seconds-long scenario with a screen that keeps changing: open a chat, clear it, send a few
 * messages, go back and open another chat. Exists to judge the `screenrecord` attachment in the Allure
 * report: this class alone records with `TEST_FINISH`, the rest of the suite keeps the `TEST_FAILURE`
 * policy of [BaseTest].
 */
class VideoRecordingDemoTest : BaseTest() {

    private val activityTestRule = UltronActivityRule(MainActivity::class.java)

    init {
        ruleSequence.addLast(activityTestRule)
    }

    companion object {
        /** Runs after [BaseTest.config] (superclass `@BeforeClass` first); `apply` re-registers the listeners. */
        @BeforeClass
        @JvmStatic
        fun recordEveryTest() {
            UltronAllureConfig.apply { recordVideoPolicy = mutableSetOf(AllureAttachStrategy.TEST_FINISH) }
        }
    }

    @Test
    fun chatFlowRecordedOnVideo() {
        FriendsListPage.openChat("Chandler Bing")
        ChatPage.clearHistory()
        listOf("first", "second", "third", "fourth").forEach { ChatPage.sendMessage("$it message") }
        UltronEspresso.closeSoftKeyboard() // otherwise the first back press only hides the keyboard
        UltronEspresso.pressBack()
        FriendsListPage.openChat("Janice")
        ChatPage.assertPageDisplayed()
        UltronEspresso.pressBack()
        FriendsListPage.assertPageDisplayed()
    }
}
