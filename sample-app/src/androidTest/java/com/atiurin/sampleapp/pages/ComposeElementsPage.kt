package com.atiurin.sampleapp.pages

import androidx.compose.ui.test.hasTestTag
import com.atiurin.sampleapp.compose.RegionsClickListenerTestTags
import com.atiurin.ultron.page.Page
import com.atiurin.sampleapp.activity.ComposeElementsActivity
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.clickListenerButton
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.likesCounterButton
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.radioButtonFemaleTestTag
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.radioButtonMaleTestTag

object ComposeElementsPage : Page<ComposeElementsPage>() {
    val status = hasTestTag(ComposeElementsActivity.Constants.statusText)
    val likesCounter = hasTestTag(likesCounterButton)
    val longAndDoubleClickButton = hasTestTag(clickListenerButton)
    val regionsNode = hasTestTag(RegionsClickListenerTestTags.regionsNode)
    val clickedRegion = hasTestTag(RegionsClickListenerTestTags.regionsClickedText)
    val editableText = hasTestTag(ComposeElementsActivity.editableText)
    val swipeableNode = hasTestTag(ComposeElementsActivity.swipeableNode)
    val disabledButton = hasTestTag(ComposeElementsActivity.disabledButton)
    val simpleCheckbox = hasTestTag(ComposeElementsActivity.simpleCheckbox)
    val progressBar = hasTestTag(ComposeElementsActivity.progressBar)
    val maleRadioButton = hasTestTag(radioButtonMaleTestTag)
    val femaleRadioButton = hasTestTag(radioButtonFemaleTestTag)
}