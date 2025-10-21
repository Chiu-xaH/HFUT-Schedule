package com.hfut.schedule.ui.screen.home.calendar.jxglstu.next

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.hfut.schedule.logic.model.jxglstu.CourseUnitBean
import com.hfut.schedule.logic.model.jxglstu.DatumResponse
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.padding.navigationBarHeightPadding
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CourseDetailApi
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.MultiCourseSheetUI
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.calendarSquareGlass
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.clearUnit
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.distinctUnit
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.parseTimeTable
import com.hfut.schedule.ui.screen.home.focus.funiction.parseTimeItem
import com.hfut.schedule.ui.style.CalendarStyle
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.special.containerBlur
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.mirror.util.ShaderState
import com.xah.transition.component.containerShare
import com.xah.uicommon.style.ClickScale
import com.xah.uicommon.style.clickableWithScale
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
    vmUI: UIViewModel,
    hazeState: HazeState,
    navController: NavHostController,
    innerPadding : PaddingValues,
    backGroundHaze : ShaderState?,
) {
    val enableHideEmptyCalendarSquare by DataStoreManager.enableHideEmptyCalendarSquare.collectAsState(initial = false)

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

    val times by DataStoreManager.courseTableTimeNextValue.collectAsState(initial = "")
    var timeTable by rememberSaveable { mutableStateOf(emptyList<CourseUnitBean>()) }
    LaunchedEffect(times) {
        timeTable = parseTimeTable(times,true)
    }

    fun refreshUI() {
        // 清空
        if(showAll) {
            clearUnit(tableAll)
        } else {
            clearUnit(table)
        }
        Handler(Looper.getMainLooper()).post { vmUI.findNewCourse.value = false }

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
                        6 -> { Handler(Looper.getMainLooper()).post { vmUI.findNewCourse.value = text.isNotEmpty() } }
                        7 -> { Handler(Looper.getMainLooper()).post { vmUI.findNewCourse.value = text.isNotEmpty() } }
                    }
                    if(showAll) {
                        if (item.weekday == 1) {
                            if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                tableAll[0].add(text)
                            }
                            if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                tableAll[7].add(text)
                            }
                            if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                tableAll[14].add(text)
                            }
                            if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                tableAll[21].add(text)
                            }
                            if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                tableAll[28].add(text)
                            }
                        }
                        if (item.weekday == 2) {
                            if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                tableAll[1].add(text)
                            }
                            if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                tableAll[8].add(text)
                            }
                            if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                tableAll[15].add(text)
                            }
                            if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                tableAll[22].add(text)
                            }
                            if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                tableAll[29].add(text)
                            }
                        }
                        if (item.weekday == 3) {
                            if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                tableAll[2].add(text)
                            }
                            if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                tableAll[9].add(text)
                            }
                            if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                tableAll[16].add(text)
                            }
                            if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                tableAll[23].add(text)
                            }
                            if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                tableAll[30].add(text)
                            }
                        }
                        if (item.weekday == 4) {
                            if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                tableAll[3].add(text)
                            }
                            if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                tableAll[10].add(text)
                            }
                            if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                tableAll[17].add(text)
                            }
                            if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                tableAll[24].add(text)
                            }
                            if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                tableAll[31].add(text)
                            }
                        }
                        if (item.weekday == 5) {
                            if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                tableAll[4].add(text)
                            }
                            if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                tableAll[11].add(text)
                            }
                            if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                tableAll[18].add(text)
                            }
                            if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                tableAll[25].add(text)
                            }
                            if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                tableAll[32].add(text)
                            }
                        }
                        if (item.weekday == 6) {
                            if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                tableAll[5].add(text)
                            }
                            if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                tableAll[12].add(text)
                            }
                            if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                tableAll[19].add(text)
                            }
                            if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                tableAll[26].add(text)
                            }
                            if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                tableAll[33].add(text)
                            }
                        }
                        if (item.weekday == 7) {
                            if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                tableAll[6].add(text)
                            }
                            if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                tableAll[13].add(text)
                            }
                            if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                tableAll[20].add(text)
                            }
                            if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                tableAll[27].add(text)
                            }
                            if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                tableAll[34].add(text)
                            }
                        }
                    } else {
                        if (item.weekday == 1) {
                            if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                table[0].add(text)
                            }
                            if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                table[5].add(text)
                            }
                            if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                table[10].add(text)
                            }
                            if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                table[15].add(text)
                            }
                            if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                table[20].add(text)
                            }
                        }
                        if (item.weekday == 2) {
                            if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                table[1].add(text)
                            }
                            if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                table[6].add(text)
                            }
                            if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                table[11].add(text)
                            }
                            if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                table[16].add(text)
                            }
                            if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                table[21].add(text)
                            }
                        }
                        if (item.weekday == 3) {
                            if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                table[2].add(text)
                            }
                            if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                table[7].add(text)
                            }
                            if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                table[12].add(text)
                            }
                            if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                table[17].add(text)
                            }
                            if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                table[22].add(text)
                            }
                        }
                        if (item.weekday == 4) {
                            if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                table[3].add(text)
                            }
                            if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                table[8].add(text)
                            }
                            if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                table[13].add(text)
                            }
                            if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                table[18].add(text)
                            }
                            if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
                                table[23].add(text)
                            }
                        }
                        if (item.weekday == 5) {
                            if (startHour in 8..9 || startTime == timeTable[0].startTime || startTime == timeTable[1].startTime) {
                                table[4].add(text)
                            }
                            if (startHour in 10..11 || startTime == timeTable[2].startTime || startTime == timeTable[3].startTime) {
                                table[9].add(text)
                            }
                            if (startHour in 14..15 || startTime == timeTable[4].startTime || startTime == timeTable[5].startTime) {
                                table[14].add(text)
                            }
                            if (startHour in 16..17 || startTime == timeTable[6].startTime || startTime == timeTable[7].startTime) {
                                table[19].add(text)
                            }
                            if (startHour in 19..21 || startTime == timeTable[8].startTime || startTime == timeTable[9].startTime || startTime == timeTable[10].startTime) {
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

    LaunchedEffect(showAll,currentWeek,times) {
        refreshUI()
    }
    val customBackgroundAlpha by DataStoreManager.customCalendarSquareAlpha.collectAsState(initial = 1f)
    val enableTransition = !(backGroundHaze != null && AppVersion.CAN_SHADER)
    val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = AppVersion.CAN_SHADER)

    Box(modifier = Modifier.fillMaxHeight()) {
        val scrollState = rememberLazyGridState()
        val shouldShowAddButton by remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset == 0 } }
        val style = CalendarStyle(showAll)
        val color =  if(enableTransition) style.containerColor.copy(customBackgroundAlpha) else Color.Transparent

        LazyVerticalGrid(
            columns = GridCells.Fixed(style.rowCount),
            modifier = style.calendarPadding(),
//            modifier = Modifier.padding(10.dp),
            state = scrollState
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) { InnerPaddingHeight(innerPadding,true) }
            items(style.rowCount*style.columnCount, key = { it }) { index ->
                val texts = if(showAll)tableAll[index].toMutableList() else table[index].toMutableList()
                if(texts.isEmpty() && enableHideEmptyCalendarSquare) {
                    Box(modifier = Modifier.height(style.height).padding(style.everyPadding))
                } else {
                    Card(
                        shape = style.containerCorner,
                        colors = CardDefaults.cardColors(containerColor = color),
                        modifier = Modifier
                            .height(style.height)
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
                                                    enableLiquidGlass
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
        // 上一周
        AnimatedVisibility(
            visible = shouldShowAddButton,
            enter = scaleIn(),
            exit = scaleOut(),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = innerPadding.calculateBottomPadding()-navigationBarHeightPadding)

                .padding(
                    horizontal = APP_HORIZONTAL_DP,
                    vertical = APP_HORIZONTAL_DP
                )
        ) {
            if (shouldShowAddButton) {
                FloatingActionButton(
                    onClick = {
                        if (currentWeek > 1) {
                            currentWeek-- - 1
                        }
                    },
                ) { Icon(Icons.Filled.ArrowBack, "Add Button") }
            }
        }
        // 中间
        AnimatedVisibility(
            visible = shouldShowAddButton,
            enter = scaleIn(),
            exit = scaleOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = innerPadding.calculateBottomPadding()-navigationBarHeightPadding)

                .padding(
                    horizontal = APP_HORIZONTAL_DP,
                    vertical = APP_HORIZONTAL_DP
                )
        ) {
            if (shouldShowAddButton) {
                ExtendedFloatingActionButton(
                    onClick = {
                        currentWeek = 1
                    },
                ) {
                    AnimatedContent(
                        targetState = currentWeek,
                        transitionSpec = {
                            scaleIn(animationSpec = tween(500)
                            ) togetherWith(scaleOut(animationSpec = tween(500)))
                        }, label = ""
                    ){ n ->
                        Text(text = "第 $n 周")
                    }
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
                .padding(bottom = innerPadding.calculateBottomPadding()-navigationBarHeightPadding)

                .padding(
                    horizontal = APP_HORIZONTAL_DP,
                    vertical = APP_HORIZONTAL_DP
                )
        ) {
            if (shouldShowAddButton) {
                FloatingActionButton(
                    onClick = {
                        if (currentWeek < 20) {
                            currentWeek++ + 1
                        }
                    },
                ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
            }
        }
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
