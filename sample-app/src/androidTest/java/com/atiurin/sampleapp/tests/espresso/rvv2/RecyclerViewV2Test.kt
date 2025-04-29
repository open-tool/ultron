package com.atiurin.sampleapp.tests.espresso.rvv2

import androidx.test.espresso.matcher.ViewMatchers
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
import com.atiurin.ultron.core.espresso.recyclerviewv2.withRecyclerViewV2
import com.atiurin.ultron.extensions.hasText
import com.atiurin.ultron.extensions.isDisplayed
import com.atiurin.ultron.extensions.withAssertion
import com.atiurin.ultron.extensions.withTimeout
import com.atiurin.ultron.testlifecycle.setupteardown.SetUp
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import com.atiurin.ultron.testlifecycle.setupteardown.TearDown
import com.atiurin.ultron.testlifecycle.setupteardown.TearDownRule
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Test

class RecyclerViewV2Test : BaseTest() {
    companion object {
        const val CUSTOM_TIMEOUT = "CUSTOM_TIMEOUT"
        val notExistItemMatcher = ViewMatchers.hasDescendant(ViewMatchers.withText("zxcbmzxmbc"))
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
        withRecyclerViewV2(R.id.recycler_friends).item(0).getChild(ViewMatchers.withId(R.id.tv_status)).hasText(
            ContactRepositoty.getFirst().status)
    }

    @Test
    fun testDisplayedItemPositions() {
        for (index in 0..3) {
            page.recyclerV2.item(index).assertMatches(
                ViewMatchers.hasDescendant(
                    ViewMatchers.withText(
                        CONTACTS[index].name
                    )
                )
            )
                .assertMatches(ViewMatchers.hasDescendant(ViewMatchers.withText(CONTACTS[index].status))).isDisplayed()
        }
    }

    @Test
    fun getRecyclerViewTest() {
        val view = page.recyclerV2.getRecyclerViewList()
        Assert.assertNotNull(view)
        Assert.assertEquals(ViewMatchers.Visibility.VISIBLE.value, view.visibility)
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
        withRecyclerViewV2(R.id.recycler_friends).item(CONTACTS.size - 1).isDisplayed()
    }

    @Test
    fun scrollToLastWithMatcher() {
        withRecyclerViewV2(R.id.recycler_friends).item(
            ViewMatchers.hasDescendant(
                ViewMatchers.withText(
                    "Friend14"
                )
            )
        ).isDisplayed()
    }

    @Test
    fun getNotExistedRecyclerItemWithPosition() {
        AssertUtils.assertException {
            page.recyclerV2.item(100).withTimeout(100).isDisplayed()
        }
    }

    @Test
    fun assertListSize() {
        page.recyclerV2.assertSize(CONTACTS.size)
    }

    @Test
    fun recyclerView_notExist() {
        AssertUtils.assertException {
            withRecyclerViewV2(ViewMatchers.withText("Not existed recycler")).withTimeout(100).isDisplayed()
        }
    }

    @Test
    fun item_notExist() {
        AssertUtils.assertException {
            page.recyclerV2.item(ViewMatchers.withText("Not existed item"), false).withTimeout(100).isDisplayed()
        }
    }

    @Test
    fun item_notExist_executionTime() {
        val timeout = 5_000L
        AssertUtils.assertExecTimeMoreThen(timeout){
            AssertUtils.assertException {
                runCatching { page.recyclerV2.withTimeout(timeout).item(ViewMatchers.withText("Not existed item")).isDisplayed() }
            }
        }
    }

    @Test
    @SetUp(CUSTOM_TIMEOUT)
    @TearDown(CUSTOM_TIMEOUT)
    fun defaultTimeoutOnItemWaiting() {
        AssertUtils.assertException { page.recyclerV2.item(10).isDisplayed() }
    }

    @Test
    @SetUp(CUSTOM_TIMEOUT)
    @TearDown(CUSTOM_TIMEOUT)
    fun customTimeoutOnItemWaiting() {
        withRecyclerViewV2(R.id.recycler_friends, 8000).item(10).isDisplayed()
    }

