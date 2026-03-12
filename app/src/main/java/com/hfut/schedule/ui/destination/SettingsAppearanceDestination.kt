package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.util.NavDestination
import com.xah.uicommon.util.language.UiText
import com.xah.uicommon.util.language.res

object SettingsAppearanceDestination : NavDestination() {
    override val key: String = "settings_appearance"
    override val title: UiText = res(R.string.navigation_label_settings_appearance)
    override val icon = R.drawable.settings

    @Composable
    override fun Content() {

    }
}

