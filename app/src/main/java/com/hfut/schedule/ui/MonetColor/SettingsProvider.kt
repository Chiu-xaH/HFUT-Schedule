package com.hfut.schedule.ui.MonetColor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.datastore.preferences.core.Preferences


fun Preferences.toSettings(): Settings {
    return Settings(
        // Theme
        themeName = ThemeNamePreference.fromPreferences(this),
        customPrimaryColor = CustomPrimaryColorPreference.fromPreferences(this),
    )
}
data class Settings(
    // Theme
    val themeName: String = ThemeNamePreference.default,
    val customPrimaryColor: String = CustomPrimaryColorPreference.default,
)
@Composable
fun SettingsProvider(
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val settings by remember {
        context.dataStore.data.map { it.toSettings() }
    }.collectAsState(initial = Settings(), context = Dispatchers.Default)

    CompositionLocalProvider(
        LocalThemeName provides settings.themeName,
        LocalCustomPrimaryColor provides settings.customPrimaryColor,
    ) {
        content()
    }
}