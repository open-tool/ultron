package com.atiurin.sampleapp.pages

import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.page.Page

object ComposeSecondPage : Page<ComposeSecondPage>() {
    val name = hasTestTag("name")
    val status = hasTestTag("status")
}