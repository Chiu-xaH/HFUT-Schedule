package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.my.holiday.NewsApiScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.common.util.language.res

data class NewsApiDestination(
    val keyword : String
) : NavDestination() {
    override val key = "news_api_$keyword"
    override val title = res(R.string.navigation_label_news)
    override val icon = R.drawable.stream

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        NewsApiScreen(vm, keyword)
    }
}