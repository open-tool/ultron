package com.atiurin.ultron.utils

import android.app.Activity
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.atiurin.ultron.log.UltronLog

object ActivityUtil {

    fun getResumedActivity(): Activity? {
        var resumedActivity: Activity? = null

        val findResumedActivity = {
            val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(Stage.RESUMED)
            if (resumedActivities.iterator().hasNext()) {
                resumedActivity = resumedActivities.iterator().next()
            }
        }

        runOnUiThread { findResumedActivity() }

        resumedActivity ?: UltronLog.error("No resumed activity found")
        return resumedActivity
    }
}