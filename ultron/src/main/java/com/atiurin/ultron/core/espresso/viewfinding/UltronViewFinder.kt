package com.atiurin.ultron.core.espresso.viewfinding

import android.view.View
import java.util.concurrent.atomic.AtomicReference

interface UltronViewFinder {
    fun findView(viewContainer: AtomicReference<View>)
}