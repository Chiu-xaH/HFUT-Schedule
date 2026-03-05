package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.WebNavigationScreen
import com.hfut.schedule.ui.util.NavDestination
import com.xah.uicommon.util.language.res

object WebFolderDestination : NavDestination() {
    override val key = "web_folder"
    override val title = res(R.string.navigation_label_web_folder)

    @Composable
    override fun Content() {
        WebNavigationScreen()
    }
}