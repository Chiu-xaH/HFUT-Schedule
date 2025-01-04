package com.hfut.schedule.ui.activity.home.focus.funictions

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.beans.Community.TodayResponse
import com.hfut.schedule.logic.beans.Community.TodayResult
import com.hfut.schedule.logic.beans.Focus.AddFocus
import com.hfut.schedule.logic.beans.Schedule
import com.hfut.schedule.logic.utils.AddCalendar.addToCalendar
import com.hfut.schedule.logic.utils.DateTimeManager
import com.hfut.schedule.logic.utils.DateTimeManager.TimeState.*
import com.hfut.schedule.logic.utils.Semseter.getSemseter
import com.hfut.schedule.logic.utils.Semseter.getSemseterCloud
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.activity.home.calendar.communtiy.DetailInfos
import com.hfut.schedule.ui.activity.home.calendar.communtiy.getCourseINFO
import com.hfut.schedule.ui.activity.home.cube.items.main.apiCheck
import com.hfut.schedule.ui.activity.home.focus.funictions.FocusDataBaseManager.addItems
import com.hfut.schedule.ui.activity.home.focus.funictions.FocusDataBaseManager.removeItems
import com.hfut.schedule.ui.activity.home.search.functions.card.TodayInfo
import com.hfut.schedule.ui.utils.components.BottomTip
import com.hfut.schedule.ui.utils.components.MyCard
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.RotatingIcon
import com.hfut.schedule.ui.utils.style.Round
import com.hfut.schedule.ui.utils.components.ScheduleIcons
import com.hfut.schedule.ui.utils.components.ScrollText

