package com.atiurin.sampleapp.activity

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class BusyActivity : Activity(){
    private val handler = Handler(Looper.getMainLooper())
    private val busyRunnable = object : Runnable {
        override fun run() {
            // Post a delayed runnable to keep the main thread busy indefinitely
            handler.postDelayed(this, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Start the busy loop
        handler.post(busyRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(busyRunnable)
    }
}