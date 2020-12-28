package com.atiurin.sampleapp.activity

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.*
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.atiurin.sampleapp.R

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        val webView: WebView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true
        val customHtml = applicationContext.assets.open("webview.html").reader().readText()
        webView.loadData(customHtml, "text/html", "UTF-8")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
    }
}