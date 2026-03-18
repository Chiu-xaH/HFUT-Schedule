package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.ExamNotificationsScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

object ExamNewsDestination : NavDestination() {
    override val key = "exam_news"
    override val title = res(R.string.navigation_label_exam_news)
    override val icon = R.drawable.stream

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        ExamNotificationsScreen(vm)
    }
}