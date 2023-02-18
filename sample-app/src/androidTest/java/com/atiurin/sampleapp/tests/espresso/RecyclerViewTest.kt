package com.atiurin.sampleapp.tests.espresso

import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.atiurin.sampleapp.MyApplication
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.MainActivity
import com.atiurin.sampleapp.data.repositories.CONTACTS
import com.atiurin.sampleapp.data.repositories.ContactRepositoty
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.ChatPage
import com.atiurin.sampleapp.pages.FriendsListPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.core.espresso.recyclerview.withRecyclerView
import com.atiurin.ultron.extensions.*
import com.atiurin.ultron.testlifecycle.setupteardown.SetUp
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import com.atiurin.ultron.testlifecycle.setupteardown.TearDown
import com.atiurin.ultron.testlifecycle.setupteardown.TearDownRule
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.junit.Assert
import org.junit.Test

class RecyclerViewTest : BaseTest() {
    companion object {
        const val CUSTOM_TIMEOUT = "CUSTOM_TIMEOUT"
        val notExistItemMatcher = hasDescendant(withText("zxcbmzxmbc"))
    }

    private val setUpRule = SetUpRule().add(CUSTOM_TIMEOUT) {
        MyApplication.CONTACTS_LOADING_TIMEOUT_MS = 6_000L
    }
    private val tearDownRule = TearDownRule().add(CUSTOM_TIMEOUT) {
        MyApplication.CONTACTS_LOADING_TIMEOUT_MS = 500L
    }

    init {
        ruleSequence.add(setUpRule, tearDownRule).addLast(ActivityTestRule(MainActivity::class.java))
    }

    val page = FriendsListPage

    @Test
    fun childTest() {
        withRecyclerView(R.id.recycler_friends).item(0).getChild(withId(R.id.tv_status)).hasText(ContactRepositoty.getFirst().status)
    }

    @Test
    fun testDisplayedItemPositions() {
        for (index in 0..3) {
            page.recycler.item(index).assertMatches(hasDescendant(withText(CONTACTS[index].name)))
                .assertMatches(hasDescendant(withText(CONTACTS[index].status))).isDisplayed()
        }
    }

    @Test
    fun getRecyclerViewTest() {
        val view = page.recycler.getRecyclerViewList()
        Assert.assertNotNull(view)
        Assert.assertEquals(Visibility.VISIBLE.value, view.visibility)
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
        withRecyclerView(R.id.recycler_friends).item(CONTACTS.size - 1).isDisplayed()
    }

    @Test
    fun scrollToLastWithMatcher() {
        withRecyclerView(R.id.recycler_friends).item(hasDescendant(withText("Friend14"))).isDisplayed()
    }

    @Test
    fun getNotExistedRecyclerItemWithPosition() {
        AssertUtils.assertException {
            page.recycler.item(100).withTimeout(100).isDisplayed()
        }
    }

