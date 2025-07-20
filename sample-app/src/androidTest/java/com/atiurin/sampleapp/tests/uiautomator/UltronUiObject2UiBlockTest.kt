package com.atiurin.sampleapp.tests.uiautomator

import com.atiurin.sampleapp.activity.UiBlockActivity
import com.atiurin.sampleapp.data.repositories.CONTACTS
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.uiblock.UiObject2UiBlockScreen
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.testlifecycle.activity.UltronActivityRule
import org.junit.Rule
import org.junit.Test

class UltronUiObject2UiBlockTest: BaseTest() {
    @get:Rule
    val activityRule = UltronActivityRule(UiBlockActivity::class.java)

    @Test
    fun notUniqueUiElement_WithDeepSearch(){
        UiObject2UiBlockScreen {
            block1.uiBlock.isDisplayed()
            block2.name.isDisplayed().hasText(CONTACTS[1].name)
            block1.deepSearchChild.withTimeout(100).isDisplayed()
        }
    }

    @Test
    fun notUniqueUiElement_WithoutDeepSearch(){
        UiObject2UiBlockScreen {
            AssertUtils.assertException {
                block2.notExisted.isDisplayed()
            }
        }
    }

    @Test
    fun uiBlockInBlock(){
        UiObject2UiBlockScreen {
            blocks.uiBlock.isDisplayed()
            blocks.item1.uiBlock.isDisplayed()
            blocks.item1.name.isDisplayed().hasText(CONTACTS[0].name)
            blocks.item1.status.isDisplayed().hasText(CONTACTS[0].status)
            AssertUtils.assertException {
                blocks.item2.notExisted.isDisplayed()
            }
        }
    }
}