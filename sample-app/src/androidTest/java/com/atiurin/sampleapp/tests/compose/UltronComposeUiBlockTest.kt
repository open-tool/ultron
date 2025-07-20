package com.atiurin.sampleapp.tests.compose

import androidx.compose.ui.test.hasTestTag
import com.atiurin.sampleapp.activity.ComposeElementsActivity
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.contactBlock1Tag
import com.atiurin.sampleapp.data.repositories.CONTACTS
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.uiblock.ComposeListUiBlock.Companion.listBlockDesc
import com.atiurin.sampleapp.pages.uiblock.ComposeUiBlockScreen
import com.atiurin.sampleapp.pages.uiblock.ContactUiBlockWithDesc
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.sampleapp.tests.espresso.descriptionPrefix
import com.atiurin.sampleapp.tests.espresso.сhildNameDesc
import com.atiurin.ultron.core.common.assertion.softAssertion
import com.atiurin.ultron.core.compose.createSimpleUltronComposeRule
import com.atiurin.ultron.extensions.assertIsDisplayed
import com.atiurin.ultron.extensions.assertTextContains
import com.atiurin.ultron.extensions.withUseUnmergedTree
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class UltronComposeUiBlockTest : BaseTest() {
    @get:Rule
    val composeRule = createSimpleUltronComposeRule<ComposeElementsActivity>()

    @Test
    fun noUniqueElementTest() {
        ComposeUiBlockScreen {
            contactBlock1.blockMatcher.withUseUnmergedTree(true).printToLog("tree")
            contactBlock1.statusDeepSearchText.assertIsDisplayed()
            AssertUtils.assertException { contactBlock1.nameWithoutDeepSearch.withTimeout(100).assertIsDisplayed() }
            contactBlock2.name.assertIsDisplayed()
        }
    }

    @Test
    fun uiBlockInBlock() {
        ComposeUiBlockScreen {
            contactListBlock.blockMatcher.withUseUnmergedTree(true).printToLog("tree")
            contactListBlock.itemWithoutDesc.statusDeepSearchText.assertIsDisplayed()
            contactListBlock.itemWithoutDesc.uiBlock.assertIsDisplayed()
            contactListBlock.item1BlockWithDesc.name.assertIsDisplayed()
            AssertUtils.assertException { contactListBlock.itemWithoutDesc.nameWithoutDeepSearch.withTimeout(100).assertIsDisplayed() }
        }
    }

    @Test
    fun childElementDescription() {
        val blockDesc = "Parent_Name"
        val expectedChildName = "${ContactUiBlockWithDesc.сhildNameDesc} $blockDesc"
        ContactUiBlockWithDesc(hasTestTag(contactBlock1Tag), blockDesc).name.assertIsDisplayed().withResultHandler {
            Assert.assertEquals(expectedChildName, it.operation.elementInfo.name)
        }.withTimeout(100).assertTextContains("Invalid text")
    }

    @Test
    fun childBlockDescriptionTest() {
        val expectedItem1Description = "1 $descriptionPrefix $listBlockDesc"
        val expectedItem2Description = "2 $descriptionPrefix $listBlockDesc"
        val expectedChildNameDescInBlock1 = "$сhildNameDesc $expectedItem1Description"
        val expectedChildNameDescInBlock2 = "$сhildNameDesc $expectedItem2Description"

        ComposeUiBlockScreen {
            softAssertion {
                contactListBlock.item1BlockWithDesc.uiBlock.withTimeout(100).withResultHandler {
                    Assert.assertEquals(expectedItem1Description, it.operation.elementInfo.name)
                }.assertTextEquals("Invalid")
                contactListBlock.item1BlockWithDesc.name.withTimeout(100).withResultHandler {
                    Assert.assertEquals(expectedChildNameDescInBlock1, it.operation.elementInfo.name)
                }.assertTextEquals("Invalid")
                contactListBlock.item2BlockFactory.name.withTimeout(100).withResultHandler {
                    Assert.assertEquals(expectedChildNameDescInBlock2, it.operation.elementInfo.name)
                }.assertTextEquals("Invalid")
            }
        }
    }

    @Test
    fun properSearchOfElementsTest(){
        ComposeUiBlockScreen {
            softAssertion {
                contactBlock1.statusDeepSearchText.assertTextContains(CONTACTS[0].status)
                contactListBlock.item1BlockWithDesc.name.assertTextContains(CONTACTS[0].name)
                contactListBlock.item1BlockWithDesc.status.assertTextContains(CONTACTS[0].status)
                contactListBlock.item2BlockFactory.name.assertTextContains(CONTACTS[1].name)
                contactListBlock.item2BlockFactory.status.assertTextContains(CONTACTS[1].status)
            }
        }
    }
}

