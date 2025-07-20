package com.atiurin.sampleapp.tests.espresso

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.UiBlockActivity
import com.atiurin.sampleapp.data.repositories.CONTACTS
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.uiblock.EspressoUiBlockScreen
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.core.common.assertion.softAssertion
import com.atiurin.ultron.core.espresso.UltronEspressoUiBlock
import com.atiurin.ultron.extensions.withName
import com.atiurin.ultron.extensions.withTimeout
import com.atiurin.ultron.testlifecycle.activity.UltronActivityRule
import org.hamcrest.Matcher
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class UltronEspressoUiBlockTest : BaseTest() {
    @get:Rule
    val activityRule = UltronActivityRule(UiBlockActivity::class.java)

    @Test
    fun notUniqueUiElement_WithDeepSearch() {
        EspressoUiBlockScreen {
            contactItem1.name.isDisplayed()
            contactItem1.deepSearchChild.withTimeout(100).isDisplayed()
        }
    }

    @Test
    fun notUniqueUiElement_WithoutDeepSearch() {
        EspressoUiBlockScreen {
            AssertUtils.assertException {
                blockWithoutDeepSearch.deepSearchFalse.withTimeout(100).isDisplayed()
            }
        }
    }

    @Test
    fun uiBlockInBlock() {
        EspressoUiBlockScreen {
            contactsListBlock.item1.name.isDisplayed().hasText(CONTACTS[0].name)
            contactsListBlock.item1.status.isDisplayed().hasText(CONTACTS[0].status)
            contactsListBlock.item2.name.isDisplayed().hasText(CONTACTS[1].name)
            contactsListBlock.item2.status.isDisplayed().hasText(CONTACTS[1].status)
        }
    }

    @Test
    fun childElementDescription() {
        val descriptionPrefix = "Item with parent"
        val blockDesc = "Parent_Name"
        val expectedChildName = "$descriptionPrefix $blockDesc"

        class BlockDesc(blockMatcher: Matcher<View>, blockDescription: String) : UltronEspressoUiBlock(blockMatcher, blockDescription) {
            val name = child(withId(R.id.name)).withName("$descriptionPrefix $blockDescription")
        }
        BlockDesc(withId(R.id.contact_item_1), blockDesc).name.isDisplayed().withResultHandler {
            Assert.assertEquals(expectedChildName, it.operation.elementInfo.name)
        }.withTimeout(100).hasText("Invalid text")
    }

    @Test
    fun childBlockDescriptionTest() {
        val listBlockDesc = "ListBlock"
        val expectedItem1Description = "1 $descriptionPrefix $listBlockDesc"
        val expectedItem2Description = "2 $descriptionPrefix $listBlockDesc"
        val expectedChildNameDescInBlock1 = "$сhildNameDesc $expectedItem1Description"
        val expectedChildNameDescInBlock2 = "$сhildNameDesc $expectedItem2Description"

        val listBlock = ListUiBlock(withId(R.id.contact_items), listBlockDesc)
        softAssertion {
            listBlock.item1.uiBlock.withTimeout(100).withResultHandler {
                Assert.assertEquals(expectedItem1Description, it.operation.elementInfo.name)
            }.hasText("Invalid")
            listBlock.item1.name.withTimeout(100).withResultHandler {
                Assert.assertEquals(expectedChildNameDescInBlock1, it.operation.elementInfo.name)
            }.hasText("Invalid")
            listBlock.item2.name.withTimeout(100).withResultHandler {
                Assert.assertEquals(expectedChildNameDescInBlock2, it.operation.elementInfo.name)
            }.hasText("Invalid")
        }
    }
}

class BlockDesc(blockMatcher: Matcher<View>, blockDescription: String) : UltronEspressoUiBlock(blockMatcher, blockDescription) {
    val name = child(withId(R.id.name)).withName("$сhildNameDesc $blockDescription")
}

class ListUiBlock(blockMatcher: Matcher<View>, blockDescription: String) : UltronEspressoUiBlock(blockMatcher, blockDescription) {
    val item1 = child(BlockDesc(withId(R.id.contact_item_1), "1 $descriptionPrefix $blockDescription"))
    val item2 = child(
        childMatcher = withId(R.id.contact_item_2),
        uiBlockFactory = { updatedMatcher ->
            BlockDesc(updatedMatcher, blockDescription = "2 $descriptionPrefix $blockDescription")
        }
    )
}

const val descriptionPrefix = "Item with parent"
const val сhildNameDesc = "NamE"

