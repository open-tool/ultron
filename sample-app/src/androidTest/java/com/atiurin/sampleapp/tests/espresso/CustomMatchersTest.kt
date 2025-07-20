package com.atiurin.sampleapp.tests.espresso

import androidx.test.espresso.matcher.ViewMatchers.withId
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.MainActivity
import com.atiurin.sampleapp.data.repositories.CONTACTS
import com.atiurin.sampleapp.data.repositories.ContactRepositoty
import com.atiurin.sampleapp.pages.ChatPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.custom.espresso.matcher.first
import com.atiurin.ultron.custom.espresso.matcher.hierarchyNumber
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.extensions.hasText
import com.atiurin.ultron.testlifecycle.activity.UltronActivityRule
import org.junit.Test

class CustomMatchersTest : BaseTest() {

    private val activityTestRule = UltronActivityRule(MainActivity::class.java)

    init {
        ruleSequence.addLast(activityTestRule)
    }

    @Test
    fun actionOnFirstMatchedView(){
        withId(R.id.tv_name).first().click()
        ChatPage.assertToolbarTitle(ContactRepositoty.getFirst().name)
    }

    @Test
    fun assertionOnFirstMatchedView(){
        withId(R.id.tv_name).first().hasText(ContactRepositoty.getFirst().name)
    }

    @Test
    fun actionOnHierarchyNumberedItem(){
        withId(R.id.tv_name).hierarchyNumber(1).click()
        ChatPage.assertToolbarTitle(CONTACTS[1].name)
    }

    @Test
    fun assertionOnHierarchyNumberedItem(){
        withId(R.id.tv_name).hierarchyNumber(1).hasText(CONTACTS[1].name)
    }
}