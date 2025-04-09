package com.hfut.schedule.ui.activity.home.focus.funictions

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.BoundsTransform
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
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.beans.Schedule
import com.hfut.schedule.logic.beans.community.TodayResponse
import com.hfut.schedule.logic.beans.community.TodayResult
import com.hfut.schedule.logic.beans.focus.AddFocus
import com.hfut.schedule.logic.db.RoomDataBaseManager
import com.hfut.schedule.logic.db.entity.CustomEventDTO
import com.hfut.schedule.logic.db.entity.CustomEventType
import com.hfut.schedule.logic.db.util.CustomEventMapper
import com.hfut.schedule.logic.utils.DataStoreManager
import com.hfut.schedule.logic.utils.DateTimeUtils
import com.hfut.schedule.logic.utils.DateTimeUtils.TimeState.ENDED
import com.hfut.schedule.logic.utils.DateTimeUtils.TimeState.NOT_STARTED
import com.hfut.schedule.logic.utils.DateTimeUtils.TimeState.ONGOING
import com.hfut.schedule.logic.utils.JxglstuCourseSchedule
import com.hfut.schedule.logic.utils.addToCalendars
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.parse.ParseJsons.getSchedule
import com.hfut.schedule.logic.utils.parse.ParseJsons.getTimeStamp
import com.hfut.schedule.logic.utils.parse.SemseterParser.getSemseter
import com.hfut.schedule.logic.utils.parse.SemseterParser.parseSemseter
import com.hfut.schedule.logic.utils.parseToDateTime
import com.hfut.schedule.ui.activity.home.calendar.communtiy.DetailInfos
import com.hfut.schedule.ui.activity.home.calendar.communtiy.getCourseINFO
import com.hfut.schedule.ui.activity.home.cube.items.main.apiCheck
import com.hfut.schedule.ui.activity.home.search.functions.card.TodayInfo
import com.hfut.schedule.ui.utils.components.BottomTip
import com.hfut.schedule.ui.utils.components.DateRangePickerModal
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.HazeBottomSheetTopBar
import com.hfut.schedule.ui.utils.components.LittleDialog
import com.hfut.schedule.ui.utils.components.RotatingIcon
import com.hfut.schedule.ui.utils.components.ScheduleIcons
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.components.StyleCardListItem
import com.hfut.schedule.ui.utils.components.TimeRangePickerDialog
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.components.appHorizontalDp
import com.hfut.schedule.ui.utils.components.showToast
import com.hfut.schedule.ui.utils.style.ColumnVertical
import com.hfut.schedule.ui.utils.style.HazeBottomSheet
import com.hfut.schedule.ui.utils.style.RowHorizontal
import com.hfut.schedule.ui.utils.style.textFiledTransplant
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import dev.chrisbanes.haze.HazeDialog
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ScheduleItem(listItem : Schedule, isFuture: Boolean, activity : Activity) {

    if(prefs.getString("my","")?.contains("Schedule") == true) {
        val startTime = listItem.startTime
        val endTime = listItem.endTime

        //判断过期不显示信息
        val startYear = startTime[0]
        val startMonth = startTime[1]
        val startDay = startTime[2]
        var startDateStr = startTime[2].toString()
        var startMonthStr = startTime[1].toString()
        if(startDay < 10) startDateStr = "0$startDay"
        if(startMonth < 10) startMonthStr = "0$startMonth"
        val getStartTime = "${startYear}${startMonthStr}${startDateStr}".toInt()

        val endYear = endTime[0]
        val endMonth = endTime[1]
        val endDay = endTime[2]
        var endDateStr = endTime[2].toString()
        var endMonthStr = endTime[1].toString()
        if(endDay < 10) endDateStr = "0$endDay"
        if(endMonth < 10) endMonthStr = "0$endMonth"
        val getEndTime = "${endYear}${endMonthStr}${endDateStr}".toLong()


        val nowTime = DateTimeUtils.Date_yyyy_MM_dd.replace("-","").toLong()


        if(!isFuture) {
            if(nowTime in getStartTime .. getEndTime)
                ScheduleItemUI(listItem,false,activity)
        }
        else {
            if(nowTime < getStartTime)
                ScheduleItemUI(listItem,true,activity)
        }
    }
}

