package com.atiurin.sampleapp.activity

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.viewinterop.AndroidView
import com.atiurin.sampleapp.R

internal val composeInViewTag = "ComposeInView"
internal val viewInComposeTag = "ViewInCompose"

class ComposeInteropActivity : ComponentActivity() {

    @ExperimentalMaterialApi
    @ExperimentalUnitApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose_interop)

        findViewById<ComposeView>(R.id.compose_in_view).setContent {
            Text(
                text = composeInViewTag,
                modifier = Modifier.testTag(composeInViewTag)
            )
        }
        findViewById<ComposeView>(R.id.view_in_compose).setContent {
            AndroidView(
                modifier = Modifier.testTag(viewInComposeTag),
                factory = { TextView(it).apply { text = viewInComposeTag } }
            )
        }
    }
}


