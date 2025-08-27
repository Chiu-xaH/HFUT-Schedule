package com.hfut.schedule.ui.screen.home.calendar.next

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.hfut.schedule.logic.model.jxglstu.CourseUnitBean
import com.hfut.schedule.logic.model.jxglstu.DatumResponse
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.padding.navigationBarHeightPadding
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.communtiy.CourseDetailApi
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.MultiCourseSheetUI
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.clearUnit
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.distinctUnit
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.parseTimeTable
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.special.containerBlur
import com.hfut.schedule.ui.util.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.transition.component.containerShare
import dev.chrisbanes.haze.HazeState


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
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    innerPadding : PaddingValues,
    backGroundHaze : HazeState?,
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

    val times by DataStoreManager.courseTableTimeNextValue.collectAsState(initial = "")
    var timeTable by rememberSaveable { mutableStateOf(emptyList<CourseUnitBean>()) }
    LaunchedEffect(times) {
        timeTable = parseTimeTable(times,true)
    }

    fun refreshUI(showAll : Boolean,timeList : List<CourseUnitBean>) {
        // 清空
        if(showAll) {
            clearUnit(tableAll)
        } else {
            clearUnit(table)
        }

        try {
            // 组装
            val json = prefs.getString("jsonNext", "")
            val datumResponse = Gson().fromJson(json, DatumResponse::class.java)
            val scheduleList = datumResponse.result.scheduleList
            val lessonList = datumResponse.result.lessonList

            for (i in scheduleList.indices) {
                val item = scheduleList[i]
                var startTime = item.startTime.toString()
                startTime =
                    startTime.substring(0, startTime.length - 2) + ":" + startTime.substring(
                        startTime.length - 2
                    )
                var room = item.room?.nameZh
                var courseId = item.lessonId.toString()
                room = room?.replace("学堂","") ?: ""


                for (j in lessonList.indices) {
                    if (courseId == lessonList[j].id) {
                        courseId = lessonList[j].courseName
                    }
                }

                val text = startTime + "\n" + courseId + "\n" + room

                if (item.weekIndex == currentWeek.toInt()) {
                    when(item.weekday) {
                        6 -> { Handler(Looper.getMainLooper()).post { vmUI.findNewCourse.value = text.isNotEmpty() } }
                        7 -> { Handler(Looper.getMainLooper()).post { vmUI.findNewCourse.value = text.isNotEmpty() } }
                    }
                    if(showAll) {
                        if (item.weekday == 1) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                tableAll[0].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                tableAll[7].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                tableAll[14].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                tableAll[21].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                tableAll[28].add(text)
                            }
                        }
                        if (item.weekday == 2) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                tableAll[1].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                tableAll[8].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                tableAll[15].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                tableAll[22].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                tableAll[29].add(text)
                            }
                        }
                        if (item.weekday == 3) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                tableAll[2].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                tableAll[9].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                tableAll[16].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                tableAll[23].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                tableAll[30].add(text)
                            }
                        }
                        if (item.weekday == 4) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                tableAll[3].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                tableAll[10].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                tableAll[17].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                tableAll[24].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                tableAll[31].add(text)
                            }
                        }
                        if (item.weekday == 5) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                tableAll[4].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                tableAll[11].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                tableAll[18].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                tableAll[25].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                tableAll[32].add(text)
                            }
                        }
                        if (item.weekday == 6) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                tableAll[5].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                tableAll[12].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                tableAll[19].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                tableAll[26].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                tableAll[33].add(text)
                            }
                        }
                        if (item.weekday == 7) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                tableAll[6].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                tableAll[13].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                tableAll[20].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                tableAll[27].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                tableAll[34].add(text)
                            }
                        }
                    } else {
                        if (item.weekday == 1) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                table[0].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                table[5].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                table[10].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                table[15].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                table[20].add(text)
                            }
                        }
                        if (item.weekday == 2) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                table[1].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                table[6].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                table[11].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                table[16].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                table[21].add(text)
                            }
                        }
                        if (item.weekday == 3) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                table[2].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                table[7].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                table[12].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                table[17].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                table[22].add(text)
                            }
                        }
                        if (item.weekday == 4) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                table[3].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                table[8].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                table[13].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                table[18].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
                                table[23].add(text)
                            }
                        }
                        if (item.weekday == 5) {
                            if (item.startTime == timeList[0].startTime || item.startTime == timeList[1].startTime) {
                                table[4].add(text)
                            }
                            if (item.startTime == timeList[2].startTime || item.startTime == timeList[3].startTime) {
                                table[9].add(text)
                            }
                            if (item.startTime == timeList[4].startTime || item.startTime == timeList[5].startTime) {
                                table[14].add(text)
                            }
                            if (item.startTime == timeList[6].startTime || item.startTime == timeList[7].startTime) {
                                table[19].add(text)
                            }
                            if (item.startTime == timeList[8].startTime || item.startTime == timeList[9].startTime || item.startTime == timeList[10].startTime) {
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
        refreshUI(showAll,timeTable)
    }

    Box(modifier = Modifier.fillMaxHeight()) {
        val scrollState = rememberLazyGridState()
        val shouldShowAddButton by remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset == 0 } }

        LazyVerticalGrid(
            columns = GridCells.Fixed(if(showAll)7 else 5),
            modifier = Modifier.padding(10.dp),
            state = scrollState
        ) {
            items(if(showAll)7 else 5) { InnerPaddingHeight(innerPadding,true) }
            items(if(showAll)42 else 30) { cell ->
                val texts = if(showAll)tableAll[cell].toMutableList() else table[cell].toMutableList()
                val route = AppNavRoute.CourseDetail.withArgs(AppNavRoute.CourseDetail.Args.NAME.default as String,cell)
                Card(
                    shape = MaterialTheme.shapes.extraSmall,
                    colors = CardDefaults.cardColors(containerColor = if(backGroundHaze != null) Color.Transparent else MaterialTheme.colorScheme.surfaceContainerHigh),

//                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                    modifier = Modifier
                        .height(125.dp)
                        .padding(if (showAll) 1.dp else 2.dp)
                        .let {
                            backGroundHaze?.let { haze ->
                                it
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .containerBlur(haze,MaterialTheme.colorScheme.surfaceContainerHigh)
                            } ?: it
                        }
                        .clickable {
                            // 只有一节课
                            if (texts.size == 1) {
                                val name =
                                    parseCourseName(if (showAll) tableAll[cell][0] else table[cell][0])
                                if (name != null) {
                                    navController.navigateForTransition(AppNavRoute.CourseDetail,AppNavRoute.CourseDetail.withArgs(name, cell))
                                }
                            } else if (texts.size > 1) {
                                multiWeekday =
                                    if (showAll) (cell + 1) % 7 else (cell + 1) % 5
                                multiWeek = currentWeek.toInt()
                                courses = texts
                                showBottomSheetMultiCourse = true
                            }
                        }
                        .containerShare(
                            sharedTransitionScope,
                            animatedContentScope,
                            route = if (texts.size == 1) {
                                val name =
                                    parseCourseName(if (showAll) tableAll[cell][0] else table[cell][0])
                                if (name != null) {
                                    AppNavRoute.CourseDetail.withArgs(name,cell)
                                } else {
                                    route
                                }
                            } else {
                                route
                            },
                            roundShape = MaterialTheme.shapes.extraSmall,
                        )
                ) {
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
                            fontSize = if (showAll) 12.sp else 14.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            item { InnerPaddingHeight(innerPadding,false) }
        }
        // 上一周
        androidx.compose.animation.AnimatedVisibility(
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
        androidx.compose.animation.AnimatedVisibility(
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
        androidx.compose.animation.AnimatedVisibility(
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
    '七' -> 7
    '八' -> 8
    '九' -> 9
    else -> throw IllegalArgumentException("未知数字: $text")
}
