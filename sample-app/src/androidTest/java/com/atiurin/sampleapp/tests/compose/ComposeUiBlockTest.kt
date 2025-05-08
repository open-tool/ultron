package com.atiurin.sampleapp.tests.compose

import com.atiurin.sampleapp.activity.ComposeElementsActivity
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.ComposeElementsPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.core.compose.createUltronComposeRule
import com.atiurin.ultron.extensions.assertIsDisplayed
import com.atiurin.ultron.extensions.withUseUnmergedTree
import org.junit.Rule
import org.junit.Test

class ComposeUiBlockTest: BaseTest() {
    @get:Rule
    val composeRule = createUltronComposeRule<ComposeElementsActivity>()

    @Test
    fun noUniqueElementTest(){
        ComposeElementsPage{
            contactBlock1.blockMatcher.withUseUnmergedTree(true).printToLog("tree")
            contactBlock1.deepSearchText.assertIsDisplayed()
            AssertUtils.assertException { contactBlock1.textWithoutDeepSearch.withTimeout(100).assertIsDisplayed() }
            contactBlock2.deepSearchText.assertIsDisplayed()
        }
    }

    @Test
    fun uiBlockInBlock(){
        ComposeElementsPage {
            contactListBlock.blockMatcher.withUseUnmergedTree(true).printToLog("tree")
            contactListBlock.firstContact.deepSearchText.assertIsDisplayed()
            contactListBlock.secondContact.deepSearchText.assertIsDisplayed()
            AssertUtils.assertException { contactListBlock.firstContact.textWithoutDeepSearch.withTimeout(100).assertIsDisplayed() }
        }
    }
}