package com.atiurin.ultron.hierarchy

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.atiurin.ultron.log.UltronLog
import java.io.File

class UiDeviceHierarchyDumper : HierarchyDumper {
    private val uiDevice: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    override fun dumpFullWindowHierarchy(file: File): HierarchyDumpResult {
        var isSuccess = false
        runCatching {
            uiDevice.dumpWindowHierarchy(file)
        }.onFailure {
            UltronLog.error("Couldn't dump window hierarchy. ${it.message}")
        }.onSuccess {
            UltronLog.debug("Window hierarchy is dumped to ${file.absolutePath}")
            isSuccess = true
        }
        return HierarchyDumpResult(isSuccess, file)
    }
}