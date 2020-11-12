package com.atiurin.sampleapp.framework

import android.app.Activity
import android.util.Log
import androidx.test.rule.ActivityTestRule

open class CustomActivityTestRule<T : Activity> : ActivityTestRule<T> {
    constructor(activityClass: Class<T>) : super(activityClass)
    constructor(activityClass: Class<T>, initialTouchMode: Boolean, launchActivity: Boolean) : super(activityClass, initialTouchMode, launchActivity)

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        Log.d("Life>>", "beforeActivityLaunched")
    }

    override fun afterActivityLaunched() {
        super.afterActivityLaunched()
        Log.d("Life>>", "afterActivityLaunched")
    }

    override fun finishActivity() {
        super.finishActivity()
        Log.d("Life>>", "finishActivity")
    }
}