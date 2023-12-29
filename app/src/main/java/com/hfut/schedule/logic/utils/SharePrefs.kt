package com.hfut.schedule.logic.utils

import android.content.Context
import android.preference.PreferenceManager
import com.hfut.schedule.App.MyApplication

object SharePrefs {
    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)

    fun Save(title : String,info : String?) {
        val saved = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
        if (saved.getString(title, "") != info) { saved.edit().putString(title,info).apply() }
    }

    fun SaveBoolean(title : String,default : Boolean,save : Boolean) {
        val saved = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
        if (saved.getBoolean(title, default) != save) { saved.edit().putBoolean(title,save).apply() }
    }

    fun SaveInt(title : String,save : Int) {
        val saved = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
        if (saved.getInt(title, 0) != save) { saved.edit().putInt(title,save).apply() }
    }
}