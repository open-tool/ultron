package com.atiurin.ultron.extensions

import androidx.annotation.IntegerRes
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector

class ByExt {
    companion object {
        fun resId(@IntegerRes resourceId: Int): BySelector {
            return By.res(getResourceName(resourceId))
        }
    }
}

fun getResourceName(@IntegerRes resourceId: Int): String {
    return InstrumentationRegistry.getInstrumentation().targetContext.resources.getResourceName(resourceId)
}