    @Test
    @SetUp(CUSTOM_TIMEOUT)
    @TearDown(CUSTOM_TIMEOUT)
    fun item_autoScroll_False_item_NotLoaded() {
        AssertUtils.assertException { page.recyclerV2.item(10, false).isDisplayed() }
    }

    @Test
    @SetUp(CUSTOM_TIMEOUT)
    @TearDown(CUSTOM_TIMEOUT)
    fun item_autoScroll_False_scroll_Force() {
        withRecyclerViewV2(R.id.recycler_friends, 8_000).item(10, false).scrollToItem().isDisplayed()
    }

    @Test
    fun itemMatcher_autoScroll_false_itemNotDisplayed() {
        AssertUtils.assertException {
            page.recyclerV2.item(
                ViewMatchers.hasDescendant(ViewMatchers.withText("Friend14")), false
            ).isDisplayed()
        }
    }

    @Test
    fun itemMatcher_autoScroll_false_scroll_force() {
        page.recyclerV2.item(ViewMatchers.hasDescendant(ViewMatchers.withText("Friend14")), false).scrollToItem().isDisplayed()
    }

    @Test
    @SetUp(CUSTOM_TIMEOUT)
    @TearDown(CUSTOM_TIMEOUT)
    fun itemMatcher_autoScroll_false() {
        AssertUtils.assertException {
            page.recyclerV2.item(
                ViewMatchers.hasDescendant(ViewMatchers.withText("Friend14")), false
            ).scrollToItem().isDisplayed()
        }
    }

    @Test
    @SetUp(CUSTOM_TIMEOUT)
    @TearDown(CUSTOM_TIMEOUT)
    fun itemMatcher_autoScroll_true_custom_timeout() {
        withRecyclerViewV2(R.id.recycler_friends, 10_000).item(
            ViewMatchers.hasDescendant(
                ViewMatchers.withText("Friend14")
            )
        ).isDisplayed()
    }

    @Test
    fun getViewHolder() {
        val position = CONTACTS.size - 1
        Assert.assertEquals(
            position,
            page.recyclerV2.item(ViewMatchers.hasDescendant(ViewMatchers.withText(CONTACTS[position].name)))
                .getViewHolder()
                ?.layoutPosition
        )
    }

    @Test
    fun transferFromGenericToSubclass() {
        val position = 5
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(position)
            .status.hasText(CONTACTS[position].status).isDisplayed()
    }

    @Test
    fun getViewHolderList() {
        page.recyclerV2.waitItemsLoaded()
        Assert.assertTrue(page.recyclerV2.getViewHolderList(
            ViewMatchers.hasDescendant(
                ViewMatchers.withId(
                    R.id.tv_name
                )
            )
        ).isNotEmpty())
    }

    @Test
    fun waitLoaded_ofAlreadyLoadedList() {
        page.recyclerV2.waitItemsLoaded()
        page.recyclerV2.waitItemsLoaded()
    }

    @Test
    fun waitLoaded_allItemsLoaded() {
        val count = page.recyclerV2.waitItemsLoaded().getSize()
        Assert.assertEquals(CONTACTS.size, count)
    }

    @Test
    fun getLastItem() {
        page.recyclerV2.lastItem().isDisplayed().click()
    }

    @Test
    fun getLastItemWithCustomType() {
        page.recyclerV2.getLastItem<FriendsListPage.FriendRecyclerItemV2>().name.hasText(
            ContactRepositoty.getLast().name)
    }

    @Test
    fun perfScroll() {
        page.recyclerV2.apply {
            for (i in 0..10) {
                lastItem().isDisplayed()
                firstItem().isDisplayed()
            }
        }
    }

    @Test
    fun getViewHolderAtPosition_outOfVisibleList() {
        Assert.assertNull(page.recyclerV2.waitItemsLoaded().getViewHolderAtPosition(15))
    }

    @Test
    fun getViewHolderAtPosition_inVisibleList() {
        Assert.assertNotNull(page.recyclerV2.waitItemsLoaded().getViewHolderAtPosition(2))
    }

