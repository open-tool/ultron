package com.atiurin.sampleapp.activity

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.atiurin.sampleapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class XmlAndComposeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_compose)
        lifecycleScope.launch {
            // add compose element after delay
            delay(3000)

            val container = findViewById<LinearLayout>(R.id.container)

            val composeView = ComposeView(this@XmlAndComposeActivity).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                setContent {
                    Text(
                        text = "Compose Text",
                        fontSize = 18.sp
                    )
                }
            }

            container.addView(composeView)
        }
    }
}