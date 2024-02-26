package com.atiurin.sampleapp.test.compose.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.atiurin.sampleapp.activity.ComposeChatActivity.Companion.chatNameTestTag
import com.atiurin.sampleapp.activity.ComposeChatActivity.Companion.chatStatusTestTag
import com.atiurin.sampleapp.activity.ComposeListActivity
import com.atiurin.sampleapp.compose.contactNameTestTag
import com.atiurin.sampleapp.compose.contactStatusTestTag
import com.atiurin.sampleapp.compose.contactsListTestTag
import com.atiurin.sampleapp.data.repositories.ContactRepository
import org.junit.Rule
import org.junit.Test

class GoogleComposeListTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComposeListActivity>()

    @Test
    fun openFirstItemTest() {
        composeRule.onNodeWithTag(contactsListTestTag)
            .onChildAt(0)
            .performClick()
        composeRule.onNodeWithTag(chatNameTestTag).assertIsDisplayed()
    }

    @Test
    fun openChatTest() {
        val contact = ContactRepository.getLast()
        val item = composeRule.onNode(
            matcher = hasTestTag(contactsListTestTag),
            useUnmergedTree = true
        ).performScrollToNode(hasAnyDescendant(hasText(contact.name)))
            .onChildren()
            .filterToOne(hasAnyDescendant(hasText(contact.name)))

    //        assert item children
        item.onChildren()
            .filterToOne(hasTestTag(contactNameTestTag))
            .assertTextEquals(contact.name)
        item.onChildren()
            .filterToOne(hasTestTag(contactStatusTestTag))
            .assertTextEquals(contact.status)
        item.performClick()
//
        composeRule.onNodeWithTag(chatNameTestTag)
            .assertIsDisplayed()
            .assertTextEquals(contact.name)
        composeRule.onNodeWithTag(chatStatusTestTag)
            .assertIsDisplayed()
            .assertTextEquals(contact.status)
    }
}