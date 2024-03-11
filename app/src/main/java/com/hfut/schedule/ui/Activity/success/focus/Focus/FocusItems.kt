package com.hfut.schedule.ui.Activity.success.focus.Focus

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.sp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.datamodel.Focus.AddFocus
import com.hfut.schedule.logic.datamodel.MyList
import com.hfut.schedule.logic.datamodel.Schedule
import com.hfut.schedule.logic.utils.AddCalendar.AddCalendar
import com.hfut.schedule.ui.Activity.success.calendar.nonet.getCourseINFO
import com.hfut.schedule.ui.Activity.success.search.Search.SchoolCard.SchoolCardItem
import com.hfut.schedule.ui.UIUtils.MyToast


@Composable
fun TodayCardItem(vmUI : UIViewModel) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium

    ){ SchoolCardItem(vmUI) }
}

@Composable
fun MyScheuleItem(item : Int, MySchedule : MutableList<Schedule>,Future: Boolean ) {

        val startTime = MySchedule[item].startTime
        val endTime = MySchedule[item].endTime

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


        val nowTime = GetDate.Date_yyyy_MM_dd.replace("-","").toInt()


        if(!Future) {
            if(nowTime in getStartTime .. getEndTime)
                ScheduleItems(MySchedule = MySchedule, item = item, false)
        }
        else {
            if(nowTime < getStartTime)
                ScheduleItems(MySchedule = MySchedule, item = item,true)
        }

}


@Composable
fun ScheduleItems(MySchedule: MutableList<Schedule>, item : Int,Future : Boolean) {

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp)
            //.size(width = 350.dp, height = 90.dp)
            ,shape = MaterialTheme.shapes.medium

        ) {
            ListItem(
                headlineContent = {  Text(text = MySchedule[item].title) },
                overlineContent = {Text(text = MySchedule[item].time)},
                supportingContent = { Text(text = MySchedule[item].info)},
                leadingContent = { ScheduleIcons(title = MySchedule[item].title) },
                modifier = Modifier.clickable {},
                trailingContent = {
                    if(Future)
                    FilledTonalIconButton(
                        onClick = {
                            try {
                                var startTime = MySchedule[item].startTime
                                var endTime = MySchedule[item].endTime
                                AddCalendar(startTime,endTime, MySchedule[item].info, MySchedule[item].title,MySchedule[item].time)
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
                    else Text(text = "进行中")
                }
            )
        }

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WangkeItem(item : Int, MyWangKe: MutableList<MyList>) {

        val startTime = MyWangKe[item].startTime
        val endTime = MyWangKe[item].endTime

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


        val nowTime = GetDate.Date_yyyy_MM_dd.replace("-","").toInt()

        if(nowTime <= getEndTime) {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ) {

                ListItem(
                    headlineContent = {  Text(text = MyWangKe[item].title) },
                    overlineContent = { Text(text = MyWangKe[item].time) },
                    supportingContent = { Text(text = MyWangKe[item].info)},
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.net),
                            contentDescription = "Localized description",
                        )
                    },
                    trailingContent = {
                        FilledTonalIconButton(
                            onClick = {
                                AddCalendar(startTime,endTime, MyWangKe[item].info, MyWangKe[item].title,MyWangKe[item].time)
                                MyToast("添加到系统日历成功")
                            }
                        ) {
                            Icon( painterResource(R.drawable.add_task),
                                contentDescription = "Localized description",)
                        }
                    },
                    modifier = Modifier.clickable {
                        //AddCalendar(Place = MyWangKe()[item].info, Title = MyWangKe[item].title)
                    }
                )
            }
        }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodayCourseItem(item : Int) {
    var week = GetDate.Benweeks.toInt()

    var weekday = GetDate.dayweek
    if(weekday == 0) weekday = 7
    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)
    {
        Spacer(modifier = Modifier.height(100.dp))
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp),
            shape = MaterialTheme.shapes.medium

        ) {
            ListItem(
                headlineContent = {  Text(text = getCourseINFO(weekday,week)[item][0].name) },
                overlineContent = { Text(text = getCourseINFO(weekday,week)[item][0].classTime)},
                supportingContent = { Text(text = getCourseINFO(weekday,week)[item][0].place)},
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.schedule),
                        contentDescription = "Localized description",
                    )
                }, modifier = Modifier.clickable {  }
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TomorrowCourseItem(item : Int) {

    var weekdaytomorrow = GetDate.dayweek + 1
    var week = GetDate.Benweeks.toInt()
    //当第二天为周日时，变值为0
    //当第二天为下一周的周一时，周数+1
    when(weekdaytomorrow) {
        7 -> weekdaytomorrow = 0
        1 -> week += 1
    }

    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
        Spacer(modifier = Modifier.height(100.dp))
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp),
            shape = MaterialTheme.shapes.medium,
            ) {
            ListItem(
                headlineContent = {  Text(text = getCourseINFO(weekdaytomorrow,week)[item][0].name) },
                overlineContent = {Text(text = getCourseINFO(weekdaytomorrow,week)[item][0].classTime)},
                supportingContent = { Text(text = getCourseINFO(weekdaytomorrow,week)[item][0].place)},
                leadingContent = { Icon(painterResource(R.drawable.exposure_plus_1), contentDescription = "Localized description",) },
                modifier = Modifier.clickable {}
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
            Spacer(modifier = Modifier.height(100.dp))
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
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
                            RemoveItems(AddedItems[item].id)

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


    val sheetState = rememberModalBottomSheetState()
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
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false },sheetState = sheetState) {
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
                                    AddItems(title,info,remark)
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
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                    ) {
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
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(text = getTimeStamp(), color = Color.Gray, fontSize = 14.sp)
    }
}


