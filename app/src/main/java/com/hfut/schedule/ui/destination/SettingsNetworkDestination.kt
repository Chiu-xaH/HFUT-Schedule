package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.util.NavDestination
import com.xah.common.util.language.UiText
import com.xah.common.util.language.res

object SettingsNetworkDestination : NavDestination() {
    override val key: String = "settings_network"
    override val title: UiText = res(R.string.navigation_label_settings_network)
    override val icon = R.drawable.settings

    @Composable
    override fun Content() {

    }
}