    @Test
    fun getItemsAdapterPositionList() {
        page.recyclerV2.waitItemsLoaded()
        val matcher = ViewMatchers.hasDescendant(
            Matchers.allOf(
                ViewMatchers.withId(R.id.tv_name),
                ViewMatchers.withText(Matchers.containsString("Friend"))
            )
        )
        Assert.assertEquals(0, page.recyclerV2.getViewHolderList(matcher).size)
        Assert.assertTrue(page.recyclerV2.getItemsAdapterPositionList(matcher).isNotEmpty())
    }

    @Test
    fun firstItemMatched_existItem() {
        val pattern = "Friend"
        val expectedContacts = CONTACTS.filter { it.name.contains(pattern) }
        val matcher = ViewMatchers.hasDescendant(
            Matchers.allOf(
                ViewMatchers.withId(R.id.tv_name),
                ViewMatchers.withText(Matchers.containsString(pattern))
            )
        )

        page.recyclerV2.firstItemMatched(matcher).isDisplayed().click()
        ChatPage.assertToolbarTitle(expectedContacts.first().name)
    }

    @Test
    fun itemMatched_existItem() {
        val pattern = "Friend"
        val expectedContacts = CONTACTS.filter { it.name.contains(pattern) }
        val matcher = ViewMatchers.hasDescendant(
            Matchers.allOf(
                ViewMatchers.withId(R.id.tv_name),
                ViewMatchers.withText(Matchers.containsString(pattern))
            )
        )

        page.recyclerV2.itemMatched(matcher, 1).isDisplayed().click()
        ChatPage.assertToolbarTitle(expectedContacts[1].name)
    }

    @Test
    fun itemMatched_notExistItem() {
        val matcher = ViewMatchers.hasDescendant(
            Matchers.allOf(
                ViewMatchers.withId(R.id.tv_name),
                ViewMatchers.withText(Matchers.containsString("Friend"))
            )
        )
        AssertUtils.assertException { page.recyclerV2.withTimeout(1000).itemMatched(matcher, 99) }
    }

    @Test
    fun lastItemMatched_existItem() {
        val pattern = "Friend"
        val expectedContacts = CONTACTS.filter { it.name.contains(pattern) }
        val matcher = ViewMatchers.hasDescendant(
            Matchers.allOf(
                ViewMatchers.withId(R.id.tv_name),
                ViewMatchers.withText(Matchers.containsString(pattern))
            )
        )

        page.recyclerV2.itemMatched(matcher, expectedContacts.lastIndex).isDisplayed().click()
        ChatPage.assertToolbarTitle(expectedContacts.last().name)
    }

