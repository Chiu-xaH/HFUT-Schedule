package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.other.wechat.AlumniScreen
import com.hfut.schedule.ui.util.NavDestination
import com.xah.uicommon.util.language.res

object AlumniDestination : NavDestination() {
    override val key = "library"
    override val title = res(R.string.navigation_label_library)

    @Composable
    override fun Content() {
        AlumniScreen()
    }
}