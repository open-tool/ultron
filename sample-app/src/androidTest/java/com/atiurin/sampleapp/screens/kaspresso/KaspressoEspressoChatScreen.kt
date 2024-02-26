package com.atiurin.sampleapp.screens.kaspresso

import android.view.View
import com.atiurin.sampleapp.R
import com.atiurin.ultron.page.Screen
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.Matcher

object KaspressoEspressoChatScreen : Screen<KaspressoEspressoChatScreen>(){
    val toolbarTitle = KTextView { withId(R.id.toolbar_title) }
    val messageTextInput = KEditText { withId(R.id.message_input_text) }
    val sendMessageButton = KButton { withId(R.id.send_button) }
    val messagesList = KRecyclerView(
        builder = { withId(R.id.messages_list) },
        itemTypeBuilder = {}
    )
}