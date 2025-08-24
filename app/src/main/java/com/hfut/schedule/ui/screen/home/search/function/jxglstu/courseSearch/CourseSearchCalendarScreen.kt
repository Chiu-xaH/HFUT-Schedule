package com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.model.jxglstu.CourseUnitBean
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.SemseterParser
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.ScrollText
import com.hfut.schedule.ui.component.webview.getPureUrl
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.next.JxglstuCourseTableSearch
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.CourseTotalForApi
import com.hfut.schedule.ui.style.color.topBarTransplantColor
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.first

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
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val route = remember { AppNavRoute.CourseSearchCalendar.route }
    var showAll by remember { mutableStateOf(false) }
    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            roundShape = MaterialTheme.shapes.medium,
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                Column(
                    modifier = Modifier.topBarBlur(hazeState),
                ) {
                    TopAppBar(
                        colors = topBarTransplantColor(),
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
                            TopBarNavigationIcon(navController,animatedContentScope,route,AppNavRoute.CourseSearchCalendar.icon)
                        },
                        actions = {
                            Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
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
                val uiState by vm.searchDatumData.state.collectAsState()
                var timeTable: List<CourseUnitBean>? by remember { mutableStateOf(null) }
                val refreshNetwork = suspend m@ {
                    vm.searchDatumData.clear()
                    val cookies = getJxglstuCookie(vm) ?: return@m

                    val uiState = vm.courseSearchResponse.state.first()
                    if(uiState !is UiState.Success) {
                        return@m
                    }
                    val data = uiState.data
                    val lessonIds = data.map { it.id }

                    val studentId = (vm.studentId.state.value as? UiState.Success)?.data ?: return@m
                    val bizTypeId = (vm.bizTypeIdResponse.state.value as? UiState.Success)?.data ?: return@m
                    // 先获取 timeTableLayoutId
                    vm.timeTableLayoutIdResp.clear()
                    vm.getTimeTableLayoutId(cookies,studentId,bizTypeId,term)
                    val timeTableLayoutId = (vm.timeTableLayoutIdResp.state.value as? UiState.Success)?.data ?: return@m
                    // 然后获取 作息表
                    vm.lessonTimesResponse.clear()
                    vm.getLessonTimes(cookies,timeTableLayoutId)
                    val table = (vm.lessonTimesResponse.state.value as? UiState.Success)?.data ?: return@m
                    timeTable = table
                    // 最后获取课表
                    vm.getDatumFromSearch(cookies,lessonIds)
                }
                LaunchedEffect(Unit) {
                    refreshNetwork()
                }
                CommonNetworkScreen(uiState, onReload = refreshNetwork) {
                    val data = (uiState as UiState.Success).data
                    timeTable?.let {
                        JxglstuCourseTableSearch(showAll,vm,vmUI,hazeState,innerPadding,it,data)
                    }
                }
            }
        }
    }
}