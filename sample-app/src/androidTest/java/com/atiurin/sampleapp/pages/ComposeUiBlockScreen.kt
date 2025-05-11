package com.atiurin.sampleapp.pages

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasTestTag
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.contactBlock1Tag
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.contactBlock2Tag
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.firstNameTag
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.lastNameTag
import com.atiurin.ultron.core.compose.child
import com.atiurin.ultron.core.compose.page.ComposeUiBlock
import com.atiurin.ultron.extensions.withName

class ContactUiBlock(parent: SemanticsMatcher): ComposeUiBlock(parent){
    val textWithoutDeepSearch = child(hasTestTag(firstNameTag), descendantSearch = false).withName("No deep search element")
    val deepSearchText = child(hasTestTag(lastNameTag))
}

class ContactUiBlockWithCustomConstructor(parent: SemanticsMatcher): ComposeUiBlock(parent){
    private var parentName = parent.description

    constructor(parent: SemanticsMatcher, parentName: String = ""): this(parent){
        this.parentName = parentName
    }
    val deepSearchText by lazy { child(hasTestTag(lastNameTag)).withName("Last name with parent $parentName") }
}

class ListUiBlock(parent: SemanticsMatcher): ComposeUiBlock(parent){
    val firstContact = child(uiBlock = ContactUiBlock(hasTestTag(contactBlock1Tag)))
    //multiplatform & custom constructor variant
    val secondContact = child(
        childMatcher = hasTestTag(contactBlock2Tag),
        uiBlockFactory = { m -> ContactUiBlock(m) }
    )
}