package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.news.home.NewsScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.common.ui.util.res

object NewsDestination : NavDestination() {
    override val key = "news"
    override val title = res(R.string.navigation_label_news)
    override val icon = R.drawable.stream

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        NewsScreen(vm)
    }
}