package com.atiurin.sampleapp.idlingresources

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource

object DemoIdlingResource: IdlingResource {
    val counting = CountingIdlingResource("DEMO")
    override fun getName() = counting.name

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        counting.registerIdleTransitionCallback(callback)
    }

    override fun isIdleNow(): Boolean = counting.isIdleNow
}