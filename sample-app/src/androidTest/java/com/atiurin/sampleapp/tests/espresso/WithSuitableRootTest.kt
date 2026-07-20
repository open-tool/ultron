package com.atiurin.sampleapp.tests.espresso

import com.atiurin.sampleapp.activity.MainActivity
import com.atiurin.sampleapp.data.repositories.ContactRepositoty
import com.atiurin.sampleapp.pages.ChatPage
import com.atiurin.sampleapp.pages.FriendsListPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.testlifecycle.activity.UltronActivityRule
import org.junit.Test

class WithSuitableRootTest : BaseTest() {

    private val activityTestRule = UltronActivityRule(MainActivity::class.java)

    init {
        ruleSequence.addLast(activityTestRule)
    }

    @Test
    fun withSuitableRootForViewMatcher() {
        FriendsListPage
            .getListItem(0).click()
        ChatPage
            .assertToolbarTitleWithSuitableRoot(ContactRepositoty.getFirst().name)
    }

    @Test
    fun withSuitableRootForRecyclerMatcher() {
        FriendsListPage
            .assertPageDisplayedWithSuitableRoot()
    }

    @Test
    fun withSuitableRootForRecyclerItemMatcher() {
        FriendsListPage
            .assertPageDisplayedWithSuitableRoot()
            .getListItem(0).withSuitableRoot().isDisplayed().click()
        ChatPage
            .assertToolbarTitle(ContactRepositoty.getFirst().name)
    }
}