package com.atiurin.sampleapp.pages

import androidx.test.espresso.matcher.ViewMatchers.*
import com.atiurin.ultron.page.Page
import com.atiurin.ultron.recyclerview.UltronRecyclerViewItem
import com.atiurin.ultron.recyclerview.withRecyclerView
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.framework.step
import com.atiurin.ultron.extensions.hasText
import com.atiurin.ultron.extensions.isDisplayed
import org.hamcrest.Matchers.allOf
import org.junit.Assert

object FriendsListPage : Page<FriendsListPage>() {
    val friendsRecycler = withRecyclerView(R.id.recycler_friends)

    fun assertPageDisplayed() = apply {
        step("Assert friends list page displayed") {
            friendsRecycler.recyclerViewMatcher.isDisplayed()
        }
    }

    class FriendRecyclerItem : UltronRecyclerViewItem() {
        val name by lazy { getChild(withId(R.id.tv_name)) }
        val status by lazy { getChild(withId(R.id.tv_status)) }
    }

    fun getListItem(contactName: String): FriendRecyclerItem {
        return friendsRecycler.getItem(hasDescendant(allOf(withId(R.id.tv_name), withText(contactName))))
    }

    fun openChat(name: String) = apply {
        step("Open chat with friend '$name'") {
            this.getListItem(name).click()
            ChatPage { assertPageDisplayed() }
        }
    }

    fun assertStatus(name: String, status: String) = apply {
        step("Assert friend with name '$name' has status '$status'") {
            getListItem(name).status.hasText(status).isDisplayed()
        }
    }

    fun assertName(nameText: String) = apply {
        step("Assert friend name '$nameText' in the right place") {
            getListItem(nameText).name.hasText(nameText).isDisplayed()
        }
    }

    fun assertFriendsListSize(size: Int) {
        Assert.assertEquals(size, friendsRecycler.getSize())
    }
}
