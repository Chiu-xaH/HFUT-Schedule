package com.hfut.schedule.ui.screen.home.search.function.school.classroom

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.enumeration.Campus
import com.hfut.schedule.logic.enumeration.getCampus
import com.hfut.schedule.logic.model.uniapp.UniAppBuildingBean
import com.hfut.schedule.logic.model.uniapp.UniAppClassroomLesson
import com.hfut.schedule.logic.network.repo.hfut.UniAppRepository
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.xah.uicommon.style.padding.navigationBarHeightPadding
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ClassroomScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val route = remember { AppNavRoute.Classroom.route }
    val scope = rememberCoroutineScope()
    val chipsUiState by vm.uniAppBuildingsResp.state.collectAsState()
    val refreshNetworkChips = suspend m@ {
        if(chipsUiState is UiState.Success) {
            return@m
        }
        var jwt = DataStoreManager.uniAppJwt.first()
        if(jwt.isEmpty() || jwt.isEmpty()) {
            val loginResult = UniAppRepository.login()
            if(loginResult == false) {
                return@m
            }
            jwt = DataStoreManager.uniAppJwt.first()
        }
        vm.uniAppBuildingsResp.clear()
        vm.getBuildings(token = jwt)
    }
    LaunchedEffect(Unit) {
        refreshNetworkChips()
    }

    val itemsUiState by vm.uniAppClassroomsResp.state.collectAsState()

    var page by remember { mutableIntStateOf(1) }
    var date by remember { mutableStateOf(DateTimeManager.Date_yyyy_MM_dd) }
    var campus by remember { mutableStateOf<Campus?>(getCampus()) }
    var selectedBuildings = remember { mutableStateListOf<UniAppBuildingBean>() }
    var selectedFloors = remember { mutableStateListOf<Int>() }
    val refreshNetworkItems = suspend m@ {
        var jwt = DataStoreManager.uniAppJwt.first()
        if(jwt.isEmpty() || jwt.isEmpty()) {
            val loginResult = UniAppRepository.login()
            if(loginResult == false) {
                return@m
            }
            jwt = DataStoreManager.uniAppJwt.first()
        }
        vm.uniAppClassroomsResp.clear()
        vm.getClassrooms(
            page = page,
            date = date,
            campus = campus,
            buildings = selectedBuildings.map { it.campusAssoc },
            floors = selectedFloors,
            token = jwt
        )
    }

    LaunchedEffect(page,date,campus,selectedFloors,selectedBuildings) {
        refreshNetworkItems()
    }

    CustomTransitionScaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        route = route,
        navHostController = navController,
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.Classroom.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(navController,route, AppNavRoute.Classroom.icon)
                    }
                )
                // 日期选择
                // 建筑选择（可多选）
                // 校区选择
                // 楼层选择（可多选）
            }
        },
    ) { innerPadding ->
        val listState = rememberLazyListState()
        val scheduleModifier =  Modifier.fillMaxWidth().height(45.dp).padding(horizontal = APP_HORIZONTAL_DP).padding(bottom = APP_HORIZONTAL_DP)
        Box(modifier = Modifier.hazeSource(hazeState).fillMaxSize()) {
            CommonNetworkScreen(itemsUiState, onReload = refreshNetworkItems) {
                val list = (itemsUiState as UiState.Success).data
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn {
                        item { InnerPaddingHeight(innerPadding,true) }
                        items(list.size,key = { it }) { index ->
                            val item = list[index]
                            CustomCard(color = cardNormalColor()) {
                                TransplantListItem(
                                    headlineContent = { Text(item.nameZh) },
                                    overlineContent = { Text(item.campusNameZh) },
                                )
                                val activities = item.roomOccupationInfoVms ?: emptyList()
                                // 横向时间轴
                                ClassroomSchedule(activities, modifier = scheduleModifier) {
                                    showToast(it.activityName)
                                }
                            }
                        }
                        item { PaddingForPageControllerButton() }
                        item { InnerPaddingHeight(innerPadding,false) }
                    }
                    PageController(
                        listState,
                        page,
                        nextPage = { page = it },
                        previousPage = { page = it },
                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                    )
                }
            }
        }
    }
}

@Composable
fun ClassroomSchedule(
    lessons: List<UniAppClassroomLesson>,
    modifier: Modifier = Modifier,
    onClick : (UniAppClassroomLesson) -> Unit
) {
    // 时间范围从 8:00 (480 分钟) 到 22:00 (1320 分钟)
    val startTimeInMinutes = 8 * 60 // 8:00 = 480分钟
    val endTimeInMinutes = 22 * 60 // 22:00 = 1320分钟

    // 每小时的宽度
    val totalTimeRange = endTimeInMinutes - startTimeInMinutes
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp // 屏幕宽度
    val timeUnitWidth = screenWidth / totalTimeRange // 每分钟的宽度

    Box(modifier = modifier) {
        // 绘制课程时间块
        lessons.forEach { lesson ->
            val startMinutes = convertTimeToMinutes(lesson.startTimeString)
            val endMinutes = convertTimeToMinutes(lesson.endTimeString)

            // 计算占用的宽度
            val startX = (startMinutes - startTimeInMinutes) * timeUnitWidth.value
            val endX = (endMinutes - startTimeInMinutes) * timeUnitWidth.value
            // 获取父布局大小
            var parentHeight by remember { mutableStateOf(0.dp) }

            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .offset(x = startX.dp)
                    .width((endX - startX).dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
                    .align(Alignment.TopStart)
                    .onGloballyPositioned { coordinates ->
                        parentHeight = coordinates.size.height.dp
                    }
                    .clickable {
                        onClick(lesson)
                    }
                ,
            ) {
                // 计算动态字体大小，根据父布局高度来调整
                val fontSize = with(LocalDensity.current) { (parentHeight.value * 0.125f).sp }


                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = lesson.startTimeString,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = fontSize,
                        lineHeight = fontSize,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )

                    Text(
                        text = lesson.endTimeString,
                        color = MaterialTheme.colorScheme.onPrimary,
                        lineHeight = fontSize,
                        fontSize = fontSize,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

// 辅助函数：转换HH-MM格式的时间为分钟数
fun convertTimeToMinutes(time: String): Int {
    val (hour, minute) = time.split(':').map { it.toInt() }
    return hour * 60 + minute
}
