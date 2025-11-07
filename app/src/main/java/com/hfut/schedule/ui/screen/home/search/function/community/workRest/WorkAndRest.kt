package com.hfut.schedule.ui.screen.home.search.function.community.workRest

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.network.util.MyApiParse.getMy
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.LargeCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getFormCommunity
import com.hfut.schedule.ui.style.special.CustomBottomSheet
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.mirror.shader.glassLayer
import com.xah.mirror.shader.smallStyle
import com.xah.mirror.util.ShaderState
import com.xah.transition.component.iconElementShare
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@Composable
private fun TimeTableUI(friendUserName : String? = null) {
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
            CustomCard(color = cardNormalColor()) {
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
            DividerTextExpandedWith("线下课程(智慧社区数据源)") {
                courseBasicInfoDTOList.forEachIndexed { index, item ->
                    val type = item.trainingCategoryName_dictText
                    val str = type?.let { " | $it" } ?: ""
                    val id = item.courseId
                    CardListItem(
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
) {
    val route = remember { AppNavRoute.WorkAndRest.withArgs() }

    TransplantListItem(
        headlineContent = { ScrollText(text = AppNavRoute.WorkAndRest.label) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.WorkAndRest.icon), contentDescription = null,modifier = Modifier.iconElementShare(route = route))
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.WorkAndRest,route)
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TimeTableScreen(
    navController : NavHostController,
    friendId : String?
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
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val route = remember { AppNavRoute.WorkAndRest.withArgs(friendId) }
    val backdrop = rememberLayerBackdrop()

    CustomTransitionScaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        route = route,
        navHostController = navController,
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState),
                colors = topBarTransplantColor(),
                title = { Text(AppNavRoute.WorkAndRest.label) },
                navigationIcon = {
                    TopBarNavigationIcon(navController,route,AppNavRoute.WorkAndRest.icon)
                },
                actions = {
                    LiquidButton(
                        onClick = {
                            scope.launch {
                                if(url == null) {
                                    showToast("正在从云端获取数据")
                                } else {
                                    Starter.startWebView(context,url!!,"校历", icon = R.drawable.schedule)
                                    showToast("即将打开网页链接,可自行下载或保存图片")
                                }
                            }
                        },
                        backdrop = backdrop,
                        modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
                    ) {
                        Text("校历")
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .backDropSource(backdrop)
                .hazeSource(hazeState)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            InnerPaddingHeight(innerPadding,true)
            TimeTableUI(friendId)
            InnerPaddingHeight(innerPadding,false)
        }
    }
}


