package com.atiurin.sampleapp.pages

import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import com.atiurin.sampleapp.compose.contactNameTestTag
import com.atiurin.sampleapp.compose.contactStatusTestTag
import com.atiurin.sampleapp.compose.contactsListContentDesc
import com.atiurin.sampleapp.compose.getContactItemTestTagById
import com.atiurin.sampleapp.data.entities.Contact
import com.atiurin.ultron.core.compose.list.UltronComposeListItem
import com.atiurin.ultron.core.compose.list.composeList
import com.atiurin.ultron.page.Page

object ComposeListPage : Page<ComposeListPage>() {
    val lazyList = composeList(hasContentDescription(contactsListContentDesc))

    fun assertContactStatus(contact: Contact) = apply {
        getContactItemById(contact).status.assertTextEquals(contact.status)
    }
    fun getFirstVisibleItem(): ComposeFriendListItem = lazyList.getFirstVisibleItem()
    fun getItemByIndex(index: Int): ComposeFriendListItem = lazyList.getVisibleItem(index)
    fun getContactItemById(contact: Contact): ComposeFriendListItem = lazyList.getItem(hasTestTag(getContactItemTestTagById(contact)))
    fun getContactItemByName(contact: Contact): ComposeFriendListItem = lazyList.getItem(hasAnyDescendant(hasText(contact.name) and hasTestTag(contactNameTestTag)))
    class ComposeFriendListItem : UltronComposeListItem(){
        val name by lazy { getChild(hasTestTag(contactNameTestTag)) }
        val status by lazy { getChild(hasTestTag(contactStatusTestTag)) }
    }
}