@Composable
private fun ScheduleItemUI(listItem: Schedule, isFuture : Boolean, activity : Activity) {
    val time = listItem.time
    val info = listItem.info
    val title = listItem.title
    val showPublic = listItem.showPublic

    val itemUI = @Composable {
            StyleCardListItem(
                headlineContent = {  Text(text = title) },
                overlineContent = {Text(text = time)},
                supportingContent = { Text(text = info)},
                leadingContent = { ScheduleIcons(title = title) },
                modifier = Modifier.clickable {},
                trailingContent = {
                    if(isFuture)
                        FilledTonalIconButton(
                            onClick = {
                                try {
                                    val startTime = listItem.startTime
                                    val endTime = listItem.endTime
                                    addToCalendars(startTime,endTime, info, title,time,activity)
                                    showToast("添加到系统日历成功")
                                } catch (e : SecurityException) {
                                    showToast("未授予权限")
                                    e.printStackTrace()
                                }
                            }
                        ) {
                            Icon( painterResource(R.drawable.event_upcoming),
                                contentDescription = "Localized description",)
                        }
                }
            )
//        }
    }

    if(prefs.getBoolean("SWITCHMYAPIS", apiCheck())) {
        //正常接受所有来自服务器的信息
        itemUI()
    } else {
        //仅接受showPublic为true
        if(showPublic) {
            itemUI()
        }
    }
}

@Composable
fun NetCourseItem(listItem : Schedule, isFuture: Boolean, activity: Activity) {
    val time = listItem.time
    val info = listItem.info
    val title = listItem.title
    val showPublic = listItem.showPublic


    val itemUI = @Composable {
        if(prefs.getString("my","")?.contains("Schedule") == true) {
            val startTime = listItem.startTime
            val endTime = listItem.endTime

            //判断过期不显示信息
            val endYear = endTime[0]
            val endMonth = endTime[1]
            val endDay = endTime[2]
            var endDateStr = endTime[2].toString()
            var endMonthStr = endTime[1].toString()
            if(endDay < 10) endDateStr = "0$endDay"
            if(endMonth < 10) endMonthStr = "0$endMonth"
            val getEndTime = "${endYear}${endMonthStr}${endDateStr}".toLong()


            val nowTime = DateTimeUtils.Date_yyyy_MM_dd.replace("-","").toLong()


            if(isFuture) {
                if(nowTime < getEndTime) {
                        StyleCardListItem(
                            headlineContent = {  Text(text = title) },
                            overlineContent = { Text(text = time) },
                            supportingContent = { Text(text = info)},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.net),
                                    contentDescription = "Localized description",
                                )
                            },
                            trailingContent = {
                                FilledTonalIconButton(
                                    onClick = {
                                        addToCalendars(startTime,endTime, info, title,time,activity)

                                    }
                                ) {
                                    Icon( painterResource(R.drawable.event_upcoming),
                                        contentDescription = "Localized description",)
                                }
                            },
                            modifier = Modifier.clickable { openOperation(info) }
                        )
                }
            } else {
                if(nowTime == getEndTime) {
                        StyleCardListItem(
                            headlineContent = {  Text(text = title) },
                            overlineContent = { Text(text = time) },
                            supportingContent = { Text(text = info)},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.net),
                                    contentDescription = "Localized description",
                                )
                            },
                            trailingContent = {
                                Text(text = "今日截止")
                            },
                            modifier = Modifier.clickable { openOperation(info) }
                        )
                }
            }
        }
    }

    if(prefs.getBoolean("SWITCHMYAPIS", apiCheck())) {
        //正常接受所有来自服务器的信息
        itemUI()
    } else {
        //仅接受showPublic为true
        if(showPublic) {
            itemUI()
        }
    }
}

