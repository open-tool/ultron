package com.atiurin.sampleapp.screens.ultron

import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.data.entities.Contact
import com.atiurin.ultron.allure.step.step
import com.atiurin.ultron.core.espresso.recyclerview.UltronRecyclerViewItem
import com.atiurin.ultron.core.espresso.recyclerview.withRecyclerView
import com.atiurin.ultron.extensions.hasText
import com.atiurin.ultron.extensions.isDisplayed
import com.atiurin.ultron.page.Screen

object UltronRecyclerViewScreen : Screen<UltronRecyclerViewScreen>() {
    private val recycler = withRecyclerView(R.id.recycler_friends)
    private fun getContactItem(contact: Contact): ContactItem {
        return recycler.getItem(hasDescendant(withText(contact.name)))
    }

    fun openFirstChat(): UltronEspressoChatScreen {
        return step("Open 1st chat") {
            recycler.firstItem().click()
            UltronEspressoChatScreen.assertScreenDisplayed()
        }
    }

    fun assertItemUi(contact: Contact) = apply {
        step("Assert item ui for $contact") {
            getContactItem(contact).apply {
                name.hasText(contact.name).isDisplayed()
                status.hasText(contact.status).isDisplayed()
                avatar.isDisplayed()
            }
        }
    }

    fun openContactChat(contact: Contact): UltronEspressoChatScreen {
        return step("Open chat with $contact") {
            getContactItem(contact).click()
            UltronEspressoChatScreen.assertScreenDisplayed()
        }
    }

    fun assertEmptyList() {
        recycler.waitItemsLoaded().assertEmpty()
    }

    private class ContactItem : UltronRecyclerViewItem() {
        val name by lazy { getChild(withId(R.id.tv_name)) }
        val status by lazy { getChild(withId(R.id.tv_status)) }
        val avatar by lazy { getChild(withId(R.id.avatar)) }
    }
}