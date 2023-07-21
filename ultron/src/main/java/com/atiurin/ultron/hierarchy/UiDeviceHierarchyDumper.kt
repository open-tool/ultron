package com.atiurin.ultron.hierarchy

import com.atiurin.ultron.core.config.UltronConfig.UiAutomator.Companion.uiDevice
import com.atiurin.ultron.log.UltronLog
import java.io.File

class UiDeviceHierarchyDumper : HierarchyDumper {
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