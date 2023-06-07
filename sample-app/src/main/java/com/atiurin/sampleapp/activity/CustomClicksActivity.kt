package com.atiurin.sampleapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.async.task.CompatAsyncTask
import com.atiurin.sampleapp.async.task.CompatAsyncTask.Companion.ASYNC

class CustomClicksActivity : AppCompatActivity() {

    private val compatAsyncTask = CompatAsyncTask()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_clicks)
        if(shouldBeAsyncTaskStart()) {
            startCompatAsyncTask()
        }
    }

    fun shouldBeAsyncTaskStart(): Boolean = intent.getBooleanExtra(ASYNC, false)

    fun startCompatAsyncTask() {
        compatAsyncTask.start()
    }

    fun stopCompatAsyncTask() {
        compatAsyncTask.stop()
    }

}