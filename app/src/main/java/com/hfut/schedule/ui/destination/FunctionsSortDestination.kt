package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.SearchEditScreen
import com.hfut.schedule.ui.util.NavDestination
import com.xah.common.ui.util.res

object FunctionsSortDestination : NavDestination() {
    override val key = "functions_sort"
    override val title = res(R.string.navigation_label_functions_sort)
    override val icon = R.drawable.edit

    @Composable
    override fun Content() {
        SearchEditScreen()
    }
}