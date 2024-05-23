package com.atiurin.ultron.utils
import kotlin.native.concurrent.ThreadLocal
import platform.Foundation.NSThread

actual fun sleep(timeMs: Long) {
    val thread = NSThread.currentThread()
    @ThreadLocal val date = platform.Foundation.NSDate()
    val futureTime = date.timeIntervalSinceReferenceDate + milliseconds / 1000.0
    while (date.timeIntervalSinceReferenceDate < futureTime) {
        // Busy wait until the desired time elapses
    }
}