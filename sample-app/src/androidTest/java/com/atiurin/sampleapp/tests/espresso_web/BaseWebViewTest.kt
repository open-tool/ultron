package com.atiurin.sampleapp.tests.espresso_web

import androidx.test.core.app.ActivityScenario
import com.atiurin.sampleapp.activity.WebViewActivity
import com.atiurin.sampleapp.pages.WebViewPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.core.espressoweb.webelement.WebDocument
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpTearDownRule
import org.junit.Before

abstract class BaseWebViewTest : BaseTest() {
    val page = WebViewPage()

    private val startActivity = SetUpTearDownRule().addSetUp {
        ActivityScenario.launch(WebViewActivity::class.java)
        WebDocument.forceJavascriptEnabled()
    }

    init {
        ruleSequence.addLast(startActivity)
    }
}