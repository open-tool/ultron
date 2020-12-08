package com.atiurin.ultron.utils

import android.content.Context
import androidx.annotation.IntegerRes
import androidx.test.platform.app.InstrumentationRegistry

fun getResourceName(@IntegerRes resourceId: Int, context: Context): String {
    return context.resources.getResourceName(resourceId)
}

fun getTargetResourceName(@IntegerRes resourceId: Int): String {
    return getResourceName(resourceId, InstrumentationRegistry.getInstrumentation().targetContext)
}

fun getTestResourceName(@IntegerRes resourceId: Int): String {
    return getResourceName(resourceId, InstrumentationRegistry.getInstrumentation().context)
}