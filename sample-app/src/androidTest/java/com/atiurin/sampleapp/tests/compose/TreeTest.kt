package com.atiurin.sampleapp.tests.compose

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToString
import com.atiurin.sampleapp.activity.ComposeElementsActivity
import com.atiurin.sampleapp.pages.ComposeElementsPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.allure.attachment.AttachUtil
import com.atiurin.ultron.core.compose.createUltronComposeRule
import com.atiurin.ultron.extensions.withTimeout
import com.atiurin.ultron.file.MimeType
import com.atiurin.ultron.utils.createCacheFile
import org.junit.Test

class TreeTest : BaseTest() {
    val page = ComposeElementsPage
    val composeRule = createUltronComposeRule<ComposeElementsActivity>()
    init {
        ruleSequence.add(composeRule)
    }
    @Test
    fun genereteSemanticsTreeTest(){
        val node = composeRule.onRoot().printToString()
        val file = createCacheFile("tree_", ".log")
        file.writeText(node)
        val fileName = AttachUtil.attachFile(file, MimeType.PLAIN_TEXT)
        hasText("asdasdqweqwe").withTimeout(100).assertIsDisplayed()
        composeRule.mainClock
    }
}