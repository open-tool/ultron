package com.atiurin.ultron.utils

import android.view.View
import android.view.ViewGroup

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

/**
 * Returns true when this view's visibility is [View.VISIBLE], false otherwise.
 *
 * ```
 * if (view.isVisible) {
 *     // Behavior...
 * }
 * ```
 *
 * Setting this property to true sets the visibility to [View.VISIBLE], false to [View.GONE].
 *
 * ```
 * view.isVisible = true
 * ```
 */
inline var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }
