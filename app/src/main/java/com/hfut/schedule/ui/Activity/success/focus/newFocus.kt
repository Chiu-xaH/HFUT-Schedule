package com.hfut.schedule.ui.Activity.success.focus

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.datamodel.MyAPIResponse
import com.hfut.schedule.logic.datamodel.MyList
import com.hfut.schedule.logic.datamodel.Schedule
import com.hfut.schedule.logic.utils.AddCalendar
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.success.focus.Focus.ScheduleIcons
import com.hfut.schedule.ui.Activity.success.focus.Focus.openOperation
import com.hfut.schedule.ui.UIUtils.MyToast
import kotlin.Exception


fun getURL() : String {
    val my = prefs.getString("my","")
    return try {
        Gson().fromJson(my,MyAPIResponse::class.java).API
    } catch (e : Exception) {
        "https://www.chiuxah.xyz/"
    }
}
//真为Schedule。否则Wangke
fun getResult(mode : Boolean): MutableList<newSchedule> {
    val listSchedule = mutableListOf<newSchedule>()
    val listWangke = mutableListOf<newSchedule>()
    return try {
        val json = Gson().fromJson(prefs.getString("newFocus",""),APIResponse::class.java)
        val schedule = json.Schedule
        val wangke = json.Wangke

        for(i in schedule.indices) {
            listSchedule.add(schedule[i])
        }
        for(i in wangke.indices) {
            listWangke.add(wangke[i])
        }
        if(mode) listSchedule else listWangke
    } catch (e : Exception) {
        if(mode) listSchedule else listWangke
    }

}

fun transTime(dateTime : String) : List<Int> {
    val year = dateTime.substringBefore("-").toInt()
    val month = dateTime.substring(5,7).toInt()
    val date = dateTime.substring(8,10).toInt()
    val hour = dateTime.substringAfter(" ").substring(0,2).toInt()
    val minute = dateTime.substringAfter(" ").substring(3,5).toInt()
    return listOf(year,month,date,hour,minute)
}
@Composable
fun newScheuleItem(item : Int, MySchedule : MutableList<newSchedule>, Future: Boolean ) {

    val MySchedules = MySchedule[item]
    if(SharePrefs.prefs.getString("newFocus","")?.contains("Schedule") == true) {
        val startTime = transTime(MySchedules.startTime)
        val endTime = transTime(MySchedules.endTime)

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
                newScheduleItems(MySchedule = getResult(true), item = item, false)
        }
        else {
            if(nowTime < getStartTime)
                newScheduleItems(MySchedule = getResult(true), item = item,true)
        }
    }
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun newScheduleItems(MySchedule: MutableList<newSchedule>, item : Int, Future : Boolean) {
    val MySchedules = MySchedule[item]
    val time = MySchedules.remark
    val info = MySchedules.info
    val title = MySchedules.title

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp)
        //.size(width = 350.dp, height = 90.dp)
        ,shape = MaterialTheme.shapes.medium

    ) {
        ListItem(
            headlineContent = {  Text(text = title) },
            overlineContent = { Text(text = time) },
            supportingContent = { Text(text = info) },
            leadingContent = { ScheduleIcons(title = title) },
            modifier = Modifier.clickable {},
            trailingContent = {
                if(Future)
                    FilledTonalIconButton(
                        onClick = {
                            try {
                                var startTime = transTime(MySchedules.startTime)
                                var endTime = transTime(MySchedules.endTime)
                                AddCalendar.AddCalendar(startTime,endTime, info, title,time)
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun newWangkeItem(item : Int, MyWangKe: MutableList<newSchedule>, Future: Boolean) {
    val MyWangKes = MyWangKe[item]
    val time = MyWangKes.remark
    val info = MyWangKes.info
    val title = MyWangKes.title

    if(SharePrefs.prefs.getString("newFocus","")?.contains("Schedule") == true) {
        val startTime = transTime(MyWangKes.startTime)
        val endTime = transTime(MyWangKes.endTime)

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


        if(Future) {
            if(nowTime < getEndTime) {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 5.dp),
                    shape = MaterialTheme.shapes.medium,
                ) {

                    ListItem(
                        headlineContent = {  Text(text = title) },
                        overlineContent = { Text(text = time) },
                        supportingContent = { Text(text = info) },
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.net),
                                contentDescription = "Localized description",
                            )
                        },
                        trailingContent = {
                            FilledTonalIconButton(
                                onClick = {
                                    AddCalendar.AddCalendar(startTime,endTime, info, title,time)
                                    MyToast("添加到系统日历成功")
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
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 5.dp),
                    shape = MaterialTheme.shapes.medium,
                ) {

                    ListItem(
                        headlineContent = {  Text(text = title) },
                        overlineContent = { Text(text = time) },
                        supportingContent = { Text(text = info) },
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

