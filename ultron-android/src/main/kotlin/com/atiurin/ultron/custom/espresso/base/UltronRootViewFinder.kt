package com.atiurin.ultron.custom.espresso.base

import androidx.test.espresso.Root
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.utils.isVisible
import com.atiurin.ultron.utils.runOnUiThread

fun getRootViewsList(): List<Root> = runOnUiThread {
    UltronConfig.Espresso.activeRootLister.listActiveRoots()
}

fun getVisibleRootViews(): List<Root> = getRootViewsList().filter { it.decorView.isVisible }