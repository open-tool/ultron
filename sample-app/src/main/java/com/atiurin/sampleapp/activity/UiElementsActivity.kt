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

class UiElementsActivity : AppCompatActivity() {
    var lastEventDescription: TextView? = null
    var clickedInRow = 0
    var lastEvent = Event.NO_EVENT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uielements)
        val simpleButton: Button = findViewById(R.id.button1)
        lastEventDescription = findViewById(R.id.last_event_status)
        val enableCheckBox: CheckBox = findViewById(R.id.checkbox_enable)
        val clickableCheckBox: CheckBox = findViewById(R.id.checkbox_clickable)
        val selectedCheckBox: CheckBox = findViewById(R.id.checkbox_selected)
        val focusableCheckBox: CheckBox = findViewById(R.id.checkbox_focusable)
        val radioGroupVisibility: RadioGroup = findViewById(R.id.radio_group_visibility)
        val etContentDescription: EditText = findViewById(R.id.et_contentDesc)
        val webView: WebView = findViewById(R.id.webview)
        val jsCheckBox: CheckBox = findViewById(R.id.checkbox_js_enabled)
        webView.settings.javaScriptEnabled = true
        val customHtml = """
            <!DOCTYPE html>                
                <html>
                <head>
                  <title>Android Web View</title>
                </head>
                <body>
                    <h1>Fruits</h1>
                    <ol>
                        <li><a href = "apple.html" id = "apple">Apple</a></li>
                        <li><a href = "banana.html" id = "banana">Banana</a></li>
                    </ol>
                     <p>The button below activates a JavaScript when it is clicked.</p>
                    <form>
                        <input type="button" id="button1" value="Click me" onclick="msg()">
                    </form>
                    <script>
                        function msg() {
                            alert("Hello world!");
                        }
                    </script>
               </body>
            </html>
            """
        webView.loadData(customHtml, "text/html", "UTF-8")

        simpleButton.setOnClickListener {
            setLastEvent(Event.CLICK, getString(R.string.button_event_click))
        }
        simpleButton.setOnLongClickListener { view ->
            setLastEvent(Event.LONG_CLICK, getString(R.string.button_event_long_click))
            return@setOnLongClickListener true
        }
        enableCheckBox.setOnClickListener { view ->
            val checked = (view as CheckBox).isChecked
            simpleButton.isEnabled = checked
            setLastEvent(Event.ENABLED, checked.toString())
        }
        clickableCheckBox.setOnClickListener { view ->
            val checked = (view as CheckBox).isChecked
            simpleButton.isClickable = checked
            setLastEvent(Event.CLICKABLE, checked.toString())
        }
        selectedCheckBox.setOnClickListener { view ->
            val checked = (view as CheckBox).isChecked
            simpleButton.isSelected = checked
            setLastEvent(Event.SELECTED, checked.toString())
        }
        jsCheckBox.setOnClickListener { view ->
            val checked = (view as CheckBox).isChecked
            webView.settings.javaScriptEnabled = checked
            setLastEvent(Event.JS_ENABLED, checked.toString())
        }
        radioGroupVisibility.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_visible -> simpleButton.visibility = View.VISIBLE
                R.id.radio_invisible -> simpleButton.visibility = View.INVISIBLE
                R.id.radio_gone -> simpleButton.visibility = View.GONE
            }
            setLastEvent(Event.DISPLAYED, simpleButton.visibility.toString())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusableCheckBox.visibility = VISIBLE
            focusableCheckBox.setOnClickListener { view ->
                val checked = (view as CheckBox).isChecked
                if (checked) {
                    simpleButton.focusable = FOCUSABLE
                } else {
                    simpleButton.focusable = NOT_FOCUSABLE
                }
                setLastEvent(Event.FOCUSABLE, checked.toString())
            }
        }
        val addTextChangedListener =
            etContentDescription.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(text: Editable?) {
                    simpleButton.contentDescription = text
                    setLastEvent(Event.CONTENT_DESC, text.toString())
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
    }

    fun setLastEvent(event: Event, desc: String) {
        var status = desc
        lastEvent = event
        if (lastEvent == Event.CLICK) {
            clickedInRow++
            status += " $clickedInRow"
        } else clickedInRow = 0
        lastEventDescription?.text = "${event.name}: $status"
    }

    enum class Event {
        NO_EVENT, CLICK, LONG_CLICK, CLICKABLE, ENABLED, SELECTED, FOCUSABLE, DISPLAYED, JS_ENABLED, CONTENT_DESC
    }
}