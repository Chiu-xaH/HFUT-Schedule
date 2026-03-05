package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.SearchEditScreen
import com.hfut.schedule.ui.util.NavDestination
import com.xah.uicommon.util.language.res

object SearchEditDestination : NavDestination() {
    override val key = "functions_sort"
    override val title = res(R.string.navigation_label_functions_sort)

    @Composable
    override fun Content() {
        SearchEditScreen()
    }
}