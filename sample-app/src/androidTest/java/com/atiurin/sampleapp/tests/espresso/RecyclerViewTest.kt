package com.atiurin.sampleapp.tests.espresso

import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.atiurin.sampleapp.MyApplication
import com.atiurin.sampleapp.activity.MainActivity
import com.atiurin.sampleapp.data.repositories.CONTACTS
import com.atiurin.sampleapp.data.repositories.ContactRepositoty
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.FriendsListPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.extensions.assertMatches
import com.atiurin.ultron.extensions.hasText
import com.atiurin.ultron.extensions.isDisplayed
import com.atiurin.ultron.extensions.withTimeout
import com.atiurin.ultron.recyclerview.withRecyclerView
import com.atiurin.ultron.testlifecycle.setupteardown.SetUp
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpTearDownRule
import com.atiurin.ultron.testlifecycle.setupteardown.TearDown
import org.junit.Assert
import org.junit.Test

class RecyclerViewTest : BaseTest() {
    companion object {
        const val CUSTOM_TIMEOUT = "CUSTOM_TIMEOUT"
    }

    private val setUpTearDownRule = SetUpTearDownRule()
        .addSetUp(CUSTOM_TIMEOUT) {
            MyApplication.CONTACTS_LOADING_TIMEOUT_MS = 6_000L
        }.addTearDown(CUSTOM_TIMEOUT) {
            MyApplication.CONTACTS_LOADING_TIMEOUT_MS = 500L
        }

    init {
        ruleSequence.add(setUpTearDownRule).addLast(ActivityTestRule(MainActivity::class.java))
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
    fun testListSize() {
        page.friendsRecycler.atPosition(0)
            .assertMatches(hasDescendant(withText(ContactRepositoty.getFirst().name)))
        val actualSize = page.friendsRecycler.getSize()
        Assert.assertEquals(CONTACTS.size, actualSize)
    }

    @Test
    fun getRecyclerViewTest() {
        val view = page.friendsRecycler.getRecyclerViewList()
        Assert.assertNotNull(view)
        Assert.assertEquals(Visibility.VISIBLE.value, view?.visibility)
    }

    @Test
    fun scrollToItemTest() {
        val contact = CONTACTS[CONTACTS.size - 1]
        val item = page.getListItem(contact.name)
        item.isDisplayed()
        item.name.hasText(contact.name)
        item.status.hasText(contact.status)
    }

    @Test
    fun wrongChild() {
        val contact = CONTACTS[CONTACTS.size - 1]
        val wrongContact = CONTACTS[CONTACTS.size - 2]
        val item = page.getListItem(contact.name)
        item.isDisplayed()
        AssertUtils.assertException { item.name.withTimeout(100).hasText(wrongContact.name) }
    }

    @Test
    fun recyclerViewItemClassTest() {
        val contact = CONTACTS[1]
        with(page.getListItem(contact.name)) {
            this.isDisplayed().isClickable()
            this.name.isDisplayed().isEnabled().hasText(contact.name)
            this.status.isDisplayed().isEnabled().hasText(contact.status)
        }
    }

    @Test
    fun scrollToLastItem() {
        page.friendsRecycler.item(CONTACTS.size - 1).isDisplayed()
    }

    @Test
    fun scrollToLastWithMatcher() {
        page.friendsRecycler.item(hasDescendant(withText("Friend14"))).isDisplayed()
    }

    @Test
    fun getNotExistedRecyclerItemWithPosition() {
        AssertUtils.assertException {
            page.friendsRecycler.item(100).withTimeout(100).isDisplayed()
        }
    }

    @Test
    fun assertListSize() {
        page.friendsRecycler.assertSize(CONTACTS.size)
    }

    @Test
    fun recyclerView_notExist() {
        AssertUtils.assertException {
            withRecyclerView(withText("Not existed recycler")).withTimeout(100).isDisplayed()
        }
    }

    @Test
    fun item_notExist() {
        AssertUtils.assertException {
            page.friendsRecycler.item(withText("Not existed item"), false).withTimeout(100)
                .isDisplayed()
        }
    }

    @Test
    @SetUp(CUSTOM_TIMEOUT)
    @TearDown(CUSTOM_TIMEOUT)
    fun defaultTimeoutOnItemWaiting() {
        AssertUtils.assertException { page.friendsRecycler.item(10).isDisplayed() }
    }

    @Test
    @SetUp(CUSTOM_TIMEOUT)
    @TearDown(CUSTOM_TIMEOUT)
    fun customTimeoutOnItemWaiting() {
        page.friendsRecycler.item(10, scrollTimeoutMs = 8000).isDisplayed()
    }

    @Test
    @SetUp(CUSTOM_TIMEOUT)
    @TearDown(CUSTOM_TIMEOUT)
    fun item_autoScroll_False_item_NotLoaded() {
        AssertUtils.assertException { page.friendsRecycler.item(10, false).isDisplayed() }
    }

    @Test
    @SetUp(CUSTOM_TIMEOUT)
    @TearDown(CUSTOM_TIMEOUT)
    fun item_autoScroll_False_scroll_Force() {
        page.friendsRecycler.item(10, false, 8000).scrollToItem().isDisplayed()
    }

    @Test
    fun itemMatcher_autoScroll_false_() {
        AssertUtils.assertException {
            page.friendsRecycler.item(
                hasDescendant(withText("Friend14")),
                false
            ).isDisplayed()
        }
    }

    @Test
    fun itemMatcher_autoScroll_false_scroll_force() {
        page.friendsRecycler.item(hasDescendant(withText("Friend14")), false).scrollToItem()
            .isDisplayed()
    }

    @Test
    @SetUp(CUSTOM_TIMEOUT)
    @TearDown(CUSTOM_TIMEOUT)
    fun itemMatcher_autoScroll_false_custom_timeout() {
        AssertUtils.assertException {
            page.friendsRecycler.item(
                hasDescendant(withText("Friend14")),
                false
            ).scrollToItem().isDisplayed()
        }
    }

    @Test
    @SetUp(CUSTOM_TIMEOUT)
    @TearDown(CUSTOM_TIMEOUT)
    fun itemMatcher_autoScroll_true_custom_timeout() {
        page.friendsRecycler.item(hasDescendant(withText("Friend14")), scrollTimeoutMs = 8000)
            .isDisplayed()
    }
}
