package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.community.library.LibraryScreen
import com.hfut.schedule.ui.screen.home.search.function.other.TrackScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

object TrackDestination : NavDestination() {
    override val key = "track"
    override val title = res(R.string.navigation_label_track)

    @Composable
    override fun Content() {
        TrackScreen()
    }
}