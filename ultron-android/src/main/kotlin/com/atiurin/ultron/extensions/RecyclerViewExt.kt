package com.atiurin.ultron.extensions

import androidx.recyclerview.widget.RecyclerView
import com.atiurin.ultron.utils.runOnUiThread

internal fun RecyclerView.instantScrollToPosition(
    position: Int,
    snapPreferredSide: Float = 0f
) {
    val adjustedPos = position.coerceIn(0, (adapter?.itemCount ?: 1) - 1)

    runOnUiThread {
        layoutManager?.scrollToPosition(adjustedPos)

        post {
            layoutManager?.apply {
                findViewByPosition(adjustedPos)?.let { targetView ->
                    val parentExtent = if (canScrollVertically()) height else width
                    val childExtent =
                        if (canScrollVertically()) targetView.height else targetView.width

                    when {
                        childExtent > parentExtent -> {
                            if (canScrollVertically()) scrollBy(0, targetView.top)
                            else scrollBy(targetView.left, 0)
                        }

                        else -> {
                            val snapOffset = (parentExtent - childExtent) * snapPreferredSide
                            if (canScrollVertically()) {
                                scrollBy(0, targetView.top - snapOffset.toInt())
                            } else {
                                scrollBy(targetView.left - snapOffset.toInt(), 0)
                            }
                        }
                    }
                }
            }
        }
    }
}