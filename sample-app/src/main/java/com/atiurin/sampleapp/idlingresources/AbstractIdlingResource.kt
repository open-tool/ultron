package com.atiurin.sampleapp.idlingresources

import androidx.annotation.Nullable
import androidx.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicBoolean
import androidx.test.espresso.IdlingResource.ResourceCallback

abstract class AbstractIdlingResource : IdlingResource {
    @Nullable
    @Volatile
    private var mCallback: ResourceCallback? = null
    private val mIsIdleNow = AtomicBoolean(true)
    override fun getName(): String {
        return this.javaClass.name
    }

    override fun isIdleNow(): Boolean {
        return mIsIdleNow.get()
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        mCallback = callback
    }

    fun setIdleState(isIdleNow: Boolean) {
        mIsIdleNow.set(isIdleNow)
        if (isIdleNow && mCallback != null) {
            mCallback?.onTransitionToIdle()
        }
    }
}