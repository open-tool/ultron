package com.atiurin.sampleapp.tests.compose

import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import com.atiurin.sampleapp.activity.ComposeListWithPositionTestTagActivity
import com.atiurin.sampleapp.compose.contactsListContentDesc
import com.atiurin.sampleapp.compose.getContactItemTestTagByPosition
import com.atiurin.sampleapp.data.repositories.CONTACTS
import com.atiurin.ultron.core.compose.createUltronComposeRule
import com.atiurin.ultron.core.compose.list.composeList
import org.junit.Rule
import org.junit.Test

class ComposeListWithPositionTestTagTest {
    @get:Rule
    val composeRule = createUltronComposeRule<ComposeListWithPositionTestTagActivity>()
    val list = composeList(hasContentDescription(contactsListContentDesc), false)

    @Test
    fun itemOutOfVisibleScope() {
        val index = 20
        val contact = CONTACTS[index]
        list.item(hasTestTag(getContactItemTestTagByPosition(index)))
            .assertIsDisplayed()
            .assertTextContains(contact.name)
    }

    @Test
    fun lastVisibleItem() {
        val count = list.getVisibleItemsCount() - 1
        val contact = CONTACTS[count]
        list.lastVisibleItem()
            .assertIsDisplayed()
            .assertTextContains(contact.name)
            .assertTextContains(contact.status)
    }
}