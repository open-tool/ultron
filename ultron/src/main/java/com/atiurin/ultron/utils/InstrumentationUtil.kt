package com.atiurin.ultron.utils

import android.content.Context
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
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

fun getString(@StringRes resourceId: Int, context: Context): String {
    return context.getString(resourceId)
}

fun getTargetString(@StringRes resourceId: Int): String {
    return getString(resourceId, InstrumentationRegistry.getInstrumentation().targetContext)
}

fun getTestString(@StringRes resourceId: Int): String {
    return getString(resourceId, InstrumentationRegistry.getInstrumentation().context)
}