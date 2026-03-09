package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.school.SecondClassScreen
import com.hfut.schedule.ui.util.NavDestination
import com.xah.uicommon.util.language.res

object SecondClassDestination : NavDestination() {
    override val key = "second_class"
    override val title = res(R.string.navigation_label_second_class)
    override val icon = R.drawable.kid_star

    @Composable
    override fun Content() {
        SecondClassScreen()
    }
}