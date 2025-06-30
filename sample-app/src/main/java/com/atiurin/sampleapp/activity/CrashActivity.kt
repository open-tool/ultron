package com.atiurin.sampleapp.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.atiurin.sampleapp.R

class CrashActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)
        val crashButton: Button = findViewById(R.id.crash_button)
        val editText: EditText = findViewById(R.id.edit_text)
        throw RuntimeException("Test purpose crash happened!")
    }
}