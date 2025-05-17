package com.atiurin.sampleapp.pages

import androidx.compose.ui.test.hasTestTag
import com.atiurin.sampleapp.activity.ComposeElementsActivity
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.clickListenerButton
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.contactBlock1Tag
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.contactBlock2Tag
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.contactsListTag
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.likesCounterButton
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.radioButtonFemaleTestTag
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.radioButtonMaleTestTag
import com.atiurin.sampleapp.compose.RegionsClickListenerTestTags
import com.atiurin.sampleapp.framework.ultronext.progressBar
import com.atiurin.sampleapp.pages.uiblock.ContactUiBlock
import com.atiurin.sampleapp.pages.uiblock.ContactUiBlockWithCustomConstructor
import com.atiurin.sampleapp.pages.uiblock.ListUiBlock
import com.atiurin.ultron.page.Page

object ComposeElementsPage : Page<ComposeElementsPage>() {
    val contactListBlock = ListUiBlock(hasTestTag(contactsListTag))
    val contactBlock1 = ContactUiBlock(hasTestTag(contactBlock1Tag))
    val contactBlock2 = ContactUiBlockWithCustomConstructor(hasTestTag(contactBlock2Tag), "Block parent")
    val status = hasTestTag(ComposeElementsActivity.Constants.statusText)
    val likesCounter = hasTestTag(likesCounterButton)
    val longAndDoubleClickButton = hasTestTag(clickListenerButton)
    val regionsNode = hasTestTag(RegionsClickListenerTestTags.regionsNode)
    val clickedRegion = hasTestTag(RegionsClickListenerTestTags.regionsClickedText)
    val editableText = hasTestTag(ComposeElementsActivity.editableText)
    val swipeableNode = hasTestTag(ComposeElementsActivity.swipeableNode)
    val disabledButton = hasTestTag(ComposeElementsActivity.disabledButton)
    val simpleCheckbox = hasTestTag(ComposeElementsActivity.simpleCheckbox)
    val progressBar by progressBar { hasTestTag(ComposeElementsActivity.progressBar) }
    val maleRadioButton = hasTestTag(radioButtonMaleTestTag)
    val femaleRadioButton = hasTestTag(radioButtonFemaleTestTag)
    val notExistedElement = hasTestTag("NotExistedTestTag")
}
