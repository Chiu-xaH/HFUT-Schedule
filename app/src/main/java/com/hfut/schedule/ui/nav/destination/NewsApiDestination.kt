package com.hfut.schedule.ui.nav.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.network.util.Constant.PARTY_BRANCH_URL
import com.hfut.schedule.ui.screen.home.search.function.my.holiday.NewsApiScreen
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.util.res

data class NewsApiDestination(
    val keyword : Keyword
) : NavDestination() {
    override val key = "news_api_$keyword"
    override val description = keyword.keyword
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(R.string.navigation_label_news)
        val ICON = R.drawable.stream
    }

    // 自动支持webvpn，直接贴原始链接就行
    enum class Keyword(val keyword : String,val filteredHost : List<String> = emptyList()) {
        EXAM_SCHEDULE_HEFEI("周考试安排",listOf(Constant.ACADEMIC_URL, Constant.ACADEMIC_HTTP_URL)),
        HOLIDAY_SCHEDULE("放假安排",listOf(Constant.PARTY_BRANCH_URL,Constant.PARTY_BRANCH_HTTP_URL)),
        SELECT_COURSE_HEFEI("选课"),
        TRANSFER_MAJOR("转专业")
    }

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        NewsApiScreen(vm, keyword)
    }
}