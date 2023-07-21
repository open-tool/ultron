package com.atiurin.ultron.allure.hierarchy

import com.atiurin.ultron.allure.attachment.AttachUtil
import com.atiurin.ultron.hierarchy.HierarchyDumper
import com.atiurin.ultron.hierarchy.UiDeviceHierarchyDumper
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.utils.createCacheFile

class AllureHierarchyDumper {
    private val dumper: HierarchyDumper = UiDeviceHierarchyDumper()

    fun dumpAndAttach(name: String = "window_hierarchy"): Boolean {
        val tempFile = createCacheFile()
        val result = dumper.dumpFullWindowHierarchy(tempFile)
        val fileName = AttachUtil.attachFile(
            name = "$name${result.mimeType.extension}",
            file = tempFile,
            mimeType = result.mimeType
        )
        UltronLog.info("WindowHierarchy file '$fileName' has attached to Allure report")
        return result.isSuccess
    }
}