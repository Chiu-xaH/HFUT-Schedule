package com.hfut.schedule.ui.nav.destination

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.home.cube.sub.ShortcutSortScreen
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.res


object SettingsShortcutEditDestination : NavDestination() {
    override val key: String = "settings_shortcut"
    override val title: UiText = res(R.string.navigation_label_shortcut)
    override val icon = R.drawable.keyboard_command_key

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        ShortcutSortScreen()
    }
}