@Composable
fun CommunityTodayCourseItem(index : Int, vm : NetWorkViewModel, hazeState: HazeState, timeNow : String) {

    val switchShowEnded = prefs.getBoolean("SWITCHSHOWENDED",true)

    val week = DateTimeUtils.weeksBetween.toInt()

    var weekday = DateTimeUtils.dayWeek
    if(weekday == 0) weekday = 7
    //课程详情
    val list = getCourseINFO(weekday,week)[index][0]
    var showBottomSheet by remember { mutableStateOf(false) }
    val time = list.classTime

    val startTime = time.substringBefore("-")
    val endTime = time.substringAfter("-")


    val state = DateTimeUtils.getTimeState(startTime, endTime,timeNow)

    if (showBottomSheet) {

        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState,
            autoShape = false
        ) {
            HazeBottomSheetTopBar(list.name, isPaddingStatusBar = false)
            DetailInfos(list,vm = vm, hazeState = hazeState)
        }
    }

    val itemUI = @Composable {
        StyleCardListItem(
            headlineContent = { Text(text = list.name) },
            overlineContent = { Text(text = time)},
            supportingContent = { list.place?.let { Text(text = it) } },
            leadingContent = {
                when(state) {
                    NOT_STARTED -> {
                        Icon(
                            painterResource(R.drawable.schedule),
                            contentDescription = "Localized description",
                        )
                    }
                    ONGOING -> {
                        RotatingIcon(R.drawable.progress_activity)
                    }
                    ENDED -> {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Localized description",
                        )
                    }
                }

            },
            modifier = Modifier.clickable {
                showBottomSheet = true
            },
            trailingContent = {
                Text(
                    when(state) {
                        NOT_STARTED -> "未开始"
                        ONGOING -> "上课中"
                        ENDED -> "已下课"
                    }
                )
            },
        )
    }

    if(switchShowEnded) {
        itemUI()
    } else {
        if(state != ENDED) {
            itemUI()
        }
    }
}

@Composable
fun CommunityTomorrowCourseItem(index : Int, vm: NetWorkViewModel, hazeState: HazeState) {

    val weekdayTomorrow = DateTimeUtils.dayWeek + 1
    var week = DateTimeUtils.weeksBetween.toInt()
    //当第二天为下一周的周一时，周数+1
    when(weekdayTomorrow) {
        1 -> week += 1
    }

    //课程详情
    val list = getCourseINFO(weekdayTomorrow,week)[index][0]
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {

        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState,
            autoShape = false
        ) {
            HazeBottomSheetTopBar(list.name, isPaddingStatusBar = false)
            DetailInfos(list,vm = vm, hazeState = hazeState)
        }
    }

    StyleCardListItem(
        headlineContent = { Text(text = list.name) },
        overlineContent = {Text(text = list.classTime)},
        supportingContent = { list.place?.let { Text(text = it) } },
        leadingContent = { Icon(painterResource(R.drawable.exposure_plus_1), contentDescription = "Localized description") },
        modifier = Modifier.clickable {
            showBottomSheet = true
        },
        trailingContent = { Text(text = "明日")}
    )
}

