package com.atiurin.sampleapp.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import com.atiurin.sampleapp.activity.ComposeElementsActivity
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.likesCounterButton
import com.atiurin.sampleapp.activity.ComposeElementsActivity.Constants.likesCounterContentDesc

@Composable
fun ButtonWithCount() {
    val counter = remember {
        mutableStateOf(0)
    }
    Button(onClick = { counter.value++ }, modifier = Modifier
        .semantics {
            testTag = likesCounterButton
            contentDescription = likesCounterContentDesc
            likeCounter = counter.value
        }) {
        Icon(
            imageVector = Icons.Filled.Favorite, contentDescription = null
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text("Like count = ${counter.value}",
            modifier = Modifier.semantics {
                testTag = ComposeElementsActivity.likesCounterTextContainer
                contentDescription = ComposeElementsActivity.likesCounterTextContainerContentDesc
            }
        )
    }
}

val LikeCounter = SemanticsPropertyKey<Int>("LikesCounter")
var SemanticsPropertyReceiver.likeCounter by LikeCounter