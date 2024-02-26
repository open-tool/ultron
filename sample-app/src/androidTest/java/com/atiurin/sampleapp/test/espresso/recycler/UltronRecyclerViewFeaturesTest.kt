package com.atiurin.sampleapp.test.espresso.recycler

import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.atiurin.sampleapp.activity.MainActivity
import com.atiurin.sampleapp.test.base.UltronBaseTest
import org.junit.Rule
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.data.repositories.ContactRepository
import com.atiurin.sampleapp.screens.ultron.UltronEspressoChatScreen
import com.atiurin.sampleapp.test.base.UltronAuthorisedBaseTest
import com.atiurin.ultron.core.espresso.recyclerview.withRecyclerView
import org.junit.Test

class UltronRecyclerViewFeaturesTest : UltronAuthorisedBaseTest() {
    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    private val recyclerView = withRecyclerView(R.id.recycler_friends, loadTimeout = 20_000, itemSearchLimit = 100)

    @Test
    fun recyclerViewAssertionsTest() {
        recyclerView.assertNotEmpty()
        recyclerView.assertSize(ContactRepository.all().size)
        recyclerView.assertItemNotExist(withText("NOT EXISTED"), 1_000)
        recyclerView.assertHasItemAtPosition(2)
    }

    @Test
    fun assertNotUniqueItemTest() {
        recyclerView.firstItemMatched(hasDescendant(withText("Time fluid capacitor"))).click()
        UltronEspressoChatScreen.assertScreenDisplayed()
    }

    @Test
    fun itemScrollOffsetTest(){
        val item = recyclerView.item(10, scrollOffset = 2, autoScroll = false)
        item.scrollToItem()
        item.click()
        UltronEspressoChatScreen.assertScreenDisplayed()
    }
}