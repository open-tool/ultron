package com.atiurin.sampleapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.atiurin.sampleapp.managers.AccountManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val accountManager = AccountManager(applicationContext)
        if (accountManager.isLogedIn()){
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }else{
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }
        finish()
    }

}