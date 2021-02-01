package com.atiurin.sampleapp.tests.uiautomator

import android.os.SystemClock
import com.atiurin.sampleapp.framework.Log
import com.atiurin.sampleapp.pages.UiObject2ElementsPage
import com.atiurin.sampleapp.tests.UiElementsTest
import org.junit.Test

class UltronUiAutomatorPerfTest: UiElementsTest() {
    val page = UiObject2ElementsPage()

    @Test
    fun perfTest(){
        val startTime = SystemClock.elapsedRealtime()
        for (i in 1..200){
            page.button.click()
            page.eventStatus.textContains(i.toString())
        }
        Log.debug("Duration ${SystemClock.elapsedRealtime() - startTime} ms")
    }
}