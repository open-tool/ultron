package com.atiurin.sampleapp.tests.compose

import androidx.compose.ui.test.hasTestTag
import com.atiurin.sampleapp.activity.ComposeListActivity
import com.atiurin.sampleapp.compose.contactNameTestTag
import com.atiurin.sampleapp.compose.contactsListTestTag
import com.atiurin.sampleapp.data.repositories.CONTACTS
import com.atiurin.ultron.core.compose.createUltronComposeRule
import com.atiurin.ultron.core.compose.operation.UltronComposeCollectionInteraction.Companion.allNodes
import org.junit.Rule
import org.junit.Test

class CollectionInteractionTest {
    @get:Rule
    val composeRule = createUltronComposeRule<ComposeListActivity>()
    val list = hasTestTag(contactsListTestTag)
    @Test
    fun allNodes_getByIndex(){
        val index = 4
        val contact = CONTACTS[index]
        allNodes(hasTestTag(contactNameTestTag), true).get(index).assertTextContains(contact.name)
    }
}