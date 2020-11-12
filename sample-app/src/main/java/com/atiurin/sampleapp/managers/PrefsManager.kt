package com.atiurin.sampleapp.managers

import android.content.Context.MODE_PRIVATE
import android.content.Context


class PrefsManager(val context: Context){
    val PREFS_NAME = "MyPrefsFileName"

    fun savePref(key: String, value: String){
        val editor = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getPref(key: String) : String{
        val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        var value = prefs.getString(key, null)
        if (value == null) value = ""
        return value
    }

    fun remove(key: String){
        val editor = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        editor.remove(key)
        editor.commit()
    }
}

