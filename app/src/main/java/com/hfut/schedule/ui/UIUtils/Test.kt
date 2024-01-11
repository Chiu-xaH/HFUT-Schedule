package com.hfut.schedule.ui.UIUtils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.provider.CalendarContract
import androidx.annotation.RequiresApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.hfut.schedule.App.MyApplication
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun Tests() {
   // Button(onClick = { AddCalendar() }) {
     //   Text(text = "操作")
    //}
}

fun Clicks() {
    val startMillis: Long = Calendar.getInstance().run {
        set(2024, 1, 8, 7, 30)
        timeInMillis
    }
    val endMillis: Long = Calendar.getInstance().run {
        set(2024, 1, 8, 8, 30)
        timeInMillis
    }

    val intent = Intent(Intent.ACTION_INSERT)
        .setData(CalendarContract.Events.CONTENT_URI)
        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
        .putExtra(CalendarContract.Events.TITLE, "Yoga")
        .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
        .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
        .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com")
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    MyApplication.context.startActivity(intent)
//
}

fun AddCalendar(Start : List<Int>, End : List<Int>, Place : String, Title : String, Remark : String) {
    //2月10号2
    val beginTime = Calendar.getInstance()
    beginTime.set(Start[0], Start[1], Start[2], Start[3], Start[4]) // 2024年1月10日上午8:30
    val endTime = Calendar.getInstance()
    endTime.set(End[0], End[1], End[2], End[3], End[4]) // 2024年1月10日上午9:30

    val uri = CalendarContract.Events.CONTENT_URI

// 创建一个日程的内容值对象，设置日程的相关属性
    val values = ContentValues().apply {
        put(CalendarContract.Events.CALENDAR_ID, 1) // 日历的ID，可以通过查询日历提供者获取
        put(CalendarContract.Events.TITLE, Title) // 标题
        put(CalendarContract.Events.DESCRIPTION, Remark) // 备注
        put(CalendarContract.Events.DTSTART, beginTime.timeInMillis)
        put(CalendarContract.Events.DTEND, endTime.timeInMillis)
        put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai") // 时区
        put(CalendarContract.Events.EVENT_LOCATION, Place) // 地点
    }

// 在同步适配器的 onPerformSync() 方法中，使用内容解析器插入日程
    val resolver = MyApplication.context.contentResolver
    resolver.insert(uri, values) // 第三个参数是一个Bundle对象，用于指定同步适配器的相关信息，如账户名称和类型，以及是否强制同步

}