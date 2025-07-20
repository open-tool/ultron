package com.atiurin.sampleapp.tests.compose

import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToString
import com.atiurin.sampleapp.activity.ComposeElementsActivity
import com.atiurin.sampleapp.pages.ComposeElementsPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.allure.attachment.AttachUtil
import com.atiurin.ultron.core.compose.createSimpleUltronComposeRule
import com.atiurin.ultron.file.MimeType
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.utils.createCacheFile
import org.junit.Test

class TreeTest : BaseTest() {
    val page = ComposeElementsPage
    val composeRule = createSimpleUltronComposeRule<ComposeElementsActivity>()
    init {
        ruleSequence.add(composeRule)
    }
    @Test
    fun generateSemanticsTreeTest(){
        val node = composeRule.onRoot(useUnmergedTree = true).printToString()
        val file = createCacheFile("tree_", ".log")
        file.writeText(node)
        val fileName = AttachUtil.attachFile(file, MimeType.PLAIN_TEXT)
        UltronLog.error(node)
    }
}