    @Test
    fun getFirstItemMatched_existItem() {
        val pattern = "Friend"
        val expectedContact = CONTACTS.first { it.name.contains(pattern) }
        val matcher = ViewMatchers.hasDescendant(
            Matchers.allOf(
                ViewMatchers.withId(R.id.tv_name),
                ViewMatchers.withText(Matchers.containsString(pattern))
            )
        )

        page.recyclerV2.getFirstItemMatched<FriendsListPage.FriendRecyclerItemV2>(matcher).apply {
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
        val matcher = ViewMatchers.hasDescendant(
            Matchers.allOf(
                ViewMatchers.withId(R.id.tv_name),
                ViewMatchers.withText(Matchers.containsString(pattern))
            )
        )

        page.recyclerV2.getItemMatched<FriendsListPage.FriendRecyclerItemV2>(matcher, 1).apply {
            name.isDisplayed().hasText(expectedContact.name)
            status.hasText(expectedContact.status)
            click()
        }
        ChatPage.assertToolbarTitle(expectedContact.name)
    }

    @Test
    fun getItemMatched_notExistItem() {
        val matcher = ViewMatchers.hasDescendant(
            Matchers.allOf(
                ViewMatchers.withId(R.id.tv_name),
                ViewMatchers.withText(Matchers.containsString("Friend"))
            )
        )
        AssertUtils.assertException { page.recyclerV2.withTimeout(1000).getItemMatched<FriendsListPage.FriendRecyclerItemV2>(matcher, 99) }
    }

    @Test
    fun getLastItemMatched_existItem() {
        val pattern = "Friend"
        val expectedContacts = CONTACTS.filter { it.name.contains(pattern) }
        val matcher = ViewMatchers.hasDescendant(
            Matchers.allOf(
                ViewMatchers.withId(R.id.tv_name),
                ViewMatchers.withText(Matchers.containsString(pattern))
            )
        )

        page.recyclerV2.getItemMatched<FriendsListPage.FriendRecyclerItemV2>(matcher, expectedContacts.lastIndex).apply {
            name.isDisplayed().hasText(expectedContacts.last().name)
            status.hasText(expectedContacts.last().status)
            click()
        }
        ChatPage.assertToolbarTitle(expectedContacts.last().name)
    }

    @Test
    fun assertItemNotExist_notExistItem() {
        page.recyclerV2.assertItemNotExist(notExistItemMatcher, 2000)
    }

    @Test
    fun assertItemNotExist_existItem() {
        val matcher = ViewMatchers.hasDescendant(
            Matchers.allOf(
                ViewMatchers.withId(R.id.tv_name),
                ViewMatchers.withText(Matchers.containsString(CONTACTS.first().name))
            )
        )
        AssertUtils.assertException { page.recyclerV2.assertItemNotExist(matcher, 2000) }
    }

    @Test
    fun assertItemNotExistImmediately_notExistItem() {
        page.recyclerV2.assertItemNotExistImmediately(notExistItemMatcher, 2000)
    }

    @Test
    fun assertItemNotExistImmediately_existItem() {
        page.recyclerV2.waitItemsLoaded()
        val matcher = ViewMatchers.hasDescendant(
            Matchers.allOf(
                ViewMatchers.withId(R.id.tv_name),
                ViewMatchers.withText(Matchers.containsString(CONTACTS.first().name))
            )
        )
        AssertUtils.assertException { page.recyclerV2.assertItemNotExistImmediately(matcher, 2000) }
    }

    @Test
    fun assertItemOutOfLimitNotFound() {
        val rv = withRecyclerViewV2(R.id.recycler_friends, itemSearchLimit = 2)
        AssertUtils.assertException {
            rv.withTimeout(2000L)
                .item(
                    ViewMatchers.hasDescendant(
                        Matchers.allOf(
                            ViewMatchers.withId(R.id.tv_name),
                            ViewMatchers.withText(Matchers.containsString(CONTACTS[10].name))
                        )
                    )
                )
                .click()
        }
    }

    @Test
    fun assertItemInLimitFound() {
        val rv = withRecyclerViewV2(R.id.recycler_friends, itemSearchLimit = 10)
        rv.withTimeout(2000L)
            .item(
                ViewMatchers.hasDescendant(
                    Matchers.allOf(
                        ViewMatchers.withId(R.id.tv_name),
                        ViewMatchers.withText(Matchers.containsString(CONTACTS[2].name))
                    )
                )
            )
            .isDisplayed()
    }

    @Test
    fun createHandlerFromUiTest() {
        page.recyclerV2.getItemAdapterPositionAtIndex(
            ViewMatchers.hasDescendant(
                Matchers.allOf(
                    ViewMatchers.withId(R.id.tv_name),
                    ViewMatchers.withText(Matchers.containsString(CONTACTS.last().name))
                )
            ), 0)
    }

    //item+offset
    @Test
    fun item_scrollOffsetInItemCountRange_MatcherItem() {
        val target = 5
        val offset = 10
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recyclerV2.item(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recyclerV2.item(page.getItemMatcher(offsetContact), autoScroll = false).isDisplayed().click()
    }

    @Test
    fun item_scrollOffsetInItemCountRangeBothAreVisible_MatcherItem() {
        val target = 8
        val offset = 2
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recyclerV2.item(page.getItemMatcher(targetContact), scrollOffset = offset).isDisplayed()
        page.recyclerV2.item(page.getItemMatcher(offsetContact), autoScroll = false).isDisplayed()
    }

    @Test
    fun item_scrollOffsetOutOfItemCountRange_MatcherItem() {
        val target = 5
        val offset = CONTACTS.size
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS.last()
        page.recyclerV2.item(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recyclerV2.item(page.getItemMatcher(offsetContact), autoScroll = false).isDisplayed().click()
    }

    @Test
    fun item_scrollOffsetLessThenZero_MatcherItem() {
        val target = 8
        val offset = -18
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS.first()
        page.recyclerV2.item(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recyclerV2.item(page.getItemMatcher(offsetContact), autoScroll = false).isDisplayed().click()
    }

    @Test
    fun item_scrollOffsetInItemCountRange_positionItem() {
        val target = 2
        val offset = 12
        val offsetContact = CONTACTS[target + offset]
        page.recyclerV2.item(target, scrollOffset = offset)
        page.recyclerV2.item(target + offset, autoScroll = false).isDisplayed()
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(target + offset, autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun item_scrollOffsetInItemCountRangeBothAreVisible_positionItem() {
        val target = 8
        val offset = 2
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recyclerV2.item(target, scrollOffset = offset).isDisplayed()
        page.recyclerV2.item(target + offset, autoScroll = false).isDisplayed()
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(target, autoScroll = false).name.hasText(targetContact.name).isDisplayed()
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(target + offset, autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun item_scrollOffsetOutOfItemCountRange_positionItem() {
        val target = 5
        val offset = CONTACTS.size
        val offsetContact = CONTACTS.last()
        page.recyclerV2.item(target, scrollOffset = offset)
        page.recyclerV2.lastItem(autoScroll = false).isDisplayed()
        page.recyclerV2.getLastItem<FriendsListPage.FriendRecyclerItemV2>(autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun item_scrollOffsetLessThenZero_positionItem() {
        val target = 10
        val offset = -18
        val offsetContact = CONTACTS.first()
        page.recyclerV2.item(target, scrollOffset = offset)
        page.recyclerV2.getFirstItem<FriendsListPage.FriendRecyclerItemV2>(autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    //itemMatched+offset
    @Test
    fun itemMatched_scrollOffsetInItemCountRange_MatcherItem() {
        val target = 5
        val offset = 10
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recyclerV2.itemMatched(page.getItemMatcher(targetContact), 0, scrollOffset = offset)
        page.recyclerV2.item(page.getItemMatcher(offsetContact), autoScroll = false).isDisplayed().click()
    }

    @Test
    fun itemMatched_scrollOffsetInItemCountRangeBothAreVisible_MatcherItem() {
        val target = 8
        val offset = 2
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recyclerV2.itemMatched(page.getItemMatcher(targetContact), 0, scrollOffset = offset).isDisplayed()
        page.recyclerV2.item(page.getItemMatcher(offsetContact), autoScroll = false).isDisplayed()
    }

    @Test
    fun itemMatched_scrollOffsetOutOfItemCountRange_MatcherItem() {
        val target = 5
        val offset = CONTACTS.size
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS.last()
        page.recyclerV2.itemMatched(page.getItemMatcher(targetContact), 0, scrollOffset = offset)
        page.recyclerV2.item(page.getItemMatcher(offsetContact), autoScroll = false).isDisplayed().click()
    }

    //firstItemMatched+offset
    @Test
    fun firstItemMatched_scrollOffsetInItemCountRange_MatcherItem() {
        val target = 5
        val offset = 10
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recyclerV2.firstItemMatched(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recyclerV2.item(page.getItemMatcher(offsetContact), autoScroll = false).isDisplayed().click()
    }

    //lastItemMatched+offset
    @Test
    fun lastItemMatched_scrollOffsetInItemCountRange_MatcherItem() {
        val target = 5
        val offset = 10
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recyclerV2.lastItemMatched(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recyclerV2.item(page.getItemMatcher(offsetContact), autoScroll = false).isDisplayed().click()
    }

    //getItem+offset
    @Test
    fun getItem_scrollOffsetInItemCountRange_MatcherItem() {
        val target = 5
        val offset = 10
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(page.getItemMatcher(offsetContact), autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun getItem_scrollOffsetInItemCountRangeBothAreVisible_MatcherItem() {
        val target = 8
        val offset = 2
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(page.getItemMatcher(offsetContact), autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun getItem_scrollOffsetOutOfItemCountRange_MatcherItem() {
        val target = 5
        val offset = CONTACTS.size
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS.last()
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(page.getItemMatcher(offsetContact), autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun getItem_scrollOffsetLessThenZero_MatcherItem() {
        val target = 8
        val offset = -18
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS.first()
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(page.getItemMatcher(offsetContact), autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun getItem_scrollOffsetInItemCountRange_positionItem() {
        val target = 2
        val offset = 12
        val offsetContact = CONTACTS[target + offset]
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(target, scrollOffset = offset)
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(target + offset, autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun getItem_scrollOffsetInItemCountRangeBothAreVisible_positionItem() {
        val target = 8
        val offset = 2
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(target, scrollOffset = offset).name.hasText(targetContact.name).isDisplayed()
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(target + offset, autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun getItem_scrollOffsetOutOfItemCountRange_positionItem() {
        val target = 5
        val offset = CONTACTS.size
        val offsetContact = CONTACTS.last()
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(target, scrollOffset = offset)
        page.recyclerV2.getLastItem<FriendsListPage.FriendRecyclerItemV2>(autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun getItem_scrollOffsetLessThenZero_positionItem() {
        val target = 10
        val offset = -18
        val offsetContact = CONTACTS.first()
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(target, scrollOffset = offset)
        page.recyclerV2.getFirstItem<FriendsListPage.FriendRecyclerItemV2>(autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    //getItemMatched+offset
    @Test
    fun getItemMatched_scrollOffsetInItemCountRange_MatcherItem() {
        val target = 5
        val offset = 10
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recyclerV2.getItemMatched<FriendsListPage.FriendRecyclerItemV2>(page.getItemMatcher(targetContact), 0, scrollOffset = offset)
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(page.getItemMatcher(offsetContact), autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun getItemMatched_scrollOffsetInItemCountRangeBothAreVisible_MatcherItem() {
        val target = 8
        val offset = 2
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recyclerV2.getItemMatched<FriendsListPage.FriendRecyclerItemV2>(page.getItemMatcher(targetContact), 0, scrollOffset = offset)
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(page.getItemMatcher(offsetContact), autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun getItemMatched_scrollOffsetOutOfItemCountRange_MatcherItem() {
        val target = 5
        val offset = CONTACTS.size
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS.last()
        page.recyclerV2.getItemMatched<FriendsListPage.FriendRecyclerItemV2>(page.getItemMatcher(targetContact), 0, scrollOffset = offset)
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(page.getItemMatcher(offsetContact), autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    //getFirstItemMatched+offset
    @Test
    fun getFirstItemMatched_scrollOffsetInItemCountRange_MatcherItem() {
        val target = 5
        val offset = 10
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recyclerV2.getFirstItemMatched<FriendsListPage.FriendRecyclerItemV2>(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(page.getItemMatcher(offsetContact), autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    //getLastItemMatched+offset
    @Test
    fun getLastItemMatched_scrollOffsetInItemCountRange_MatcherItem() {
        val target = 5
        val offset = 10
        val targetContact = CONTACTS[target]
        val offsetContact = CONTACTS[target + offset]
        page.recyclerV2.getLastItemMatched<FriendsListPage.FriendRecyclerItemV2>(page.getItemMatcher(targetContact), scrollOffset = offset)
        page.recyclerV2.getItem<FriendsListPage.FriendRecyclerItemV2>(page.getItemMatcher(offsetContact), autoScroll = false).name.hasText(offsetContact.name).isDisplayed()
    }

    @Test
    fun validItemCustomAssertion() {
        val contact = CONTACTS.first()
        page.recyclerV2.firstItem().withAssertion("Toolbar title = ${contact.name}") {
            ChatPage.assertToolbarTitle(contact.name)
        }.click()
    }

    @Test
    fun invalidItemCustomAssertion() {
        AssertUtils.assertException {
            val invalidExpectedName = "InvalidTitle"
            page.recyclerV2.firstItem().withTimeout(3000).withAssertion("Toolbar title = $invalidExpectedName") {
                ChatPage.assertToolbarTitle(invalidExpectedName)
            }.click()
        }
    }

    @Test
    fun swipeUntil() {
        ViewMatchers.withId(R.id.recycler_friends).withAssertion(isListened = true) {
            ViewMatchers.withText(CONTACTS.last().name).withTimeout(200).isDisplayed()
        }.swipeUp()
    }
}