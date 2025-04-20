package com.hfut.schedule.logic.util.storage

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.hfut.schedule.App.MyApplication
//特别想彻底重构这里，当时键值乱飞，已经难以重构了
object SharedPrefs {
    val prefs: SharedPreferences = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    private val saved: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)

    fun saveString(title : String, info : String?) {
        if (saved.getString(title, "") != info) { saved.edit().putString(title,info).apply() }
    }

    fun saveBoolean(title : String, default : Boolean, save : Boolean) {
        if (saved.getBoolean(title, default) != save) { saved.edit().putBoolean(title,save).apply() }
    }

    fun saveInt(title : String, save : Int) {
        if (saved.getInt(title, 0) != save) { saved.edit().putInt(title,save).apply() }
    }

    fun saveLong(title : String, info : Long) {
        if (saved.getLong(title, 0L) != info) { saved.edit().putLong(title,info).apply() }
    }
}