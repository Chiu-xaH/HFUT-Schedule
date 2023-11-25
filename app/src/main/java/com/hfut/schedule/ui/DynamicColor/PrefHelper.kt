package com.hfut.schedule.ui.DynamicColor

import android.content.Context
import android.content.SharedPreferences

object PrefHelper {
    private const val PREF_NAME = "pref_materialyoucatalog"

    const val KEY_CURRENT_THEME = "key_current_theme"

    fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    inline fun SharedPreferences.operation(editor: (SharedPreferences.Editor) -> Unit) {
        val edit = edit()
        editor(edit)
        edit.apply()
    }

    fun SharedPreferences.Editor.put(pair: Pair<String, Any>) {
        val key = pair.first
        when (val value = pair.second) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            else -> error("Only primitive types can be stored in SharedPreference")
        }
    }

    fun SharedPreferences.get(key: String, defaultValue: Any): Any? {
        return when (defaultValue) {
            is String -> getString(key, defaultValue)
            is Int -> getInt(key, defaultValue)
            is Boolean -> getBoolean(key, defaultValue)
            is Long -> getLong(key, defaultValue)
            is Float -> getFloat(key, defaultValue)
            else -> error("Only primitive types can be stored in SharedPreference")
        }
    }
}