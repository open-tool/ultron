package com.atiurin.ultron.core.uiautomator

import android.app.Instrumentation
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice

object UiAutomatorConfig {
    val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
}