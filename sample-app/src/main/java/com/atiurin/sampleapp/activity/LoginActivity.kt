package com.atiurin.sampleapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.atiurin.sampleapp.R
import com.atiurin.sampleapp.managers.AccountManager

class LoginActivity : AppCompatActivity(){
    lateinit var etUserName : EditText
    lateinit var etPassword : EditText
    lateinit var loginBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val bar = supportActionBar
        bar!!.title = "Login or Sign Up"

        etUserName = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        loginBtn = findViewById(R.id.login_button)

        loginBtn.setOnClickListener{
            val accountManager = AccountManager(applicationContext)
            val userName = etUserName.text.toString()
            val password = etPassword.text.toString()

            if (userName.isEmpty()){
                with(etUserName){
                    setHint("Enter user name")
                    setHintTextColor(resources.getColor(android.R.color.holo_red_dark))
                }
            }
            if (password.isEmpty()){
                with(etPassword){
                    setHint("Enter password")
                    setHintTextColor(resources.getColor(android.R.color.holo_red_dark))
                }
            }
            val result = accountManager.login(userName, password)
            if (result){
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }else{
                var toast = Toast.makeText(applicationContext, "Wrong login or password", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                    toast.show()
            }
        }
    }


}