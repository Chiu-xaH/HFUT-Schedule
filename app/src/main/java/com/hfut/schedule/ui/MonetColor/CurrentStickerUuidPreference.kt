package com.hfut.schedule.ui.MonetColor

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object CurrentStickerUuidPreference : BasePreference<String> {
    private const val CURRENT_STICKER_UUID = "currentStickerUuid"
    override val default = ""

    val key = stringPreferencesKey(CURRENT_STICKER_UUID)

    fun put(context: Context, scope: CoroutineScope, value: String) {
        scope.launch(Dispatchers.IO) {
            context.dataStore.put(key, value)
        }
    }

    override fun fromPreferences(preferences: Preferences): String = preferences[key] ?: default
}