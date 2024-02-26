package com.atiurin.sampleapp.test.espresso.recycler

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.atiurin.sampleapp.activity.MainActivity
import com.atiurin.sampleapp.data.repositories.ContactRepository
import com.atiurin.sampleapp.screens.ultron.UltronEspressoChatScreen
import com.atiurin.sampleapp.screens.ultron.UltronRecyclerViewScreen
import com.atiurin.sampleapp.test.base.UltronAuthorisedBaseTest
import com.atiurin.ultron.testlifecycle.setupteardown.SetUp
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import com.atiurin.ultron.testlifecycle.setupteardown.TearDown
import com.atiurin.ultron.testlifecycle.setupteardown.TearDownRule
import org.junit.Test

class UltronRecyclerViewTest : UltronAuthorisedBaseTest() {
    private val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)
    private val setUpRule = SetUpRule().add("CLEAR_LIST") {
            ContactRepository.clear()
        }
    private val tearDownRule = TearDownRule().add("LOAD_LIST") {
        ContactRepository.loadContacts()
    }

    init {
        ruleSequence.add(setUpRule).addLast(activityScenarioRule)
        ruleSequence.add(tearDownRule)
    }

    @Test
    fun openFirstChatTest() {
        UltronRecyclerViewScreen.openFirstChat()
    }

    @Test
    fun sendMessageTest() {
        val contact = ContactRepository.getLast()
        UltronRecyclerViewScreen {
            assertItemUi(contact)
            openContactChat(contact)
        }
        UltronEspressoChatScreen {
            sendMessage("SOME MESSAGE")
        }
    }

    @Test
    @SetUp("CLEAR_LIST")
    @TearDown("LOAD_LIST")
    fun emptyListState() {
        UltronRecyclerViewScreen.assertEmptyList()
    }
}
