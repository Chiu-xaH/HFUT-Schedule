package com.hfut.schedule.logic.utils.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.hfut.schedule.App.MyApplication

object SharePrefs {
    val prefs: SharedPreferences = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)

    @JvmStatic
    fun saveString(title : String, info : String?) {
        val saved = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
        if (saved.getString(title, "") != info) { saved.edit().putString(title,info).apply() }
    }

    @JvmStatic
    fun saveBoolean(title : String, default : Boolean, save : Boolean) {
        val saved = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
        if (saved.getBoolean(title, default) != save) { saved.edit().putBoolean(title,save).apply() }
    }

    @JvmStatic
    fun saveInt(title : String, save : Int) {
        val saved = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
        if (saved.getInt(title, 0) != save) { saved.edit().putInt(title,save).apply() }
    }

    @JvmStatic
    fun saveLong(title : String, info : Long) {
        val saved = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
        if (saved.getLong(title, 0L) != info) { saved.edit().putLong(title,info).apply() }
    }
}