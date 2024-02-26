package com.atiurin.sampleapp.screens.kaspresso

import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.atiurin.sampleapp.compose.LazyListItemPosition
import com.atiurin.sampleapp.compose.contactNameTestTag
import com.atiurin.sampleapp.compose.contactStatusTestTag
import com.atiurin.sampleapp.compose.contactsListTestTag
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode
import io.github.kakaocup.compose.node.element.lazylist.KLazyListItemNode
import io.github.kakaocup.compose.node.element.lazylist.KLazyListNode

class KaspressoComposeListScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<KaspressoComposeListScreen>(semanticsProvider = semanticsProvider) {
    val contactsList = KLazyListNode(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = {
            hasTestTag(contactsListTestTag)
        },
        itemTypeBuilder = {
            itemType(::ContactLazyListItemNode)
        },
        positionMatcher = { position -> SemanticsMatcher.expectValue(LazyListItemPosition, position) }
    )
}

class ContactLazyListItemNode(
    semanticsNode: SemanticsNode,
    semanticsProvider: SemanticsNodeInteractionsProvider
) : KLazyListItemNode<ContactLazyListItemNode>(semanticsNode, semanticsProvider) {
    val name: KNode = child {
        useUnmergedTree = true
        hasTestTag(contactNameTestTag)
    }
    val status: KNode = child {
        hasTestTag(contactStatusTestTag)
    }
    val avatar: KNode = child {
        hasContentDescription("avatar")
    }
}