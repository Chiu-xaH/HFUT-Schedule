package com.hfut.schedule.logic

import android.content.Context
import android.preference.PreferenceManager
import com.hfut.schedule.MyApplication

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
}