@Composable
fun CustomItem(item : CustomEventDTO,hazeState: HazeState,isFuture: Boolean,activity: Activity,refresh : () -> Unit) {
    val dateTime = item.dateTime
    val nowTimeNum = DateTimeUtils.Date_yyyy_MM_dd.replace("-","").toLong()
    val endNum = with(dateTime.end) { "$year${parseTimeItem(month)}${parseTimeItem(day)}" }.toLong()

    if(item.type == CustomEventType.SCHEDULE) {
        val startNum = with(dateTime.start) { "$year${parseTimeItem(month)}${parseTimeItem(day)}" }.toLong()
        if(nowTimeNum in startNum .. endNum) {
            // 显示在首页
            if(!isFuture)
                CustomItemUI(item, isFuture, activity, hazeState, refresh = refresh)
        } else {
            // 显示在第二页
            if(isFuture)
                // 判断是否过期
                CustomItemUI(item, isFuture, activity, hazeState, isOutOfDate = nowTimeNum > endNum, refresh)
        }
    } else {
        if(endNum == nowTimeNum) {
            // 显示在首页
            if(!isFuture)
                CustomItemUI(item, isFuture, activity, hazeState, refresh = refresh)
        } else {
            // 显示在第二页
            if(isFuture)
                // 判断是否过期
                CustomItemUI(item, isFuture, activity, hazeState, isOutOfDate = endNum > nowTimeNum, refresh = refresh)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomItemUI(item: CustomEventDTO,isFuture: Boolean,activity: Activity,hazeState: HazeState,isOutOfDate : Boolean = false,refresh: () -> Unit) {
    val title = item.title
    val description = item.description
    val dateTime = item.dateTime
    var id by remember { mutableIntStateOf(-1) }
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    if(showDialog)
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                if(id > 0) {
                    scope.launch {
                        async { RoomDataBaseManager.customEventDao.del(id) }.await()
                        launch { refresh() }
                        launch { showDialog = false }
                    }
                } else {
                    showToast("id错误")
                }
            },
            dialogText = "要删除此项吗",
            hazeState = hazeState
        )


    StyleCardListItem(
        headlineContent = { Text(text = title, textDecoration = if(isOutOfDate) TextDecoration.LineThrough else TextDecoration.None) },
        overlineContent = { Text(text = item.remark,textDecoration = if(isOutOfDate) TextDecoration.LineThrough else TextDecoration.None) },
        supportingContent = { description?.let { Text(text = it,textDecoration = if(isOutOfDate) TextDecoration.LineThrough else TextDecoration.None) } },
        leadingContent = {
            Icon(
                painterResource(if(item.type == CustomEventType.SCHEDULE) R.drawable.calendar else R.drawable.net),
                contentDescription = "Localized description",
            )
        },
        trailingContent = {
            ColumnVertical {
                if(isOutOfDate) {
                    FilledTonalIconButton(
                        onClick = {
                            id = item.id
                            showDialog = true
                        }
                    ) { Icon(painterResource(R.drawable.delete), null) }
                } else {
                    if(isFuture) {
                        FilledTonalIconButton(
                            onClick = {
                                addToCalendars(
                                    dateTime,
                                    description,
                                    title,
                                    null,
                                    activity
                                )
                            }
                        ) { Icon(painterResource(R.drawable.event_upcoming), null) }
                    } else {
                        if(item.type == CustomEventType.NET_COURSE) {
                            Text("今日截止")
                        }
                    }
                }
                Text("本地")
            }
        },
        modifier = Modifier.combinedClickable(
            onClick = { description?.let { openOperation(it) } },
            onDoubleClick = {
                //双击操作
            },
            onLongClick = {
                //长按操作
                id = item.id
                showDialog = true
            })
    )
}

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
//            modifier = Modifier.background(Color.Transparent).clip(RoundedCornerShape(appHorizontalDp())),
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
    var enabled by remember { mutableStateOf(false) }
    var entity by remember { mutableStateOf<CustomEventDTO?>(null) }

    val scope = rememberCoroutineScope()
    BackHandler {
        showChange(false)
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
//            .skipToLookaheadSize()
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
                            showChange(false)
                        }
                    ) {
                        Icon(Icons.Filled.Close,null)
                    }
                },
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    scope.launch {
                        async {
                            if(enabled && entity != null) {
                                // 添加到数据库
                                RoomDataBaseManager.customEventDao.insert(CustomEventMapper.dtoToEntity(entity!!))
                                showToast("执行完成 请检查是否显示")
                            }
                        }.await()
                        // 关闭
                        launch { showChange(false)  }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(appHorizontalDp()))
            {
                Text(if(enabled) "添加" else "关闭")
            }
        }
    ) { innerPadding ->
        if(showSurface) {
            AnimatedVisibility(
                visible = true,
                enter  = fadeIn(),
                exit = fadeOut()
            ) {
                Column(modifier = Modifier.padding(innerPadding)) {
                    AddEventUI(enabled,{ enabled = it },{ entity = it })
                }
            }
        }
    }
}

