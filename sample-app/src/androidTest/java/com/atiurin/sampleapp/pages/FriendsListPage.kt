package com.atiurin.sampleapp.pages

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.*
import com.atiurin.ultron.extensions.hasText
import com.atiurin.ultron.extensions.isDisplayed
import com.atiurin.ultron.page.Page
import com.atiurin.ultron.recyclerview.RecyclerViewItem
import com.atiurin.ultron.recyclerview.withRecyclerView
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.framework.step
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Assert

object FriendsListPage : Page<FriendsListPage>() {
    val friendsRecycler = withId(R.id.recycler_friends)

    fun assertPageDisplayed() = apply {
        step("Assert friends list page displayed") {
            friendsRecycler.isDisplayed()
        }
    }

    class FriendRecyclerItem(list: Matcher<View>, item: Matcher<View>, autoScroll: Boolean = true) :
        RecyclerViewItem(list, item, autoScroll) {
        val name = getChildMatcher(withId(R.id.tv_name))
        val status = getChildMatcher(withId(R.id.tv_status))
    }

    fun getFriendsListItem(title: String): FriendRecyclerItem {
        return FriendRecyclerItem(
            withId(R.id.recycler_friends),
            hasDescendant(allOf(withId(R.id.tv_name), withText(title)))
        )
    }

    fun openChat(name: String) = apply {
        step("Open chat with friend '$name'") {
            this.getFriendsListItem(name).click()
            ChatPage { assertPageDisplayed() }
        }
    }

    fun assertStatus(name: String, status: String) = apply {
        step("Assert friend with name '$name' has status '$status'") {
            this.getFriendsListItem(name).status.hasText(status)
        }
    }

    fun assertName(nameText: String) = apply {
        step("Assert friend name '$nameText' in the right place") {
            this.getFriendsListItem(nameText).name.hasText(nameText)
        }
    }

    fun assertFriendsListSize(size: Int) {
        Assert.assertEquals(size, withRecyclerView(friendsRecycler).getSize())
    }
}
