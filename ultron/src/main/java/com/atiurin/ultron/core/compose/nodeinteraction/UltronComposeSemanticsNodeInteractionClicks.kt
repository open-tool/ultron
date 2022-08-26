package com.atiurin.ultron.core.compose.nodeinteraction

import com.atiurin.ultron.core.common.options.ClickOption
import com.atiurin.ultron.core.common.options.DoubleClickOption
import com.atiurin.ultron.core.common.options.LongClickOption

fun UltronComposeSemanticsNodeInteraction.click(option: ClickOption? = null) = click(UltronComposeOffsets.CENTER, option)
fun UltronComposeSemanticsNodeInteraction.clickCenterLeft(option: ClickOption? = null) = click(UltronComposeOffsets.CENTER_LEFT, option)
fun UltronComposeSemanticsNodeInteraction.clickCenterRight(option: ClickOption? = null) = click(UltronComposeOffsets.CENTER_RIGHT, option)
fun UltronComposeSemanticsNodeInteraction.clickTopCenter(option: ClickOption? = null) = click(UltronComposeOffsets.TOP_CENTER, option)
fun UltronComposeSemanticsNodeInteraction.clickTopLeft(option: ClickOption? = null) = click(UltronComposeOffsets.TOP_LEFT, option)
fun UltronComposeSemanticsNodeInteraction.clickTopRight(option: ClickOption? = null) = click(UltronComposeOffsets.TOP_RIGHT, option)
fun UltronComposeSemanticsNodeInteraction.clickBottomCenter(option: ClickOption? = null) = click(UltronComposeOffsets.BOTTOM_CENTER, option)
fun UltronComposeSemanticsNodeInteraction.clickBottomLeft(option: ClickOption? = null) = click(UltronComposeOffsets.BOTTOM_LEFT, option)
fun UltronComposeSemanticsNodeInteraction.clickBottomRight(option: ClickOption? = null) = click(UltronComposeOffsets.BOTTOM_RIGHT, option)

fun UltronComposeSemanticsNodeInteraction.longClick(option: LongClickOption? = null) = longClick(UltronComposeOffsets.CENTER, option)
fun UltronComposeSemanticsNodeInteraction.longClickCenterLeft(option: LongClickOption? = null) = longClick(UltronComposeOffsets.CENTER_LEFT, option)
fun UltronComposeSemanticsNodeInteraction.longClickCenterRight(option: LongClickOption? = null) = longClick(UltronComposeOffsets.CENTER_RIGHT, option)
fun UltronComposeSemanticsNodeInteraction.longClickTopCenter(option: LongClickOption? = null) = longClick(UltronComposeOffsets.TOP_CENTER, option)
fun UltronComposeSemanticsNodeInteraction.longClickTopLeft(option: LongClickOption? = null) = longClick(UltronComposeOffsets.TOP_LEFT, option)
fun UltronComposeSemanticsNodeInteraction.longClickTopRight(option: LongClickOption? = null) = longClick(UltronComposeOffsets.TOP_RIGHT, option)
fun UltronComposeSemanticsNodeInteraction.longClickBottomCenter(option: LongClickOption? = null) = longClick(UltronComposeOffsets.BOTTOM_CENTER, option)
fun UltronComposeSemanticsNodeInteraction.longClickBottomLeft(option: LongClickOption? = null) = longClick(UltronComposeOffsets.BOTTOM_LEFT, option)
fun UltronComposeSemanticsNodeInteraction.longClickBottomRight(option: LongClickOption? = null) = longClick(UltronComposeOffsets.BOTTOM_RIGHT, option)

fun UltronComposeSemanticsNodeInteraction.doubleClick(option: DoubleClickOption? = null) = doubleClick(UltronComposeOffsets.CENTER, option)
fun UltronComposeSemanticsNodeInteraction.doubleClickCenterLeft(option: DoubleClickOption? = null) = doubleClick(UltronComposeOffsets.CENTER_LEFT, option)
fun UltronComposeSemanticsNodeInteraction.doubleClickCenterRight(option: DoubleClickOption? = null) = doubleClick(UltronComposeOffsets.CENTER_RIGHT, option)
fun UltronComposeSemanticsNodeInteraction.doubleClickTopCenter(option: DoubleClickOption? = null) = doubleClick(UltronComposeOffsets.TOP_CENTER, option)
fun UltronComposeSemanticsNodeInteraction.doubleClickTopLeft(option: DoubleClickOption? = null) = doubleClick(UltronComposeOffsets.TOP_LEFT, option)
fun UltronComposeSemanticsNodeInteraction.doubleClickTopRight(option: DoubleClickOption? = null) = doubleClick(UltronComposeOffsets.TOP_RIGHT, option)
fun UltronComposeSemanticsNodeInteraction.doubleClickBottomCenter(option: DoubleClickOption? = null) = doubleClick(UltronComposeOffsets.BOTTOM_CENTER, option)
fun UltronComposeSemanticsNodeInteraction.doubleClickBottomLeft(option: DoubleClickOption? = null) = doubleClick(UltronComposeOffsets.BOTTOM_LEFT, option)
fun UltronComposeSemanticsNodeInteraction.doubleClickBottomRight(option: DoubleClickOption? = null) = doubleClick(UltronComposeOffsets.BOTTOM_RIGHT, option)

