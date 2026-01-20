package com.hfut.schedule.ui.screen.home.search.function.school.classroom

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.Campus
import com.hfut.schedule.logic.enumeration.getCampus
import com.hfut.schedule.logic.model.NavigationBarItemData
import com.hfut.schedule.logic.model.uniapp.ClassroomOccupiedCause
import com.hfut.schedule.logic.model.uniapp.UniAppBuildingBean
import com.hfut.schedule.logic.model.uniapp.UniAppCampus
import com.hfut.schedule.logic.model.uniapp.UniAppClassroomLessonBean
import com.hfut.schedule.logic.model.uniapp.UniAppEmptyClassroomLesson
import com.hfut.schedule.logic.network.repo.hfut.UniAppRepository
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager.ShowTeacherConfig
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.AnimatedIconButton
import com.hfut.schedule.ui.component.button.HazeBottomBar
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.ShareTwoContainer2D
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.dialog.DateRangePickerModal
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.screen.RefreshIndicator
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.component.status.PrepareSearchIcon
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.common.DraggableWeekButton
import com.hfut.schedule.ui.screen.home.calendar.common.ScheduleTopDate
import com.hfut.schedule.ui.screen.home.calendar.common.TimeTableWeekSwap
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.distinctUnit
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.getNewWeek
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.next.CourseDetailOrigin
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableItem
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableType
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.parseJxglstuIntTime
import com.hfut.schedule.ui.screen.home.calendar.timetable.ui.TimeTable
import com.hfut.schedule.ui.screen.home.calendar.timetable.ui.TimeTableDetail
import com.hfut.schedule.ui.screen.home.calendar.timetable.ui.TimeTablePreview
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getJxglstuStartDate
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.containerBackDrop
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.util.navigation.AppAnimationManager.currentPage
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.transition.component.containerShare
import com.xah.transition.util.currentRouteWithoutArgs
import com.xah.uicommon.component.text.BottomTip
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.RowHorizontal
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.xah.uicommon.style.padding.navigationBarHeightPadding
import com.xah.uicommon.util.LogUtil
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.WatchEvent
import java.time.LocalDate

private enum class ClassroomBarItems(val page : Int) {
    EMPTY_CLASSROOM(0),CLASSROOM_LESSONS(1)
}

