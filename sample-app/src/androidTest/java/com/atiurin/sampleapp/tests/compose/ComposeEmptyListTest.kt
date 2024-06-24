package com.atiurin.sampleapp.tests.compose

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.test.hasTestTag
import com.atiurin.sampleapp.framework.utils.AssertUtils
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.core.compose.createDefaultUltronComposeRule
import com.atiurin.ultron.core.compose.list.composeList
import org.junit.Rule
import org.junit.Test

class ComposeEmptyListTest : BaseTest() {
    @get:Rule
    val composeRule = createDefaultUltronComposeRule()

    private val emptyListTestTag = "emptyList"

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

    private fun setEmptyListContent() {
        composeRule.setContent {
            LazyColumn(
                modifier = Modifier.semantics { testTag = emptyListTestTag }
            ) {}
        }
    }
}