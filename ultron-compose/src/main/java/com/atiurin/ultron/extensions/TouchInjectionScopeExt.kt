package com.atiurin.ultron.extensions

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.TouchInjectionScope
import com.atiurin.ultron.core.compose.nodeinteraction.SwipePosition
import com.atiurin.ultron.core.compose.nodeinteraction.UltronComposeOffsets
import com.atiurin.ultron.core.compose.option.ComposeSwipeOption

internal fun TouchInjectionScope.getUltronComposeOffset(position: UltronComposeOffsets) = when (position) {
    UltronComposeOffsets.CENTER -> center
    UltronComposeOffsets.CENTER_LEFT -> centerLeft.copy(x = 1f)
    UltronComposeOffsets.CENTER_RIGHT -> centerRight
    UltronComposeOffsets.TOP_CENTER -> topCenter.copy(y = 1f)
    UltronComposeOffsets.TOP_LEFT -> topLeft.copy(x = 1f, y = 1f)
    UltronComposeOffsets.TOP_RIGHT -> topRight.copy(y = 1f)
    UltronComposeOffsets.BOTTOM_CENTER -> bottomCenter
    UltronComposeOffsets.BOTTOM_LEFT -> bottomLeft.copy(x = 1f)
    UltronComposeOffsets.BOTTOM_RIGHT -> bottomRight
}

internal fun TouchInjectionScope.getDefaultLongClickDuration() = viewConfiguration.longPressTimeoutMillis + 100
internal fun TouchInjectionScope.getDefaultDoubleClickDelay() = (viewConfiguration.doubleTapMinTimeMillis + viewConfiguration.doubleTapTimeoutMillis) / 2
internal fun TouchInjectionScope.provideSwipeDownPosition(option: ComposeSwipeOption): SwipePosition {
    val startY = top + option.startYOffset
    val endY = bottom + option.endYOffset
    val startX = centerX + option.startXOffset
    val endX = centerX + option.endXOffset
    require(startY <= endY) {
        "startY=$startY needs to be less than or equal to endY=$endY"
    }
    return SwipePosition(start = Offset(startX, startY), end = Offset(startY, endY))
}
internal fun TouchInjectionScope.provideSwipeUpPosition(option: ComposeSwipeOption): SwipePosition {
    val startY = bottom + option.startYOffset
    val endY = top + option.endYOffset
    val startX = centerX + option.startXOffset
    val endX = centerX + option.endXOffset
    require(startY >= endY) {
        "startY=$startY needs to be greater than or equal to endY=$endY"
    }
    return SwipePosition(start = Offset(startX, startY), end = Offset(startY, endY))
}
internal fun TouchInjectionScope.provideSwipeLeftPosition(option: ComposeSwipeOption): SwipePosition {
    val startY = centerY + option.startYOffset
    val endY = centerY + option.endYOffset
    val startX = right + option.startXOffset
    val endX = left + option.endXOffset
    require(startX >= endX) {
        "startX=$startX needs to be greater than or equal to endX=$endX"
    }
    return SwipePosition(start = Offset(startX, startY), end = Offset(startY, endY))
}
internal fun TouchInjectionScope.provideSwipeRightPosition(option: ComposeSwipeOption): SwipePosition {
    val startY = centerY + option.startYOffset
    val endY = centerY + option.endYOffset
    val startX = left + option.startXOffset
    val endX = right + option.endXOffset
    require(startX <= endX) {
        "startX=$startX needs to be less than or equal to endX=$endX"
    }
    return SwipePosition(start = Offset(startX, startY), end = Offset(startY, endY))
}




