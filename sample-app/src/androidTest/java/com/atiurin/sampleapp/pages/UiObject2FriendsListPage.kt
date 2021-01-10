package com.atiurin.sampleapp.pages

import androidx.test.uiautomator.By
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.data.repositories.ContactRepositoty
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2.Companion.by
import com.atiurin.ultron.core.uiautomator.uiobject2.UltronUiObject2.Companion.byResId
import com.atiurin.ultron.page.Page

object UiObject2FriendsListPage : Page<UiObject2FriendsListPage>() {
    val list = byResId(R.id.recycler_friends)
    val topElement = by(By.text(ContactRepositoty.getFirst().name))
    val bottomElement = by(By.text(ContactRepositoty.getLast().name))
}