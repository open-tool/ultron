package com.atiurin.sampleapp.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.*
import android.webkit.WebView
import android.widget.*
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.async.AsyncDataLoading
import com.atiurin.sampleapp.async.GetContacts
import com.atiurin.sampleapp.async.UseCase
import com.atiurin.sampleapp.compose.ContactsList
import com.atiurin.sampleapp.compose.getContactItemTestTagById
import com.atiurin.sampleapp.data.entities.Contact
import com.atiurin.sampleapp.data.repositories.ContactRepositoty
import com.atiurin.sampleapp.data.viewmodel.ContactsViewModel
import com.atiurin.sampleapp.data.viewmodel.DataViewModel
import com.atiurin.sampleapp.view.listeners.OnSwipeTouchListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class UiElementsActivity : AppCompatActivity() {
    var lastEventDescription: TextView? = null
    var clickedInRow = 0
    var lastEvent = Event.NO_EVENT
    val model: DataViewModel by viewModels()
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uielements)
        val simpleButton: Button = findViewById(R.id.button1)
        simpleButton.visibility = GONE
        lastEventDescription = findViewById(R.id.last_event_status)
        val enableCheckBox: CheckBox = findViewById(R.id.checkbox_enable)
        val clickableCheckBox: CheckBox = findViewById(R.id.checkbox_clickable)
        val selectedCheckBox: CheckBox = findViewById(R.id.checkbox_selected)
        val focusableCheckBox: CheckBox = findViewById(R.id.checkbox_focusable)
        val radioGroupVisibility: RadioGroup = findViewById(R.id.radio_group_visibility)
        val etContentDescription: EditText = findViewById(R.id.et_contentDesc)
        val webView: WebView = findViewById(R.id.webview)
        val jsCheckBox: CheckBox = findViewById(R.id.checkbox_js_enabled)
        val imageView : ImageView = findViewById(R.id.image_view)
        webView.settings.javaScriptEnabled = true
        val customHtml = applicationContext.assets.open("webview_small.html").reader().readText()
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
        val context = this
        imageView.setOnTouchListener(object : OnSwipeTouchListener(context) {
            override fun onSwipeUp() {
                setLastEvent(Event.SWIPE_UP)
                Log.d("Ultron", "onSwipeTop")
            }

            override fun onSwipeRight() {
                setLastEvent(Event.SWIPE_RIGHT)
                Log.d("Ultron", "onSwipeRight")
            }

            override fun onSwipeLeft() {
                setLastEvent(Event.SWIPE_LEFT)
                Log.d("Ultron", "onSwipeLeft")
            }

            override fun onSwipeDown() {
                setLastEvent(Event.SWIPE_DOWN)
                Log.d("Ultron", "onSwipeBottom")
            }

        })
        val observer = Observer<String> {
            simpleButton.visibility = VISIBLE
            setLastEvent(Event.DATA_LOADED)
        }
        model.data.observe(this, observer)
        GlobalScope.async {
            AsyncDataLoading(1600)(
                UseCase.None,
                onSuccess = { model.data.value = it },
                onFailure = { Toast.makeText(this@UiElementsActivity, "Failed to load data", Toast.LENGTH_LONG).show() }
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
    }

    @SuppressLint("SetTextI18n")
    fun setLastEvent(event: Event, desc: String? = null) {
        var status = desc
        lastEvent = event
        if (lastEvent == Event.CLICK) {
            clickedInRow++
            status += " $clickedInRow"
        } else clickedInRow = 0
        lastEventDescription?.text = "${event.name}${if (desc != null) ": $status" else ""}"
    }

    enum class Event {
        NO_EVENT, CLICK, LONG_CLICK, CLICKABLE, ENABLED, SELECTED, FOCUSABLE, DISPLAYED, JS_ENABLED, CONTENT_DESC,
        SWIPE_LEFT, SWIPE_RIGHT, SWIPE_UP, SWIPE_DOWN, DATA_LOADED
    }
}