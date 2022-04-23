package com.atiurin.sampleapp.tests.compose

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.test.*
import com.atiurin.sampleapp.activity.ComposeListActivity
import com.atiurin.sampleapp.compose.*
import com.atiurin.sampleapp.data.repositories.CONTACTS
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.pages.ComposeListPage
import com.atiurin.sampleapp.pages.ComposeSecondPage
import com.atiurin.ultron.core.common.options.ContentDescriptionContainsOption
import com.atiurin.ultron.core.common.options.TextContainsOption
import com.atiurin.ultron.core.compose.createUltronComposeRule
import com.atiurin.ultron.core.compose.list.composeList
import com.atiurin.ultron.extensions.assertTextEquals
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.extensions.assertIsDisplayed
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.lang.AssertionError

class ComposeListTest {
    @get:Rule
    val composeRule = createUltronComposeRule<ComposeListActivity>()
    val listWithMergedTree = composeList(hasTestTag(contactsListTestTag), false)
    val listPage = ComposeListPage
    val notExistedList = composeList(hasTestTag("askjhsalk jdhas dlqk "))
    val emptyListTestTag = "emptyList"

    @Test
    fun item_existItem() {
        val index = 20
        val contact = CONTACTS[index]
        listWithMergedTree.item(hasText(contact.name))
            .assertIsDisplayed()
            .assertTextContains(contact.name)
    }

    @Test
    fun item_notExistItem() {
        AssertUtils.assertException { listWithMergedTree.item(hasText("123gakshgdasl kgas")).assertIsDisplayed() }
    }

    @Test
    fun visibleItem_indexInScope() {
        val index = 2
        val contact = CONTACTS[index]
        listWithMergedTree.visibleItem(index)
            .assertTextContains(contact.name)
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
            .assertTextContains(contact.name)
            .assertTextContains(contact.status)
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
        val child = children.find { child -> child.config[SemanticsProperties.Text].any { it.text == contact.name } }
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
        listPage.getContactItemById(contact).apply {
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
        listPage.lazyList.visibleChild(hasTestTag(contactsListHeaderTag))
            .assertTextEquals("Lazy column header")
    }

    @Test
    fun listHeader_asChild_TextEquals_mergedTree() {
        listWithMergedTree.visibleChild(hasTestTag(contactsListHeaderTag))
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
    fun assertNotEmpty_notEmptyList() {
        listWithMergedTree.assertNotEmpty()
    }

    @Test
    fun assertNotEmpty_emptyList() {
        setEmptyListContent()
        AssertUtils.assertException {
            composeList(hasTestTag(emptyListTestTag)).withTimeout(100).assertNotEmpty()
        }
    }

    @Test
    fun assertEmpty_emptyList() {
        setEmptyListContent()
        composeList(hasTestTag(emptyListTestTag)).assertEmpty()
    }

    @Test
    fun assertEmpty_notEmptyList() {
        AssertUtils.assertException { listWithMergedTree.withTimeout(100).assertEmpty() }
    }

    @Test
    fun assertVisibleItemsCount_properCountProvided(){
        val count = listWithMergedTree.getVisibleItemsCount()
        listWithMergedTree.assertVisibleItemsCount(count)
    }

    @Test
    fun assertVisibleItemsCount_invalidCountProvided(){
        AssertUtils.assertException { listWithMergedTree.withTimeout(1000).assertVisibleItemsCount(100) }
    }

    private fun setEmptyListContent() {
        composeRule.setContent {
            LazyColumn(
                modifier = Modifier.semantics { testTag = emptyListTestTag }
            ) {}
        }
    }
}


