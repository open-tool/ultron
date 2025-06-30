package com.atiurin.sampleapp.tests.espresso

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.activity.CrashActivity
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.allure.step.step
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.extensions.hasText
import org.junit.Test

class CrashTest: BaseTest() {
    override val beforeTest: () -> Unit = {
        step("Launch app"){
            ActivityScenario.launch(CrashActivity::class.java)
        }
    }

    @Test
    fun crashAppTest() = test {
        go {
            withId(R.id.crash_button).click().isDisplayed()
            withId(R.id.edit_text).hasText("CLICKED")
        }
    }
}