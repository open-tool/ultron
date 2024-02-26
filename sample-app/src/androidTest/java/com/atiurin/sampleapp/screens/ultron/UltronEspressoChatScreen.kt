package com.atiurin.sampleapp.screens.ultron

import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.atiurin.sampleapp.R
import com.atiurin.ultron.allure.step.step
import com.atiurin.ultron.core.espresso.recyclerview.withRecyclerView
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.extensions.isDisplayed
import com.atiurin.ultron.extensions.replaceText
import io.github.kakaocup.kakao.screen.Screen

object UltronEspressoChatScreen : Screen<UltronEspressoChatScreen>() {
    private val toolbarTitle = withId(R.id.toolbar_title)
    private val messageText = withId(R.id.message_input_text)
    private val sendButton = withId(R.id.send_button)
    private val messagesList = withRecyclerView(R.id.messages_list)
    private fun getMessageItem(text: String) = messagesList.item(hasDescendant(withText(text)))

    fun assertScreenDisplayed() = apply {
        step("Assert chat screen displayed") {
            toolbarTitle.isDisplayed()
        }
    }

    fun sendMessage(text: String) = apply {
        step("Send message '$text'") {
            messageText.replaceText(text)
            sendButton.click()
            getMessageItem(text).isDisplayed()
        }
    }
}