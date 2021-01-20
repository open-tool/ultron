package com.atiurin.sampleapp.tests.espresso

import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.atiurin.ultron.recyclerview.withRecyclerView
import com.atiurin.sampleapp.activity.MainActivity
import com.atiurin.sampleapp.data.repositories.CONTACTS
import com.atiurin.sampleapp.data.repositories.ContactRepositoty
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.FriendsListPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.extensions.assertMatches
import com.atiurin.ultron.extensions.hasText
import com.atiurin.ultron.extensions.isDisplayed
import com.atiurin.ultron.recyclerview.UltronRecyclerViewItem
import org.junit.Assert
import org.junit.Test

class RecyclerViewTest : BaseTest() {
    init {
        ruleSequence.addLast(ActivityTestRule(MainActivity::class.java))
    }
    val page = FriendsListPage

    @Test
    fun testDisplayedItemPositions() {
        for (index in 0..3) {
            page.friendsRecycler.atPosition(index)
                .assertMatches(hasDescendant(withText(CONTACTS[index].name)))
                .assertMatches(hasDescendant(withText(CONTACTS[index].status)))
                .isDisplayed()
        }
    }

    @Test
    fun testListSize(){
        page.friendsRecycler.atPosition(0)
            .assertMatches(hasDescendant(withText(ContactRepositoty.getFirst().name)))
        val actualSize = page.friendsRecycler.getSize()
        Assert.assertEquals(CONTACTS.size, actualSize)
    }

    @Test
    fun getRecyclerViewTest(){
        val view = page.friendsRecycler.getRecyclerViewList()
        Assert.assertNotNull(view)
        Assert.assertEquals(Visibility.VISIBLE.value, view?.visibility)
    }

    @Test
    fun scrollToItemTest(){
        val contact = CONTACTS[CONTACTS.size - 1]
        val item = page.getListItem(contact.name)
        item.isDisplayed()
        item.name.hasText(contact.name)
        item.status.hasText(contact.status)
    }

    @Test
    fun recyclerViewItemClassTest(){
        val contact = CONTACTS[1]
        with(page.getListItem(contact.name)){
            this.isDisplayed().isClickable()
            this.name.isDisplayed().isEnabled().hasText(contact.name)
            this.status.isDisplayed().isEnabled().hasText(contact.status)
        }
    }

    @Test
    fun getNotExistedRecyclerItem(){
        AssertUtils.assertException { page.friendsRecycler.getSimpleItem(100).withTimeout(100).isDisplayed() }
    }

    @Test
    fun assertListSize(){
        page.friendsRecycler.assertSize(CONTACTS.size)
    }
}
