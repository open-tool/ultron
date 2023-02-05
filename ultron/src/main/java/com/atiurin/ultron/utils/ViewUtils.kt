package com.atiurin.ultron.utils

import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach

/**
 * Returns a [Sequence] over this view and its descendants recursively.
 * This is a depth-first traversal similar to [View.findViewById].
 * A view with no children will return a single-element sequence of itself.
 */
val View.allViews: Sequence<View>
    get() = sequence {
        yield(this@allViews)
        if (this@allViews is ViewGroup) {
            yieldAll(this@allViews.descendants)
        }
    }

/**
 * Returns a [Sequence] over the child views in this view group recursively.
 * This performs a depth-first traversal.
 * A view with no children will return a zero-element sequence.
 */
val ViewGroup.descendants: Sequence<View>
    get() = sequence {
        forEach { child ->
            yield(child)
            if (child is ViewGroup) {
                yieldAll(child.descendants)
            }
        }
    }