@Composable
fun TimeStampItem() = BottomTip(getTimeStamp())

@Composable
fun TermTip() = BottomTip(parseSemseter(getSemseter()))

@Composable
fun TodayUI(hazeState: HazeState) {
    var showBottomSheet by  remember { mutableStateOf(false) }

    if (showBottomSheet ) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            isFullExpand = false,
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("聚焦通知")
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    TodayInfo()
                }
            }
        }
    }

    //判断明天是否有早八
    val weekdayTomorrow = DateTimeUtils.dayWeek + 1
    var week = DateTimeUtils.weeksBetween.toInt()
    //当第二天为下一周的周一时，周数+1
    when(weekdayTomorrow) {
        1 -> week += 1
    }

    val list = getCourseINFO(weekdayTomorrow,week)
    val time = if(list.size < 1)
        ""
    else list[0][0].classTime.substringBefore(":")

    //判断明天是否需要调休
    var rest by  remember { mutableStateOf(false) }
    val schedule = getSchedule()
    for(element in schedule) {
        if(element.title.contains("调休")) {
            if(element.time.substringBefore(" ") == DateTimeUtils.tomorrow_MM_DD) {
                rest = true
            }
        }
    }

    Box(modifier = Modifier.clickable { showBottomSheet = true }) {
        if( getToday()?.todayExam?.courseName == null &&
            getToday()?.todayCourse?.courseName == null &&
            getToday()?.todayActivity?.activityName == null &&
            getToday()?.bookLending?.bookName == null) {

            TransplantListItem(
                headlineContent = { ScrollText(text = if(rest) "有调休安排" else if( time == "08")"明天有早八" else if(time == "10") "明天有早十"  else if(time == "14" || time == "16" || time == "19" )  "明天睡懒觉" else "明天没有课") },
                overlineContent = { ScrollText(text = "明天") },
                leadingContent = { Icon(painter = painterResource(  if(rest) R.drawable.error else if( time == "08") R.drawable.sentiment_sad else if (time == "10") R.drawable.sentiment_dissatisfied else R.drawable.sentiment_very_satisfied) , contentDescription = "")},
            )
        } else {
            if(getToday()?.todayExam?.courseName != null) {
                TransplantListItem(
                    headlineContent = { ScrollText(text = getToday()?.todayExam?.courseName.toString()) },
                    overlineContent = { ScrollText(text = getToday()?.todayExam?.place + "  " + getToday()?.todayExam?.startTime) },
                    leadingContent = { Icon(painter = painterResource(R.drawable.draw), contentDescription = "")},
                )
            }
            if(getToday()?.todayCourse?.courseName != null) {
                TransplantListItem(
                    headlineContent = { ScrollText(text = getToday()?.todayCourse?.courseName.toString()) },
                    overlineContent = { ScrollText(text = getToday()?.todayCourse?.place + "  " + getToday()?.todayCourse?.startTime) },
                    leadingContent = { Icon(painter = painterResource(R.drawable.calendar), contentDescription = "")},
                )
            }
            if(getToday()?.bookLending?.bookName != null) {
                TransplantListItem(
                    headlineContent = { ScrollText(text = getToday()?.bookLending?.bookName.toString()) },
                    overlineContent = { ScrollText(text = getToday()?.bookLending?.returnTime.toString()) },
                    leadingContent = { Icon(painter = painterResource(R.drawable.book), contentDescription = "")},
                )
            }
            if(getToday()?.todayActivity?.activityName != null) {
                TransplantListItem(
                    headlineContent = { ScrollText(text = getToday()?.todayActivity?.activityName.toString()) },
                    overlineContent = { ScrollText(text = getToday()?.todayActivity?.startTime.toString()) },
                    leadingContent = { Icon(painter = painterResource(R.drawable.schedule), contentDescription = "")},
                )
            }

        }
    }
}

