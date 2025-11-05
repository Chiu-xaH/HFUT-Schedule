package com.hfut.schedule.ui.screen.home.calendar.communtiy

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
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager.weeksBetween
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.common.DraggableWeekButton
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.getNewWeek
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.next.CourseDetailOrigin
import com.hfut.schedule.ui.screen.home.calendar.timetable.NewTimeTableUI
import com.hfut.schedule.ui.screen.home.calendar.timetable.TimeTableDetail
import com.hfut.schedule.ui.screen.home.calendar.timetable.TimeTableItem
import com.hfut.schedule.ui.screen.home.calendar.timetable.TimeTableType
import com.hfut.schedule.ui.screen.home.calendar.timetable.allToTimeTableData
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getStartWeekFromCommunity
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.mirror.util.ShaderState
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.padding.navigationBarHeightPadding
import dev.chrisbanes.haze.HazeState
import java.time.LocalDate

@Composable
fun CommunityCourseTableUI(
    showAll: Boolean,
    innerPaddings: PaddingValues,
    friendUserName : String? = null,
    onDateChange : (LocalDate) ->Unit,
    today: LocalDate,
    vm : NetWorkViewModel,
    hazeState: HazeState,
    backGroundHaze : ShaderState?,
    navController : NavHostController,
    onSwapShowAll : (Boolean) -> Unit
) {
    val context = LocalContext.current
    //切换周数
    var currentWeek by rememberSaveable {
        mutableLongStateOf(
            if(weeksBetween > 20) {
                getNewWeek()
            } else if(weeksBetween < 1) {
                onDateChange(getStartWeekFromCommunity())
                1
            } else weeksBetween
        )
    }

    var sheet by remember { mutableStateOf(courseDetailDTOList(0,0,"","","", listOf(0),0,"","")) }

    val items by produceState(initialValue = List(MyApplication.MAX_WEEK) { emptyList() }) {
        value = allToTimeTableData(context,friendUserName)
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

    val drag = remember { 5f }

    fun nextWeek() {
        if (currentWeek < 20) {
            onDateChange(today.plusDays(7))
            currentWeek++
        }
    }
    fun previousWeek() {
        if (currentWeek > 1) {
            onDateChange(today.minusDays(7))
            currentWeek--
        }
    }
    var bean by remember { mutableStateOf<List<TimeTableItem>?>(null) }
    var showBottomSheetDetail by remember { mutableStateOf(false) }

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
    //课程详情
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState,
            autoShape = false
        ) {
            HazeBottomSheetTopBar(sheet.name, isPaddingStatusBar = false)
            DetailInfos(sheet,friendUserName != null, vm = vm, hazeState )
        }
    }
    var totalDragX by remember { mutableFloatStateOf(0f) }
    val scrollState = rememberScrollState()

    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(today) {
            detectHorizontalDragGestures(
                onDragEnd = {
                    // 手指松开后根据累积的水平拖动量决定
                    if (totalDragX > drag) { // 阈值
                        previousWeek()
                    } else if (totalDragX < -drag) {
                        nextWeek()
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
        val shouldShowAddButton by remember { derivedStateOf { scrollState.value == 0 } }

        NewTimeTableUI(
            items,
            currentWeek.toInt(),
            showAll,
            modifier = Modifier
                .padding(horizontal = APP_HORIZONTAL_DP-(if (showAll) 1.75.dp else 2.5.dp)-1.dp)
                .verticalScroll(scrollState)
            ,
            innerPadding = innerPaddings,
            shaderState = backGroundHaze,
        ) { list ->
            // 只有一节课
            if (list.size == 1) {
                val item = list[0]
                // 如果是考试
                when(item.type) {
                    TimeTableType.COURSE -> {
                        if(friendUserName == null) {
                            navController.navigateForTransition(AppNavRoute.CourseDetail, AppNavRoute.CourseDetail.withArgs(item.name, CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item.hashCode()}" ))
                        } else {
                            bean = list
                            showBottomSheetDetail = true
                        }
                    }
                    TimeTableType.FOCUS -> {
                        item.id?.let {
                            navController.navigateForTransition(AppNavRoute.AddEvent, AppNavRoute.AddEvent.withArgs(it, CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item.hashCode()}" ))
                        }
                    }
                    TimeTableType.EXAM -> {
                        bean = list
                        showBottomSheetDetail = true
                    }
                }
            } else if (list.size > 1) {
                bean = list
                showBottomSheetDetail = true
            }
        }

//        LazyVerticalGrid(
//            columns = GridCells.Fixed(style.rowCount),
//            modifier = style.calendarPadding(),
//            state = scrollState
//        ) {
//            item(span = { GridItemSpan(maxLineSpan) }) { InnerPaddingHeight(innerPaddings,true) }
//            items(style.rowCount*style.columnCount) { cell ->
//                val itemList = if(showAll)tableAll[cell].toMutableList() else table[cell].toMutableList()
//                val texts = transferSummaryCourseInfos(itemList).toMutableList()
//                if(texts.isEmpty() && backGroundHaze != null) {
//                    // 隐藏
//                    Box(modifier = Modifier
//                        .height(calendarSquareHeight.dp)
//                        .padding(style.everyPadding))
//                } else {
//                    Card(
//                        shape = style.containerCorner,
//                        colors = CardDefaults.cardColors(containerColor = color),
//                        modifier = Modifier
//                            .height(calendarSquareHeight.dp)
//                            .padding(style.everyPadding)
//                            .let {
//                                if (backGroundHaze != null) {
//                                    it
//                                        .clip(style.containerCorner)
//                                        .let {
//                                            if (AppVersion.CAN_SHADER) {
//                                                it.calendarSquareGlass(
//                                                    backGroundHaze,
//                                                    style.containerColor.copy(customBackgroundAlpha),
//                                                    enableLiquidGlass,
//                                                )
//                                            } else {
//                                                it
//                                            }
//                                        }
//                                } else {
//                                    it
//                                }
//                            }
//                            .clickableWithScale(ClickScale.SMALL.scale) {
//                                if (texts.size == 1) {
//                                    // 如果是考试
//                                    if (texts[0].contains("考试")) {
//                                        showToast(texts[0].replace("\n", " "))
//                                        return@clickableWithScale
//                                    }
//                                    if (texts[0].contains("日程")) {
//                                        showToast(texts[0].replace("\n", " "))
//                                        return@clickableWithScale
//                                    }
//                                    sheet = itemList[0]
//                                    showBottomSheet = true
//                                } else if (itemList.size > 1) {
//                                    multiWeekday =
//                                        if (showAll) (cell + 1) % 7 else (cell + 1) % 5
//                                    multiWeek = currentWeek.toInt()
//                                    courses = itemList
//                                    showBottomSheetMultiCourse = true
//                                }
//                            }
//                    ) {
//                        if(texts.size == 1) {
//                            val l = texts[0].split("\n")
//                            if(l.size < 2) {
//                                return@Card
//                            }
//                            val time = l[0]
//                            val name = l[1]
//                            val place = if(l.size == 3) {
//                                val p = l[2]
//                                if(p == "null" || p.isBlank() || p.isEmpty()) {
//                                    null
//                                } else {
//                                    p
//                                }
//                            } else null
//
//                            Column(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .padding(horizontal = CARD_NORMAL_DP) ,
//                                verticalArrangement = Arrangement.SpaceBetween,
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                Text(
//                                    text = time,
//                                    fontSize = style.textSize,
//                                    textAlign = TextAlign.Center,
//                                    modifier = Modifier.fillMaxWidth()
//                                )
//                                Box(
//                                    modifier = Modifier
//                                        .weight(1f) // 占据中间剩余的全部空间
//                                        .fillMaxWidth(),
//                                    contentAlignment = Alignment.TopCenter
//                                ) {
//                                    Text(
//                                        text = name,
//                                        fontSize = style.textSize,
//                                        textAlign = TextAlign.Center,
//                                        overflow = TextOverflow.Ellipsis, // 超出显示省略号
//                                        modifier = Modifier.fillMaxWidth()
//                                    )
//                                }
//                                place?.let {
//                                    Text(
//                                        text = it,
//                                        fontSize = style.textSize,
//                                        textAlign = TextAlign.Center,
//                                        modifier = Modifier.fillMaxWidth()
//                                    )
//                                }
//                            }
//                        } else if(texts.size > 1) {
//                            val name = texts.map {
//                                it.split("\n")[1][0]
//                            }.joinToString(",")
//                            val isExam = if(texts.toString().contains("考试")) FontWeight.SemiBold else FontWeight.Normal
//                            Column(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .padding(horizontal = CARD_NORMAL_DP) ,
//                                verticalArrangement = Arrangement.SpaceBetween,
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                Text(
//                                    text = texts[0].substringBefore("\n"),
//                                    fontSize = style.textSize,
//                                    textAlign = TextAlign.Center,
//                                    modifier = Modifier.fillMaxWidth(),
//                                    fontWeight = isExam
//                                )
//                                Box(
//                                    modifier = Modifier
//                                        .weight(1f) // 占据中间剩余的全部空间
//                                        .fillMaxWidth(),
//                                    contentAlignment = Alignment.TopCenter
//                                ) {
//                                    Text(
//                                        text = "${texts.size}节课冲突",
//                                        fontSize = style.textSize,
//                                        textAlign = TextAlign.Center,
//                                        overflow = TextOverflow.Ellipsis, // 超出显示省略号
//                                        modifier = Modifier.fillMaxWidth(),
//                                        fontWeight = isExam
//                                    )
//                                }
//                                Text(
//                                    text = name,
//                                    fontSize = style.textSize,
//                                    textAlign = TextAlign.Center,
//                                    modifier = Modifier.fillMaxWidth()
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//            item(span = { GridItemSpan(maxLineSpan) }) { InnerPaddingHeight(innerPaddings,false) }
//        }

        DraggableWeekButton(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(.5f).compositeOver(MaterialTheme.colorScheme.surface),
            shaderState = backGroundHaze,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = innerPaddings.calculateBottomPadding() - navigationBarHeightPadding)
                .padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP),
            expanded = shouldShowAddButton,
            onClick = {
                if(weeksBetween < 1) {
                    currentWeek = 1
                    onDateChange(getStartWeekFromCommunity())
                } else {
                    currentWeek = weeksBetween
                    onDateChange(LocalDate.now())
                }
            },
            currentWeek = currentWeek,
            key = today,
            onNext = { nextWeek() },
            onPrevious = { previousWeek() }
        )
    }
}