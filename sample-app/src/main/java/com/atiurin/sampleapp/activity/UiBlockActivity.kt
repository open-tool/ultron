package com.atiurin.sampleapp.activity

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.data.repositories.CONTACTS

class UiBlockActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uiblock)
        val contactItem1: LinearLayout = this.findViewById(R.id.contact_item_1)
        val contactItem2: LinearLayout = this.findViewById(R.id.contact_item_2)
        contactItem1.findViewById<TextView>(R.id.name).text = CONTACTS[0].name
        contactItem1.findViewById<TextView>(R.id.status).text = CONTACTS[0].status
        contactItem2.findViewById<TextView>(R.id.name).text = CONTACTS[1].name
        contactItem2.findViewById<TextView>(R.id.status).text = CONTACTS[1].status
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}