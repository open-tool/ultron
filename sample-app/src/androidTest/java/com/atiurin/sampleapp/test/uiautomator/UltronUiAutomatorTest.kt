package com.atiurin.sampleapp.test.uiautomator

import com.atiurin.sampleapp.test.base.UltronBaseTest
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.atiurin.sampleapp.activity.UiElementsActivity
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2.Companion.byResId
import org.junit.Rule
import org.junit.Test
import com.atiurin.sampleapp.R
import com.atiurin.ultron.core.uiautomator.uiobject.UltronUiObject.Companion.uiResId
import com.atiurin.ultron.core.uiautomator.uiobject.UltronUiObject.Companion.uiText
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2.Companion.by
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2.Companion.byText
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.page.Screen
import com.atiurin.ultron.utils.getResourceName
import org.junit.Assert.assertTrue

class UltronUiAutomatorTest : UltronBaseTest() {
    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(UiElementsActivity::class.java)

    @Test
    fun googleUiAutomatorTest() {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val event = uiDevice.wait(
            Until.findObject(
                By.res(
                    getResourceName(
                        R.id.last_event_status,
                        InstrumentationRegistry.getInstrumentation().targetContext
                    )
                )
            ),
            3_000
        )
        val enableCheckbox = uiDevice.findObject(By.text("Enable button"))
        enableCheckbox.click()
        assertTrue("Assert event contains ENABLED", event.text.contains("ENABLED"))
    }

    @Test
    fun ultronUiAutomatorTest() {
        UiAutomatorScreen {
            enableCheckbox.click()
            lastStatus.textContains("ENABLED")
        }
    }
}

object UiAutomatorScreen : Screen<UiAutomatorScreen>() {
    val lastStatus = uiResId(R.id.last_event_status)
    val enableCheckbox = uiText("Enable button")
}