    @Test
    fun assertListSize() {
        page.recycler.assertSize(CONTACTS.size)
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
            page.recycler.item(withText("Not existed item"), false).withTimeout(100).isDisplayed()
        }
    }

    @Test
    @SetUp(CUSTOM_TIMEOUT)
    @TearDown(CUSTOM_TIMEOUT)
    fun defaultTimeoutOnItemWaiting() {
        AssertUtils.assertException { page.recycler.item(10).isDisplayed() }
    }

    @Test
    @SetUp(CUSTOM_TIMEOUT)
    @TearDown(CUSTOM_TIMEOUT)
    fun customTimeoutOnItemWaiting() {
        withRecyclerView(R.id.recycler_friends, 8000).item(10).isDisplayed()
    }

    @Test
    @SetUp(CUSTOM_TIMEOUT)
    @TearDown(CUSTOM_TIMEOUT)
    fun item_autoScroll_False_item_NotLoaded() {
        AssertUtils.assertException { page.recycler.item(10, false).isDisplayed() }
    }

    @Test
    @SetUp(CUSTOM_TIMEOUT)
    @TearDown(CUSTOM_TIMEOUT)
    fun item_autoScroll_False_scroll_Force() {
        withRecyclerView(R.id.recycler_friends, 8_000).item(10, false).scrollToItem().isDisplayed()
    }

    @Test
    fun itemMatcher_autoScroll_false_itemNotDisplayed() {
        AssertUtils.assertException {
            page.recycler.item(
                hasDescendant(withText("Friend14")), false
            ).isDisplayed()
        }
    }

    @Test
    fun itemMatcher_autoScroll_false_scroll_force() {
        page.recycler.item(hasDescendant(withText("Friend14")), false).scrollToItem().isDisplayed()
    }

    @Test
    @SetUp(CUSTOM_TIMEOUT)
    @TearDown(CUSTOM_TIMEOUT)
    fun itemMatcher_autoScroll_false() {
        AssertUtils.assertException {
            page.recycler.item(
                hasDescendant(withText("Friend14")), false
            ).scrollToItem().isDisplayed()
        }
    }

    @Test
    @SetUp(CUSTOM_TIMEOUT)
    @TearDown(CUSTOM_TIMEOUT)
    fun itemMatcher_autoScroll_true_custom_timeout() {
        withRecyclerView(R.id.recycler_friends, 10_000).item(hasDescendant(withText("Friend14"))).isDisplayed()
    }

    @Test
    fun getViewHolder() {
        val position = CONTACTS.size - 1
        Assert.assertEquals(
            position,
            page.recycler.item(hasDescendant(withText(CONTACTS[position].name)))
                .getViewHolder()
                ?.layoutPosition
        )
    }

    @Test
    fun transferFromGenericToSubclass() {
        val position = 5
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(position)
            .status.hasText(CONTACTS[position].status).isDisplayed()
    }

    @Test
    fun getViewHolderList() {
        page.recycler.waitItemsLoaded()
        Assert.assertTrue(page.recycler.getViewHolderList(hasDescendant(withId(R.id.tv_name))).isNotEmpty())
    }

    @Test
    fun waitLoaded_ofAlreadyLoadedList() {
        page.recycler.waitItemsLoaded()
        page.recycler.waitItemsLoaded()
    }

    @Test
    fun waitLoaded_allItemsLoaded() {
        val count = page.recycler.waitItemsLoaded().getSize()
        Assert.assertEquals(CONTACTS.size, count)
    }

    @Test
    fun getLastItem() {
        page.recycler.lastItem().isDisplayed().click()
    }

    @Test
    fun getLastItemWithCustomType() {
        page.recycler.getLastItem<FriendsListPage.FriendRecyclerItem>().name.hasText(ContactRepositoty.getLast().name)
    }

    @Test
    fun perfScroll() {
        page.recycler.apply {
            for (i in 0..10) {
                lastItem().isDisplayed()
                firstItem().isDisplayed()
            }
        }
    }

    @Test
    fun getViewHolderAtPosition_outOfVisibleList() {
        Assert.assertNull(page.recycler.waitItemsLoaded().getViewHolderAtPosition(15))
    }

    @Test
    fun getViewHolderAtPosition_inVisibleList() {
        Assert.assertNotNull(page.recycler.waitItemsLoaded().getViewHolderAtPosition(2))
    }

    @Test
    fun getItemsAdapterPositionList() {
        page.recycler.waitItemsLoaded()
        val matcher = hasDescendant(allOf(withId(R.id.tv_name), withText(containsString("Friend"))))
        Assert.assertEquals(0, page.recycler.getViewHolderList(matcher).size)
        Assert.assertTrue(page.recycler.getItemsAdapterPositionList(matcher).isNotEmpty())
    }

    @Test
    fun firstItemMatched_existItem() {
        val pattern = "Friend"
        val expectedContacts = CONTACTS.filter { it.name.contains(pattern) }
        val matcher = hasDescendant(allOf(withId(R.id.tv_name), withText(containsString(pattern))))

        page.recycler.firstItemMatched(matcher).isDisplayed().click()
        ChatPage.assertToolbarTitle(expectedContacts.first().name)
    }

    @Test
    fun itemMatched_existItem() {
        val pattern = "Friend"
        val expectedContacts = CONTACTS.filter { it.name.contains(pattern) }
        val matcher = hasDescendant(allOf(withId(R.id.tv_name), withText(containsString(pattern))))

        page.recycler.itemMatched(matcher, 1).isDisplayed().click()
        ChatPage.assertToolbarTitle(expectedContacts[1].name)
    }

    @Test
    fun itemMatched_notExistItem() {
        val matcher = hasDescendant(allOf(withId(R.id.tv_name), withText(containsString("Friend"))))
        AssertUtils.assertException { page.recycler.withTimeout(1000).itemMatched(matcher, 99) }
    }

    @Test
    fun lastItemMatched_existItem() {
        val pattern = "Friend"
        val expectedContacts = CONTACTS.filter { it.name.contains(pattern) }
        val matcher = hasDescendant(allOf(withId(R.id.tv_name), withText(containsString(pattern))))

        page.recycler.itemMatched(matcher, expectedContacts.lastIndex).isDisplayed().click()
        ChatPage.assertToolbarTitle(expectedContacts.last().name)
    }

    @Test
    fun getFirstItemMatched_existItem() {
        val pattern = "Friend"
        val expectedContact = CONTACTS.filter { it.name.contains(pattern) }.first()
        val matcher = hasDescendant(allOf(withId(R.id.tv_name), withText(containsString(pattern))))

        page.recycler.getFirstItemMatched<FriendsListPage.FriendRecyclerItem>(matcher).apply {
            name.isDisplayed().hasText(expectedContact.name)
            status.hasText(expectedContact.status)
            click()
        }
        ChatPage.assertToolbarTitle(expectedContact.name)
    }

    @Test
    fun getItemMatched_existItem() {
        val pattern = "Friend"
        val expectedContact = CONTACTS.filter { it.name.contains(pattern) }[1]
        val matcher = hasDescendant(allOf(withId(R.id.tv_name), withText(containsString(pattern))))

        page.recycler.getItemMatched<FriendsListPage.FriendRecyclerItem>(matcher, 1).apply {
            name.isDisplayed().hasText(expectedContact.name)
            status.hasText(expectedContact.status)
            click()
        }
        ChatPage.assertToolbarTitle(expectedContact.name)
    }

    @Test
    fun getItemMatched_notExistItem() {
        val matcher = hasDescendant(allOf(withId(R.id.tv_name), withText(containsString("Friend"))))
        AssertUtils.assertException { page.recycler.withTimeout(1000).getItemMatched<FriendsListPage.FriendRecyclerItem>(matcher, 99) }
    }

    @Test
    fun getLastItemMatched_existItem() {
        val pattern = "Friend"
        val expectedContacts = CONTACTS.filter { it.name.contains(pattern) }
        val matcher = hasDescendant(allOf(withId(R.id.tv_name), withText(containsString(pattern))))

        page.recycler.getItemMatched<FriendsListPage.FriendRecyclerItem>(matcher, expectedContacts.lastIndex).apply {
            name.isDisplayed().hasText(expectedContacts.last().name)
            status.hasText(expectedContacts.last().status)
            click()
        }
        ChatPage.assertToolbarTitle(expectedContacts.last().name)
    }

    @Test
    fun assertItemNotExist_notExistItem() {
        page.recycler.assertItemNotExist(notExistItemMatcher, 2000)
    }

    @Test
    fun assertItemNotExist_existItem() {
        val matcher = hasDescendant(allOf(withId(R.id.tv_name), withText(containsString(CONTACTS.first().name))))
        AssertUtils.assertException { page.recycler.assertItemNotExist(matcher, 2000) }
    }

    @Test
    fun assertItemNotExistImmediately_notExistItem() {
        page.recycler.assertItemNotExistImmediately(notExistItemMatcher, 2000)
    }

    @Test
    fun assertItemNotExistImmediately_existItem() {
        page.recycler.waitItemsLoaded()
        val matcher = hasDescendant(allOf(withId(R.id.tv_name), withText(containsString(CONTACTS.first().name))))
        AssertUtils.assertException { page.recycler.assertItemNotExistImmediately(matcher, 2000) }
    }

    @Test
    fun assertItemOutOfLimitNotFound() {
        val rv = withRecyclerView(R.id.recycler_friends, itemSearchLimit = 2)
        AssertUtils.assertException {
            rv.withTimeout(2000L)
                .item(hasDescendant(allOf(withId(R.id.tv_name), withText(containsString(CONTACTS[10].name)))))
                .click()
        }
    }

    @Test
    fun assertItemInLimitFound() {
        val rv = withRecyclerView(R.id.recycler_friends, itemSearchLimit = 10)
        rv.withTimeout(2000L)
            .item(hasDescendant(allOf(withId(R.id.tv_name), withText(containsString(CONTACTS[2].name)))))
            .isDisplayed()
    }

    @Test
    fun createHandlerFromUiTest() {
        page.recycler.getItemAdapterPositionAtIndex(hasDescendant(allOf(withId(R.id.tv_name), withText(containsString(CONTACTS.last().name)))), 0)
    }

    //item+offset
    @Test
    fun item_scrollOffsetInItemCountRange_MatcherItem() {
        val target = 5
        val offset = 10
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recycler.item(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recycler.item(page.getItemMatcher(offsetContact), autoScroll = false).isDisplayed().click()
    }

    @Test
    fun item_scrollOffsetInItemCountRangeBothAreVisible_MatcherItem() {
        val target = 8
        val offset = 2
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recycler.item(page.getItemMatcher(targetContact), scrollOffset = offset).isDisplayed()
        page.recycler.item(page.getItemMatcher(offsetContact), autoScroll = false).isDisplayed()
    }

    @Test
    fun item_scrollOffsetOutOfItemCountRange_MatcherItem() {
        val target = 5
        val offset = CONTACTS.size
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS.last()
        page.recycler.item(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recycler.item(page.getItemMatcher(offsetContact), autoScroll = false).isDisplayed().click()
    }

    @Test
    fun item_scrollOffsetLessThenZero_MatcherItem() {
        val target = 8
        val offset = -18
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS.first()
        page.recycler.item(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recycler.item(page.getItemMatcher(offsetContact), autoScroll = false).isDisplayed().click()
    }

    @Test
    fun item_scrollOffsetInItemCountRange_positionItem() {
        val target = 2
        val offset = 12
        val offsetContact = CONTACTS[target + offset]
        page.recycler.item(target, scrollOffset = offset)
        page.recycler.item(target + offset, autoScroll = false).isDisplayed()
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(target + offset, autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun item_scrollOffsetInItemCountRangeBothAreVisible_positionItem() {
        val target = 8
        val offset = 2
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recycler.item(target, scrollOffset = offset).isDisplayed()
        page.recycler.item(target + offset, autoScroll = false).isDisplayed()
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(target, autoScroll = false).name.hasText(targetContact.name).isDisplayed()
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(target + offset, autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun item_scrollOffsetOutOfItemCountRange_positionItem() {
        val target = 5
        val offset = CONTACTS.size
        val offsetContact = CONTACTS.last()
        page.recycler.item(target, scrollOffset = offset)
        page.recycler.lastItem(autoScroll = false).isDisplayed()
        page.recycler.getLastItem<FriendsListPage.FriendRecyclerItem>(autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun item_scrollOffsetLessThenZero_positionItem() {
        val target = 10
        val offset = -18
        val offsetContact = CONTACTS.first()
        page.recycler.item(target, scrollOffset = offset)
        page.recycler.getFirstItem<FriendsListPage.FriendRecyclerItem>(autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    //itemMatched+offset
    @Test
    fun itemMatched_scrollOffsetInItemCountRange_MatcherItem() {
        val target = 5
        val offset = 10
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recycler.itemMatched(page.getItemMatcher(targetContact), 0, scrollOffset = offset)
        page.recycler.item(page.getItemMatcher(offsetContact), autoScroll = false).isDisplayed().click()
    }

    @Test
    fun itemMatched_scrollOffsetInItemCountRangeBothAreVisible_MatcherItem() {
        val target = 8
        val offset = 2
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recycler.itemMatched(page.getItemMatcher(targetContact), 0, scrollOffset = offset).isDisplayed()
        page.recycler.item(page.getItemMatcher(offsetContact), autoScroll = false).isDisplayed()
    }

    @Test
    fun itemMatched_scrollOffsetOutOfItemCountRange_MatcherItem() {
        val target = 5
        val offset = CONTACTS.size
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS.last()
        page.recycler.itemMatched(page.getItemMatcher(targetContact), 0, scrollOffset = offset)
        page.recycler.item(page.getItemMatcher(offsetContact), autoScroll = false).isDisplayed().click()
    }

    //firstItemMatched+offset
    @Test
    fun firstItemMatched_scrollOffsetInItemCountRange_MatcherItem() {
        val target = 5
        val offset = 10
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recycler.firstItemMatched(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recycler.item(page.getItemMatcher(offsetContact), autoScroll = false).isDisplayed().click()
    }

    //lastItemMatched+offset
    @Test
    fun lastItemMatched_scrollOffsetInItemCountRange_MatcherItem() {
        val target = 5
        val offset = 10
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recycler.lastItemMatched(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recycler.item(page.getItemMatcher(offsetContact), autoScroll = false).isDisplayed().click()
    }

    //getItem+offset
    @Test
    fun getItem_scrollOffsetInItemCountRange_MatcherItem() {
        val target = 5
        val offset = 10
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(page.getItemMatcher(offsetContact), autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun getItem_scrollOffsetInItemCountRangeBothAreVisible_MatcherItem() {
        val target = 8
        val offset = 2
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(page.getItemMatcher(offsetContact), autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun getItem_scrollOffsetOutOfItemCountRange_MatcherItem() {
        val target = 5
        val offset = CONTACTS.size
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS.last()
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(page.getItemMatcher(offsetContact), autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun getItem_scrollOffsetLessThenZero_MatcherItem() {
        val target = 8
        val offset = -18
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS.first()
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(page.getItemMatcher(offsetContact), autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun getItem_scrollOffsetInItemCountRange_positionItem() {
        val target = 2
        val offset = 12
        val offsetContact = CONTACTS[target + offset]
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(target, scrollOffset = offset)
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(target + offset, autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun getItem_scrollOffsetInItemCountRangeBothAreVisible_positionItem() {
        val target = 8
        val offset = 2
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(target, scrollOffset = offset).name.hasText(targetContact.name).isDisplayed()
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(target + offset, autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun getItem_scrollOffsetOutOfItemCountRange_positionItem() {
        val target = 5
        val offset = CONTACTS.size
        val offsetContact = CONTACTS.last()
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(target, scrollOffset = offset)
        page.recycler.getLastItem<FriendsListPage.FriendRecyclerItem>(autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun getItem_scrollOffsetLessThenZero_positionItem() {
        val target = 10
        val offset = -18
        val offsetContact = CONTACTS.first()
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(target, scrollOffset = offset)
        page.recycler.getFirstItem<FriendsListPage.FriendRecyclerItem>(autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    //getItemMatched+offset
    @Test
    fun getItemMatched_scrollOffsetInItemCountRange_MatcherItem() {
        val target = 5
        val offset = 10
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recycler.getItemMatched<FriendsListPage.FriendRecyclerItem>(page.getItemMatcher(targetContact), 0, scrollOffset = offset)
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(page.getItemMatcher(offsetContact), autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun getItemMatched_scrollOffsetInItemCountRangeBothAreVisible_MatcherItem() {
        val target = 8
        val offset = 2
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recycler.getItemMatched<FriendsListPage.FriendRecyclerItem>(page.getItemMatcher(targetContact), 0, scrollOffset = offset)
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(page.getItemMatcher(offsetContact), autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun getItemMatched_scrollOffsetOutOfItemCountRange_MatcherItem() {
        val target = 5
        val offset = CONTACTS.size
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS.last()
        page.recycler.getItemMatched<FriendsListPage.FriendRecyclerItem>(page.getItemMatcher(targetContact), 0, scrollOffset = offset)
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(page.getItemMatcher(offsetContact), autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    //getFirstItemMatched+offset
    @Test
    fun getFirstItemMatched_scrollOffsetInItemCountRange_MatcherItem() {
        val target = 5
        val offset = 10
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recycler.getFirstItemMatched<FriendsListPage.FriendRecyclerItem>(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(page.getItemMatcher(offsetContact), autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    //getLastItemMatched+offset
    @Test
    fun getLastItemMatched_scrollOffsetInItemCountRange_MatcherItem() {
        val target = 5
        val offset = 10
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recycler.getLastItemMatched<FriendsListPage.FriendRecyclerItem>(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(page.getItemMatcher(offsetContact), autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun validItemCustomAssertion() {
        val contact = CONTACTS.first()
        page.recycler.firstItem().withAssertion("Toolbar title = ${contact.name}") {
            ChatPage.assertToolbarTitle(contact.name)
        }.click()
    }

    @Test
    fun invalidItemCustomAssertion() {
        AssertUtils.assertException {
            val invalidExpectedName = "InvalidTitle"
            page.recycler.firstItem().withTimeout(3000).withAssertion("Toolbar title = $invalidExpectedName") {
                ChatPage.assertToolbarTitle(invalidExpectedName)
            }.click()
        }
    }

    @Test
    fun swipeUntil() {
        withId(R.id.recycler_friends).withAssertion(isListened = true) {
            withText(CONTACTS.last().name).withTimeout(200).isDisplayed()
        }.swipeUp()
    }
}

