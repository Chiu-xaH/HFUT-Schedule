package com.hfut.schedule.logic

import android.content.Context
import com.hfut.schedule.MyApplication

object SharePrefs {
    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val prefs_Savedusername = prefs.getString("Username", "")
    val prefs_Savedpassword = prefs.getString("Password","")
    val prefs_key = prefs.getString("cookie", "")
    val prefs_json = prefs.getString("json", "")
}