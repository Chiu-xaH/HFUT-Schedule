package com.hfut.schedule.ui.screen.home.calendar.uniapp

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.ShareTwoContainer2D
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.common.DraggableWeekButton
import com.hfut.schedule.ui.screen.home.calendar.common.TimeTableWeekSwap
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.getNewWeek
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.next.CourseDetailOrigin
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableItem
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableType
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.allToTimeTableDataUniApp
import com.hfut.schedule.ui.screen.home.calendar.timetable.ui.TimeTable
import com.hfut.schedule.ui.screen.home.calendar.timetable.ui.TimeTableDetail
import com.hfut.schedule.ui.screen.home.calendar.timetable.ui.TimeTablePreview
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getJxglstuStartDate
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.xah.mirror.util.ShaderState
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.padding.navigationBarHeightPadding
import dev.chrisbanes.haze.HazeState
import java.time.LocalDate


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun UniAppCoursesScreen(
    showAll: Boolean,
    innerPadding: PaddingValues,
    onDateChange: (LocalDate) ->Unit,
    today: LocalDate,
    hazeState: HazeState,
    navController: NavHostController,
    backGroundHaze : ShaderState?,
    onSwapShowAll : (Boolean) -> Unit
) {
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
        onDateChange(getJxglstuStartDate())
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
                onDateChange(getJxglstuStartDate())
            } else {
                currentWeek = DateTimeManager.weeksBetweenJxglstu
                onDateChange(LocalDate.now())
            }
        }

        override fun goToWeek(i: Long) {
            if(currentWeek == i) {
                return
            }
            if (i in 1..MyApplication.MAX_WEEK) {
                val day = 7L*(i - currentWeek)
                onDateChange(today.plusDays(day))
                currentWeek = i
            }
            showToast("第${currentWeek}周")
        }

        override fun nextWeek() {
            if (currentWeek < MyApplication.MAX_WEEK) {
                onDateChange(today.plusDays(7))
                currentWeek++
            }
        }

        override fun previousWeek() {
            if (currentWeek > 1) {
                onDateChange(today.minusDays(7))
                currentWeek--
            }
        }
    } }
    val items by produceState(initialValue = List(MyApplication.MAX_WEEK) { emptyList() }) {
        value = allToTimeTableDataUniApp()
    }

    LaunchedEffect(currentWeek,items) {
        if(currentWeek >= items.size) {
            Exception("LaunchedEffect received week out of bounds for length ${items.size} of items[${currentWeek-1}]").printStackTrace()
            return@LaunchedEffect
        } else {
            val list = items[currentWeek.toInt()-1]
            val weekend = list.find { it.dayOfWeek == 6 || it.dayOfWeek == 7 } != null
            if(weekend && !showAll) {
                // 展开
                onSwapShowAll(true)
            }
        }
    }
    // 课程表布局
    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(today) {
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
            shaderState = backGroundHaze,
            onTapBlankRegion = {
                if(isExpand) {
                    isExpand = false
                } else {
                    showToast("空白区域双击添加日程,长按切换周")
                }
            },
            onLongTapBlankRegion = {
                isExpand = !isExpand
            },
            onDoubleTapBlankRegion = {
                navController.navigateForTransition(
                    AppNavRoute.AddEvent,
                    AppNavRoute.AddEvent.withArgs()
                )
            }
        ) { list ->
            // 只有一节课
            if (list.size == 1) {
                val item = list[0]
                // 如果是考试
                when(item.type) {
                    TimeTableType.COURSE -> {
                        navController.navigateForTransition(AppNavRoute.CourseDetail, AppNavRoute.CourseDetail.withArgs(item.name, CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item.hashCode()}" ))
                    }
                    TimeTableType.FOCUS -> {
                        item.id?.let {
                            navController.navigateForTransition(AppNavRoute.AddEvent, AppNavRoute.AddEvent.withArgs(it, CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item.hashCode()}" ))
                        }
                    }
                    TimeTableType.EXAM -> {
                        navController.navigateForTransition(AppNavRoute.Exam, AppNavRoute.Exam.withArgs(CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item.hashCode()}"))
                    }
                }
            } else if (list.size > 1) {
                bean = list
                showBottomSheetDetail = true
            }
        }

        ShareTwoContainer2D(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = innerPadding.calculateBottomPadding() - navigationBarHeightPadding)
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
                    shaderState = backGroundHaze,
                    currentWeek = currentWeek,
                    key = today,
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
