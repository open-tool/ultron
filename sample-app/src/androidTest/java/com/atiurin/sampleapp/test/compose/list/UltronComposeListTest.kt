package com.atiurin.sampleapp.test.compose.list

import com.atiurin.sampleapp.activity.ComposeListActivity
import com.atiurin.sampleapp.data.repositories.ContactRepository
import com.atiurin.sampleapp.screens.ultron.UltronComposeChatScreen
import com.atiurin.sampleapp.screens.ultron.UltronComposeListScreen
import com.atiurin.sampleapp.test.base.UltronBaseTest
import com.atiurin.ultron.core.compose.createUltronComposeRule
import org.junit.Rule
import org.junit.Test

class UltronComposeListTest : UltronBaseTest() {
    @get:Rule
    val composeRule = createUltronComposeRule<ComposeListActivity>()

    @Test
    fun openFirstItemTest() {
        UltronComposeListScreen.openFirstChat()
    }

    @Test
    fun openChatTest(){
        val contact = ContactRepository.getLast()
        UltronComposeListScreen {
            assertItemUI(contact)
            openContactChat(contact)
        }
        UltronComposeChatScreen {
            assertScreenUI(contact)
        }
    }
}