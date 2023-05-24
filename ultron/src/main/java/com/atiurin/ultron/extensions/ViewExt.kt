package com.atiurin.ultron.extensions

import android.view.View
import com.atiurin.ultron.utils.runOnUiThread

/**
 * Ultron is not responsible for the outcome of these actions.
 * If you use this approach, you clearly understand what you are doing.
 */
fun View.perform(action: View.() -> Unit) {
    runOnUiThread {
        action(this)
    }
}