package com.atiurin.sampleapp.pages.uiblock

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasTestTag
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.contactBlock1Tag
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.contactBlock2Tag
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.contactNameTag
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.contactStatusTag
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.contactsListTag
import com.atiurin.sampleapp.pages.uiblock.ComposeListUiBlock.Companion.listBlockDesc
import com.atiurin.ultron.core.compose.child
import com.atiurin.ultron.core.compose.page.UltronComposeUiBlock
import com.atiurin.ultron.extensions.withName
import com.atiurin.ultron.page.Screen

object ComposeUiBlockScreen : Screen<ComposeUiBlockScreen>(){
    val contactBlock1 = ContactUiBlockWithoutDesc(hasTestTag(contactBlock1Tag))
    val contactBlock2 = ContactUiBlockWithDesc(hasTestTag(contactBlock2Tag), "Block parent")
    val contactListBlock = ComposeListUiBlock(hasTestTag(contactsListTag), listBlockDesc)
}
class ContactUiBlockWithoutDesc(blockMatcher: SemanticsMatcher) : UltronComposeUiBlock(blockMatcher) {
    val nameWithoutDeepSearch = child(hasTestTag(contactNameTag), descendantSearch = false).withName("No deep search element")
    val statusDeepSearchText = child(hasTestTag(contactStatusTag))
}

class ContactUiBlockWithDesc(blockMatcher: SemanticsMatcher, blockDescription: String) : UltronComposeUiBlock(blockMatcher, blockDescription) {
    val name = child(hasTestTag(contactNameTag)).withName("$сhildNameDesc $blockDescription")
    val status = child(hasTestTag(contactStatusTag))

    companion object {
        const val сhildNameDesc = "NamE"
    }
}

class ComposeListUiBlock(parent: SemanticsMatcher, blockDescription: String) : UltronComposeUiBlock(parent) {
    val itemWithoutDesc = child(uiBlock = ContactUiBlockWithoutDesc(hasTestTag(contactBlock1Tag)))
    val item1BlockWithDesc = child(ContactUiBlockWithDesc(hasTestTag(contactBlock1Tag), "1 $descriptionPrefix $blockDescription"))
    val item2BlockFactory = child(
        childMatcher = hasTestTag(contactBlock2Tag),
        uiBlockFactory = { updatedMatcher ->
            ContactUiBlockWithDesc(updatedMatcher, blockDescription = "2 $descriptionPrefix $blockDescription")
        }
    )

    companion object {
        const val descriptionPrefix = "Item with parent"
        val listBlockDesc = "ListBlock"
    }
}

