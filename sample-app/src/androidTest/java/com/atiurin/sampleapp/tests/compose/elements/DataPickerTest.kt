package com.atiurin.sampleapp.tests.compose.elements

import androidx.compose.ui.test.hasTestTag
import com.atiurin.sampleapp.activity.ComposeRouterActivity
import com.atiurin.sampleapp.compose.DatePickerTestData
import com.atiurin.sampleapp.compose.DatePickerTestTags
import com.atiurin.sampleapp.compose.DatePickerTestTags.SetDatePickerTimeCustomActionLabel
import com.atiurin.sampleapp.compose.screen.NavigationTestTags
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.core.compose.createUltronComposeRule
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.extensions.performCustomAccessibilityActionWithLabel
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import org.junit.Test
import java.util.concurrent.TimeUnit

class DataPickerTest : BaseTest() {
    private val composeRule = createUltronComposeRule<ComposeRouterActivity>()
    private val navigateRule = SetUpRule().add { hasTestTag(NavigationTestTags.DatePicker).click() }
    init {
        ruleSequence.add(composeRule, navigateRule)
    }
    @Test
    fun selectDateTest(){
        hasTestTag(DatePickerTestTags.DockerIconButton).click()
        DatePickerTestData.time = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(5)
        hasTestTag(DatePickerTestTags.DataPicker)
            .performCustomAccessibilityActionWithLabel(SetDatePickerTimeCustomActionLabel)
            .withTimeout(5000).printToLog("Ultron")
        Thread.sleep(10_000)
    }
}


