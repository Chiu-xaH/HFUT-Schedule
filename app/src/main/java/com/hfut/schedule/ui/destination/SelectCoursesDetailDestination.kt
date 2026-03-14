package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse.SelectCourseDetailScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.common.util.language.res

data class SelectCoursesDetailDestination(
    val index : Int,
    val name : String
) : NavDestination() {
    override val key = "select_courses_detail_${name}_$index"
    override val title = res(R.string.navigation_label_select_courses_detail)
    override val icon = R.drawable.ads_click

    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        SelectCourseDetailScreen(vm,index,name)
    }
}