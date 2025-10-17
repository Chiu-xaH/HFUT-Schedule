package com.hfut.schedule.logic.util.storage.kv

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.hfut.schedule.application.MyApplication
import androidx.core.content.edit

//特别想彻底重构这里，当时键值乱飞，已经难以重构了
object SharedPrefs {
    private const val PREFS = "com.hfut.schedule_preferences"
    const val LIBRARY_TOKEN = "LibraryToken"
    val prefs: SharedPreferences = MyApplication.Companion.context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val saved: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.Companion.context)

    fun saveString(title : String, info : String?) {
        if (saved.getString(title, "") != info) { saved.edit { putString(title, info) } }
    }

    fun saveBoolean(title : String, default : Boolean, save : Boolean) {
        if (saved.getBoolean(title, default) != save) { saved.edit { putBoolean(title, save) } }
    }

    fun saveInt(title : String, save : Int) {
        if (saved.getInt(title, 0) != save) { saved.edit { putInt(title, save) } }
    }
    fun saveFloat(title: String, save: Float, default: Float) {
        if (saved.getFloat(title, default) != save) { saved.edit { putFloat(title, save) } }
    }

    fun saveLong(title : String, info : Long) {
        if (saved.getLong(title, 0L) != info) { saved.edit { putLong(title, info) } }
    }
}
