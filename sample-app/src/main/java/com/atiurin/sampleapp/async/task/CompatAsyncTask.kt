package com.atiurin.sampleapp.async.task

import android.os.AsyncTask

@Suppress("DEPRECATION")
class CompatAsyncTask : AsyncTask<Void, Void, Void>() {

    companion object {
        const val COMPAT_ASYNC_TASK_TIME_EXECUTION = 5000
        const val ASYNC = "ASYNC"
    }

    @Deprecated("Suppress")
    override fun doInBackground(vararg params: Void?): Void? {
        val startTime = System.currentTimeMillis()
        while (!isCancelled && System.currentTimeMillis() - startTime < COMPAT_ASYNC_TASK_TIME_EXECUTION) {
            Thread.sleep(1000)
        }
        return null
    }

    @Deprecated("Suppress")
    override fun onPostExecute(result: Void?) {}

    fun start() {
        executeOnExecutor(THREAD_POOL_EXECUTOR)
    }

    fun stop() {
        cancel(true)
    }
}

