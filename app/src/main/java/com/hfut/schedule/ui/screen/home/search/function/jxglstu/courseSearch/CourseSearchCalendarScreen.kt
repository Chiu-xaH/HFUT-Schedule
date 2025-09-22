package com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
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
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.SemseterParser
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.lesson.JxglstuCourseTableSearch
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CourseSearchCalendarScreen(
    term : Int,
    courseName : String?,
    courseCode : String?,
    classes : String?,
    vm : NetWorkViewModel,
    vmUI : UIViewModel,
    navController : NavHostController,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val route = remember { AppNavRoute.CourseSearchCalendar.route }
    var showAll by remember { mutableStateOf(false) }
        CustomTransitionScaffold (
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            roundShape = MaterialTheme.shapes.medium,
            route = route,
            
            navHostController = navController,
            topBar = {
                Column(
                    modifier = Modifier.topBarBlur(hazeState),
                ) {
                    MediumTopAppBar(
                        colors = topBarTransplantColor(),
                        scrollBehavior = scrollBehavior,
                        title = {
                            Column {
                                Text(AppNavRoute.CourseSearchCalendar.label)
                                classes?.let {
                                    Text(
                                        "检索班级: $it",
                                        modifier = Modifier.padding(start = 2.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                }
                                courseName?.let {
                                    Text(
                                        "检索课程名: $it",
                                        modifier = Modifier.padding(start = 2.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                }
                                courseCode?.let {
                                    Text(
                                        "检索课程代码: $it",
                                        modifier = Modifier.padding(start = 2.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                }
                                Text(
                                    "检索学期: ${SemseterParser.parseSemseter(term)}",
                                    modifier = Modifier.padding(start = 2.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        },
                        navigationIcon = {
                            TopBarNavigationIcon(navController,route,AppNavRoute.CourseSearchCalendar.icon)
                        },
                        actions = {
                            Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                                FilledTonalIconButton (onClick = { showToast("正在开发") }) {
                                    Icon(painter = painterResource(id = R.drawable.save), contentDescription = "")
                                }
                                FilledTonalIconButton (onClick = { showAll = !showAll }) {
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
                    .hazeSource(hazeState)
                    .fillMaxSize()
            ) {
                val uiState by vm.courseSearchResponse.state.collectAsState()
                CommonNetworkScreen(uiState, onReload = {}) {
                    val list = (uiState as UiState.Success).data
                    JxglstuCourseTableSearch(showAll,vm,vmUI,hazeState,innerPadding,list)
                }
            }
        }
//    }
}