package com.atiurin.sampleapp.test.espresso.recycler

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.hasSibling
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.MainActivity
import com.atiurin.sampleapp.data.repositories.ContactRepository
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test

class GoogleRecyclerViewTest {
    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun openFirstChatTest() {
        onView(withId(R.id.recycler_friends))
            .check(matches(isDisplayed()))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    click()
                )
            )
        onView(withId(R.id.toolbar_title))
            .check(matches(isDisplayed()))
    }

    @Test
    fun sendMessageTest() {
        val contact = ContactRepository.getLast()
        Thread.sleep(2000)
        onView(withId(R.id.recycler_friends))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText(contact.name))
                )
            )
        onView(allOf(
            withId(R.id.tv_status), withText(contact.status),
            hasSibling(allOf(withId(R.id.tv_name), withText(contact.name)))
        ))
        // TODO the rest code if you're a hero
    }
}