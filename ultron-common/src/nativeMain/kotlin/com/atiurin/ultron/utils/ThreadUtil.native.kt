package com.atiurin.ultron.utils

import platform.Foundation.NSThread

actual fun sleep(timeMs: Long) {
    val thread = NSThread.currentThread()
    val date = platform.Foundation.NSDate()
    val futureTime = date.timeIntervalSinceReferenceDate + timeMs / 1000.0
    while (platform.Foundation.NSDate().timeIntervalSinceReferenceDate < futureTime) {
        // Busy wait until the desired time elapses
    }
}