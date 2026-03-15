package com.hfut.schedule.ui.destination

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.fix.about.About
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.text
import com.xah.navigation.utils.LocalNavDependencies

object SettingsAboutDeveloperDestination : NavDestination() {
    override val key: String = "settings_backup"
    override val title: UiText = text("关于")
    override val icon = R.drawable.info

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        About(vm)
    }
}

