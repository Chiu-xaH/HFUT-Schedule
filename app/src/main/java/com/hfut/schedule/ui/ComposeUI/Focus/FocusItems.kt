package com.hfut.schedule.ui.ComposeUI.Focus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.GetDate
import com.hfut.schedule.logic.datamodel.AddFocus
import com.hfut.schedule.logic.datamodel.FocusCourse
import com.hfut.schedule.logic.datamodel.MyList
import com.hfut.schedule.logic.datamodel.Schedule
import com.hfut.schedule.ui.ComposeUI.MyToast
import com.hfut.schedule.ui.ComposeUI.Search.SchoolCard.SchoolCardItem

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
fun WangkeItem(item : Int, MyWangKe: MutableList<MyList>,key: ((Int) -> Any)?) {

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


@Composable
fun AddItem(item : Int, AddedItems : MutableList<AddFocus>) {

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
                headlineContent = {  Text(text = AddedItems[item].title) },
                overlineContent = {Text(text = AddedItems[item].remark)},
                supportingContent = { Text(text = AddedItems[item].title)},
                leadingContent = { Icon(painterResource(R.drawable.add), contentDescription = "Localized description",) },
                modifier = Modifier.clickable {}
            )
        }
    }
}

@Composable
fun BoxScope.AddButton(isVisible: Boolean) {

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
                    onClick = { MyToast("暂未开发") },
                ) { Icon(Icons.Filled.Add, "Add Button") }
            }
        }
}


