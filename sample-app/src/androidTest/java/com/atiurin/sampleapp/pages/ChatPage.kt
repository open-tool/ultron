package com.atiurin.sampleapp.pages

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.framework.*
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.extensions.hasText
import com.atiurin.ultron.extensions.isDisplayed
import com.atiurin.ultron.extensions.typeText
import com.atiurin.ultron.page.Page
import com.atiurin.ultron.recyclerview.RecyclerViewItem
import com.atiurin.ultron.recyclerview.withRecyclerView
import com.atiurin.sampleapp.framework.utils.EspressoUtil
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Assert

object ChatPage : Page<ChatPage>() {
    fun assertPageDisplayed() = apply {
        step("Assert friends list page displayed") {
            messagesList.isDisplayed()
        }
    }

    val messagesList = withId(R.id.messages_list)
    val clearHistoryBtn = withText("Clear history")
    val inputMessageText = withId(R.id.message_input_text)
    val sendMessageBtn = withId(R.id.send_button)

    fun getMessageListItem(text: String): ChatRecyclerItem {
        return ChatRecyclerItem(
            messagesList,
            ViewMatchers.hasDescendant(
                allOf(
                    withId(R.id.message_text),
                    withText(text)
                )
            )
        )
    }

    private fun getListItemAtPosition(position: Int): ChatRecyclerItem {
        return ChatRecyclerItem(messagesList, position)
    }

    fun getTitle(title: String): Matcher<View> {
        return allOf(withId(R.id.toolbar_title), withText(title))
    }

    class ChatRecyclerItem : RecyclerViewItem {
        constructor(list: Matcher<View>, item: Matcher<View>) : super(list, item)
        constructor(list: Matcher<View>, position: Int) : super(list, position)

        val text = getChildMatcher(withId(R.id.message_text))
    }

    fun sendMessage(text: String) = apply {
        step("Send message with text '$text") {
            inputMessageText.typeText(text)
            sendMessageBtn.click()
            this.getMessageListItem(text).text
                .isDisplayed()
                .hasText(text)
        }
    }

    fun assertMessageTextAtPosition(position: Int, text: String) = apply {
        this.getListItemAtPosition(position).text.isDisplayed().hasText(text)
    }

    fun clearHistory() = apply {
        step("Clear chat history") {
            EspressoUtil.openOptionsMenu()
            clearHistoryBtn.click()
            Assert.assertEquals(0, withRecyclerView(messagesList).getSize())
        }
    }

    fun assertMessageDisplayed(text: String) {
        getMessageListItem(text).text
            .isDisplayed()
            .hasText(text)
    }
}