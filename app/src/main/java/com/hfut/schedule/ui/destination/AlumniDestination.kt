package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.other.wechat.AlumniScreen
import com.hfut.schedule.ui.util.NavDestination
import com.xah.common.util.language.res

object AlumniDestination : NavDestination() {
    override val key = "alumni"
    override val title = res(R.string.navigation_label_alumni)
    override val icon = R.drawable.person_book

    @Composable
    override fun Content() {
        AlumniScreen()
    }
}