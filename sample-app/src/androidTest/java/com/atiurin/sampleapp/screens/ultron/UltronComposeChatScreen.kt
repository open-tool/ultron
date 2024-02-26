package com.atiurin.sampleapp.screens.ultron

import androidx.compose.ui.test.hasTestTag
import com.atiurin.sampleapp.activity.ComposeChatActivity.Companion.chatNameTestTag
import com.atiurin.sampleapp.activity.ComposeChatActivity.Companion.chatStatusTestTag
import com.atiurin.sampleapp.data.entities.Contact
import com.atiurin.ultron.allure.step.step
import com.atiurin.ultron.extensions.assertIsDisplayed
import com.atiurin.ultron.extensions.withTimeout
import com.atiurin.ultron.page.Screen

object UltronComposeChatScreen : Screen<UltronComposeChatScreen>() {
    private val name = hasTestTag(chatNameTestTag)
    private val status = hasTestTag(chatStatusTestTag).withTimeout(1000)

    fun assertScreenDisplayed() = apply {
        step("Assert chat screen is displayed") {
            name.assertIsDisplayed()
        }
    }

    fun assertScreenUI(contact: Contact) = apply {
        step("Assert chat screen ui for $contact") {
            name.assertIsDisplayed().assertTextContains(contact.name)
            status.assertIsDisplayed().assertTextContains(contact.status)
        }
    }
}