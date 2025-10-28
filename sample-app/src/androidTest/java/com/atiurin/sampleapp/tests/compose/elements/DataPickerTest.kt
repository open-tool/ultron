package com.atiurin.sampleapp.tests.compose.elements

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performCustomAccessibilityActionWithLabel
import com.atiurin.sampleapp.activity.ComposeRouterActivity
import com.atiurin.sampleapp.compose.DatePickerTestData
import com.atiurin.sampleapp.compose.DatePickerTestTags
import com.atiurin.sampleapp.compose.DatePickerTestTags.SetDatePickerTimeCustomActionLabel
import com.atiurin.sampleapp.compose.screen.NavigationTestTags
import com.atiurin.sampleapp.framework.utils.TimeUtils
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.sampleapp.utils.convertMillisToDate
import com.atiurin.ultron.core.compose.createSimpleUltronComposeRule
import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeSemanticsNodeInteraction
import com.atiurin.ultron.core.compose.operation.ComposeOperationType
import com.atiurin.ultron.core.compose.operation.UltronComposeOperationParams
import com.atiurin.ultron.extensions.assertTextContains
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import org.junit.Test
import java.util.concurrent.TimeUnit

class DataPickerTest : BaseTest() {
    private val composeRule = createSimpleUltronComposeRule<ComposeRouterActivity>()
    private val navigateRule = SetUpRule().add { hasTestTag(NavigationTestTags.DatePicker).click() }
    init {
        ruleSequence.add(composeRule, navigateRule)
    }

    /**
     * [convertMillisToDate] is defined in app to show the date, see [com.atiurin.sampleapp.compose.DatePickerKt.DatePickerDocked]
     * [TimeUtils.getTimestampStartOfDay] is used cause DatePicker return a start of the selected date timestamp
     */
    @Test
    fun selectDateTest(){
        hasTestTag(DatePickerTestTags.DockedIconButton).click()
        val time = TimeUtils.getTimestampStartOfDay() + TimeUnit.DAYS.toMillis(120)
        hasTestTag(DatePickerTestTags.DataPicker).setDatePickerTime(time)
        hasTestTag(DatePickerTestTags.SelectedDateValue).assertTextContains(convertMillisToDate(time))
    }
}

// Make this action native for Ultron
@OptIn(ExperimentalTestApi::class)
fun UltronComposeSemanticsNodeInteraction.setDatePickerTime(timeMs: Long) = perform(
    UltronComposeOperationParams(
        operationName = "SetDatePickerTime '${TimeUtils.formatTimestamp(timeMs)}' for '${elementInfo.name}'",
        operationDescription = "Compose SetDatePickerTime '${TimeUtils.formatTimestamp(timeMs)}' for '${elementInfo.name}' during $timeoutMs ms",
        operationType = ComposeOperationType.CUSTOM
    )
) {
    DatePickerTestData.time = timeMs
    semanticsNodeInteraction.performCustomAccessibilityActionWithLabel(SetDatePickerTimeCustomActionLabel)
}

fun SemanticsMatcher.setDatePickerTime(timeMs: Long) = UltronComposeSemanticsNodeInteraction(this).setDatePickerTime(timeMs)




