package com.atiurin.sampleapp.pages.uiblock

import androidx.test.uiautomator.BySelector
import com.atiurin.sampleapp.R
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2.Companion.bySelector
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2UiBlock
import com.atiurin.ultron.page.Screen

object UiObject2UiBlockScreen : Screen<UiObject2UiBlockScreen>() {
    val block1 = ContactItemUiObject2Block { bySelector(R.id.contact_item_1) }
    val block2 = ContactItemUiObject2Block("Block 2") { bySelector(R.id.contact_item_2) }
    val blocks = UiObject2ListUiBlock("Item blocks") { bySelector(R.id.contact_items) }
}


class ContactItemUiObject2Block(blockDesc: String = "", blockSelector: () -> BySelector) : UltronUiObject2UiBlock(blockDesc, blockSelector) {
    val name = child(bySelector(R.id.name)).withName("Name in block $blockDesc")
    val status = child(bySelector(R.id.status))
    val deepSearchChild = child(bySelector(R.id.deep_search_child))
    val notExisted = child(bySelector(R.id.recycler_friends)).withTimeout(100)
}

class UiObject2ListUiBlock(desc: String = "", parent: () -> BySelector) : UltronUiObject2UiBlock(desc, parent) {
    val item1 = child(ContactItemUiObject2Block { bySelector(R.id.contact_item_1) })
    val item2 = child(
        selector = bySelector(R.id.contact_item_2),
        description = "Item 2 in block $desc",
        uiBlockFactory = { desc, selector ->
            ContactItemUiObject2Block(desc, selector)
        }
    )
}