package com.atiurin.sampleapp.activity

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.atiurin.sampleapp.R

@Suppress("DEPRECATION")
class CustomClicksActivity : AppCompatActivity() {

    private val infiniteTask = CompatAsyncTask()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_clicks)
    }

    class CompatAsyncTask : AsyncTask<Void, Void, Void>() {

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

    fun startCompatAsyncTask() {
        infiniteTask.start()
    }

    fun stopCompatAsyncTask() {
        infiniteTask.stop()
    }

    companion object {
        const val COMPAT_ASYNC_TASK_TIME_EXECUTION = 5000
    }
}