package com.atiurin.sampleapp.managers

import android.content.Context

class AccountManager(val context: Context){
    companion object {
        private const val expectedUserName = "joey"
        private const val expectedPassword = "1234"
        private const val USER_KEY = "username"
        private const val PASSWORD_KEY = "password"
    }

    fun login(user: String, password: String) : Boolean{
        var success = false
        // there should be some network request to app server
        if ((user == Companion.expectedUserName) &&(password == expectedPassword)){
            success = true
            with(PrefsManager(context)){
                savePref(USER_KEY, user)
                savePref(PASSWORD_KEY, password)
            }
        }
        return success
    }

    fun isLogedIn() : Boolean{
        var userName = ""
        var password = ""
        with(PrefsManager(context)){
            userName = getPref(USER_KEY)
            password = getPref(PASSWORD_KEY)
        }
        if (userName.isEmpty() || password.isEmpty()) return false
        return true
    }

    fun logout(){
        with(PrefsManager(context)){
            remove(USER_KEY)
            remove(PASSWORD_KEY)
        }
    }
}