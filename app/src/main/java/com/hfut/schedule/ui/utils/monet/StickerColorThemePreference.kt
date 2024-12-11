package com.hfut.schedule.ui.utils.monet

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object StickerColorThemePreference : BasePreference<Boolean> {
    private const val STICKER_COLOR_THEME = "stickerColorTheme"
    override val default = false

    val key = booleanPreferencesKey(STICKER_COLOR_THEME)

    fun put(context: Context, scope: CoroutineScope, value: Boolean) {
        scope.launch(Dispatchers.IO) {
            context.dataStore.put(key, value)
        }
    }

    override fun fromPreferences(preferences: Preferences): Boolean = preferences[key] ?: default
}