fun getToday() : TodayResult? {
    val json = prefs.getString("TodayNotice","")
    return try {
        val result = Gson().fromJson(json,TodayResponse::class.java).result
        val course = result.todayCourse
        val book = result.bookLending
        val exam = result.todayExam
        val activity = result.todayActivity
        TodayResult(course,book,exam,activity)
    } catch (e : Exception) {
        null
    }
}

@Composable
fun AddEventUI(enabled: Boolean,onEnabled : (Boolean) -> Unit,onEntity : (CustomEventDTO?) -> Unit) {
    val activity = LocalActivity.current
    var isScheduleType by remember { mutableStateOf(true) }
    var title by remember { mutableStateOf("事件") }
    var description by remember { mutableStateOf("") }
    var remark by remember { mutableStateOf("") }

    var time by remember { mutableStateOf(Pair("","")) }
    var date by remember { mutableStateOf(Pair("","")) }

    var showSelectDateDialog by remember { mutableStateOf(false) }
    var showSelectTimeDialog by remember { mutableStateOf(false) }


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


    LaunchedEffect(title,time,date,remark) {
        onEnabled(title.isNotBlank() && title.isNotEmpty() && time.first.isNotEmpty() && time.second.isNotEmpty() && date.first.isNotEmpty() && date.second.isNotEmpty() && remark.isNotBlank() && remark.isNotEmpty())
    }

    // 向上回传数据
    LaunchedEffect(enabled) {
        if(enabled) {
            onEntity(
                parseToDateTime(startDate = date.first, startTime = time.first, endDate = date.second, endTime = time.second)?.let {
                    CustomEventDTO(
                        title = title,
                        dateTime = it,
                        type = if(isScheduleType) CustomEventType.SCHEDULE else CustomEventType.NET_COURSE,
                        description = description.let { desp -> if(desp.isNotEmpty() && desp.isNotBlank()) desp else null },
                        remark = remark
                    )
                }
            )
        } else {
            onEntity(null)
        }
    }


    val typeIcon = @Composable {
        Icon(painterResource(if(isScheduleType) R.drawable.calendar else R.drawable.net),null)
    }

    if(showSelectDateDialog)
        DateRangePickerModal(onSelected = { date = it }) { showSelectDateDialog = false }
    if(showSelectTimeDialog)
        TimeRangePickerDialog(onSelected = { time = it }) { showSelectTimeDialog = false }


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
        TransplantListItem(
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

        Spacer(Modifier.height(5.dp))

        CustomTextField(input = title, label = { Text("标题") }) { title = it }
        Spacer(Modifier.height(15.dp))
        CustomTextField(input = description, label = { Text("备注(可空)") }) { description = it }
        Spacer(Modifier.height(10.dp))
        CustomTextField(input = remark, label = { Text("自定义时间显示(可选)") }) { remark = it }
        Spacer(Modifier.height(10.dp))
        TransplantListItem(
            headlineContent = { Text("开始 ${date.first + " " + time.first}\n结束 ${date.second + " " + time.second}") },
            leadingContent = { Icon(painterResource(R.drawable.schedule),null) }
        )
        RowHorizontal {
            TextButton(
                onClick = { showSelectDateDialog = true }
            ) { Text("选择日期范围") }
            TextButton(
                onClick = { showSelectTimeDialog = true }
            ) { Text("选择时间范围") }
        }
    }
}


@Composable
fun CustomTextField(
    input : String = "",
    label : @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier,
    onValueChange : (String) -> Unit
) {
    Row {
        TextField(
            modifier = modifier
                .weight(1f)
                .padding(horizontal = appHorizontalDp()),
            value = input,
            onValueChange = onValueChange,
            label = label,
            trailingIcon = trailingIcon,
            leadingIcon = leadingIcon,
            supportingText = supportingText,
            isError = isError,
            singleLine = singleLine,
            enabled = enabled,
            shape = MaterialTheme.shapes.medium,
            colors = textFiledTransplant(),
        )
    }
}