private val items = listOf(
    NavigationBarItemData(
        ClassroomBarItems.EMPTY_CLASSROOM.name,"空教室", R.drawable.meeting_room, R.drawable.meeting_room_filled
    ),
    NavigationBarItemData(
        ClassroomBarItems.CLASSROOM_LESSONS.name,"教室课程表", R.drawable.calendar, R.drawable.calendar_month_filled
    ),
)

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ClassroomScreen(
    vm : NetWorkViewModel,
    navTopController : NavHostController,
) {
    val route = remember { AppNavRoute.Classroom.route }
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val currentAnimationIndex by DataStoreManager.animationType.collectAsState(initial = 0)
    val targetPage = when(navController.currentRouteWithoutArgs()) {
        ClassroomBarItems.EMPTY_CLASSROOM.name -> ClassroomBarItems.EMPTY_CLASSROOM
        ClassroomBarItems.CLASSROOM_LESSONS.name -> ClassroomBarItems.CLASSROOM_LESSONS
        else -> ClassroomBarItems.EMPTY_CLASSROOM
    }
    // 保存上一页页码 用于决定左右动画
    if(currentAnimationIndex == 2) {
        LaunchedEffect(targetPage) {
            currentPage = targetPage.page
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val backDrop = rememberLayerBackdrop()

    val chipsUiState by vm.uniAppBuildingsResp.state.collectAsState()

    var date by remember { mutableStateOf(DateTimeManager.Date_yyyy_MM_dd) }
    var campus by remember { mutableStateOf<Campus?>(getCampus()) }
    var selectedBuildings = remember { mutableStateListOf<UniAppBuildingBean>() }
    var selectedFloors = remember { mutableStateListOf<Int>() }
    var input by remember { mutableStateOf("") }

    val refreshNetworkSearch = suspend m@ {
        var jwt = DataStoreManager.uniAppJwt.first()
        if(jwt.isEmpty() || jwt.isEmpty()) {
            val loginResult = UniAppRepository.login()
            if(loginResult == false) {
                return@m
            }
            jwt = DataStoreManager.uniAppJwt.first()
        }
        vm.uniAppSearchClassroomsResp.clear()
        vm.searchClassrooms(input,jwt,1)
    }

    var showSelectDateDialog by remember { mutableStateOf(false) }
    if(showSelectDateDialog)
        DateRangePickerModal(text = "",onSelected = { date = it.second }) { showSelectDateDialog = false }

    CustomTransitionScaffold (
        route = route,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        navHostController = navTopController,
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.Classroom.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(navTopController,route, AppNavRoute.Classroom.icon)
                    },
                    actions = {
                        if(targetPage == ClassroomBarItems.EMPTY_CLASSROOM) {
                            LiquidButton(
                                onClick = {
                                    showSelectDateDialog = true
                                },
                                modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),
                                backdrop = backDrop
                            ) {
                                Text(date)
                            }
                        }
                    }
                )

                if(targetPage == ClassroomBarItems.EMPTY_CLASSROOM) {
                    CustomCard(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(.35f),
                        modifier = Modifier.containerBackDrop(backDrop, MaterialTheme.shapes.medium)
                    ) {
                        // 校区选择 多个Chip
                        val campusList = Campus.entries
                        LazyRow(modifier = Modifier.padding(top = CARD_NORMAL_DP*3)) {
                            item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                            items(campusList.size) { index ->
                                val item = campusList[index]
                                val selected = item == campus
                                FilterChip (
                                    border = null,
                                    colors = FilterChipDefaults.filterChipColors(containerColor = MaterialTheme.colorScheme.surface, selectedContainerColor = MaterialTheme.colorScheme.primary, selectedLabelColor = MaterialTheme.colorScheme.onPrimary),
                                    selected = selected,
                                    onClick = {
                                        campus = if(selected) {
                                            null
                                        } else {
                                            item
                                        }
                                    },
                                    label = { Text(item.description) },
                                    modifier = Modifier.padding(end = if(index == campusList.size-1) 0.dp else CARD_NORMAL_DP*2)
                                )
                            }
                            item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                        }
                        // 建筑选择（可多选） 多个Chip
                        if(chipsUiState is UiState.Success) {
                            val buildingList = (chipsUiState as UiState.Success).data.filter {
                                when(campus) {
                                    Campus.XC -> UniAppCampus.XC.code == it.campusAssoc
                                    Campus.TXL -> UniAppCampus.TXL.code == it.campusAssoc
                                    Campus.FCH -> UniAppCampus.FCH.code == it.campusAssoc
                                    else -> true
                                }
                            }
                            LazyRow() {
                                item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                                items(buildingList.size) { index ->
                                    val item = buildingList[index]
                                    val selected = item in selectedBuildings
                                    FilterChip (
                                        border = null,
                                        colors = FilterChipDefaults.filterChipColors(containerColor = MaterialTheme.colorScheme.surface, selectedContainerColor = MaterialTheme.colorScheme.primary, selectedLabelColor = MaterialTheme.colorScheme.onPrimary),
                                        selected = selected,
                                        onClick = {
                                            if(selected) {
                                                selectedBuildings.remove(item)
                                            } else {
                                                selectedBuildings.add(item)
                                            }
                                        },
                                        label = { Text(item.nameZh) },
                                        modifier = Modifier.padding(end = if(index == buildingList.size-1) 0.dp else CARD_NORMAL_DP*2)
                                    )
                                }
                                item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                            }
                        }
                        // 楼层选择（可多选） 多个Chip
                        LazyRow(modifier = Modifier.padding(bottom = CARD_NORMAL_DP*3)) {
                            item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                            items(20) { index ->
                                val item = index + 1
                                val selected = item in selectedFloors
                                FilterChip (
                                    border = null,
                                    colors = FilterChipDefaults.filterChipColors(containerColor = MaterialTheme.colorScheme.surface, selectedContainerColor = MaterialTheme.colorScheme.primary, selectedLabelColor = MaterialTheme.colorScheme.onPrimary),
                                    selected = selected,
                                    onClick = {
                                        if(selected) {
                                            selectedFloors.remove(item)
                                        } else {
                                            selectedFloors.add(item)
                                        }
                                    },
                                    label = { Text("${item}F") },
                                    modifier = Modifier.padding(end = if(index == 20-1) 0.dp else CARD_NORMAL_DP*2)
                                )
                            }
                            item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                        }
                    }
                } else {
                    CustomTextField(
                        input = input,
                        modifier = Modifier
                            .padding(horizontal = APP_HORIZONTAL_DP)
                            .containerBackDrop(backDrop, MaterialTheme.shapes.medium),
                        label = { Text("搜索教室") },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        refreshNetworkSearch()
                                    }
                                }
                            ) {
                                Icon(painterResource(R.drawable.search),null)
                            }
                        }
                    ) { input = it }
                    Spacer(Modifier.height(CARD_NORMAL_DP))
                }
            }
        },
        bottomBar = {
            HazeBottomBar(hazeState, items,navController)
        }
    ) { innerPadding ->
        val animation = AppAnimationManager.getAnimationType(currentAnimationIndex,targetPage.page)

        NavHost(
            navController = navController,
            startDestination = ClassroomBarItems.EMPTY_CLASSROOM.name,
            enterTransition = { animation.enter },
            exitTransition = { animation.exit },
            modifier = Modifier
                .backDropSource(backDrop)
                .hazeSource(state = hazeState)
        ) {
            composable(ClassroomBarItems.EMPTY_CLASSROOM.name) {
                EmptyClassroomScreen(vm,innerPadding,navTopController,date,campus,selectedBuildings,selectedFloors)
            }
            composable(ClassroomBarItems.CLASSROOM_LESSONS.name) {
                SearchClassroomScreen(vm,navTopController,innerPadding) {
                    scope.launch {
                        refreshNetworkSearch()
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
private fun EmptyClassroomScreen(
    vm : NetWorkViewModel,
    innerPadding : PaddingValues,
    navTopController : NavHostController,
    date : String,
    campus : Campus?,
    selectedBuildings: SnapshotStateList<UniAppBuildingBean>,
    selectedFloors: SnapshotStateList<Int>
) {
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

    val itemsUiState by vm.uniAppEmptyClassroomsResp.state.collectAsState()

    var page by remember { mutableIntStateOf(1) }
    val refreshNetworkItems = suspend m@ {
        var jwt = DataStoreManager.uniAppJwt.first()
        if(jwt.isEmpty() || jwt.isEmpty()) {
            val loginResult = UniAppRepository.login()
            if(loginResult == false) {
                return@m
            }
            jwt = DataStoreManager.uniAppJwt.first()
        }
        vm.uniAppEmptyClassroomsResp.clear()
        vm.getEmptyClassrooms(
            page = page,
            date = date,
            campus = campus,
            buildings = selectedBuildings.map { it.id },
            floors = selectedFloors,
            token = jwt
        )
    }

    LaunchedEffect(date,page,campus,selectedFloors.size,selectedBuildings.size) {
        refreshNetworkItems()
    }
    val isToday = DateTimeManager.Date_yyyy_MM_dd == date

    val occupyList = remember { ClassroomOccupiedCause.entries }
    var showDialog by remember { mutableStateOf(false) }
    var info by remember { mutableStateOf<UniAppEmptyClassroomLesson?>(null) }
    if(showDialog && info != null) {
        Dialog(
            onDismissRequest = { showDialog = false },
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
//                    .padding(APP_HORIZONTAL_DP)
            ) {
                Column(
                    modifier = Modifier.padding(APP_HORIZONTAL_DP)
                ) {
                    Text(info!!.teacherName)
                    Text(info!!.date)
                    Text(info!!.activityName)
                    val cause = occupyList.find { it.activityType == info!!.activityType }?.description

                    Text(
                        cause ?: info!!.activityType
                    )
                    Text(info!!.startTimeString + "~" + info!!.endTimeString)
                }
            }
        }
    }

    val listState = rememberLazyListState()
    val scheduleModifier =  Modifier
        .fillMaxWidth()
        .height(45.dp)
        .padding(horizontal = APP_HORIZONTAL_DP)
        .padding(bottom = APP_HORIZONTAL_DP)
    Box(modifier = Modifier.fillMaxSize()) {
        CommonNetworkScreen(itemsUiState, onReload = refreshNetworkItems) {
            val list = (itemsUiState as UiState.Success).data
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(state = listState) {
                    item { InnerPaddingHeight(innerPadding,true) }
                    items(list.size,key = { it }) { index ->
                        val item = list[index]
                        val route = AppNavRoute.ClassroomLessons.withArgs(item.id,item.nameZh)
                        val activities = item.roomOccupationInfoVms ?: emptyList()
                        val isAllDayFree = activities.isEmpty()
                        val isOccupied = activities.find { DateTimeManager.getTimeState(it.startTimeString,it.endTimeString) == DateTimeManager.TimeState.ONGOING } != null
                        CustomCard(
                            color = cardNormalColor(),
                            modifier = Modifier.containerShare(route).clickable {
                                navTopController.navigateForTransition(AppNavRoute.ClassroomLessons,route)
                            }
                        ) {
                            TransplantListItem(
                                headlineContent = { Text(item.nameZh) },
                                overlineContent = { Text(item.campusNameZh) },
                                leadingContent = {
                                    if(isToday) {
                                        // 是否正在占用
                                        if(isOccupied) {
                                            LoadingIcon()
                                        } else {
                                            Icon(painterResource(R.drawable.lasso_select),null)
                                        }
                                    } else {
                                        Icon(painterResource(R.drawable.meeting_room),null)
                                    }
                                },
                                trailingContent = {
                                    if(isToday) {
                                        Text(
                                            if(isAllDayFree) {
                                                "全天空闲"
                                            } else {
                                                // 是否正在占用
                                                if(isOccupied) {
                                                    "占用中"
                                                } else {
                                                    "空闲中"
                                                }
                                            }
                                        )
                                    } else {
                                        if(isAllDayFree) {
                                            Text("全天空闲")
                                        }
                                    }
                                }
                            )
                            // 横向时间轴
                            if(!isAllDayFree) {
                                ClassroomSchedule(activities, modifier = scheduleModifier) {
                                    info = it
                                    showDialog = true
                                }
                            }
                        }
                    }
                    item { PaddingForPageControllerButton() }
                    item { InnerPaddingHeight(innerPadding,false) }
                }
                PageController(
                    listState,
                    page,
                    onNextPage = { page = it },
                    onPreviousPage = { page = it },
                    paddingBottom = false,
                    paddingSafely = false,
                    modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                )
            }
        }
    }
}

@Composable
private fun ClassroomSchedule(
    lessons: List<UniAppEmptyClassroomLesson>,
    modifier: Modifier = Modifier,
    rangeMinutes :  Pair<Int,Int> = Pair(8 * 60,22 * 60),
    onClick : (UniAppEmptyClassroomLesson) -> Unit
) {
    // 时间范围从 8:00 (480 分钟) 到 22:00 (1320 分钟)
    val startTimeInMinutes = rangeMinutes.first
    val endTimeInMinutes = rangeMinutes.second

    BoxWithConstraints(modifier = modifier) {
        val timelineWidth = maxWidth
        val dpPerMinute = timelineWidth / (endTimeInMinutes - startTimeInMinutes)

        // 绘制课程时间块
        lessons.forEach { lesson ->
            val startMinutes = convertTimeToMinutes(lesson.startTimeString)
            val endMinutes = convertTimeToMinutes(lesson.endTimeString)

            val safeStart = startMinutes.coerceIn(startTimeInMinutes, endTimeInMinutes)
            val safeEnd = endMinutes.coerceIn(startTimeInMinutes, endTimeInMinutes)

            val offsetX = (safeStart - startTimeInMinutes) * dpPerMinute.value
            val width = (safeEnd - safeStart) * dpPerMinute.value

            var parentHeight by remember { mutableStateOf(0.dp) }

            Box(
                modifier = Modifier
                    .offset(x = offsetX.dp)
                    .width(width.dp)
                    .fillMaxHeight()
                    .clip(MaterialTheme.shapes.extraSmall)
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
private fun convertTimeToMinutes(time: String): Int {
    val (hour, minute) = time.split(':').map { it.toInt() }
    return hour * 60 + minute
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
private fun SearchClassroomScreen(
    vm : NetWorkViewModel,
    navTopController : NavHostController,
    innerPadding : PaddingValues,
    refreshNetwork : () -> Unit
) {
    val uiState by vm.uniAppSearchClassroomsResp.state.collectAsState()
    LaunchedEffect(Unit) {
        if(uiState is UiState.Loading) {
            vm.uniAppSearchClassroomsResp.emitPrepare()
        }
    }
    val refreshing = uiState is UiState.Loading
    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = refreshNetwork)
    val listState = rememberLazyListState()
    Box(modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
        RefreshIndicator(
            refreshing,
            pullRefreshState,
            Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f)
                .padding(innerPadding)
        )
        CommonNetworkScreen(uiState, onReload = refreshNetwork, prepareContent = { PrepareSearchIcon() }) {
            val list = (uiState as UiState.Success).data
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(state = listState) {
                    item { InnerPaddingHeight(innerPadding,true) }
                    items(list.size, key = { list[it].id }) { index ->
                        val item = list[index]
                        val route = AppNavRoute.ClassroomLessons.withArgs(item.id,item.nameZh)
                        CardListItem(
                            leadingContent = {
                                Icon(painterResource(R.drawable.meeting_room),null)
                            },
                            headlineContent = { Text(item.nameZh) },
                            overlineContent = { Text("${item.building.campus.nameZh} ${item.building.nameZh}")},
                            supportingContent = { Text("${item.floor}F | ${item.roomType.nameZh} | ${item.seatsForLesson}人") },
                            color = cardNormalColor(),
                            cardModifier = Modifier.containerShare(route, MaterialTheme.shapes.medium),
                            modifier = Modifier.clickable {
                                navTopController.navigateForTransition(AppNavRoute.ClassroomLessons,route)
                            }
                        )
                    }
//                    item { PaddingForPageControllerButton() }
                    item { InnerPaddingHeight(innerPadding,false) }
                }
//                PageController(
//                    listState,
//                    page,
//                    nextPage = { page = it },
//                    previousPage = { page = it },
//                    paddingBottom = false,
//                    modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
//                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ClassroomLessonsScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
    roomId : Int,
    name: String
) {
    var showAll by remember { mutableStateOf(false) }
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.ClassroomLessons.withArgs(roomId,name) }
    val semester by produceState<Int?>(initialValue = null) {
        value = SemesterParser.getSemester()
    }
    val uiState by vm.uniAppClassroomLessonsResp.state.collectAsState()
    val refreshNetwork = suspend m@ {
        vm.uniAppClassroomLessonsResp.emitPrepare()
        if(semester == null) {
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
        vm.uniAppClassroomLessonsResp.clear()
        vm.getClassroomLessons(semester!!,roomId,jwt)
    }

    LaunchedEffect(semester) {
        refreshNetwork()
    }

    var today by rememberSaveable() { mutableStateOf(DateTimeManager.getToday()) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    CustomTransitionScaffold (
        route = route,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        navHostController = navController,
        topBar = {
            Column(modifier = Modifier.topBarBlur(hazeState)) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(name) },
                    navigationIcon = {
                        TopBarNavigateIcon(navController)
                    },
                    actions = {
                        AnimatedIconButton(
                            valueState = showAll,
                            onClick = { showAll = !showAll }
                        )
                    }
                )
                ScheduleTopDate(showAll, today)
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier.hazeSource(hazeState).fillMaxSize()) {
            CommonNetworkScreen(uiState, onReload = refreshNetwork) {
                val list = (uiState as UiState.Success).data

                val scrollState = rememberScrollState()
                var showBottomSheetDetail by remember { mutableStateOf(false) }
                var bean by remember { mutableStateOf<List<TimeTableItem>?>(null) }

                if (showBottomSheetDetail) {
                    HazeBottomSheet (
                        onDismissRequest = {
                            showBottomSheetDetail = false
                        },
                        autoShape = false,
                        showBottomSheet = showBottomSheetDetail,
                        hazeState = hazeState
                    ) {
                        bean?.let { TimeTableDetail(it) }
                    }
                }


                val initialWeek = if(DateTimeManager.weeksBetweenJxglstu > MyApplication.MAX_WEEK) {
                    getNewWeek()
                } else if(DateTimeManager.weeksBetweenJxglstu < 1) {
                    1L
                } else {
                    DateTimeManager.weeksBetweenJxglstu
                }

                var currentWeek by rememberSaveable { mutableLongStateOf(initialWeek) }

                var totalDragX by remember { mutableFloatStateOf(0f) }
                val shouldShowAddButton by remember { derivedStateOf { scrollState.value == 0 } }
                var isExpand by remember { mutableStateOf(false) }

                val weekSwap = remember(currentWeek) { object : TimeTableWeekSwap {
                    override fun backToCurrentWeek() {
                        if(DateTimeManager.weeksBetweenJxglstu < 1) {
                            currentWeek = 1
                            today = getJxglstuStartDate()
                        } else {
                            currentWeek = DateTimeManager.weeksBetweenJxglstu
                            today = LocalDate.now()
                        }
                    }

                    override fun goToWeek(i: Long) {
                        if(currentWeek == i) {
                            return
                        }
                        if (i in 1..MyApplication.MAX_WEEK) {
                            val day = 7L*(i - currentWeek)
                            today = today.plusDays(day)
                            currentWeek = i
                        }
                        showToast("第${currentWeek}周")
                    }

                    override fun nextWeek() {
                        if (currentWeek < MyApplication.MAX_WEEK) {
                            today = today.plusDays(7)
                            currentWeek++
                        }
                    }

                    override fun previousWeek() {
                        if (currentWeek > 1) {
                            today = today.minusDays(7)
                            currentWeek--
                        }
                    }
                } }

                val items by produceState(initialValue = List(MyApplication.MAX_WEEK) { emptyList() }) {
                    value = withContext(Dispatchers.Default) {
                        uniAppToTimeTableData(name,list)
                    }
                }

                LaunchedEffect(currentWeek,items) {
                    if(currentWeek > items.size) {
                        Exception("LaunchedEffect received week out of bounds for length ${items.size} of items[${currentWeek-1}]").printStackTrace()
                        return@LaunchedEffect
                    } else {
                        val list = items[currentWeek.toInt()-1]
                        val weekend = list.find { it.dayOfWeek == 6 || it.dayOfWeek == 7 } != null
                        if(weekend && !showAll) {
                            // 展开
                            showAll = true
                        }
                    }
                }
                // 课程表布局
                Box(modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                // 手指松开后根据累积的水平拖动量决定
                                if (totalDragX > MyApplication.SWIPE) { // 阈值
                                    weekSwap.previousWeek()
                                } else if (totalDragX < -MyApplication.SWIPE) {
                                    weekSwap.nextWeek()
                                }
                                totalDragX = 0f // 重置
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume() // 防止滚动穿透
                                totalDragX += dragAmount
                            }
                        )
                    }
                ) {
                    TimeTable(
                        items,
                        currentWeek.toInt(),
                        showAll,
                        modifier = Modifier
                            .padding(horizontal = APP_HORIZONTAL_DP-(if (showAll) 1.75.dp else 2.5.dp)-1.dp)
                            .verticalScroll(scrollState)
                        ,
                        innerPadding = innerPadding,
                        onTapBlankRegion = {
                            if(isExpand) {
                                isExpand = false
                            } else {
                                showToast("长按切换周")
                            }
                        },
                        onLongTapBlankRegion = {
                            isExpand = !isExpand
                        },
                    ) { list ->
                        bean = list
                        showBottomSheetDetail = true
                    }

                    ShareTwoContainer2D(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = innerPadding.calculateBottomPadding())
                            .padding(APP_HORIZONTAL_DP),
                        show = !isExpand,
                        defaultContent = {
                            TimeTablePreview(
                                items = items, // 一周课程,
                                currentWeek = currentWeek.toInt(),
                                innerPadding = innerPadding,
                            ) {
                                weekSwap.goToWeek(it.toLong())
                                isExpand = !isExpand
                            }
                        },
                        secondContent = {
                            DraggableWeekButton(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(.5f).compositeOver(MaterialTheme.colorScheme.surface),
                                expanded = shouldShowAddButton,
                                onClick = {
                                    weekSwap.backToCurrentWeek()
                                },
                                shaderState = null,
                                currentWeek = currentWeek,
                                key = null,
                                onNext = { weekSwap.nextWeek() },
                                onPrevious = { weekSwap.previousWeek() },
                                onLongClick = {
                                    isExpand = !isExpand
                                }
                            )
                        }
                    )
                    // 中间
                }
            }
        }
    }
}

private suspend fun uniAppToTimeTableData(targetPlace : String,list: List<UniAppClassroomLessonBean>): List<List<TimeTableItem>> {
    try {
        val result = List(MyApplication.MAX_WEEK) { mutableStateListOf<TimeTableItem>() }
        val enableCalendarShowTeacher = DataStoreManager.enableCalendarShowTeacher.first()
        for(item in list) {
            val courseName = item.course.nameZh
            val multiTeacher = item.teacherAssignmentList.size > 1
            for(schedule in item.schedules) {
                val place = schedule.room?.nameZh ?: continue
                if(targetPlace != place) {
                    continue
                }
                val list = result[schedule.weekIndex-1]
                val teacher = when(enableCalendarShowTeacher) {
                    ShowTeacherConfig.ALL.code -> schedule.teacherName
                    ShowTeacherConfig.ONLY_MULTI.code -> {
                        if(multiTeacher) {
                            schedule.teacherName
                        } else {
                            null
                        }
                    }
                    else -> null
                }
                list.add(
                    TimeTableItem(
                        teacher = teacher,
                        type = TimeTableType.COURSE,
                        name = courseName,
                        dayOfWeek = schedule.weekday,
                        startTime = parseJxglstuIntTime(schedule.startTime),
                        endTime = parseJxglstuIntTime(schedule.endTime),
                        place = item.className,
                    )
                )
            }
        }
        // 去重
        distinctUnit(result)
        return result
    } catch (e : Exception) {
        LogUtil.error(e)
        return List(MyApplication.MAX_WEEK) { emptyList<TimeTableItem>() }
    }
}
