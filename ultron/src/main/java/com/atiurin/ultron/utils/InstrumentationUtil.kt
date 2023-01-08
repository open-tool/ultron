package com.atiurin.ultron.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Looper
import androidx.annotation.*
import androidx.test.platform.app.InstrumentationRegistry
import com.atiurin.ultron.exceptions.UltronException
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask

fun getResourceName(@AnyRes resourceId: Int, context: Context): String {
    return context.resources.getResourceName(resourceId)
}

fun getTargetResourceName(@AnyRes resourceId: Int): String {
    return getResourceName(resourceId, InstrumentationRegistry.getInstrumentation().targetContext)
}

fun getTestResourceName(@AnyRes resourceId: Int): String {
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

fun getDrawable(@DrawableRes resourceId: Int, context: Context): Drawable? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        context.getDrawable(resourceId)
    } else {
        throw UltronException("getDrawable is not supported for SDKs lower ${Build.VERSION_CODES.LOLLIPOP}")
    }
}

fun getTargetDrawable(@DrawableRes resourceId: Int): Drawable? {
    return getDrawable(resourceId, InstrumentationRegistry.getInstrumentation().targetContext)
}

fun getTestDrawable(@DrawableRes resourceId: Int): Drawable? {
    return getDrawable(resourceId, InstrumentationRegistry.getInstrumentation().context)
}

fun getColor(@ColorRes colorResId: Int, context: Context): Int {
    return if (Build.VERSION.SDK_INT <= 22) context.resources.getColor(colorResId) else context.getColor(colorResId)
}

fun getTargetColor(@ColorRes colorResId: Int): Int {
    return getColor(colorResId, InstrumentationRegistry.getInstrumentation().targetContext)
}

fun getTestColor(@ColorRes colorResId: Int): Int {
    return getColor(colorResId, InstrumentationRegistry.getInstrumentation().context)
}

fun getColorHex(color: Int): String {
    return String.format(
        Locale.ROOT, "#%02X%06X", 0xFF and Color.alpha(color), 0xFFFFFF and color
    )
}

fun <T> runOnUiThread(action: () -> T): T {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        return action()
    }

    // Note: This implementation is directly taken from ActivityTestRule
    val task: FutureTask<T> = FutureTask(action)
    InstrumentationRegistry.getInstrumentation().runOnMainSync(task)
    try {
        return task.get()
    } catch (e: ExecutionException) { // Expose the original exception
        throw e.cause!!
    }
}