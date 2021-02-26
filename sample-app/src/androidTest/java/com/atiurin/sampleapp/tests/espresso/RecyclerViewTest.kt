package com.atiurin.sampleapp.tests.espresso

import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.atiurin.sampleapp.MyApplication
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.MainActivity
import com.atiurin.sampleapp.data.repositories.CONTACTS
import com.atiurin.sampleapp.data.repositories.ContactRepositoty
import com.atiurin.sampleapp.framework.Log
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.ChatPage
import com.atiurin.sampleapp.pages.FriendsListPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.core.espresso.recyclerview.withRecyclerView
import com.atiurin.ultron.extensions.hasText
import com.atiurin.ultron.extensions.isDisplayed
import com.atiurin.ultron.extensions.withTimeout
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
    fun transferFromGenericToSubclass(){
        val position = 5
        page.recycler.getItem<FriendsListPage.FriendRecyclerItem>(position)
            .status.hasText(CONTACTS[position].status).isDisplayed()
    }

    @Test
    fun getViewHolderList(){
        page.recycler.waitItemsLoaded()
        Assert.assertTrue(page.recycler.getViewHolderList(hasDescendant(withId(R.id.tv_name))).isNotEmpty())
    }

    @Test
    fun waitLoaded_ofAlreadyLoadedList(){
        page.recycler.waitItemsLoaded()
        page.recycler.waitItemsLoaded()
    }

    @Test
    fun waitLoaded_allItemsLoaded(){
        val count = page.recycler.waitItemsLoaded().getSize()
        Assert.assertEquals(CONTACTS.size, count)
    }

    @Test
    fun getLastItem(){
        page.recycler.lastItem().isDisplayed().click()
    }

    @Test
    fun getLastItemWithCustomType(){
        page.recycler.getLastItem<FriendsListPage.FriendRecyclerItem>().name.hasText(ContactRepositoty.getLast().name)
    }

    @Test
    fun perfScroll(){
        page.recycler.apply {
            for (i in 0 ..10){
                lastItem().isDisplayed()
                firstItem().isDisplayed()
            }
        }
    }

    @Test
    fun getViewHolderAtPosition_outOfVisibleList(){
        Assert.assertNull(page.recycler.waitItemsLoaded().getViewHolderAtPosition(15))
    }

    @Test
    fun getViewHolderAtPosition_inVisibleList(){
        Assert.assertNotNull(page.recycler.waitItemsLoaded().getViewHolderAtPosition(2))
    }

    @Test
    fun getItemsAdapterPositionList(){
        page.recycler.waitItemsLoaded()
        val matcher = hasDescendant(allOf(withId(R.id.tv_name), withText(containsString("Friend"))))
        Assert.assertEquals(0, page.recycler.getViewHolderList(matcher).size)
        Assert.assertTrue(page.recycler.getItemsAdapterPositionList(matcher).isNotEmpty())
    }

    @Test
    fun firstItemMatched_existItem(){
        val pattern = "Friend"
        val expectedContacts = CONTACTS.filter { it.name.contains(pattern) }
        val matcher = hasDescendant(allOf(withId(R.id.tv_name), withText(containsString(pattern))))

        page.recycler.firstItemMatched(matcher).isDisplayed().click()
        ChatPage.assertToolbarTitle(expectedContacts.first().name)
    }

    @Test
    fun itemMatched_existItem(){
        val pattern = "Friend"
        val expectedContacts = CONTACTS.filter { it.name.contains(pattern) }
        val matcher = hasDescendant(allOf(withId(R.id.tv_name), withText(containsString(pattern))))

        page.recycler.itemMatched(matcher, 1).isDisplayed().click()
        ChatPage.assertToolbarTitle(expectedContacts[1].name)
    }

    @Test
    fun itemMatched_notExistItem(){
        val matcher = hasDescendant(allOf(withId(R.id.tv_name), withText(containsString("Friend"))))
        AssertUtils.assertException { page.recycler.withTimeout(1000).itemMatched(matcher, 99) }
    }

    @Test
    fun lastItemMatched_existItem(){
        val pattern = "Friend"
        val expectedContacts = CONTACTS.filter { it.name.contains(pattern) }
        val matcher = hasDescendant(allOf(withId(R.id.tv_name), withText(containsString(pattern))))

        page.recycler.itemMatched(matcher, expectedContacts.lastIndex).isDisplayed().click()
        ChatPage.assertToolbarTitle(expectedContacts.last().name)
    }

    @Test
    fun getFirstItemMatched_existItem(){
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
    fun getItemMatched_existItem(){
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
    fun getItemMatched_notExistItem(){
        val matcher = hasDescendant(allOf(withId(R.id.tv_name), withText(containsString("Friend"))))
        AssertUtils.assertException { page.recycler.withTimeout(1000).getItemMatched<FriendsListPage.FriendRecyclerItem>(matcher, 99) }
    }

    @Test
    fun getLastItemMatched_existItem(){
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
}
