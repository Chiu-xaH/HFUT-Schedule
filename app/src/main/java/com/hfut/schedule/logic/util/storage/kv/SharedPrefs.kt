package com.hfut.schedule.logic.util.storage.kv

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.hfut.schedule.application.MyApplication
import androidx.core.content.edit
import com.hfut.schedule.logic.util.other.AppVersion

//特别想彻底重构这里，当时键值乱飞，已经难以重构了
@Deprecated("为KMP适配计划的开始做铺垫，将彻底弃用SP，此类会被删除，请改用`DataStoreManager`存储键值对")
object SharedPrefs {
    private val PREFS = "${AppVersion.appPackageName}_preferences"
    @Deprecated("为KMP适配计划的开始做铺垫垫，即将去除")
    const val LIBRARY_TOKEN = "LibraryToken"
    @Deprecated("为KMP适配计划的开始做铺垫，即将去除")
    const val SECOND_CLASS_TOKEN = "second_class_token"
    @Deprecated("为KMP适配计划的开始做铺垫，即将去除")
    val prefs: SharedPreferences = MyApplication.context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val saved: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)

    @Deprecated("为KMP适配计划的开始做铺垫，即将去除")
    fun saveString(title : String, info : String?) {
        if (saved.getString(title, "") != info) { saved.edit { putString(title, info) } }
    }

    @Deprecated("为KMP适配计划的开始做铺垫，即将去除")
    fun saveBoolean(title : String, default : Boolean, save : Boolean) {
        if (saved.getBoolean(title, default) != save) { saved.edit { putBoolean(title, save) } }
    }

    @Deprecated("为KMP适配计划的开始做铺垫垫，即将去除")
    fun saveInt(title : String, save : Int) {
        if (saved.getInt(title, 0) != save) { saved.edit { putInt(title, save) } }
    }
    @Deprecated("为KMP适配计划的开始做铺垫，彻底弃用SP")
    fun saveFloat(title: String, save: Float, default: Float) {
        if (saved.getFloat(title, default) != save) { saved.edit { putFloat(title, save) } }
    }

    @Deprecated("为KMP适配计划的开始做铺垫，彻底弃用SP")
    fun saveLong(title : String, info : Long) {
        if (saved.getLong(title, 0L) != info) { saved.edit { putLong(title, info) } }
    }
}
