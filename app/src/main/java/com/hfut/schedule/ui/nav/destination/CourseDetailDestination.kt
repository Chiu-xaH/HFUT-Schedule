package com.hfut.schedule.ui.nav.destination

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.jxglstu.CourseBookBean
import com.hfut.schedule.logic.model.jxglstu.lessons
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.DetailItems
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.util.res
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

data class CourseDetailDestination(
    val lesson : lessons,
    val courseBookData : Map<Long, CourseBookBean> = emptyMap(),
    val classroom : String? = null,
    val origin : String? = null,
) : NavDestination() {
    override val key = "course_detail_${lesson.id}_${origin}"
    override val description = lesson.course.nameZh
    override val title = TITLE
    override val icon = ICON

    companion object {
        val TITLE = res(R.string.navigation_label_course_detail)
        val ICON = R.drawable.category
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
        val hazeState = rememberHazeState(blurEnabled = blur)

        Scaffold (
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.topBarBlur(hazeState),
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(description) },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(hazeState)
            ) {
                DetailItems(innerPadding,lesson,courseBookData)
            }
        }
    }
}