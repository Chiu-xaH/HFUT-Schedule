package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.my.holiday.NewsApiScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class NewsApiDestination(
    val keyword : String
) : NavDestination() {
    override val key = "news_api_$keyword"
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(R.string.navigation_label_news)
        val ICON = R.drawable.stream
    }

    enum class Keyword(val keyword : String) {
        EXAM_SCHEDULE_HEFEI("周考试安排"),HOLIDAY_SCHEDULE("放假安排"),SELECT_COURSE("选课"),TRANSFER_MAJOR("转专业")
    }

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        NewsApiScreen(vm, keyword)
    }
}