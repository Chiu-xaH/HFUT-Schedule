package com.hfut.schedule.ui.screen.home.calendar.zjgd

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.model.zhijian.ZhiJianCourseItemDto
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.getMondayOfWeek
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.icon.DepartmentIcons
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.BottomSheetTopBar
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.common.calendarSquareGlass
import com.hfut.schedule.ui.screen.home.calendar.common.numToChinese
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.clearUnit
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.distinctUnit
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.getNewWeek
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.ApiToFailRate
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch.ApiForCourseSearch
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getJxglstuStartDate
import com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch.ApiToTeacherSearch
import com.hfut.schedule.ui.style.CalendarStyle
import com.hfut.schedule.ui.style.corner.bottomSheetRound
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.mirror.util.ShaderState
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.ClickScale
import com.xah.uicommon.style.clickableWithScale
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.xah.uicommon.style.padding.navigationBarHeightPadding
import dev.chrisbanes.haze.HazeState
import java.time.LocalDate

@Composable
fun ZhiJianCourseTableUI(
    showAll: Boolean,
    vm : NetWorkViewModel,
    innerPadding: PaddingValues,
    studentId : String,
    today: LocalDate,
    onDateChange: (LocalDate) ->Unit,
    backGroundHaze : ShaderState?,
    hazeState: HazeState,
    onSwapShowAll : (Boolean) -> Unit
) {
    val uiState by vm.zhiJianCourseResp.state.collectAsState()
    val table = remember { List(30) { mutableStateListOf<ZhiJianCourseItemDto>() } }
    val tableAll = remember { List(42) { mutableStateListOf<ZhiJianCourseItemDto>() } }
    val refreshNetwork = suspend m@ {
        val token = prefs.getString("ZhiJian","")
        if(token == null) {
            return@m
        }
        val date = getMondayOfWeek(today).format(DateTimeManager.formatter_YYYY_MM_DD)
        vm.zhiJianCourseResp.clear()
        vm.getZhiJianCourses(studentId,date,token)
    }

    var currentWeek by rememberSaveable {
        mutableLongStateOf(
            if(DateTimeManager.weeksBetweenJxglstu > 20) {
                getNewWeek()
            } else if(DateTimeManager.weeksBetweenJxglstu < 1) {
                onDateChange(getJxglstuStartDate())
                1L
            } else {
                DateTimeManager.weeksBetweenJxglstu
            }
        )
    }

    LaunchedEffect(currentWeek,studentId) {
        if(studentId.length != 10) {
            return@LaunchedEffect
        }
        refreshNetwork()
    }
    var multiWeekday by remember { mutableIntStateOf(0) }
    var multiWeek by remember { mutableIntStateOf(0) }
    var showBottomSheetMultiCourse by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<List<ZhiJianCourseItemDto>>(emptyList()) }
    if (showBottomSheetMultiCourse) {
        HazeBottomSheet (
            showBottomSheet = showBottomSheetMultiCourse,
            onDismissRequest = {
                showBottomSheetMultiCourse = false
            },
            autoShape = false,
            hazeState = hazeState
        ) {
            MultiCourseSheetUIForZhiJian(courses = selectedItem ,weekday = multiWeekday,week = multiWeek,vm = vm, hazeState = hazeState)
        }
    }
    var showBottomSheetCourse by remember { mutableStateOf(false) }
    if (showBottomSheetCourse) {
        HazeBottomSheet (
            showBottomSheet = showBottomSheetCourse,
            onDismissRequest = {
                showBottomSheetCourse = false
            },
            autoShape = false,
            hazeState = hazeState
        ) {
            CourseDetail(vm,hazeState,selectedItem[0])
        }
    }

    var findNewCourse by remember { mutableStateOf(false) }

    val customBackgroundAlpha by DataStoreManager.customCalendarSquareAlpha.collectAsState(initial = 1f)
    val enableTransition = !(backGroundHaze != null && AppVersion.CAN_SHADER)
    val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = AppVersion.CAN_SHADER)

    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val list = (uiState as UiState.Success).data
        fun refreshUI() {
            // 清空
            if(showAll) {
                clearUnit(tableAll)
            } else {
                clearUnit(table)
            }
            findNewCourse = false

            try {
                // 组装
                for (item in list) {
                    when(item.weekday) {
                        6 -> findNewCourse = true
                        7 -> findNewCourse = true
                    }
                    if(showAll) {
                        if (item.weekday == 1) {
                            if (item.startPeriod in 1..2) {
                                tableAll[0].add(item)
                            }
                            if (item.startPeriod in 3..4) {
                                tableAll[7].add(item)
                            }
                            if (item.startPeriod in 5..6) {
                                tableAll[14].add(item)
                            }
                            if (item.startPeriod in 7..8) {
                                tableAll[21].add(item)
                            }
                            if (item.startPeriod > 8) {
                                tableAll[28].add(item)
                            }
                        }
                        if (item.weekday == 2) {
                            if (item.startPeriod in 1..2) {
                                tableAll[1].add(item)
                            }
                            if (item.startPeriod in 3..4) {
                                tableAll[8].add(item)
                            }
                            if (item.startPeriod in 5..6) {
                                tableAll[15].add(item)
                            }
                            if (item.startPeriod in 7..8) {
                                tableAll[22].add(item)
                            }
                            if (item.startPeriod > 8) {
                                tableAll[29].add(item)
                            }
                        }
                        if (item.weekday == 3) {
                            if (item.startPeriod in 1..2) {
                                tableAll[2].add(item)
                            }
                            if (item.startPeriod in 3..4) {
                                tableAll[9].add(item)
                            }
                            if (item.startPeriod in 5..6) {
                                tableAll[16].add(item)
                            }
                            if (item.startPeriod in 7..8) {
                                tableAll[23].add(item)
                            }
                            if (item.startPeriod > 8) {
                                tableAll[30].add(item)
                            }
                        }
                        if (item.weekday == 4) {
                            if (item.startPeriod in 1..2) {
                                tableAll[3].add(item)
                            }
                            if (item.startPeriod in 3..4) {
                                tableAll[10].add(item)
                            }
                            if (item.startPeriod in 5..6) {
                                tableAll[17].add(item)
                            }
                            if (item.startPeriod in 7..8) {
                                tableAll[24].add(item)
                            }
                            if (item.startPeriod > 8) {
                                tableAll[31].add(item)
                            }
                        }
                        if (item.weekday == 5) {
                            if (item.startPeriod in 1..2) {
                                tableAll[4].add(item)
                            }
                            if (item.startPeriod in 3..4) {
                                tableAll[11].add(item)
                            }
                            if (item.startPeriod in 5..6) {
                                tableAll[18].add(item)
                            }
                            if (item.startPeriod in 7..8) {
                                tableAll[25].add(item)
                            }
                            if (item.startPeriod > 8) {
                                tableAll[32].add(item)
                            }
                        }
                        if (item.weekday == 6) {
                            if (item.startPeriod in 1..2) {
                                tableAll[5].add(item)
                            }
                            if (item.startPeriod in 3..4) {
                                tableAll[12].add(item)
                            }
                            if (item.startPeriod in 5..6) {
                                tableAll[19].add(item)
                            }
                            if (item.startPeriod in 7..8) {
                                tableAll[26].add(item)
                            }
                            if (item.startPeriod > 8) {
                                tableAll[33].add(item)
                            }
                        }
                        if (item.weekday == 7) {
                            if (item.startPeriod in 1..2) {
                                tableAll[6].add(item)
                            }
                            if (item.startPeriod in 3..4) {
                                tableAll[13].add(item)
                            }
                            if (item.startPeriod in 5..6) {
                                tableAll[20].add(item)
                            }
                            if (item.startPeriod in 7..8) {
                                tableAll[27].add(item)
                            }
                            if (item.startPeriod > 8) {
                                tableAll[34].add(item)
                            }
                        }
                    } else {
                        if (item.weekday == 1) {
                            if (item.startPeriod in 1..2) {
                                table[0].add(item)
                            }
                            if (item.startPeriod in 3..4) {
                                table[5].add(item)
                            }
                            if (item.startPeriod in 5..6) {
                                table[10].add(item)
                            }
                            if (item.startPeriod in 7..8) {
                                table[15].add(item)
                            }
                            if (item.startPeriod > 8) {
                                table[20].add(item)
                            }
                        }
                        if (item.weekday == 2) {
                            if (item.startPeriod in 1..2) {
                                table[1].add(item)
                            }
                            if (item.startPeriod in 3..4) {
                                table[6].add(item)
                            }
                            if (item.startPeriod in 5..6) {
                                table[11].add(item)
                            }
                            if (item.startPeriod in 7..8) {
                                table[16].add(item)
                            }
                            if (item.startPeriod > 8) {
                                table[21].add(item)
                            }
                        }
                        if (item.weekday == 3) {
                            if (item.startPeriod in 1..2) {
                                table[2].add(item)
                            }
                            if (item.startPeriod in 3..4) {
                                table[7].add(item)
                            }
                            if (item.startPeriod in 5..6) {
                                table[12].add(item)
                            }
                            if (item.startPeriod in 7..8) {
                                table[17].add(item)
                            }
                            if (item.startPeriod > 8) {
                                table[22].add(item)
                            }
                        }
                        if (item.weekday == 4) {
                            if (item.startPeriod in 1..2) {
                                table[3].add(item)
                            }
                            if (item.startPeriod in 3..4) {
                                table[8].add(item)
                            }
                            if (item.startPeriod in 5..6) {
                                table[13].add(item)
                            }
                            if (item.startPeriod in 7..8) {
                                table[18].add(item)
                            }
                            if (item.startPeriod > 8) {
                                table[23].add(item)
                            }
                        }
                        if (item.weekday == 5) {
                            if (item.startPeriod in 1..2) {
                                table[4].add(item)
                            }
                            if (item.startPeriod in 3..4) {
                                table[9].add(item)
                            }
                            if (item.startPeriod in 5..6) {
                                table[14].add(item)
                            }
                            if (item.startPeriod in 7..8) {
                                table[19].add(item)
                            }
                            if (item.startPeriod > 8) {
                                table[24].add(item)
                            }
                        }
                    }
                }

                // 去重
                if(showAll) {
                    distinctUnit(tableAll)
                } else {
                    distinctUnit(table)
                }
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
        LaunchedEffect(showAll) {
            refreshUI()
        }
        LaunchedEffect(findNewCourse) {
            if(findNewCourse && !showAll) {
                onSwapShowAll(true)
            }
        }
        val scrollState = rememberLazyGridState()
        val shouldShowAddButton by remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset == 0 } }
        val style = CalendarStyle(showAll)
        val color =  if(enableTransition) style.containerColor.copy(customBackgroundAlpha) else Color.Transparent
        val calendarSquareHeight by DataStoreManager.calendarSquareHeight.collectAsState(initial = MyApplication.CALENDAR_SQUARE_HEIGHT)
        Box {
            LazyVerticalGrid(
                columns = GridCells.Fixed(style.rowCount),
                modifier = style.calendarPadding(),
                state = scrollState
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    InnerPaddingHeight(
                        innerPadding,
                        true
                    )
                }
                items(style.rowCount * style.columnCount) { cell ->
                    val itemList =
                        if (showAll) tableAll[cell].toMutableList() else table[cell].toMutableList()
                    if (itemList.isEmpty() && backGroundHaze != null) {
                        // 隐藏
                        Box(modifier = Modifier
                            .height(calendarSquareHeight.dp)
                            .padding(style.everyPadding))
                    } else {
                        Card(
                            shape = style.containerCorner,
                            colors = CardDefaults.cardColors(containerColor = color),
                            modifier = Modifier
                                .height(calendarSquareHeight.dp)
                                .padding(style.everyPadding)
                                .let {
                                    if(backGroundHaze != null) {
                                        it
                                            .clip(style.containerCorner)
                                            .let {
                                                if(AppVersion.CAN_SHADER) {
                                                    it.calendarSquareGlass(
                                                        backGroundHaze,
                                                        style.containerColor.copy(customBackgroundAlpha),
                                                        enableLiquidGlass,
                                                    )
                                                } else {
                                                    it
                                                }
                                            }
                                    } else {
                                        it
                                    }
                                }
                                .clickableWithScale(ClickScale.SMALL.scale) {
                                    if (itemList.size == 1) {
                                        selectedItem = itemList
                                        showBottomSheetCourse = true
                                    } else if (itemList.size > 1) {
                                        multiWeekday =
                                            if (showAll) (cell + 1) % 7 else (cell + 1) % 5
                                        multiWeek = currentWeek.toInt()
                                        selectedItem = itemList
                                        showBottomSheetMultiCourse = true
                                    }
                                }
                        ) {

                            if (itemList.size == 1) {
                                val l = itemList[0]
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = CARD_NORMAL_DP),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "${l.startPeriod}-${l.endPeriod}节",
                                        fontSize = style.textSize,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(1f) // 占据中间剩余的全部空间
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.TopCenter
                                    ) {
                                        Text(
                                            text = l.courseName,
                                            fontSize = style.textSize,
                                            textAlign = TextAlign.Center,
                                            overflow = TextOverflow.Ellipsis, // 超出显示省略号
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    Text(
                                        text = l.place,
                                        fontSize = style.textSize,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            } else if (itemList.size > 1) {
                                val name = itemList.map {
                                    it.courseName[0]
                                }.joinToString(",")
                                val l = itemList[0]
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = CARD_NORMAL_DP),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "${l.startPeriod}-${l.endPeriod}节",
                                        fontSize = style.textSize,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(1f) // 占据中间剩余的全部空间
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.TopCenter
                                    ) {
                                        Text(
                                            text = "${itemList.size}节课冲突",
                                            fontSize = style.textSize,
                                            textAlign = TextAlign.Center,
                                            overflow = TextOverflow.Ellipsis, // 超出显示省略号
                                            modifier = Modifier.fillMaxWidth(),
                                        )
                                    }
                                    Text(
                                        text = name,
                                        fontSize = style.textSize,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    InnerPaddingHeight(
                        innerPadding,
                        false
                    )
                }
            }
            // 上一周
            AnimatedVisibility(
                visible = shouldShowAddButton,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = innerPadding.calculateBottomPadding() - navigationBarHeightPadding)
                    .padding(APP_HORIZONTAL_DP)
            ) {
                FloatingActionButton(
                    onClick = {
                        if (currentWeek > 1) {
                            currentWeek-- - 1
                            onDateChange(today.minusDays(7))
                        }
                    },
                ) { Icon(Icons.Filled.ArrowBack, "Add Button") }
            }
            // 中间
            AnimatedVisibility(
                visible = shouldShowAddButton,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = innerPadding.calculateBottomPadding() - navigationBarHeightPadding)
                    .padding(APP_HORIZONTAL_DP)
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        if (DateTimeManager.weeksBetweenJxglstu < 1) {
                            currentWeek = 1
                            onDateChange(getJxglstuStartDate())
                        } else {
                            currentWeek = DateTimeManager.weeksBetweenJxglstu
                            onDateChange(LocalDate.now())
                        }
                    },
                ) {
                    AnimatedContent(
                        targetState = currentWeek,
                        transitionSpec = {
                            scaleIn(
                                animationSpec = tween(500)
                            ) togetherWith (scaleOut(animationSpec = tween(500)))
                        }, label = ""
                    ) { n ->
                        Text(text = "第 $n 周")
                    }
                }
            }
            // 下一周
            AnimatedVisibility(
                visible = shouldShowAddButton,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = innerPadding.calculateBottomPadding() - navigationBarHeightPadding)
                    .padding(APP_HORIZONTAL_DP)
            ) {
                FloatingActionButton(
                    onClick = {
                        if (currentWeek < 20) {
                            currentWeek++ + 1
                            onDateChange(today.plusDays(7))
                        }
                    },
                ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiCourseSheetUIForZhiJian(week : Int, weekday : Int, courses : List<ZhiJianCourseItemDto>, vm: NetWorkViewModel, hazeState: HazeState) {
    var showBottomSheetTotalCourse by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(-1) }
    if (showBottomSheetTotalCourse) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheetTotalCourse = false
            },
            hazeState = hazeState,
            autoShape = false,
            showBottomSheet = showBottomSheetTotalCourse
        ) {
            CourseDetail(vm,hazeState,courses[selectedIndex])
        }
    }
    Column {
        HazeBottomSheetTopBar("第${week}周 周${numToChinese(weekday)}", isPaddingStatusBar = false)
        LargeCard(
            title = "${courses.size}节课冲突"
        ) {
            for(index in courses.indices) {
                val course = courses[index]
                TransplantListItem(
                    headlineContent = {
                        Text(course.courseName)
                    },
                    supportingContent = {
                        Text("${course.place} ${course.startPeriod}-${course.endPeriod}节")
                    },
                    leadingContent = {
                        Text((index+1).toString())
                    },
                    modifier = Modifier.clickable {
                        selectedIndex = index
                        showBottomSheetTotalCourse = true
                    }
                )
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseDetail(
    vm: NetWorkViewModel,
    hazeState : HazeState,
    course : ZhiJianCourseItemDto
) {

    var showBottomSheet_Teacher by remember { mutableStateOf(false) }
    val sheetState_Teacher = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (showBottomSheet_Teacher) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Teacher = false },
            sheetState = sheetState_Teacher,
            shape = bottomSheetRound(sheetState_Teacher)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    BottomSheetTopBar("教师检索 ${course.teacher}")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ApiToTeacherSearch(course.teacher,vm,innerPadding)
                }
            }
        }
    }

    val sheetState_FailRate = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_FailRate by remember { mutableStateOf(false) }

    if (showBottomSheet_FailRate) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_FailRate = false },
            sheetState = sheetState_FailRate,
            shape = bottomSheetRound(sheetState_FailRate)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    BottomSheetTopBar("挂科率 ${course.courseName}")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    ApiToFailRate(course.courseName,vm, hazeState =hazeState ,innerPadding)
                }
            }
        }
    }

    var showBottomSheet_Search by remember { mutableStateOf(false) }

    var searchAll by remember { mutableStateOf(false) }
    ApiForCourseSearch(vm,null, course.code.let { if(searchAll) it.substringBefore("--") else it },showBottomSheet_Search, hazeState = hazeState) {
        showBottomSheet_Search = false
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        HazeBottomSheetTopBar(course.courseName, isPaddingStatusBar = false)
        CustomCard (color = cardNormalColor()){
            TransplantListItem(
                headlineContent = {
                    Text(course.place)
                },
                overlineContent = { Text("地点") },
                leadingContent = {
                    Icon(painterResource(R.drawable.near_me),null)
                }
            )
            TransplantListItem(
                headlineContent = {
                    Text(course.teacher)
                },
                overlineContent = { Text("教师") },
                modifier = Modifier.clickable {
                    showBottomSheet_Teacher = true
                },
                leadingContent = {
                    Icon(painterResource(R.drawable.person),null)
                }
            )
            TransplantListItem(
                headlineContent = {
                    Text(
                        course.date + " " + "周" + numToChinese(course.weekday) + " " +
                                course.startPeriod.toString() + "-" + course.endPeriod + "节")
                },
                overlineContent = { Text("时间") },
                leadingContent = {
                    Icon(painterResource(R.drawable.schedule),null)
                }
            )
            PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = {
                    Text(course.code)
                },
                overlineContent = { Text("课程代码--教学班") },
                trailingContent = {
                    FilledTonalButton(
                        onClick = {
                            searchAll = true
                            showBottomSheet_Search = true
                        }
                    ) {
                        Text("开课查询")
                    }
                },
                modifier = Modifier.clickable {
                    ClipBoardUtils.copy(course.code)
                },
                leadingContent = {
                    Icon(painterResource(R.drawable.tag),null)
                }
            )
            TransplantListItem(
                headlineContent = {
                    Text(course.type)
                },
                overlineContent = { Text("类型") },
                leadingContent = {
                    Icon(painterResource(R.drawable.kid_star),null)
                }
            )
            TransplantListItem(
                headlineContent = {
                    Text(course.department)
                },
                overlineContent = { Text("开设学院") },
                leadingContent = {
                    DepartmentIcons(course.department)
                }
            )
            TransplantListItem(
                overlineContent = {
                    Text("班级")
                },
                headlineContent = {
                    Text(course.classes)
                },
                leadingContent = {
                    Icon(painterResource(R.drawable.sensor_door),null)
                },
            )
            TransplantListItem(
                headlineContent = {
                    Text("挂科率")
                },
                leadingContent = {
                    Icon(painterResource(AppNavRoute.FailRate.icon),null)
                },
                modifier = Modifier.clickable {
                    showBottomSheet_FailRate = true
                }
            )
        }
        CardListItem(
            headlineContent = {
                Text("在开课查询中查看详情")
            },
            modifier = Modifier.clickable {
                searchAll = false
                showBottomSheet_Search = true
            },
            trailingContent = {
                Icon(Icons.Default.ArrowForward,null)
            }
        )
        Spacer(Modifier.height(APP_HORIZONTAL_DP).navigationBarsPadding())
    }
}
