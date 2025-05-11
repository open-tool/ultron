package com.atiurin.sampleapp.tests.espresso

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.atiurin.sampleapp.activity.UiBlockActivity
import com.atiurin.sampleapp.data.repositories.CONTACTS
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.EspressoUiBlockScreen
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.extensions.isDisplayed
import com.atiurin.ultron.extensions.withTimeout
import org.junit.Rule
import org.junit.Test

class EspressoUiBlockTest: BaseTest() {
    @get:Rule
    val activityRule = ActivityScenarioRule(UiBlockActivity::class.java)

    @Test
    fun notUniqueUiElement_WithDeepSearch(){
        EspressoUiBlockScreen {
            contactItem1.name.isDisplayed()
            contactItem1.deepSearchChild.withTimeout(100).isDisplayed()
        }
    }

    @Test
    fun notUniqueUiElement_WithoutDeepSearch(){
        EspressoUiBlockScreen {
            AssertUtils.assertException {
                blockWithoutDeepSearch.deepSearchFalse.withTimeout(100).isDisplayed()
            }
        }
    }

    @Test
    fun uiBlockInBlock(){
        EspressoUiBlockScreen {
            contactsListBlock.item1.name.isDisplayed().hasText(CONTACTS[0].name)
            contactsListBlock.item1.status.isDisplayed().hasText(CONTACTS[0].status)
            contactsListBlock.item2.name.isDisplayed().hasText(CONTACTS[1].name)
            contactsListBlock.item2.status.isDisplayed().hasText(CONTACTS[1].status)
        }
    }
}