package com.atiurin.sampleapp.pages.uiblock

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.atiurin.sampleapp.R
import com.atiurin.ultron.core.espresso.UltronEspressoUiBlock
import com.atiurin.ultron.extensions.withName
import com.atiurin.ultron.page.Screen
import org.hamcrest.Matcher

object EspressoUiBlockScreen : Screen<EspressoUiBlockScreen>() {
    val contactItem1 = ContactItemUiBlock(withId(R.id.contact_item_1))
    val blockWithoutDeepSearch = ContactItemUiBlockWithoutDeepSearch(withId(R.id.contact_item_2))
    val contactsListBlock = ContactsListUiBlock(withId(R.id.contact_items))
}

class ContactItemUiBlock(parent: Matcher<View>) : UltronEspressoUiBlock(parent) {
    private var parentName = "defaultValue"

    constructor(parent: Matcher<View>, parentName: String = "") : this(parent) {
        this.parentName = parentName
    }

    val name by lazy { child(withId(R.id.name)).withName("Contact name with parent $parentName") }
    val status = child(withId(R.id.status)).withName("Contact item status")
    val deepSearchChild = child(withId(R.id.deep_search_child))
}

class ContactItemUiBlockWithoutDeepSearch(parent: Matcher<View>) : UltronEspressoUiBlock(parent) {
    val deepSearchFalse = child(withId(R.id.deep_search_child), descendantSearch = false)
}

class ContactsListUiBlock(parent: Matcher<View>) : UltronEspressoUiBlock(parent) {
    val item1 = child(ContactItemUiBlock(withId(R.id.contact_item_1)))
    val item2 = child(
        childMatcher = withId(R.id.contact_item_2),
        uiBlockFactory = { updatedMatcher ->
            ContactItemUiBlock(updatedMatcher, parentName = "Contact Item 2")
        }
    )
}