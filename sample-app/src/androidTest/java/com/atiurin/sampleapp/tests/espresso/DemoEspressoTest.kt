package com.atiurin.sampleapp.tests.espresso

import androidx.test.rule.ActivityTestRule
import com.atiurin.sampleapp.activity.MainActivity
import com.atiurin.sampleapp.framework.Log
import com.atiurin.sampleapp.pages.ChatPage
import com.atiurin.sampleapp.pages.FriendsListPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.core.espresso.UltronEspresso
import com.atiurin.ultron.extensions.doesNotExist
import com.atiurin.ultron.extensions.isDisplayed
import org.junit.Test

class DemoEspressoTest : BaseTest() {

    private val activityTestRule = ActivityTestRule(MainActivity::class.java)

    init {
        ruleSequence.addLast(activityTestRule)
    }


    @Test
    fun friendsItemCheck() {
        FriendsListPage {
            assertName("Janice")
            assertStatus("Janice", "Oh. My. God")
        }
    }

    @Test
    fun sendMessage() {
        FriendsListPage.openChat("Chandler Bing")
        ChatPage
            .clearHistory()
            .sendMessage("test message")
    }

    @Test
    fun checkMessagesPositionsInChat() {
        val firstMessage = "first message"
        val secondMessage = "second message"
        FriendsListPage.openChat("Janice")
        ChatPage {
            clearHistory()
            sendMessage(firstMessage)
            sendMessage(secondMessage)
            assertMessageTextAtPosition(0, firstMessage)
        }
    }

    @Test
    fun pressBackTest(){
        FriendsListPage.openChat("Chandler Bing")
        ChatPage.assertPageDisplayed()
        UltronEspresso.pressBack()
        FriendsListPage.assertPageDisplayed()
    }

    @Test
    fun openContextualActionModeOverflowMenuTest(){
        FriendsListPage.openChat("Chandler Bing")
        ChatPage.clearHistoryBtn.doesNotExist()
        UltronEspresso.openContextualActionModeOverflowMenu()
        ChatPage.clearHistoryBtn.isDisplayed()
    }

    @Test
    fun openActionBarOverflowOrOptionsMenuTest(){
        FriendsListPage.openChat("Chandler Bing")
        ChatPage.clearHistoryBtn.doesNotExist()
        UltronEspresso.openActionBarOverflowOrOptionsMenu()
        ChatPage.clearHistoryBtn.isDisplayed()
    }
}