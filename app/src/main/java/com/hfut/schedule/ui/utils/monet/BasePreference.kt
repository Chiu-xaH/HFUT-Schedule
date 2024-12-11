package com.hfut.schedule.ui.utils.monet

import androidx.datastore.preferences.core.Preferences


interface BasePreference<T> {
    val default: T

    fun fromPreferences(preferences: Preferences): T
}