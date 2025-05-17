package com.atiurin.sampleapp.pages.uiblock

import androidx.test.uiautomator.BySelector
import com.atiurin.sampleapp.R
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2.Companion.bySelector
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2UiBlock
import com.atiurin.ultron.page.Screen

object UiObject2UiBlockScreen : Screen<UiObject2UiBlockScreen>() {
    val block1 = ContactItemUiObject2Block { bySelector(R.id.contact_item_1) }
    val block2 = UiObject2UiBlockWithoutDeepSearch { bySelector(R.id.contact_item_2) }
    val blocks = UiObject2ListUiBlock { bySelector(R.id.contact_items) }
}


class ContactItemUiObject2Block(val parent: () -> BySelector) : UltronUiObject2UiBlock(parent) {
    val name = child { bySelector(R.id.name) }
    val status = child { bySelector(R.id.status) }
    val deepSearchChild = child { bySelector(R.id.deep_search_child) }
}

class UiObject2UiBlockWithoutDeepSearch(val parent: () -> BySelector?) : UltronUiObject2UiBlock(parent) {
    private var parentName = parent.toString()

    constructor(parentName: String, parent: () -> BySelector?) : this(parent) {
        this.parentName = parentName
    }
    val name = child { bySelector(R.id.name) }
    val notExisted by lazy {
        child { bySelector(R.id.recycler_friends) }.withName("Not existed with parent $parentName")
    }
}

class UiObject2ListUiBlock(parent: () -> BySelector?) : UltronUiObject2UiBlock(parent) {
    val item1 = child(ContactItemUiObject2Block { bySelector(R.id.contact_item_1) })
    val item2 = child(
        childSelector = { bySelector(R.id.contact_item_2) },
        uiBlockFactory = { selector ->
            val b = UiObject2UiBlockWithoutDeepSearch("Contact Item 2", selector)
            b
        }
    )
}