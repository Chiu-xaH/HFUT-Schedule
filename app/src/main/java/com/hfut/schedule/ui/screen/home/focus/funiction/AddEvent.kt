package com.hfut.schedule.ui.screen.home.focus.funiction

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomEventDTO
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.database.util.CustomEventMapper
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.addToCalendars
import com.hfut.schedule.logic.util.sys.parseToDateTime
import com.hfut.schedule.ui.component.CustomTextField
import com.hfut.schedule.ui.component.DateRangePickerModal
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.MyCustomCard
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TimeRangePickerDialog
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.cardNormalColor
import com.hfut.schedule.ui.component.cardNormalDp
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.screen.home.cube.sub.ShareBarRoutes
import com.hfut.schedule.viewmodel.UIViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class ShareRoutes {
    BUTTON,SURFACE
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AddEventFloatButton(
    isVisible: Boolean,
    hazeState: HazeState,
    vmUI : UIViewModel,
    innerPaddings: PaddingValues,
) {
    // 懒加载
    var showSurface by remember { mutableStateOf(false) }

    var showAddUI by remember { mutableStateOf(false) }
    // 容器转换动画
    val isCenterAnimation by DataStoreManager.motionAnimationTypeFlow.collectAsState(initial = false)
    val boundsTransform by remember { mutableStateOf(
        BoundsTransform { _, _ ->
            if(!isCenterAnimation) {
                spring(
                    stiffness = StiffnessMediumLow,
                    visibilityThreshold = Rect.VisibilityThreshold
                )
            } else {
                tween(durationMillis = MyApplication.ANIMATION_SPEED, easing = FastOutSlowInEasing)
            }
        }
    ) }
    // 通知父布局开始进行模糊和缩放，同时暂时关闭topBar和bottomBar的实时模糊
    LaunchedEffect(showAddUI) {
        vmUI.isAddUIExpanded = showAddUI
        if(showAddUI) {
            // 进入
            showSurface = false
            delay(MyApplication.ANIMATION_SPEED * 1L)
            showSurface = true
        } else {
            // 退出
            showSurface = false
        }
    }
    SharedTransitionLayout {
        AnimatedContent(
//            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            targetState = showAddUI,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = MyApplication.ANIMATION_SPEED)) togetherWith fadeOut(animationSpec = tween(durationMillis = MyApplication.ANIMATION_SPEED*2))
            },
            label = ""
        ) { targetShowAddUI ->
            // 这里是 AnimatedContentScope 的作用域
            if (targetShowAddUI) {
                SurfaceUI(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this,
                    showSurface = showSurface,
                    showChange = { showAddUI = it },
                    boundsTransform
                )
            } else {
                ButtonUI(
                    isVisible = isVisible,
                    innerPaddings = innerPaddings,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this,
                    showAddUI,
                    showChange = { showAddUI = it },
                    boundsTransform
                )
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.ButtonUI(
    isVisible: Boolean,
    innerPaddings : PaddingValues,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    showAddUI : Boolean,
    showChange : (Boolean) -> Unit,
    boundsTransform: BoundsTransform
) {
    if (isVisible) {
        FloatingActionButton(
            modifier = Modifier
                .padding(innerPaddings)
                .padding(horizontal = appHorizontalDp(), vertical = appHorizontalDp())
                .sharedBounds(
                    boundsTransform = boundsTransform,
                    enter = fadeIn(tween(durationMillis = MyApplication.ANIMATION_SPEED)),
                    exit = fadeOut(tween(durationMillis = MyApplication.ANIMATION_SPEED)),
                    sharedContentState = rememberSharedContentState(key = "container"),
                    animatedVisibilityScope = animatedContentScope,
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                ),
            elevation =  FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
            onClick = { showChange(true) },
        ) { Icon(Icons.Filled.Add, "Add Button") }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.SurfaceUI(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    showSurface : Boolean,
    showChange: (Boolean) -> Unit,
    boundsTransform: BoundsTransform
) {
    BackHandler {
        showChange(false)
    }
    Scaffold(
//        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(appHorizontalDp()))
            .sharedBounds(
                enter = fadeIn(tween(durationMillis = MyApplication.ANIMATION_SPEED)),
                exit = fadeOut(tween(durationMillis = MyApplication.ANIMATION_SPEED)),
                sharedContentState = rememberSharedContentState(key = "container"),
                animatedVisibilityScope = animatedContentScope,
                boundsTransform = boundsTransform,
                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
            ),
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    scrolledContainerColor = Color.Transparent,
                ),
                title = { Text("添加日程") },
                actions = {
                    IconButton(
                        onClick = {
//                            navHostController.popBackStack()
                            showChange(false)
                        }
                    ) {
                        Icon(Icons.Filled.Close,null)
                    }
                },
            )
        },
    ) { innerPadding ->
        if(showSurface) {
            AnimatedVisibility(
                visible = true,
                enter  = fadeIn(),
                exit = fadeOut()
            ) {
                Column(modifier = Modifier.padding(innerPadding).background(Color.Transparent)) {
                    AddEventUI(showChange)
                }
            }
        }
    }
}

@Composable
fun AddEventUI(showChange: (Boolean) -> Unit) {
    var enabled by remember { mutableStateOf(false) }

    val activity = LocalActivity.current
    var isScheduleType by remember { mutableStateOf(true) }
    var title by remember { mutableStateOf("事件") }
    var description by remember { mutableStateOf("") }
    var remark by remember { mutableStateOf("") }

    var time by remember { mutableStateOf(Pair("","")) }
    var date by remember { mutableStateOf(Pair("","")) }

    var showSelectDateDialog by remember { mutableStateOf(false) }
    var showSelectTimeDialog by remember { mutableStateOf(false) }


    val scope = rememberCoroutineScope()

    LaunchedEffect(date,time) {
        remark = if(date.first == date.second) { // 当天日程
            "${date.first.substringAfter("-")} " +
                    // 同时间
                    if(time.first == time.second) {
                        time.first
                    } else {
                        time.first + " ~ " + time.second
                    }
        } else {
            "${date.first.substringAfter("-") + " " + time.first} ~ ${date.second.substringAfter("-") + " " + time.second}"
        }
    }

    // 向上回传数据
    LaunchedEffect(title,time,date,remark) {
        enabled = title.isNotBlank() && title.isNotEmpty() && time.first.isNotEmpty() && time.second.isNotEmpty() && date.first.isNotEmpty() && date.second.isNotEmpty() && remark.isNotBlank() && remark.isNotEmpty()
    }



    val typeIcon = @Composable {
        Icon(painterResource(if(isScheduleType) R.drawable.calendar else R.drawable.net),null)
    }

    if(showSelectDateDialog)
        DateRangePickerModal(onSelected = { date = it }) { showSelectDateDialog = false }
    if(showSelectTimeDialog)
        TimeRangePickerDialog(onSelected = { time = it }) { showSelectTimeDialog = false }


    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                scope.launch {
                    async {
                        val entity = parseToDateTime(startDate = date.first, startTime = time.first, endDate = date.second, endTime = time.second)?.let {
                            CustomEventDTO(
                                title = title,
                                dateTime = it,
                                type = if(isScheduleType) CustomEventType.SCHEDULE else CustomEventType.NET_COURSE,
                                description = description.let { desp -> if(desp.isNotEmpty() && desp.isNotBlank()) desp else null },
                                remark = remark
                            )
                        }
                        if(enabled && entity != null) {
                            // 添加到数据库
                            DataBaseManager.customEventDao.insert(CustomEventMapper.dtoToEntity(entity))
                            showToast("执行完成 请检查是否显示")
                        }
                    }.await()
                    // 关闭
                    launch { showChange(false) }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = appHorizontalDp()).align(Alignment.BottomCenter)
        ) {
            Text(if(enabled) "添加" else "关闭")
        }
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            DividerTextExpandedWith("预览") {
                StyleCardListItem(
                    headlineContent = { Text(title) },
                    leadingContent = typeIcon,
                    overlineContent = { Text(remark) },
                    supportingContent =  if(description.isNotBlank() && description.isNotEmpty()) { { Text(description) } } else null,
                    trailingContent = { FilledTonalIconButton(
                        onClick = {
                            activity?.let { addToCalendars(startDate = date.first, startTime = time.first, endDate = date.second, endTime = time.second, description, title,null, it) }
                        }, enabled = enabled
                    ) { Icon(painterResource(R.drawable.event_upcoming),null) } },
                    modifier = Modifier.clickable { openOperation(description) }
                )
            }
            DividerTextExpandedWith("配置") {
                CustomTextField(input = title, label = { Text("标题") }) { title = it }
                Spacer(Modifier.height(5.dp + cardNormalDp()))
                CustomTextField(input = description, label = { Text("备注(可空)") }) { description = it }
                Spacer(Modifier.height(5.dp + cardNormalDp()))
                CustomTextField(input = remark, label = { Text("自定义时间显示(可选)") }) { remark = it }
                Spacer(Modifier.height(5.dp))
                StyleCardListItem(
                    headlineContent = { Text("类型：" + if(isScheduleType) "日程/课程" else "网课" ) },
                    supportingContent = { Text(if(isScheduleType) "日程类型旨在用户自行添加新加课程、实验、会议等，添加后，同步显示在课程表方格中；在未开始时位于其他事项，进行期间会显示为重要事项，结束后位于其他事项并划线标记" else "网课类型旨在用户自行添加需要在截止日期之前的网络作业等，添加后，在未开始和进行期间位于其他事项，截止日期当天会显示为重要事项，结束后位于其他事项并划线标记" ) },
                    leadingContent = {
                        FilledTonalIconButton(
                            onClick = { isScheduleType = !isScheduleType },
                            content = typeIcon
                        )
                    } ,
                    modifier = Modifier.clickable {
                        isScheduleType = !isScheduleType
                    }
                )
                Spacer(Modifier.height(5.dp - cardNormalDp()))
                MyCustomCard(containerColor = cardNormalColor()) {
                    TransplantListItem(
                        headlineContent = { Text("开始 ${date.first + " " + time.first}\n结束 ${date.second + " " + time.second}") },
                        leadingContent = { Icon(painterResource(R.drawable.schedule),null) }
                    )
                    Row(modifier = Modifier.align(Alignment.End)) {
                        Text(
                            text = "选择日期范围",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.Bottom).padding(horizontal = appHorizontalDp(), vertical = appHorizontalDp() - 5.dp).clickable {
                                showSelectDateDialog = true
                            }
                        )
                        Text(
                            text = "选择时间范围",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.Top).padding(horizontal = appHorizontalDp(), vertical = appHorizontalDp() - 5.dp).clickable {
                                showSelectTimeDialog = true
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(40.dp + appHorizontalDp()))
        }
    }
}

