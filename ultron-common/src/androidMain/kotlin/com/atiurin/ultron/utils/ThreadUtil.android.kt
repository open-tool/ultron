package com.atiurin.ultron.utils

import android.os.SystemClock

actual fun sleep(timeMs: Long) {
    SystemClock.sleep(timeMs)
}