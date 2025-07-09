package com.atiurin.sampleapp.tests.compose

import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performScrollToIndex
import com.atiurin.sampleapp.activity.ComposeListActivity
import com.atiurin.sampleapp.compose.contactStatusTestTag
import com.atiurin.sampleapp.compose.contactsListContentDesc
import com.atiurin.sampleapp.compose.contactsListHeaderTag
import com.atiurin.sampleapp.compose.contactsListTestTag
import com.atiurin.sampleapp.compose.getContactItemTestTagById
import com.atiurin.sampleapp.data.repositories.CONTACTS
import com.atiurin.sampleapp.data.repositories.ContactRepositoty
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.ComposeListPage
import com.atiurin.sampleapp.pages.ComposeSecondPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.core.common.options.ContentDescriptionContainsOption
import com.atiurin.ultron.core.common.options.TextContainsOption
import com.atiurin.ultron.core.compose.createUltronComposeRule
import com.atiurin.ultron.core.compose.list.composeList
import com.atiurin.ultron.extensions.assertIsDisplayed
import com.atiurin.ultron.extensions.assertTextEquals
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.extensions.findNodeInTree
import com.atiurin.ultron.extensions.withDescription
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class ComposeListTest : BaseTest() {
    @get:Rule
    val composeRule = createUltronComposeRule<ComposeListActivity>()

    private val listWithMergedTree = composeList(hasTestTag(contactsListTestTag), false).withDescription("Contacts list")
    private val listPage = ComposeListPage
    private val notExistedList = composeList(hasTestTag("askjhsalk jdhas dlqk "))
    private val emptyListTestTag = "emptyList"

    @Test
    fun item_existItem() {
        val index = 20
        val contact = CONTACTS[index]
        listWithMergedTree.assertIsDisplayed()
        listWithMergedTree.item(hasAnyDescendant(hasText(contact.name)).withDescription("Contact '${contact.name}'"))
            .assertIsDisplayed()
            .assertMatches(hasAnyDescendant(hasText(contact.name)).withDescription("Contact '${contact.name}'"))
    }

    @Test
    fun item_notExistItem() {
        AssertUtils.assertException {
            listWithMergedTree.item(hasText("123gakshgdasl kgas")).assertIsDisplayed()
        }
    }

    @Test
    fun visibleItem_indexInScope() {
        val index = 2
        val contact = CONTACTS[index]
        listWithMergedTree.visibleItem(index).printToLog("ULTRON")
            .assertMatches(hasAnyDescendant(hasText(contact.name)))
    }

    @Test
    fun visibleItem_indexOutOfScope() {
        AssertUtils.assertException {
            listWithMergedTree.visibleItem(20).assertIsDisplayed()
        }
    }

    @Test
    fun firstVisibleItem() {
        val contact = CONTACTS[0]
        listWithMergedTree.firstVisibleItem()
            .assertIsDisplayed()
            .assertMatches(hasAnyDescendant(hasText(contact.name)))
            .assertMatches(hasAnyDescendant(hasText(contact.status)))
    }

    @Test
    fun getVisibleItemByIndex() {
        val index = 3
        val contact = CONTACTS[index]
        listPage.getItemByIndex(index).apply {
            name.assertTextEquals(contact.name)
            status.assertTextEquals(contact.status)
        }
    }

    @Test
    fun getFirstVisibleItem() {
        val contact = CONTACTS[0]
        listPage.getFirstVisibleItem().apply {
            name.assertTextEquals(contact.name)
            status.assertTextEquals(contact.status)
        }
    }

    @Test
    fun scrollToIndex() {
        val index = 20
        val contact = CONTACTS[index]
        listWithMergedTree.scrollToIndex(index)
        hasText(contact.name).assertIsDisplayed()
    }

    @Test
    fun scrollToKey() {
        val index = 20
        val contact = CONTACTS[index]
        listWithMergedTree.scrollToKey(contact.name)
        hasText(contact.name).assertIsDisplayed()
    }

    @Test
    fun customPerformOnLazyList() {
        val index = 20
        val contact = CONTACTS[index]
        val children = listWithMergedTree.performOnList { node, interactoion ->
            interactoion.performScrollToIndex(index)
            node.children
        }
        hasText(contact.name).assertIsDisplayed()
        Assert.assertTrue(children.size > 10)
        val child = children.findNodeInTree(hasText(contact.name))
        Assert.assertNotNull(child)
    }

    @Test
    fun moveToAnotherComposeActivityPageTest() {
        val contact = CONTACTS.first()
        hasText(contact.name).click()
        ComposeSecondPage.name.assertTextEquals(contact.name)
        ComposeSecondPage.status.assertTextEquals(contact.status)
    }

    @Test
    fun getItem_ByTestTag_assertNameAndStatusOfContact() {
        val index = 20
        val contact = CONTACTS[index]
        listPage.getContactItemByTestTag(contact).apply {
            name.assertTextEquals(contact.name)
            status.assertTextContains(contact.status)
        }
    }

    @Test
    fun getItem_ByMatcher_assertNameAndStatusOfContact() {
        val index = 20
        val contact = CONTACTS[index]
        listPage.getContactItemByName(contact).apply {
            name.assertTextEquals(contact.name)
            status.assertTextContains(contact.status)
        }
    }

    @Test
    fun listHeader_asChild_TextContains() {
        listPage.lazyList.visibleChild(hasTestTag(contactsListHeaderTag))
            .assertTextContains("header", option = TextContainsOption(substring = true))
    }

    @Test
    fun listHeader_asChild_TextEquals() {
        listPage.lazyList.visibleChild(hasTestTag(contactsListHeaderTag).withDescription("header"))
            .assertTextEquals("Lazy column header")
    }

    @Test
    fun listHeader_asChild_TextEquals_mergedTree() {
        listWithMergedTree.visibleChild(hasTestTag(contactsListHeaderTag).withDescription("header"))
            .assertTextEquals("Lazy column header")
    }

    @Test
    fun visibleItemChild() {
        val index = 3
        val contact = CONTACTS[index]
        listPage.lazyList.onVisibleItemChild(index, hasTestTag(contactStatusTestTag)).assertTextEquals(contact.status)
    }

    @Test
    fun assertIsDisplayed_visibleList() {
        listWithMergedTree.assertIsDisplayed()
    }

    @Test
    fun assertIsDisplayed_invisibleList() {
        AssertUtils.assertException {
            notExistedList.withTimeout(1000).assertIsDisplayed()
        }
    }

    @Test
    fun assertIsNotDisplayed_visibleList() {
        AssertUtils.assertException { listWithMergedTree.withTimeout(1000).assertIsNotDisplayed() }
    }

    @Test
    fun assertExists_existedList() {
        listWithMergedTree.assertExists()
    }

    @Test
    fun assertExists_notExistedList() {
        AssertUtils.assertException { notExistedList.withTimeout(1000).assertExists() }
    }

    @Test
    fun assertDoesNotExist_notExistedList() {
        notExistedList.assertDoesNotExist()
    }

    @Test
    fun assertDoesNotExist_existedList() {
        AssertUtils.assertException { listWithMergedTree.withTimeout(1000).assertDoesNotExist() }
    }

    @Test
    fun assertContentDescriptionEquals_properContentDescription() {
        listWithMergedTree.assertContentDescriptionEquals(contactsListContentDesc)
    }

    @Test
    fun assertContentDescriptionEquals_invalidContentDescription() {
        AssertUtils.assertException { listWithMergedTree.withTimeout(1000).assertContentDescriptionEquals("some invalid desc") }
    }

    @Test
    fun assertContentDescriptionContains_properContentDescription() {
        listWithMergedTree.assertContentDescriptionContains(contactsListContentDesc.substring(0, 5), ContentDescriptionContainsOption(substring = true))
    }

    @Test
    fun assertContentDescriptionContains_invalidContentDescription() {
        AssertUtils.assertException { listWithMergedTree.withTimeout(1000).assertContentDescriptionContains("some invalid") }
    }

    @Test
    fun assertVisibleItemsCount_properCountProvided() {
        val count = listWithMergedTree.getVisibleItemsCount()
        listWithMergedTree.assertVisibleItemsCount(count)
    }

    @Test
    fun assertVisibleItemsCount_invalidCountProvided() {
        AssertUtils.assertException { listWithMergedTree.withTimeout(1000).assertVisibleItemsCount(100) }
    }

    @Test
    fun itemByPosition_propertyConfiguredTest() {
        val index = 20
        val contact = CONTACTS[index]
        val item = listPage.lazyList.item(20).assertIsDisplayed()
        item.assertMatches(hasTestTag(getContactItemTestTagById(contact)))
    }

    @Test
    fun getItemByPosition_propertyConfiguredTest() {
        val index = 20
        val contact = CONTACTS[index]
        listPage.getItemByPosition(index).apply {
            name.assertTextEquals(contact.name)
            status.assertTextEquals(contact.status)
            assertIsDisplayed()
        }
    }

    @Test
    fun assertItemDoesNotExistWithSearch_NotExistedItem() {
        listWithMergedTree.assertItemDoesNotExist(hasText("NOT EXISTED TeXT"))
    }

    @Test
    fun assertItemDoesNotExistWithSearch_ExistedItem() {
        val contact = ContactRepositoty.getLast()
        AssertUtils.assertException {
            listWithMergedTree.withTimeout(2000).assertItemDoesNotExist(hasText(contact.name))
        }
    }

    @Test
    fun getItem_NotExistedItemChild() {
        val index = 20
        val contact = CONTACTS[index]
        listPage.getContactItemByName(contact).apply {
            AssertUtils.assertException { notExisted.withTimeout(1000).assertIsDisplayed() }
        }
    }

    @Test
    fun assertNotEmpty_notEmptyList() {
        listWithMergedTree.assertNotEmpty()
    }

    @Test
    fun assertEmpty_notEmptyList() {
        AssertUtils.assertException { listWithMergedTree.withTimeout(100).assertEmpty() }
    }

}


