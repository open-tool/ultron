package com.atiurin.sampleapp.screens.kaspresso

import android.view.View
import com.atiurin.sampleapp.R
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.Matcher

object KaspressoRecyclerViewScreen : Screen<KaspressoRecyclerViewScreen>(){
    val recyclerView = KRecyclerView(
        builder = { withId(R.id.recycler_friends) },
        itemTypeBuilder = {
            itemType(::KContactListItem)
        }
    )

    class KContactListItem(matcher: Matcher<View>) : KRecyclerItem<KContactListItem>(matcher) {
        val name = KTextView(matcher) { withId(R.id.tv_name) }
        val status = KTextView(matcher) { withId(R.id.tv_status) }
        val avatar = KImageView(matcher) { withId(R.id.avatar) }
    }
}