@Composable
fun MyScheuleItem(item : Int, MySchedule : MutableList<Schedule>,Future: Boolean,activity : Activity ) {

    val MySchedules = MySchedule[item]
    if(prefs.getString("my","")?.contains("Schedule") == true) {
        val startTime = MySchedules.startTime
        val endTime = MySchedules.endTime

        //判断过期不显示信息

        val startYear = startTime[0]
        var startMonth = startTime[1]
        var startDay = startTime[2]
        var startDateStr = startTime[2].toString()
        var startMonthStr = startTime[1].toString()
        if(startDay < 10) startDateStr = "0$startDay"
        if(startMonth < 10) startMonthStr = "0$startMonth"
        val getStartTime = "${startYear}${startMonthStr}${startDateStr}".toInt()

        val endYear = endTime[0]
        var endMonth = endTime[1]
        var endDay = endTime[2]
        var endDateStr = endTime[2].toString()
        var endMonthStr = endTime[1].toString()
        if(endDay < 10) endDateStr = "0$endDay"
        if(endMonth < 10) endMonthStr = "0$endMonth"
        val getEndTime = "${endYear}${endMonthStr}${endDateStr}".toInt()


        val nowTime = DateTimeManager.Date_yyyy_MM_dd.replace("-","").toInt()


        if(!Future) {
            if(nowTime in getStartTime .. getEndTime)
                ScheduleItems(MySchedule = MySchedule, item = item, false,activity)
        }
        else {
            if(nowTime < getStartTime)
                ScheduleItems(MySchedule = MySchedule, item = item,true,activity)
        }
    }
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun ScheduleItems(MySchedule: MutableList<Schedule>, item : Int,Future : Boolean,activity : Activity) {
    val MySchedules = MySchedule[item]
    val time = MySchedules.time
    val info = MySchedules.info
    val title = MySchedules.title
    val showPublic = MySchedules.showPublic
    val switch_api = SharePrefs.prefs.getBoolean("SWITCHMYAPIS", apiCheck())


    @Composable
    fun Item() {
        MyCard {
            ListItem(
                headlineContent = {  Text(text = title) },
                overlineContent = {Text(text = time)},
                supportingContent = { Text(text = info)},
                leadingContent = { ScheduleIcons(title = title) },
                modifier = Modifier.clickable {},
                trailingContent = {
                    if(Future)
                        FilledTonalIconButton(
                            onClick = {
                                try {
                                    var startTime = MySchedules.startTime
                                    var endTime = MySchedules.endTime
                                    addToCalendar(startTime,endTime, info, title,time,activity)
                                    MyToast("添加到系统日历成功")
                                } catch (e : SecurityException) {
                                    MyToast("未授予权限")
                                    e.printStackTrace()
                                }
                            }
                        ) {
                            Icon( painterResource(R.drawable.add_task),
                                contentDescription = "Localized description",)
                        }
                }
            )
        }
    }

    if(switch_api) {
        //正常接受所有来自服务器的信息
        Item()
    } else {
        //仅接受showPublic为true
        if(showPublic) {
            Item()
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WangkeItem(item : Int, MyWangKe: MutableList<Schedule>,Future: Boolean,activity: Activity) {
    val MyWangKes = MyWangKe[item]
    val time = MyWangKes.time
    val info = MyWangKes.info
    val title = MyWangKes.title
    val showPublic = MyWangKes.showPublic

    @Composable
    fun Item() {
        if(prefs.getString("my","")?.contains("Schedule") == true) {
            val startTime = MyWangKes.startTime
            val endTime = MyWangKes.endTime

            //判断过期不显示信息
            val startYear = startTime[0]
            var startMonth = startTime[1]
            var startDay = startTime[2]
            var startDateStr = startTime[2].toString()
            var startMonthStr = startTime[1].toString()
            if(startDay < 10) startDateStr = "0$startDay"
            if(startMonth < 10) startMonthStr = "0$startMonth"
            val getStartTime = "${startYear}${startMonthStr}${startDateStr}".toInt()

            val endYear = endTime[0]
            var endMonth = endTime[1]
            var endDay = endTime[2]
            var endDateStr = endTime[2].toString()
            var endMonthStr = endTime[1].toString()
            if(endDay < 10) endDateStr = "0$endDay"
            if(endMonth < 10) endMonthStr = "0$endMonth"
            val getEndTime = "${endYear}${endMonthStr}${endDateStr}".toInt()


            val nowTime = DateTimeManager.Date_yyyy_MM_dd.replace("-","").toInt()


            if(Future) {
                if(nowTime < getEndTime) {
                    MyCard {
                        ListItem(
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
                                        addToCalendar(startTime,endTime, info, title,time,activity)

                                    }
                                ) {
                                    Icon( painterResource(R.drawable.add_task),
                                        contentDescription = "Localized description",)
                                }
                            },
                            modifier = Modifier.clickable { openOperation(info) }
                        )
                    }
                }
            } else {
                if(nowTime == getEndTime) {
                    MyCard {
                        ListItem(
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
    }

    val switch_api = SharePrefs.prefs.getBoolean("SWITCHMYAPIS", apiCheck())


    if(switch_api) {
        //正常接受所有来自服务器的信息
        Item()
    } else {
        //仅接受showPublic为true
        if(showPublic) {
            Item()
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodayCourseItem(item : Int,vm : NetWorkViewModel) {


    val switch_show_ended = prefs.getBoolean("SWITCHSHOWENDED",true)


    var week = DateTimeManager.Benweeks.toInt()

    var weekday = DateTimeManager.dayweek
    if(weekday == 0) weekday = 7
    //课程详情
    val list = getCourseINFO(weekday,week)[item][0]
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val time = list.classTime

    val startTime = time.substringBefore("-")
    val endTime = time.substringAfter("-")
    val state = DateTimeManager.getTimeState(startTime, endTime)

    if (showBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
        ) {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { list.name.let { Text(it) } },
            )
            DetailInfos(list,vm = vm)
        }
    }
    @Composable
    fun Item() {
        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
            //Spacer(modifier = Modifier.height(100.dp))
            MyCard {
                ListItem(
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
//                                Icon(
//                                    painterResource(R.drawable.progress_activity),
//                                    contentDescription = "Localized description",
//                                )
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
//                colors = when(state) {
//                    ONGOING -> ListItemDefaults.colors()
//                    NOT_STARTED -> ListItemDefaults.colors(MaterialTheme.colorScheme.primaryContainer)
//                    ENDED -> ListItemDefaults.colors(MaterialTheme.colorScheme.surfaceVariant.copy(.5f))
//                }
                )
            }
        }
    }

    if(switch_show_ended) {
        Item()
    } else {
        if(state != DateTimeManager.TimeState.ENDED) {
            Item()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TomorrowCourseItem(item : Int,vm: NetWorkViewModel) {

    var weekdaytomorrow = DateTimeManager.dayweek + 1
    var week = DateTimeManager.Benweeks.toInt()
    //当第二天为下一周的周一时，周数+1
    when(weekdaytomorrow) {
        1 -> week += 1
    }

    //课程详情
    val list = getCourseINFO(weekdaytomorrow,week)[item][0]
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
        ) {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { list.name.let { Text(it) } },
            )
            DetailInfos(list,vm = vm)
        }
    }


    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
        //Spacer(modifier = Modifier.height(100.dp))
        MyCard {
            ListItem(
                headlineContent = { list.name?.let { Text(text = it) } },
                overlineContent = {Text(text = list.classTime)},
                supportingContent = { list.place?.let { Text(text = it) } },
                leadingContent = { Icon(painterResource(R.drawable.exposure_plus_1), contentDescription = "Localized description",) },
                modifier = Modifier.clickable {
                    showBottomSheet = true
                },
                trailingContent = { Text(text = "明日")}
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("Range")
@Composable
fun AddItem(item : Int, AddedItems : MutableList<AddFocus>) {
    var isClicked by remember { mutableStateOf(false) }
        var Lists by remember { mutableStateOf( AddedItems() ) }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            //Spacer(modifier = Modifier.height(100.dp))
            MyCard {
                ListItem(
                    headlineContent = { Text(text = AddedItems[item].title) },
                    overlineContent = { Text(text = AddedItems[item].remark) },
                    supportingContent = { Text(text = AddedItems[item].info) },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.lightbulb),
                            contentDescription = "Localized description",
                        )
                    },
                    colors = if (isClicked) ListItemDefaults.colors(MaterialTheme.colorScheme.errorContainer) else ListItemDefaults.colors(),
                    modifier = Modifier.combinedClickable(
                        onClick = { MyToast("长按删除") },
                        onDoubleClick = {
                                        //双击操作
                        },
                        onLongClick = {
                            //长按操作
                            isClicked = true

                            removeItems(AddedItems[item].id)

                            // AddedItems().removeAt(item)
                            MyToast("下次数据刷新时将删除")
                            //EditItems(AddedItems[item].id,AddedItems[item].title,AddedItems[item].info,AddedItems[item].remark)
                            // MyToast("就那几个字删了重新添加吧")

                        })
                )
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("Range")
@Composable
fun BoxScope.AddButton(isVisible: Boolean,innerPaddings : PaddingValues) {


    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "" // 使用弹簧动画
    )

    var title by remember { mutableStateOf( "") }
    var info by remember { mutableStateOf( "") }
    var remark by remember { mutableStateOf( "") }


    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false },sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        actions = {

                            FilledTonalIconButton(
                                modifier = Modifier
                                    .scale(scale.value)
                                    .padding(25.dp),
                                interactionSource = interactionSource,
                                onClick = {
                                    addItems(title,info,remark)
                                    showBottomSheet = false
                                    Toast.makeText(MyApplication.context,"添加成功", Toast.LENGTH_SHORT).show()
                                }
                            ) { Icon(Icons.Filled.Check, contentDescription = "") }
                        },
                        title = { Text("添加聚焦卡片") }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {

                    Spacer(modifier = Modifier.height(10.dp))
                    MyCard {
                        ListItem(
                            headlineContent = {  Text(text = title) },
                            overlineContent = {Text(text = remark)},
                            supportingContent = { Text(text = info)},
                            leadingContent = { Icon(painterResource(R.drawable.lightbulb), contentDescription = "Localized description",) },
                            modifier = Modifier.clickable {}
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 15.dp),
                            value = title,
                            onValueChange = { title = it },
                            leadingIcon = { Icon( painterResource(R.drawable.title), contentDescription = "Localized description") },
                            label = { Text("标题" ) },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            colors = TextFieldDefaults.textFieldColors(focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                                unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 15.dp),
                            value = info,
                            onValueChange = { info = it },
                            leadingIcon = { Icon( painterResource(R.drawable.info_i), contentDescription = "Localized description") },
                            label = { Text("内容" ) },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            colors = TextFieldDefaults.textFieldColors(focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                                unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 15.dp),
                            value = remark,
                            onValueChange = { remark = it },
                            leadingIcon = { Icon( painterResource(R.drawable.format_italic), contentDescription = "Localized description") },
                            label = { Text("备注" ) },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            colors = TextFieldDefaults.textFieldColors(focusedIndicatorColor = Color.Transparent,
                                // 有焦点时的颜色，透明
                                unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }


    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn() ,
        exit = scaleOut(),
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(innerPaddings)
            .padding(horizontal = 15.dp, vertical = 15.dp)
    ) {
        if (isVisible) {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
            ) { Icon(Icons.Filled.Add, "Add Button") }
        }
    }
}

@Composable
fun TimeStampItem() {
    BottomTip(getTimeStamp())
}

@Composable
fun SemsterTip() {
    BottomTip(getSemseter(getSemseterCloud()))
}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodayUI() {
    ////////////////////////////////////////////////////////////
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by  remember { mutableStateOf(false) }
    if (showBottomSheet ) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("聚焦通知") }
                    )
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

    //////////////////////////////////////////////////////////////////////////
    //判断明天是否有早八
    var weekdaytomorrow = DateTimeManager.dayweek + 1
    var week = DateTimeManager.Benweeks.toInt()
    //当第二天为下一周的周一时，周数+1
    when(weekdaytomorrow) {
        1 -> week += 1
    }

    val list = getCourseINFO(weekdaytomorrow,week)
    val time = if(list.size < 1)
        ""
    else list[0][0].classTime.substringBefore(":")

    //判断明天是否需要调休
    var tiaoXiu by  remember { mutableStateOf(false) }
  ///  var tiaoXiuInfo by remember { mutableStateOf("") }
    val schedule = MySchedule()
    for(i in 0 until schedule.size) {
        val schedules = schedule[i]
        if(schedules.title.contains("调休")) {
            if(schedules.time.substringBefore(" ") == DateTimeManager.tomorrow) {
                tiaoXiu = true
            //    tiaoXiuInfo = schedules.info
            }
        }
    }

    Box(modifier = Modifier.clickable { showBottomSheet = true }) {
        if( getToday()?.todayExam?.courseName == null &&
            getToday()?.todayCourse?.courseName == null &&
            getToday()?.todayActivity?.activityName == null &&
            getToday()?.bookLending?.bookName == null) {

            ListItem(
                headlineContent = { ScrollText(text = if(tiaoXiu) "有调休安排" else if( time == "08")"明天有早八" else if(time == "10") "明天有早十"  else if(time == "14" || time == "16" || time == "19" )  "明天睡懒觉" else "明天没有课") },
                overlineContent = { ScrollText(text = "明天") },
                leadingContent = { Icon(painter = painterResource(  if(tiaoXiu) R.drawable.error else if( time == "08") R.drawable.sentiment_sad else if (time == "10") R.drawable.sentiment_dissatisfied else R.drawable.sentiment_very_satisfied) , contentDescription = "")},
            )
        } else {
            if(getToday()?.todayExam?.courseName != null) {
                ListItem(
                    headlineContent = { ScrollText(text = getToday()?.todayExam?.courseName.toString()) },
                    overlineContent = { ScrollText(text = getToday()?.todayExam?.place + "  " + getToday()?.todayExam?.startTime) },
                    leadingContent = { Icon(painter = painterResource(R.drawable.draw), contentDescription = "")},
                )
            }
            if(getToday()?.todayCourse?.courseName != null) {
                ListItem(
                    headlineContent = { ScrollText(text = getToday()?.todayCourse?.courseName.toString()) },
                    overlineContent = { ScrollText(text = getToday()?.todayCourse?.place + "  " + getToday()?.todayCourse?.startTime) },
                    leadingContent = { Icon(painter = painterResource(R.drawable.calendar), contentDescription = "")},
                )
            }
            if(getToday()?.bookLending?.bookName != null) {
                ListItem(
                    headlineContent = { ScrollText(text = getToday()?.bookLending?.bookName.toString()) },
                    overlineContent = { ScrollText(text = getToday()?.bookLending?.returnTime.toString()) },
                    leadingContent = { Icon(painter = painterResource(R.drawable.book), contentDescription = "")},
                )
            }
            if(getToday()?.todayActivity?.activityName != null) {
                ListItem(
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



