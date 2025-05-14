package com.atiurin.sampleapp.tests.espresso

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.atiurin.sampleapp.MyApplication
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.MainActivity
import com.atiurin.sampleapp.data.repositories.ContactRepositoty
import com.atiurin.sampleapp.framework.Log
import com.atiurin.sampleapp.pages.FriendsListPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.extensions.isDisplayed
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import org.junit.Test

class RecyclerPerfTest : BaseTest() {

    private val activityTestRule = ActivityTestRule(MainActivity::class.java)
    private val timeoutRule = SetUpRule().add {
        MyApplication.CONTACTS_LOADING_TIMEOUT_MS = 0L
    }

    init {
        ruleSequence.addLast(timeoutRule, activityTestRule)
    }

    @Test
    fun test2() {
        Log.time("Scroll+Click") {
            (0..100).forEach { _ ->
                onView(withId(R.id.recycler_friends))
                    .perform(
                        RecyclerViewActions
                            .scrollTo<RecyclerView.ViewHolder>(
                                hasDescendant(withText(ContactRepositoty.getLast().name)),
                            )
                    )
                withText(ContactRepositoty.getLast().name).isDisplayed()
                onView(withId(R.id.recycler_friends))
                    .perform(
                        RecyclerViewActions
                            .scrollTo<RecyclerView.ViewHolder>(
                                hasDescendant(withText(ContactRepositoty.getFirst().name)),
                            )
                    )
                withText(ContactRepositoty.getFirst().name).isDisplayed()
            }
        }
    }

    @Test
    fun recyclerViewV1PerfTest() {
        Log.time("FriendsPageClick") {
            (0..100).forEach { _ ->
                FriendsListPage.getListItem(ContactRepositoty.getLast().name).name.isDisplayed()
                FriendsListPage.getListItem(ContactRepositoty.getFirst().name).name.isDisplayed()
            }
        }
    }
}
