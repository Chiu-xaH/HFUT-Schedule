package com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch


import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuLesson
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.nav.destination.CourseSearchTableDestination
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.JxglstuCourseTableSearch
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.topBarBlur
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.color.topBarTransplantColor
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CourseSearchCalendarScreen(
    term : Int?,
    courseName : String?,
    courseCode : String?,
    classes : String?,
    list: List<JxglstuLesson>
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    var showAll by remember { mutableStateOf(false) }
    val backdrop = rememberLayerBackdrop()

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    colors = topBarTransplantColor(),
                    scrollBehavior = scrollBehavior,
                    title = {
                        Column {
                            Text(CourseSearchTableDestination.TITLE.asString())
                            classes?.let {
                                Text(
                                    "班级: $it",
                                    modifier = Modifier.padding(start = 2.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                            courseName?.let {
                                Text(
                                    "课程名: $it",
                                    modifier = Modifier.padding(start = 2.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                            courseCode?.let {
                                Text(
                                    "课程代码: $it",
                                    modifier = Modifier.padding(start = 2.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                            term?.let {
                                Text(
                                    "学期: ${SemesterParser.parseSemester(it)}",
                                    modifier = Modifier.padding(start = 2.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    },
                    actions = {
                        Row() {
                            LiquidButton(
                                onClick = { showAll = !showAll },
                                isCircle = true,
                                backdrop = backdrop,
                                modifier = Modifier.padding(end = APP_HORIZONTAL_DP)
                            ) {
                                Icon(painter = painterResource(id = if (showAll) R.drawable.collapse_content else R.drawable.expand_content), contentDescription = "")
                            }
                        }
                    }
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .backDropSource(backdrop)
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            JxglstuCourseTableSearch(showAll,innerPadding,list) {
                showAll = it
            }
        }
    }
}