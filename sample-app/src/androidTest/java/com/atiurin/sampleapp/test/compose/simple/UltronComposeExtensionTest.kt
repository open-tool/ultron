package com.atiurin.sampleapp.test.compose.simple

import androidx.compose.ui.test.hasTestTag
import com.atiurin.sampleapp.activity.ComposeElementsActivity
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.likesCounterButton
import com.atiurin.sampleapp.framework.ultronext.assertLikesCount
import com.atiurin.sampleapp.framework.ultronext.getLikes
import com.atiurin.sampleapp.test.base.UltronBaseTest
import com.atiurin.ultron.core.compose.createUltronComposeRule
import com.atiurin.ultron.extensions.click
import org.junit.Rule
import org.junit.Test

class UltronComposeExtensionTest : UltronBaseTest() {
    @get:Rule
    val composeRule = createUltronComposeRule<ComposeElementsActivity>()

    @Test
    fun assertLikesTest(){
        val likesButton = hasTestTag(likesCounterButton)
        likesButton.click().assertLikesCount(expected = 1)
        val initialValue = likesButton.getLikes()
        val clicksAmount = 10
        for (i in 1..clicksAmount){
            likesButton.click()
        }
        likesButton.assertLikesCount(initialValue + clicksAmount)
    }
}