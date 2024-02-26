package com.atiurin.sampleapp.screens.kaspresso

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.atiurin.sampleapp.activity.ComposeChatActivity
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class KaspressoComposeChatScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<KaspressoComposeChatScreen>(semanticsProvider = semanticsProvider) {
    val name = KNode(semanticsProvider) {
        hasTestTag(ComposeChatActivity.chatNameTestTag)
    }
    val status = KNode(semanticsProvider) {
        hasTestTag(ComposeChatActivity.chatStatusTestTag)
    }

}