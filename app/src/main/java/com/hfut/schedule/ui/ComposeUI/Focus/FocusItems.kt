package com.hfut.schedule.ui.ComposeUI.Focus

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
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
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.GetDate
import com.hfut.schedule.logic.datamodel.AddFocus
import com.hfut.schedule.logic.datamodel.FocusCourse
import com.hfut.schedule.logic.datamodel.MyList
import com.hfut.schedule.logic.datamodel.Schedule
import com.hfut.schedule.ui.ComposeUI.MyToast
import com.hfut.schedule.ui.ComposeUI.Search.SchoolCard.SchoolCardItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun TodayCardItem(vm : LoginSuccessViewModel) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium

    ){ SchoolCardItem(vm) }
}


@Composable
fun ExamItem(item : Map<String,String>) {
    var date = GetDate.Date_yyyy_MM_dd
    val todaydate = date?.substring(0, 4) + date?.substring(5, 7)  + date?.substring(8, 10)
    var get = item["日期时间"]
    val examdate = (get?.substring(0,4)+ get?.substring(5, 7) ) + get?.substring(8, 10)

    var  times = item["日期时间"]
    times = times?.substring(5,times.length)
    //判断考完试不显示信息
    if(examdate.toInt() >= todaydate.toInt()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Spacer(modifier = Modifier.height(100.dp))
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium

            )  {
                ListItem(
                    headlineContent = {  Text(text = "${item["课程名称"]}") },
                    overlineContent = { Text(text = times!!) },
                    supportingContent = { Text(text = "${item["考场"]}") },
                    leadingContent = { Icon(painterResource(R.drawable.draw), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable {},
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.errorContainer)
                )
            }
        }
    }
}


@Composable
fun MyScheuleItem(item : Int, MySchedule : MutableList<Schedule> ) {
    var date = GetDate.Date_MM_dd
    val todaydate = (date?.substring(0, 2) ) + date?.substring(3, 5)
    val get = MySchedule[item].time
    val examdate = (get?.substring(0, 2) ) + get?.substring(3, 5)
    //判断考完试不显示信息
    if (examdate.toInt() == todaydate.toInt()) {
        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)
        {
            Spacer(modifier = Modifier.height(100.dp))
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                ),
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
                    leadingContent = {
                        if (MySchedule[item].title.contains("实验"))
                            Icon(painterResource(R.drawable.science), contentDescription = "Localized description",)
                        else
                            Icon(painterResource(R.drawable.calendar), contentDescription = "Localized description",)
                    },
                    modifier = Modifier.clickable {}
                )
            }
        }
    }
}


@Composable
fun FutureMyScheuleItem(item : Int, MySchedule : MutableList<Schedule> ) {
    var date = GetDate.Date_MM_dd
    val todaydate = (date?.substring(0, 2) ) + date?.substring(3, 5)
    val get = MySchedule[item].time
    val examdate = (get?.substring(0, 2) ) + get?.substring(3, 5)
    //判断考完试不显示信息
    if (examdate.toInt() > todaydate.toInt()) {
        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)
        {
            Spacer(modifier = Modifier.height(100.dp))
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                ),
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
                    leadingContent = {
                        if (MySchedule[item].title.contains("实验"))
                            Icon(painterResource(R.drawable.science), contentDescription = "Localized description",)
                        else
                            Icon(painterResource(R.drawable.calendar), contentDescription = "Localized description",)
                    },
                    modifier = Modifier.clickable {}
                )
            }
        }
    }
}


@Composable
fun WangkeItem(item : Int, MyWangKe: MutableList<MyList>) {

    var date = GetDate.Date_MM_dd
    val todaydate = (date?.substring(0, 2) ) + date?.substring(3, 5)
    val get = MyWangKe[item].time
    val Wangkedate = (get?.substring(0, 2) ) + get?.substring(3, 5)
    //判断过期不显示信息
    if(Wangkedate.toInt() >= todaydate.toInt()) {

        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)
        {
            Spacer(modifier = Modifier.height(100.dp))
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,

                ) {

                ListItem(
                    headlineContent = {  Text(text = MyWangKe[item].title) },
                    overlineContent = {Text(text = MyWangKe[item].time)},
                    supportingContent = { Text(text = MyWangKe[item].info)},
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.net),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {}
                )
            }
        }
    }
}



@Composable
fun TodayCourseItem(item : Int,Datum: MutableList<FocusCourse>) {
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
                headlineContent = {  Text(text = Datum[item].name) },
                overlineContent = {Text(text = Datum[item].time)},
                supportingContent = { Text(text = Datum[item].room)},
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


@Composable
fun TomorrowCourseItem(item : Int,DatumTomorrow: MutableList<FocusCourse>) {

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
                headlineContent = {  Text(text = DatumTomorrow[item].name) },
                overlineContent = {Text(text = DatumTomorrow[item].time)},
                supportingContent = { Text(text = DatumTomorrow[item].room)},
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

    //if(!isClicked){
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

                        },
                        onLongClick = {
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
  //  }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("Range")
@Composable
fun BoxScope.AddButton(isVisible: Boolean) {


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
                .padding(horizontal = 15.dp, vertical = 100.dp)
        ) {
            if (isVisible) {
                FloatingActionButton(
                    onClick = { showBottomSheet = true },
                ) { Icon(Icons.Filled.Add, "Add Button") }
            }
        }
}


