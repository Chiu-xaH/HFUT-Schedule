package com.hfut.schedule.ui.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.jxglstu.CourseBookBean
import com.hfut.schedule.logic.model.jxglstu.lessons
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.DetailItems
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavDependencies
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.util.res

data class CourseDetailDestination(
    val lesson : lessons,
    val courseBookData : Map<Long, CourseBookBean> = emptyMap()
) : NavDestination() {
    override val key = "course_api_detail_${lesson.hashCode()}"
    override val title = res(R.string.navigation_label_course_detail)
    override val icon = R.drawable.category

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        Scaffold (
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(lesson.course.nameZh) },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                DetailItems(lesson,vm,courseBookData)
            }
        }
    }
}