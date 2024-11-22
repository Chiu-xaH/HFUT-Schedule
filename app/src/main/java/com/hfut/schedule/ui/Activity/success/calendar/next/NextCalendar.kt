package com.hfut.schedule.ui.Activity.success.calendar.next

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.logic.datamodel.Jxglstu.datumResponse
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.Activity.success.calendar.next.DatumMode.*
import com.hfut.schedule.ui.UIUtils.MyToast
import java.time.LocalDate

enum class DatumMode {
    JXGLSTU,COMMUNITY,NEXT
}
@Composable
fun datumMode(Mode : DatumMode) {
    when(Mode) {
        COMMUNITY -> {

        }

        JXGLSTU -> {

        }
        NEXT -> {

        }
    }
}
@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatumUI(showAll : Boolean,
            grade : String,
            innerPadding : PaddingValues,
            vmUI : UIViewModel,
           ) {

    var table_1_1 by rememberSaveable { mutableStateOf("") }
    var table_1_2 by rememberSaveable { mutableStateOf("") }
    var table_1_3 by rememberSaveable { mutableStateOf("") }
    var table_1_4 by rememberSaveable { mutableStateOf("") }
    var table_1_5 by rememberSaveable { mutableStateOf("") }
    var table_2_1 by rememberSaveable { mutableStateOf("") }
    var table_2_2 by rememberSaveable { mutableStateOf("") }
    var table_2_3 by rememberSaveable { mutableStateOf("") }
    var table_2_4 by rememberSaveable { mutableStateOf("") }
    var table_2_5 by rememberSaveable { mutableStateOf("") }
    var table_3_1 by rememberSaveable { mutableStateOf("") }
    var table_3_2 by rememberSaveable { mutableStateOf("") }
    var table_3_3 by rememberSaveable { mutableStateOf("") }
    var table_3_4 by rememberSaveable { mutableStateOf("") }
    var table_3_5 by rememberSaveable { mutableStateOf("") }
    var table_4_1 by rememberSaveable { mutableStateOf("") }
    var table_4_2 by rememberSaveable { mutableStateOf("") }
    var table_4_3 by rememberSaveable { mutableStateOf("") }
    var table_4_4 by rememberSaveable { mutableStateOf("") }
    var table_4_5 by rememberSaveable { mutableStateOf("") }
    var table_5_1 by rememberSaveable { mutableStateOf("") }
    var table_5_2 by rememberSaveable { mutableStateOf("") }
    var table_5_3 by rememberSaveable { mutableStateOf("") }
    var table_5_4 by rememberSaveable { mutableStateOf("") }
    var table_5_5 by rememberSaveable { mutableStateOf("") }
    var table_6_1 by rememberSaveable { mutableStateOf("") }
    var table_6_2 by rememberSaveable { mutableStateOf("") }
    var table_6_3 by rememberSaveable { mutableStateOf("") }
    var table_6_4 by rememberSaveable { mutableStateOf("") }
    var table_6_5 by rememberSaveable { mutableStateOf("") }
    var table_1_6 by rememberSaveable { mutableStateOf("") }
    var table_1_7 by rememberSaveable { mutableStateOf("") }
    var table_2_6 by rememberSaveable { mutableStateOf("") }
    var table_2_7 by rememberSaveable { mutableStateOf("") }
    var table_3_6 by rememberSaveable { mutableStateOf("") }
    var table_3_7 by rememberSaveable { mutableStateOf("") }
    var table_4_6 by rememberSaveable { mutableStateOf("") }
    var table_4_7 by rememberSaveable { mutableStateOf("") }
    var table_5_6 by rememberSaveable { mutableStateOf("") }
    var table_5_7 by rememberSaveable { mutableStateOf("") }
    var table_6_6 by rememberSaveable { mutableStateOf("") }
    var table_6_7 by rememberSaveable { mutableStateOf("") }


    var sheet_1_1 by rememberSaveable { mutableStateOf("") }
    var sheet_1_2 by rememberSaveable { mutableStateOf("") }
    var sheet_1_3 by rememberSaveable { mutableStateOf("") }
    var sheet_1_4 by rememberSaveable { mutableStateOf("") }
    var sheet_1_5 by rememberSaveable { mutableStateOf("") }
    var sheet_2_1 by rememberSaveable { mutableStateOf("") }
    var sheet_2_2 by rememberSaveable { mutableStateOf("") }
    var sheet_2_3 by rememberSaveable { mutableStateOf("") }
    var sheet_2_4 by rememberSaveable { mutableStateOf("") }
    var sheet_2_5 by rememberSaveable { mutableStateOf("") }
    var sheet_3_1 by rememberSaveable { mutableStateOf("") }
    var sheet_3_2 by rememberSaveable { mutableStateOf("") }
    var sheet_3_3 by rememberSaveable { mutableStateOf("") }
    var sheet_3_4 by rememberSaveable { mutableStateOf("") }
    var sheet_3_5 by rememberSaveable { mutableStateOf("") }
    var sheet_4_1 by rememberSaveable { mutableStateOf("") }
    var sheet_4_2 by rememberSaveable { mutableStateOf("") }
    var sheet_4_3 by rememberSaveable { mutableStateOf("") }
    var sheet_4_4 by rememberSaveable { mutableStateOf("") }
    var sheet_4_5 by rememberSaveable { mutableStateOf("") }
    var sheet_5_1 by rememberSaveable { mutableStateOf("") }
    var sheet_5_2 by rememberSaveable { mutableStateOf("") }
    var sheet_5_3 by rememberSaveable { mutableStateOf("") }
    var sheet_5_4 by rememberSaveable { mutableStateOf("") }
    var sheet_5_5 by rememberSaveable { mutableStateOf("") }
    var sheet_6_1 by rememberSaveable { mutableStateOf("") }
    var sheet_6_2 by rememberSaveable { mutableStateOf("") }
    var sheet_6_3 by rememberSaveable { mutableStateOf("") }
    var sheet_6_4 by rememberSaveable { mutableStateOf("") }
    var sheet_6_5 by rememberSaveable { mutableStateOf("") }
    var sheet_1_6 by rememberSaveable { mutableStateOf("") }
    var sheet_1_7 by rememberSaveable { mutableStateOf("") }
    var sheet_2_6 by rememberSaveable { mutableStateOf("") }
    var sheet_2_7 by rememberSaveable { mutableStateOf("") }
    var sheet_3_6 by rememberSaveable { mutableStateOf("") }
    var sheet_3_7 by rememberSaveable { mutableStateOf("") }
    var sheet_4_6 by rememberSaveable { mutableStateOf("") }
    var sheet_4_7 by rememberSaveable { mutableStateOf("") }
    var sheet_5_6 by rememberSaveable { mutableStateOf("") }
    var sheet_5_7 by rememberSaveable { mutableStateOf("") }
    var sheet_6_6 by rememberSaveable { mutableStateOf("") }
    var sheet_6_7 by rememberSaveable { mutableStateOf("") }
//
//    var today by rememberSaveable { mutableStateOf(LocalDate.now()) }
//    val mondayOfCurrentWeek = today.minusDays(today.dayOfWeek.value - 1L)

    val tableall = arrayOf(
        table_1_1, table_1_2, table_1_3, table_1_4, table_1_5,table_1_6,table_1_7,
        table_2_1, table_2_2, table_2_3, table_2_4, table_2_5,table_2_6,table_2_7,
        table_3_1, table_3_2, table_3_3, table_3_4, table_3_5,table_3_6,table_3_7,
        table_4_1, table_4_2, table_4_3, table_4_4, table_4_5,table_4_6,table_4_7,
        table_5_1, table_5_2, table_5_3, table_5_4, table_5_5,table_5_6,table_5_7,
        table_6_1, table_6_2, table_6_3, table_6_4, table_6_5,table_6_6,table_6_7,
    )

    val sheetall = arrayOf(
        sheet_1_1, sheet_1_2, sheet_1_3, sheet_1_4, sheet_1_5,sheet_1_6,sheet_1_7,
        sheet_2_1, sheet_2_2, sheet_2_3, sheet_2_4, sheet_2_5,sheet_2_6,sheet_2_7,
        sheet_3_1, sheet_3_2, sheet_3_3, sheet_3_4, sheet_3_5,sheet_3_6,sheet_3_7,
        sheet_4_1, sheet_4_2, sheet_4_3, sheet_4_4, sheet_4_5,sheet_4_6,sheet_4_7,
        sheet_5_1, sheet_5_2, sheet_5_3, sheet_5_4, sheet_5_5,sheet_5_6,sheet_5_7,
        sheet_6_1, sheet_6_2, sheet_6_3, sheet_6_4, sheet_6_5,sheet_6_6,sheet_6_7,
    )

    val table = arrayOf(
        table_1_1, table_1_2, table_1_3, table_1_4, table_1_5,
        table_2_1, table_2_2, table_2_3, table_2_4, table_2_5,
        table_3_1, table_3_2, table_3_3, table_3_4, table_3_5,
        table_4_1, table_4_2, table_4_3, table_4_4, table_4_5,
        table_5_1, table_5_2, table_5_3, table_5_4, table_5_5,
        table_6_1, table_6_2, table_6_3, table_6_4, table_6_5,
    )

    val sheet = arrayOf(
        sheet_1_1, sheet_1_2, sheet_1_3, sheet_1_4, sheet_1_5,
        sheet_2_1, sheet_2_2, sheet_2_3, sheet_2_4, sheet_2_5,
        sheet_3_1, sheet_3_2, sheet_3_3, sheet_3_4, sheet_3_5,
        sheet_4_1, sheet_4_2, sheet_4_3, sheet_4_4, sheet_4_5,
        sheet_5_1, sheet_5_2, sheet_5_3, sheet_5_4, sheet_5_5,
        sheet_6_1, sheet_6_2, sheet_6_3, sheet_6_4, sheet_6_5
    )

    var Bianhuaweeks by rememberSaveable { mutableStateOf(1) }

    //填充UI与更新
    fun Update() {

        table_1_1 = ""
        table_1_2 = ""
        table_1_3 = ""
        table_1_4 = ""
        table_1_5 = ""
        table_2_1 = ""
        table_2_2 = ""
        table_2_3 = ""
        table_2_4 = ""
        table_2_5 = ""
        table_3_1 = ""
        table_3_2 = ""
        table_3_3 = ""
        table_3_4 = ""
        table_3_5 = ""
        table_4_1 = ""
        table_4_2 = ""
        table_4_3 = ""
        table_4_4 = ""
        table_4_5 = ""
        table_5_1 = ""
        table_5_2 = ""
        table_5_3 = ""
        table_5_4 = ""
        table_5_5 = ""
        //////////////////////////////////////////////////////////////////////////////////
        try {
            val json = SharePrefs.prefs.getString("jsonNext", "")
            val datumResponse = Gson().fromJson(json, datumResponse::class.java)
            val scheduleList = datumResponse.result.scheduleList
            val lessonList = datumResponse.result.lessonList
            val scheduleGroupList = datumResponse.result.scheduleGroupList

            for (i in 0 until scheduleList.size) {
                var starttime = scheduleList[i].startTime.toString()
                starttime =
                    starttime.substring(0, starttime.length - 2) + ":" + starttime.substring(
                        starttime.length - 2
                    )
                var room = scheduleList[i].room.nameZh
                val person = scheduleList[i].personName
                var date = scheduleList[i].date
                var scheduleid = scheduleList[i].lessonId.toString()
                var endtime = scheduleList[i].endTime.toString()
                var periods = scheduleList[i].periods
                var lessonType = scheduleList[i].lessonType

                room = room.replace("学堂","")

                for (k in 0 until scheduleGroupList.size) {

                    val id = scheduleGroupList[k].lessonId.toString()
                    val std = scheduleGroupList[k].stdCount
                    if ( scheduleid == id) {
                        periods = std
                    }
                }

                for (j in 0 until lessonList.size) {
                    val lessonlist_id = lessonList[j].id
                    val INFO = lessonList[j].suggestScheduleWeekInfo
                    val name = lessonList[j].courseName
                    val courseTypeName = lessonList[j].courseTypeName
                    if (scheduleid == lessonlist_id) {
                        scheduleid = name
                        endtime = INFO
                        lessonType = courseTypeName
                    }
                }



                //适配长文字布局
                scheduleid = scheduleid.replace("语言程序设计","程序设计")

                val text = starttime + "\n" + scheduleid + "\n" + room
                val info =
                    "教师:${person}"+ "  "+
                            "周数:${endtime}"+ "  "+
                            "人数:${periods}"+ "  "+
                            "类型:${lessonType}"


                //  Log.d("变化",Bianhuaweeks.toString())

                if (scheduleList[i].weekIndex == Bianhuaweeks) {
                    if (scheduleList[i].weekday == 1) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_1 = text
                            sheet_1_1 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_1 = text
                            sheet_2_1 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_1 = text
                            sheet_3_1 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_1 = text
                            sheet_4_1 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_1 = text
                            sheet_5_1 = info
                        }
                    }
                    if (scheduleList[i].weekday == 2) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_2 = text
                            sheet_1_2 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_2 = text
                            sheet_2_2 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_2 = text
                            sheet_3_2 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_2 = text
                            sheet_4_2 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_2 = text
                            sheet_5_2 = info
                        }
                    }
                    if (scheduleList[i].weekday == 3) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_3 = text
                            sheet_1_3 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_3 = text
                            sheet_2_3 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_3 = text
                            sheet_3_3 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_3 = text
                            sheet_4_3 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_3 = text
                            sheet_5_3 = info
                        }
                    }
                    if (scheduleList[i].weekday == 4) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_4 = text
                            sheet_1_4 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_4 = text
                            sheet_2_4 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_4 = text
                            sheet_3_4 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_4 = text
                            sheet_4_4 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_4 = text
                            sheet_5_4 = info
                        }
                    }
                    if (scheduleList[i].weekday == 5) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_5 = text
                            sheet_1_5 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_5 = text
                            sheet_2_5 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_5 = text
                            sheet_3_5 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_5 = text
                            sheet_4_5 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_5 = text
                            sheet_5_5 = info
                        }
                    }
                }
            }
        } catch (e : Exception) {
            e.printStackTrace()
            Log.d("错误","错误")
        }

    }
    fun UpdateAll() {
        table_1_1 = ""
        table_1_2 = ""
        table_1_3 = ""
        table_1_4 = ""
        table_1_5 = ""
        table_2_1 = ""
        table_2_2 = ""
        table_2_3 = ""
        table_2_4 = ""
        table_2_5 = ""
        table_3_1 = ""
        table_3_2 = ""
        table_3_3 = ""
        table_3_4 = ""
        table_3_5 = ""
        table_4_1 = ""
        table_4_2 = ""
        table_4_3 = ""
        table_4_4 = ""
        table_4_5 = ""
        table_5_1 = ""
        table_5_2 = ""
        table_5_3 = ""
        table_5_4 = ""
        table_5_5 = ""
        table_1_6 = ""
        table_1_7 = ""
        table_2_6 = ""
        table_2_7 = ""
        table_3_6 = ""
        table_3_7 = ""
        table_4_6 = ""
        table_4_7 = ""
        table_5_6 = ""
        table_5_7 = ""
        //////////////////////////////////////////////////////////////////////////////////

        try {
            val json =  SharePrefs.prefs.getString("jsonNext", "")
            // Log.d("测试",json!!)
            val datumResponse = Gson().fromJson(json, datumResponse::class.java)
            val scheduleList = datumResponse.result.scheduleList
            val lessonList = datumResponse.result.lessonList
            val scheduleGroupList = datumResponse.result.scheduleGroupList

            for (i in scheduleList.indices) {
                var starttime = scheduleList[i].startTime.toString()
                starttime =
                    starttime.substring(0, starttime.length - 2) + ":" + starttime.substring(
                        starttime.length - 2
                    )
                var room = scheduleList[i].room.nameZh
                val person = scheduleList[i].personName
                var date = scheduleList[i].date
                var scheduleid = scheduleList[i].lessonId.toString()
                var endtime = scheduleList[i].endTime.toString()
                var periods = scheduleList[i].periods
                var lessonType = scheduleList[i].lessonType

                room = room.replace("学堂","")

                for (k in 0 until scheduleGroupList.size) {

                    val id = scheduleGroupList[k].lessonId.toString()
                    val std = scheduleGroupList[k].stdCount
                    if ( scheduleid == id) {
                        periods = std
                    }
                }

                for (j in 0 until lessonList.size) {
                    val lessonlist_id = lessonList[j].id
                    val INFO = lessonList[j].suggestScheduleWeekInfo
                    val name = lessonList[j].courseName
                    val courseTypeName = lessonList[j].courseTypeName
                    if (scheduleid == lessonlist_id) {
                        scheduleid = name
                        endtime = INFO
                        lessonType = courseTypeName
                    }
                }



                //适配长文字布局
                scheduleid = scheduleid.replace("语言程序设计","程序设计")

                val text = starttime + "\n" + scheduleid + "\n" + room
                val info =
                    "教师:${person}"+ "  "+
                            "周数:${endtime}"+ "  "+
                            "人数:${periods}"+ "  "+
                            "类型:${lessonType}"


                //  Log.d("变化",Bianhuaweeks.toString())

                if (scheduleList[i].weekIndex == Bianhuaweeks.toInt()) {
                    when(scheduleList[i].weekday) {
                        6 -> { Handler(Looper.getMainLooper()).post { vmUI.findNewCourse.value = text.isNotEmpty() } }
                        7 -> { Handler(Looper.getMainLooper()).post { vmUI.findNewCourse.value = text.isNotEmpty() } }
                    }
                    if (scheduleList[i].weekday == 1) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_1 = text
                            sheet_1_1 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_1 = text
                            sheet_2_1 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_1 = text
                            sheet_3_1 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_1 = text
                            sheet_4_1 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_1 = text
                            sheet_5_1 = info
                        }
                    }
                    if (scheduleList[i].weekday == 2) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_2 = text
                            sheet_1_2 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_2 = text
                            sheet_2_2 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_2 = text
                            sheet_3_2 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_2 = text
                            sheet_4_2 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_2 = text
                            sheet_5_2 = info
                        }
                    }
                    if (scheduleList[i].weekday == 3) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_3 = text
                            sheet_1_3 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_3 = text
                            sheet_2_3 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_3 = text
                            sheet_3_3 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_3 = text
                            sheet_4_3 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_3 = text
                            sheet_5_3 = info
                        }
                    }
                    if (scheduleList[i].weekday == 4) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_4 = text
                            sheet_1_4 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_4 = text
                            sheet_2_4 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_4 = text
                            sheet_3_4 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_4 = text
                            sheet_4_4 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_4 = text
                            sheet_5_4 = info
                        }
                    }
                    if (scheduleList[i].weekday == 5) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_5 = text
                            sheet_1_5 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_5 = text
                            sheet_2_5 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_5 = text
                            sheet_3_5 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_5 = text
                            sheet_4_5 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_5 = text
                            sheet_5_5 = info
                        }
                    }
                    if (scheduleList[i].weekday == 6) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_6 = text
                            sheet_1_6 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_6 = text
                            sheet_2_6 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_6 = text
                            sheet_3_6 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_6 = text
                            sheet_4_6 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_6 = text
                            sheet_5_6 = info
                        }
                    }
                    if (scheduleList[i].weekday == 7) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_7 = text
                            sheet_1_7 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_7 = text
                            sheet_2_7 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_7 = text
                            sheet_3_7 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_7 = text
                            sheet_4_7 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_7 = text
                            sheet_5_7 = info
                        }
                    }
                }
            }
        } catch (e : Exception) {
            e.printStackTrace()
         //   Log.d("错误ALL","错误")
        }

    }
    Column {

        /*
        * LazyVerticalGrid(columns = GridCells.Fixed(if(showAll)7 else 5),modifier = Modifier.padding(horizontal = 10.dp)){
            items(if(showAll)7 else 5) { item ->
                if (GetDate.Benweeks > 0)
                    Text(
                        text = mondayOfCurrentWeek.plusDays(item.toLong()).toString()
                            .substringAfter("-") ,
                        textAlign = TextAlign.Center,
                        fontSize = if(showAll)12.sp else 14.sp,
                        color = Color.Gray
                    )
                else Text(
                    text = "未开学",
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontSize = if(showAll)12.sp else 14.sp
                )
            }
        }*/

        Box( modifier = Modifier
            .fillMaxHeight()
        ) {
            val scrollstate = rememberLazyGridState()
            val shouldShowAddButton by remember { derivedStateOf { scrollstate.firstVisibleItemScrollOffset == 0 } }

            LazyVerticalGrid(
                columns = GridCells.Fixed(if(showAll)7 else 5),
                modifier = Modifier.padding(10.dp),
                state = scrollstate
            ) {
                items(if(showAll)42 else 30) { cell ->
                    Card(
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier
                            .height(125.dp)
                            .padding(if (showAll) 1.dp else 2.dp)
                            .clickable {
                                if ((if (showAll) sheetall[cell] else sheet[cell]) != "")
                                    MyToast(if (showAll) sheetall[cell] else sheet[cell])
                                else MyToast("空数据")
                            }
                    ) {
                        Text(text = if(showAll)tableall[cell] else table[cell],fontSize = if(showAll)12.sp else 14.sp, textAlign = TextAlign.Center)
                    }
                }
                item {  Spacer(modifier = androidx.compose.ui.Modifier.height(innerPadding.calculateBottomPadding())) }
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = shouldShowAddButton,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(innerPadding)
                    .padding(horizontal = 15.dp, vertical = 15.dp)
            ) {
                if (shouldShowAddButton) {
                    FloatingActionButton(
                        onClick = {
                            if (Bianhuaweeks > 1) {
                                Bianhuaweeks-- - 1
                                if(showAll) UpdateAll() else Update()
                                //onDateChange(today.minusDays(7))
                            }
                        },
                    ) { Icon(Icons.Filled.ArrowBack, "Add Button") }
                }
            }


            androidx.compose.animation.AnimatedVisibility(
                visible = shouldShowAddButton,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(innerPadding)
                    .padding(horizontal = 15.dp, vertical = 15.dp)
            ) {
                if (shouldShowAddButton) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            Bianhuaweeks = 1
                            if(showAll) UpdateAll() else Update()
                            //onDateChange(LocalDate.now())
                        },
                    ) {
                        AnimatedContent(
                            targetState = Bianhuaweeks,
                            transitionSpec = {  scaleIn(animationSpec = tween(500)
                            ) with scaleOut(animationSpec = tween(500))
                            }, label = ""
                        ){annumber ->
                            Text(text = "第 $annumber 周",)
                        }
                    }
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = shouldShowAddButton,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(innerPadding)
                    .padding(horizontal = 15.dp, vertical = 15.dp)
            ) {
                if (shouldShowAddButton) {
                    FloatingActionButton(
                        onClick = {
                            if (Bianhuaweeks < 20) {
                                Bianhuaweeks++ + 1
                                if(showAll) UpdateAll() else Update()
                                //onDateChange(today.plusDays(7))
                            }
                        },
                    ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
                }
            }
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}