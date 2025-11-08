package com.hfut.schedule.ui.screen.home.calendar.jxglstu.next

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.model.jxglstu.DatumResponse
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.common.DraggableWeekButton
import com.hfut.schedule.ui.screen.home.calendar.common.calendarSquareGlass
import com.hfut.schedule.ui.screen.home.calendar.common.numToChinese
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CourseDetailApi
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.clearUnit
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.distinctUnit
import com.hfut.schedule.ui.screen.home.focus.funiction.parseTimeItem
import com.hfut.schedule.ui.style.CalendarStyle
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.mirror.util.ShaderState
import com.xah.transition.component.containerShare
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.ClickScale
import com.xah.uicommon.style.clickableWithScale
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.xah.uicommon.style.padding.navigationBarHeightPadding
import dev.chrisbanes.haze.HazeState


enum class CourseDetailOrigin(val t : String) {
    CALENDAR_JXGLSTU("教务方格"),
    CALENDAR_NEXT("下学期方格"),
    FOCUS_TOMORROW("明日聚焦"),
    FOCUS_TODAY("今日聚焦")
}

fun parseCourseName(text : String) : String? {
    try {
        if(text.contains("\n")) {
            val list = text.split("\n")
            val course = list[1]
            return course
        } else {
            return null
        }
    } catch (_:Exception) {
        return null
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun JxglstuCourseTableUINext(
    showAll: Boolean,
    vm: NetWorkViewModel,
    hazeState: HazeState,
    navController: NavHostController,
    innerPadding : PaddingValues,
    backGroundHaze : ShaderState?,
    onSwapShowAll : (Boolean) -> Unit
) {
    var showBottomSheetTotalCourse by remember { mutableStateOf(false) }
    var showBottomSheetMultiCourse by remember { mutableStateOf(false) }
    var courseName by remember { mutableStateOf("") }

    var courses by remember { mutableStateOf(listOf<String>()) }
    var multiWeekday by remember { mutableIntStateOf(0) }
    var multiWeek by remember { mutableIntStateOf(0) }

    if (showBottomSheetTotalCourse) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheetTotalCourse = false
            },
            showBottomSheet = showBottomSheetTotalCourse,
            hazeState = hazeState
        ) {
            CourseDetailApi(isNext = true,courseName = courseName, vm = vm, hazeState = hazeState)
        }
    }

    if (showBottomSheetMultiCourse) {
        HazeBottomSheet (
            showBottomSheet = showBottomSheetMultiCourse,
            onDismissRequest = {
                showBottomSheetMultiCourse = false
            },
            autoShape = false,
            hazeState = hazeState
        ) {
            MultiCourseSheetUI(courses = courses ,weekday = multiWeekday,week = multiWeek,vm = vm, hazeState = hazeState)
        }
    }

    val table = rememberSaveable { List(30) { mutableStateListOf<String>() } }
    val tableAll = rememberSaveable { List(42) { mutableStateListOf<String>() } }

    var currentWeek by rememberSaveable { mutableLongStateOf(1) }
    var findNewCourse by remember { mutableStateOf(false) }

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
            val json = prefs.getString("jsonNext", "")
            val datumResponse = Gson().fromJson(json, DatumResponse::class.java)
            val scheduleList = datumResponse.result.scheduleList
            val lessonList = datumResponse.result.lessonList

            for (i in scheduleList.indices) {
                val item = scheduleList[i]
                val startTime = item.startTime
                val startHour = startTime / 100
                val startMinute = startTime % 100
                val startTimeStr = "$startHour:${parseTimeItem(startMinute)}"

                var room = item.room?.nameZh
                var courseId = item.lessonId.toString()
                room = room?.replace("学堂","") ?: ""


                for (j in lessonList.indices) {
                    if (courseId == lessonList[j].id) {
                        courseId = lessonList[j].courseName
                    }
                }

                val text = startTimeStr + "\n" + courseId + "\n" + room

                if (item.weekIndex == currentWeek.toInt()) {
                    when(item.weekday) {
                        6 -> findNewCourse = text.isNotEmpty()
                        7 -> findNewCourse = text.isNotEmpty()
                    }
                    if(showAll) {
                        if (item.weekday == 1) {
                            if (startHour in 8..9 ) {
                                tableAll[0].add(text)
                            }
                            if (startHour in 10..11 ) {
                                tableAll[7].add(text)
                            }
                            if (startHour in 14..15 ) {
                                tableAll[14].add(text)
                            }
                            if (startHour in 16..17 ) {
                                tableAll[21].add(text)
                            }
                            if (startHour in 19..21 ) {
                                tableAll[28].add(text)
                            }
                        }
                        if (item.weekday == 2) {
                            if (startHour in 8..9 ) {
                                tableAll[1].add(text)
                            }
                            if (startHour in 10..11 ) {
                                tableAll[8].add(text)
                            }
                            if (startHour in 14..15 ) {
                                tableAll[15].add(text)
                            }
                            if (startHour in 16..17 ) {
                                tableAll[22].add(text)
                            }
                            if (startHour in 19..21 ) {
                                tableAll[29].add(text)
                            }
                        }
                        if (item.weekday == 3) {
                            if (startHour in 8..9 ) {
                                tableAll[2].add(text)
                            }
                            if (startHour in 10..11 ) {
                                tableAll[9].add(text)
                            }
                            if (startHour in 14..15 ) {
                                tableAll[16].add(text)
                            }
                            if (startHour in 16..17 ) {
                                tableAll[23].add(text)
                            }
                            if (startHour in 19..21 ) {
                                tableAll[30].add(text)
                            }
                        }
                        if (item.weekday == 4) {
                            if (startHour in 8..9 ) {
                                tableAll[3].add(text)
                            }
                            if (startHour in 10..11 ) {
                                tableAll[10].add(text)
                            }
                            if (startHour in 14..15 ) {
                                tableAll[17].add(text)
                            }
                            if (startHour in 16..17 ) {
                                tableAll[24].add(text)
                            }
                            if (startHour in 19..21 ) {
                                tableAll[31].add(text)
                            }
                        }
                        if (item.weekday == 5) {
                            if (startHour in 8..9 ) {
                                tableAll[4].add(text)
                            }
                            if (startHour in 10..11 ) {
                                tableAll[11].add(text)
                            }
                            if (startHour in 14..15 ) {
                                tableAll[18].add(text)
                            }
                            if (startHour in 16..17 ) {
                                tableAll[25].add(text)
                            }
                            if (startHour in 19..21 ) {
                                tableAll[32].add(text)
                            }
                        }
                        if (item.weekday == 6) {
                            if (startHour in 8..9 ) {
                                tableAll[5].add(text)
                            }
                            if (startHour in 10..11 ) {
                                tableAll[12].add(text)
                            }
                            if (startHour in 14..15 ) {
                                tableAll[19].add(text)
                            }
                            if (startHour in 16..17 ) {
                                tableAll[26].add(text)
                            }
                            if (startHour in 19..21 ) {
                                tableAll[33].add(text)
                            }
                        }
                        if (item.weekday == 7) {
                            if (startHour in 8..9 ) {
                                tableAll[6].add(text)
                            }
                            if (startHour in 10..11 ) {
                                tableAll[13].add(text)
                            }
                            if (startHour in 14..15 ) {
                                tableAll[20].add(text)
                            }
                            if (startHour in 16..17 ) {
                                tableAll[27].add(text)
                            }
                            if (startHour in 19..21 ) {
                                tableAll[34].add(text)
                            }
                        }
                    } else {
                        if (item.weekday == 1) {
                            if (startHour in 8..9 ) {
                                table[0].add(text)
                            }
                            if (startHour in 10..11 ) {
                                table[5].add(text)
                            }
                            if (startHour in 14..15 ) {
                                table[10].add(text)
                            }
                            if (startHour in 16..17 ) {
                                table[15].add(text)
                            }
                            if (startHour in 19..21 ) {
                                table[20].add(text)
                            }
                        }
                        if (item.weekday == 2) {
                            if (startHour in 8..9 ) {
                                table[1].add(text)
                            }
                            if (startHour in 10..11 ) {
                                table[6].add(text)
                            }
                            if (startHour in 14..15 ) {
                                table[11].add(text)
                            }
                            if (startHour in 16..17 ) {
                                table[16].add(text)
                            }
                            if (startHour in 19..21 ) {
                                table[21].add(text)
                            }
                        }
                        if (item.weekday == 3) {
                            if (startHour in 8..9 ) {
                                table[2].add(text)
                            }
                            if (startHour in 10..11 ) {
                                table[7].add(text)
                            }
                            if (startHour in 14..15 ) {
                                table[12].add(text)
                            }
                            if (startHour in 16..17 ) {
                                table[17].add(text)
                            }
                            if (startHour in 19..21 ) {
                                table[22].add(text)
                            }
                        }
                        if (item.weekday == 4) {
                            if (startHour in 8..9 ) {
                                table[3].add(text)
                            }
                            if (startHour in 10..11 ) {
                                table[8].add(text)
                            }
                            if (startHour in 14..15 ) {
                                table[13].add(text)
                            }
                            if (startHour in 16..17 ) {
                                table[18].add(text)
                            }
                            if (startHour in 19..21 ) {
                                table[23].add(text)
                            }
                        }
                        if (item.weekday == 5) {
                            if (startHour in 8..9 ) {
                                table[4].add(text)
                            }
                            if (startHour in 10..11 ) {
                                table[9].add(text)
                            }
                            if (startHour in 14..15 ) {
                                table[14].add(text)
                            }
                            if (startHour in 16..17 ) {
                                table[19].add(text)
                            }
                            if (startHour in 19..21 ) {
                                table[24].add(text)
                            }
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

    LaunchedEffect(showAll,currentWeek) {
        refreshUI()
    }
    LaunchedEffect(findNewCourse) {
        if(findNewCourse && !showAll) {
            onSwapShowAll(true)
        }
    }
    var totalDragX by remember { mutableFloatStateOf(0f) }

    fun nextWeek() {
        if (currentWeek < MyApplication.MAX_WEEK) {
            currentWeek++
        }
    }
    fun previousWeek() {
        if (currentWeek > 1) {
            currentWeek--
        }
    }

    val customBackgroundAlpha by DataStoreManager.customCalendarSquareAlpha.collectAsState(initial = 1f)
    val enableTransition = !(backGroundHaze != null && AppVersion.CAN_SHADER)
    val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = AppVersion.CAN_SHADER)
    val calendarSquareHeight by DataStoreManager.calendarSquareHeight.collectAsState(initial = MyApplication.CALENDAR_SQUARE_HEIGHT)

    Box(modifier = Modifier
        .fillMaxHeight()
        .pointerInput(Unit) {
            detectHorizontalDragGestures(
                onDragEnd = {
                    // 手指松开后根据累积的水平拖动量决定
                    if (totalDragX > MyApplication.SWIPE) { // 阈值
                        previousWeek()
                    } else if (totalDragX < -MyApplication.SWIPE) {
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
        val scrollState = rememberLazyGridState()
        val shouldShowAddButton by remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset == 0 } }
        val style = CalendarStyle(showAll)
        val color =  if(enableTransition) style.containerColor.copy(customBackgroundAlpha) else Color.Transparent
        val squareColor =  style.containerColor.copy(customBackgroundAlpha)

        LazyVerticalGrid(
            columns = GridCells.Fixed(style.rowCount),
            modifier = style.calendarPadding(),
            state = scrollState
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) { InnerPaddingHeight(innerPadding,true) }
            items(style.rowCount*style.columnCount, key = { it }) { index ->
                val texts = if(showAll)tableAll[index].toMutableList() else table[index].toMutableList()
                if(texts.isEmpty() && backGroundHaze != null) {
                    Box(modifier = Modifier.height(calendarSquareHeight.dp).padding(style.everyPadding))
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
                                                    squareColor,
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
                                // 只有一节课
                                if (texts.size == 1) {
                                    val name =
                                        parseCourseName(if (showAll) tableAll[index][0] else table[index][0])
                                    if (name != null) {
                                        navController.navigateForTransition(AppNavRoute.CourseDetail,AppNavRoute.CourseDetail.withArgs(name, CourseDetailOrigin.CALENDAR_NEXT.t + "$index"))
                                    }
                                } else if (texts.size > 1) {
                                    multiWeekday =
                                        if (showAll) (index + 1) % 7 else (index + 1) % 5
                                    multiWeek = currentWeek.toInt()
                                    courses = texts
                                    showBottomSheetMultiCourse = true
                                }
                            }
                            .let {
                                if(enableTransition) {
                                    val route = if(texts.size == 1) {
                                        val name = parseCourseName(if (showAll) tableAll[index][0] else table[index][0])
                                        if (name != null) {
                                            AppNavRoute.CourseDetail.withArgs(name, CourseDetailOrigin.CALENDAR_NEXT.t + "$index")
                                        } else {
                                            null
                                        }
                                    } else {
                                        null
                                    }

                                    route?.let { it1 ->
                                        it.containerShare(
                                            route = it1,
                                            roundShape = MaterialTheme.shapes.extraSmall,
                                        )
                                    } ?: it
                                } else{
                                    it
                                }
                            }
                    ) {
                        if(texts.size == 1) {
                            val l = texts[0].split("\n")
                            val time = l[0]
                            val name = l[1]
                            val place = l[2]
                            Column(
                                modifier = Modifier.fillMaxSize().padding(horizontal = CARD_NORMAL_DP) ,
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = time,
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
                                        text = name,
                                        fontSize = style.textSize,
                                        textAlign = TextAlign.Center,
                                        overflow = TextOverflow.Ellipsis, // 超出显示省略号
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Text(
                                    text = place,
                                    fontSize = style.textSize,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text =
                                        if (texts.size == 1) texts[0]
                                        else if (texts.size > 1) "${texts[0].substringBefore("\n")}\n" + "${texts.size}节课冲突\n点击查看"
                                        else "",
                                    fontSize = style.textSize,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }

                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) { InnerPaddingHeight(innerPadding,false) }
        }
        // 中间
        DraggableWeekButton(
            shaderState = backGroundHaze,
            expanded = shouldShowAddButton,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = innerPadding.calculateBottomPadding()-navigationBarHeightPadding)
                .padding(
                    horizontal = APP_HORIZONTAL_DP,
                    vertical = APP_HORIZONTAL_DP
                ),
            onClick = {
                currentWeek = 1
            },
            currentWeek = currentWeek,
            key = null,
            onNext = { nextWeek() },
            onPrevious = { previousWeek() }
        )
    }
}


fun parseSingleChineseDigit(text: Char): Int = when (text) {
    '零', '〇' -> 0
    '一' -> 1
    '二', '两' -> 2
    '三' -> 3
    '四' -> 4
    '五' -> 5
    '六' -> 6
    '七', '日' -> 7
    '八' -> 8
    '九' -> 9
    else -> throw IllegalArgumentException("未知数字: $text")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiCourseSheetUI(week : Int, weekday : Int, courses : List<String>, vm: NetWorkViewModel, hazeState: HazeState) {
    var courseName by remember { mutableStateOf("") }
    var showBottomSheetTotalCourse by remember { mutableStateOf(false) }
    if (showBottomSheetTotalCourse) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheetTotalCourse = false
            },
            hazeState = hazeState,
            showBottomSheet = showBottomSheetTotalCourse
        ) {
            CourseDetailApi(courseName = courseName, vm = vm, hazeState = hazeState)
        }
    }
    Column {
        HazeBottomSheetTopBar("第${week}周 周${numToChinese(weekday)}", isPaddingStatusBar = false)
        LargeCard(
            title = "${courses.size}节课冲突"
        ) {
            for(index in courses.indices) {
                val course = courses[index]
                val list = course.split("\n")
                val startTime = list[0]
                val name = list[1]
                val place =
                    if(list.size > 2) {
                        list[2]
                    } else null
                TransplantListItem(
                    headlineContent = {
                        Text(name)
                    },
                    supportingContent = {
                        Text("${place?.replace("学堂","")} $startTime")
                    },
                    leadingContent = {
                        Text((index+1).toString())
                    },
                    colors =  if(name.contains("考试")) MaterialTheme.colorScheme.errorContainer else null,
                    modifier = Modifier.clickable {
                        // 如果是考试
                        if(name.contains("考试")) {
                            return@clickable
                        }
                        if(name.contains("日程")) {
                            return@clickable
                        }
                        courseName = name
                        showBottomSheetTotalCourse = true
                    }
                )
            }
        }
        Spacer(Modifier.height(40.dp))
    }

}

