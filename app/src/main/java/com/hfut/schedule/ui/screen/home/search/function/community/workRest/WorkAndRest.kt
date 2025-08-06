package com.hfut.schedule.ui.screen.home.search.function.community.workRest

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.ParseJsons.getMy
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
   
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getFormCommunity
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.ui.util.navigateForTransition
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.transition.component.TransitionScaffold
import com.xah.transition.component.iconElementShare
import com.xah.transition.util.navigateAndSaveForTransition
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun TimeTableUI(friendUserName : String? = null) {
    val data = remember { getFormCommunity(friendUserName) } ?: return
    with(data) {
        val startDate = start.substringBefore(" ")
        val endDate = end.substringBefore(" ")

        DividerTextExpandedWith("学期",openBlurAnimation = false) {
            LargeCard(title = xn + "学年 第${xq}学期") {
                Row {
                    TransplantListItem(
                        headlineContent = { Text(startDate) },
                        overlineContent = { Text("开始")},
                        modifier = Modifier.weight(.5f),
                    )
                    TransplantListItem(
                        overlineContent = { Text("结束")},
                        headlineContent = { Text(endDate) },
                        modifier = Modifier.weight(.5f)
                    )
                }
                Row {
                    TransplantListItem(
                        headlineContent = { Text("$currentWeek / $totalWeekCount") },
                        overlineContent = { Text("教学周")},
                        modifier = Modifier.weight(.5f)
                    )
                    val percent = DateTimeManager.getPercent(startDate,endDate)

                    if(percent < 0) {
                        // 未开始
                        TransplantListItem(
                            headlineContent = { Text("${DateTimeManager.daysBetween(startDate)}天后") },
                            overlineContent = { Text("未开学")},
                            modifier = Modifier.weight(.5f)
                        )
                    } else if(percent >= 100.toDouble()) {
                        // 已结束
                        TransplantListItem(
                            headlineContent = { Text("本学期已结束")},
                            modifier = Modifier.weight(.5f)
                        )
                    } else {
                        TransplantListItem(
                            headlineContent = { Text("${formatDecimal(percent,1)}%") },
                            overlineContent = { Text("已过")},
                            modifier = Modifier.weight(.5f)
                        )
                    }
                }
            }
        }
        DividerTextExpandedWith("作息") {
            MyCustomCard(containerColor = cardNormalColor()) {
                Column {
                    if(startTime.size == endTime.size) {
                        for(i in startTime.indices step 2) {
                            Row {
                                TransplantListItem(
                                    headlineContent = { Text(startTime[i] + " ~ " + endTime[i]) },
                                    overlineContent = { Text("第${i+1}节")},
                                    modifier = Modifier.weight(.5f)
                                )
                                if(i+1 < startTime.size)
                                    TransplantListItem(
                                        headlineContent = { Text(startTime[i+1] + " ~ " + endTime[i+1]) },
                                        overlineContent = { Text("第${i+2}节")},
                                        modifier = Modifier.weight(.5f)
                                    )}
                        }
                    } else {
                        Row {
                            TransplantListItem(
                                headlineContent = { Text(startTime.joinToString("\n")) },
                                overlineContent = { Text("上课时间")},
                                modifier = Modifier.weight(.5f)
                            )
                            TransplantListItem(
                                headlineContent = { Text(endTime.joinToString("\n")) },
                                overlineContent = { Text("下课时间")},
                                modifier = Modifier.weight(.5f)
                            )
                        }
                    }
                }
            }
        }
        if(courseBasicInfoDTOList.isNotEmpty()) {
            DividerTextExpandedWith("线下课程(社区数据源 可能有更新延迟)") {
                courseBasicInfoDTOList.forEachIndexed { index, item ->
                    val type = item.trainingCategoryName_dictText
                    val str = type?.let { " | $it" } ?: ""
                    val id = item.courseId
                    StyleCardListItem(
                        headlineContent = { Text(item.courseName) },
                        supportingContent = { Text(item.className)},
                        overlineContent = { Text("学分 ${item.credit}" + " | $id"+ str)},
                        leadingContent = { Text((index+1).toString())},
                        modifier = Modifier.clickable {
                            ClipBoardUtils.copy(id)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun WorkAndRest(
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val route = remember { AppNavRoute.TimeTable.route }

    TransplantListItem(
        headlineContent = { Text(text = AppNavRoute.TimeTable.label) },
        leadingContent = {
            with(sharedTransitionScope) {
                Icon(painterResource(AppNavRoute.TimeTable.icon), contentDescription = null,modifier = iconElementShare(animatedContentScope = animatedContentScope, route = route))
            }
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.TimeTable,route)
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TimeTableScreen(
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val url by produceState<String?>(initialValue = null) {
        value = try {
            getMy()!!.SchoolCalendar
        } catch (e:Exception) {
            null
        }
    }
    val route = remember { AppNavRoute.TimeTable.route }
    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                TopAppBar(
                    modifier = Modifier.topBarBlur(hazeState),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.TimeTable.label) },
                    navigationIcon = {
                        TopBarNavigateIcon(navController,animatedContentScope,route,AppNavRoute.TimeTable.icon)
                    },
                    actions = {
                        FilledTonalButton(
                            onClick = {
                                if(url == null) {
                                    showToast("正在从云端获取数据")
                                } else {
                                    Starter.startWebView(url!!,"校历", icon = R.drawable.schedule)
                                    showToast("即将打开网页链接,可自行下载或保存图片")
                                }
                            },
                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
                        ) {
                            Text("校历")
                        }
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.hazeSource(hazeState)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                InnerPaddingHeight(innerPadding,true)
                TimeTableUI()
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiForTimeTable(friendUserName: String, hazeState: HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }

    IconButton(
        onClick = { showBottomSheet = true }
    ) {
        Icon(painterResource(R.drawable.info),null, tint = MaterialTheme.colorScheme.primary)
    }
    if (showBottomSheet) {
        HazeBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("$friendUserName 作息")
                },) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ){
                    TimeTableUI(friendUserName)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
