package com.atiurin.sampleapp.tests.espresso

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.MainActivity
import com.atiurin.sampleapp.data.repositories.ContactRepositoty
import com.atiurin.sampleapp.framework.Log
import com.atiurin.sampleapp.pages.FriendsListPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.extensions.isDisplayed
import org.junit.Test

class RecyclerPerfTest : BaseTest() {

    private val activityTestRule = ActivityTestRule(MainActivity::class.java)

    init {
        ruleSequence.addLast(activityTestRule)
    }

    @Test
    fun test2() {
        Log.time("Scroll+Click") {
            withText(ContactRepositoty.getFirst().name).isDisplayed()
            onView(withId(R.id.recycler_friends))
                .perform(
                    RecyclerViewActions
                        .scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText(ContactRepositoty.getLast().name)))
                )
                .perform(
                    RecyclerViewActions
                        .actionOnItem<RecyclerView.ViewHolder>(
                            hasDescendant(withText(ContactRepositoty.getLast().name)),
                            click()
                        )
                )
        }
    }

    @Test
    fun test3() {
        Log.time("FriendsPageClick") {
            FriendsListPage.getListItem(ContactRepositoty.getLast().name).click()
        }
    }
}