fun parseTimeItem(item : Int) : String = item.let {
    if(it < 10) {
        "0$it"
    } else {
        it.toString()
    }
}

@Composable
fun JxglstuTodayCourseItem(item : JxglstuCourseSchedule, vmUI : UIViewModel, hazeState: HazeState, timeNow : String) {

    val switchShowEnded = prefs.getBoolean("SWITCHSHOWENDED",true)
    //课程详情
    var showBottomSheet by remember { mutableStateOf(false) }
    val time = item.time

    val startTime = with(time.start) { parseTimeItem(hour) + ":" + parseTimeItem(minute) }
    val endTime = with(time.end) { parseTimeItem(hour) + ":" + parseTimeItem(minute) }


    val state = DateTimeUtils.getTimeState(startTime, endTime,timeNow)


    val itemUI = @Composable {
        StyleCardListItem(
            headlineContent = { Text(text = item.courseName) },
            overlineContent = { Text(text = "$startTime-$endTime")},
            supportingContent = { Text(text = item.place) },
            leadingContent = {
                when(state) {
                    NOT_STARTED -> {
                        Icon(
                            painterResource(R.drawable.schedule),
                            contentDescription = "Localized description",
                        )
                    }
                    ONGOING -> {
                        RotatingIcon(R.drawable.progress_activity)
                    }
                    ENDED -> {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Localized description",
                        )
                    }
                }

            },
            modifier = Modifier.clickable {
                showBottomSheet = true
            },
            trailingContent = {
                Text(
                    when(state) {
                        NOT_STARTED -> "未开始"
                        ONGOING -> "上课中"
                        ENDED -> "已下课"
                    }
                )
            },
        )
    }

    if(switchShowEnded) {
        itemUI()
    } else {
        if(state != ENDED) {
            itemUI()
        }
    }
}

@Composable
fun JxglstuTomorrowCourseItem(item : JxglstuCourseSchedule, vmUI : UIViewModel, hazeState: HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }

    val time = item.time

    val startTime = with(time.start) { parseTimeItem(hour) + ":" + parseTimeItem(minute) }
    val endTime = with(time.end) { parseTimeItem(hour) + ":" + parseTimeItem(minute) }

    StyleCardListItem(
        headlineContent = { Text(text = item.courseName) },
        overlineContent = {Text(text = "$startTime-$endTime")},
        supportingContent = { Text(text = item.place) },
        leadingContent = { Icon(painterResource(R.drawable.exposure_plus_1), contentDescription = "Localized description") },
        modifier = Modifier.clickable {
            showBottomSheet = true
        },
        trailingContent = { Text(text = "明日")}
    )
}


// 传入今天日期 YYYY-MM-DD 返回当天课程
private fun getJxglstuCourse(date : String,vmUI : UIViewModel) : List<JxglstuCourseSchedule> {
    val list = vmUI.jxglstuCourseScheduleList
    try {
        val bean = date.split("-")
        if(bean.size != 3) return emptyList()

        val currentYear = bean[0].toInt()
        val currentMonth = bean[1].toInt()
        val currentDay = bean[2].toInt()

        return list.filter {
            with(it.time.start) {
                year == currentYear && month == currentMonth && day == currentDay
            }
        }.sortedBy { it.time.start.hour }

    } catch (e : Exception) {
        return emptyList()
    }
}

fun getTodayJxglstuCourse(vmUI : UIViewModel) : List<JxglstuCourseSchedule> = getJxglstuCourse(DateTimeUtils.Date_yyyy_MM_dd,vmUI)

fun getTomorrowJxglstuCourse(vmUI : UIViewModel) : List<JxglstuCourseSchedule> = getJxglstuCourse(DateTimeUtils.tomorrow_YYYY_MM_DD,vmUI)
