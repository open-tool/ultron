package com.atiurin.sampleapp.pages.uiblock

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.atiurin.sampleapp.R
import com.atiurin.ultron.core.espresso.UltronEspressoUiBlock
import com.atiurin.ultron.extensions.withName
import com.atiurin.ultron.page.Screen
import org.hamcrest.Matcher

object EspressoUiBlockScreen : Screen<EspressoUiBlockScreen>() {
    val contactItem1 = ContactItemUiBlock(withId(R.id.contact_item_1), "Item 1")
    val blockWithoutDeepSearch = ContactItemUiBlockWithoutDeepSearch(withId(R.id.contact_item_2))
    val contactsListBlock = ContactsListUiBlock(withId(R.id.contact_items), "Items list")
}

class ContactItemUiBlock(blockMatcher: Matcher<View>, blockDescription: String) : UltronEspressoUiBlock(blockMatcher) {
    val name = child(withId(R.id.name)).withName("Contact name with parent $blockDescription")
    val status = child(withId(R.id.status)).withName("Contact item status")
    val deepSearchChild = child(withId(R.id.deep_search_child))
}

class ContactItemUiBlockWithoutDeepSearch(parent: Matcher<View>) : UltronEspressoUiBlock(parent) {
    val deepSearchFalse = child(withId(R.id.deep_search_child), descendantSearch = false)
}

class ContactsListUiBlock(blockMatcher: Matcher<View>, blockDescription: String) : UltronEspressoUiBlock(blockMatcher, blockDescription) {
    val item1 = child(ContactItemUiBlock(withId(R.id.contact_item_1), "Item 1"))
    val item2 = child(
        childMatcher = withId(R.id.contact_item_2),
        uiBlockFactory = { updatedMatcher ->
            ContactItemUiBlock(updatedMatcher, blockDescription = "Contact Item 2 with parent $blockDescription")
        }
    )
}