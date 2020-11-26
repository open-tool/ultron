package com.atiurin.ultron.core.uiautomator

import android.app.Instrumentation
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.Configurator
import androidx.test.uiautomator.UiDevice

object UiAutomatorConfig {
    val uiDevice: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    fun setIdlingTimeout(timeoutMs: Long){
        Configurator.getInstance().apply {
            waitForIdleTimeout = timeoutMs
            waitForSelectorTimeout = timeoutMs
        }
    }
}