package com.atiurin.ultron.extensions

import androidx.annotation.IntegerRes
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import com.atiurin.ultron.utils.getTargetResourceName

class ByExt {
    companion object {
        fun resId(@IntegerRes resourceId: Int): BySelector {
            return By.res(getTargetResourceName(resourceId))
        }
    }
}

