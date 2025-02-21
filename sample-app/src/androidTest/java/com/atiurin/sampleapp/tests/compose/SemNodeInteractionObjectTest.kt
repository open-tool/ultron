package com.atiurin.sampleapp.tests.compose

import com.atiurin.sampleapp.activity.ActionsStatus
import com.atiurin.sampleapp.activity.ComposeElementsActivity
import com.atiurin.sampleapp.pages.ComposeElementsPage
import com.atiurin.sampleapp.tests.BaseTest
import com.atiurin.ultron.core.common.options.TextContainsOption
import com.atiurin.ultron.core.compose.createUltronComposeRule
import com.atiurin.ultron.core.compose.nodeinteraction.click
import com.atiurin.ultron.core.compose.nodeinteraction.doubleClick
import com.atiurin.ultron.core.compose.nodeinteraction.longClick
import com.atiurin.ultron.core.compose.option.ComposeSwipeOption
import com.atiurin.ultron.extensions.assertTextEquals
import com.atiurin.ultron.extensions.withMetaInfo
import com.atiurin.ultron.extensions.withName
import org.junit.Rule
import org.junit.Test

class SemNodeInteractionObjectTest : BaseTest() {
    @get:Rule
    val composeRule = createUltronComposeRule<ComposeElementsActivity>()
    val page = ComposeElementsPage

    @Test
    fun clickTest(){
        page.likesCounter.withMetaInfo("likesCounter")
            .click()
            .assertTextContains(option = TextContainsOption(substring = true), expected = "= 1")
    }

    @Test
    fun longClickTest(){
        page.longAndDoubleClickButton.withName("longAndDoubleClickButton").longClick()
        page.status.assertTextEquals(ActionsStatus.LongClicked.name)
    }

    @Test
    fun doubleClick_doubleClickable() {
        page.longAndDoubleClickButton.withName("longAndDoubleClickButton").doubleClick()
        page.status.assertTextEquals(ActionsStatus.DoubleClicked.name)
    }

    @Test
    fun swipeDownTest(){
        page.swipeableNode.withName("swipeableNode").swipeDown()
        page.status.assertTextEquals(ActionsStatus.SwipeDown.name)
    }

    @Test
    fun swipeUp() {
        page.swipeableNode.withName("swipeableNode").swipeUp()
        page.status.assertTextEquals(ActionsStatus.SwipeUp.name)
    }

    @Test
    fun swipeRight() {
        page.swipeableNode.withName("swipeableNode").swipeRight()
        page.status.assertTextEquals(ActionsStatus.SwipeRight.name)
    }

    @Test
    fun swipeLeft() {
        page.swipeableNode.withName("swipeableNode").swipeLeft()
        page.status.assertTextEquals(ActionsStatus.SwipeLeft.name)
    }

    @Test
    fun swipe_option() {
        page.swipeableNode.withName("swipeableNode").swipeLeft(ComposeSwipeOption(durationMs = 1000L))
        page.status.assertTextEquals(ActionsStatus.SwipeLeft.name)
    }

    @Test
    fun swipe_general() {
        page.swipeableNode.withName("Swipeable Node").swipe(ComposeSwipeOption(
            startXOffset = 0.1f,
            startYOffset = 0.1f,
            endXOffset = 0.9f,
            endYOffset = 0.1f,
            durationMs = 1000L
        ))
        page.status.assertTextEquals(ActionsStatus.SwipeRight.name)
    }
}