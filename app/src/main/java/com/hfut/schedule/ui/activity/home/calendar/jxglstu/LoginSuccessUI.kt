package com.hfut.schedule.ui.activity.home.calendar.jxglstu

import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import com.hfut.schedule.ui.utils.LoadingUI
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.LoginViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.logic.beans.Community.LoginCommunityResponse
import com.hfut.schedule.logic.beans.Jxglstu.datumResponse
import com.hfut.schedule.logic.utils.DateTimeManager
import com.hfut.schedule.logic.utils.Semseter.getSemseter
import com.hfut.schedule.logic.utils.Semseter.getSemseterCloud
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.saveInt
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.activity.home.calendar.communtiy.CourseDetailApi
import com.hfut.schedule.ui.activity.home.calendar.examToCalendar
import com.hfut.schedule.ui.activity.home.calendar.getScheduleDate
import com.hfut.schedule.ui.activity.home.calendar.next.parseCourseName
import com.hfut.schedule.ui.activity.home.main.saved.isNextOpen
import com.hfut.schedule.ui.activity.home.search.functions.totalCourse.getTotalCourse
import com.hfut.schedule.ui.utils.MyToast
import com.hfut.schedule.ui.utils.Round
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class, ExperimentalAnimationApi::class)
@SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition")
@Composable
fun CalendarScreen(showAll : Boolean,
                   vm : NetWorkViewModel,
                   grade : String,
                   innerPadding : PaddingValues,
                   vmUI : UIViewModel,
                   webVpn : Boolean,
                   vm2 : LoginViewModel,
                   load : Boolean,
                   onDateChange : (LocalDate) ->Unit,
                   today: LocalDate) {


    val sheetState_totalCourse = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_totalCourse by remember { mutableStateOf(false) }
    var courseName by remember { mutableStateOf("") }
    if (showBottomSheet_totalCourse) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_totalCourse = false
            },
            sheetState = sheetState_totalCourse,
            shape = Round(sheetState_totalCourse)
        ) {
            CourseDetailApi(courseName = courseName, vm = vm)
        }
    }

    var loading by remember { mutableStateOf(load) }

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


//    var sheet_1_1 by rememberSaveable { mutableStateOf("") }
//    var sheet_1_2 by rememberSaveable { mutableStateOf("") }
//    var sheet_1_3 by rememberSaveable { mutableStateOf("") }
//    var sheet_1_4 by rememberSaveable { mutableStateOf("") }
//    var sheet_1_5 by rememberSaveable { mutableStateOf("") }
//    var sheet_2_1 by rememberSaveable { mutableStateOf("") }
//    var sheet_2_2 by rememberSaveable { mutableStateOf("") }
//    var sheet_2_3 by rememberSaveable { mutableStateOf("") }
//    var sheet_2_4 by rememberSaveable { mutableStateOf("") }
//    var sheet_2_5 by rememberSaveable { mutableStateOf("") }
//    var sheet_3_1 by rememberSaveable { mutableStateOf("") }
//    var sheet_3_2 by rememberSaveable { mutableStateOf("") }
//    var sheet_3_3 by rememberSaveable { mutableStateOf("") }
//    var sheet_3_4 by rememberSaveable { mutableStateOf("") }
//    var sheet_3_5 by rememberSaveable { mutableStateOf("") }
//    var sheet_4_1 by rememberSaveable { mutableStateOf("") }
//    var sheet_4_2 by rememberSaveable { mutableStateOf("") }
//    var sheet_4_3 by rememberSaveable { mutableStateOf("") }
//    var sheet_4_4 by rememberSaveable { mutableStateOf("") }
//    var sheet_4_5 by rememberSaveable { mutableStateOf("") }
//    var sheet_5_1 by rememberSaveable { mutableStateOf("") }
//    var sheet_5_2 by rememberSaveable { mutableStateOf("") }
//    var sheet_5_3 by rememberSaveable { mutableStateOf("") }
//    var sheet_5_4 by rememberSaveable { mutableStateOf("") }
//    var sheet_5_5 by rememberSaveable { mutableStateOf("") }
//    var sheet_6_1 by rememberSaveable { mutableStateOf("") }
//    var sheet_6_2 by rememberSaveable { mutableStateOf("") }
//    var sheet_6_3 by rememberSaveable { mutableStateOf("") }
//    var sheet_6_4 by rememberSaveable { mutableStateOf("") }
//    var sheet_6_5 by rememberSaveable { mutableStateOf("") }
//    var sheet_1_6 by rememberSaveable { mutableStateOf("") }
//    var sheet_1_7 by rememberSaveable { mutableStateOf("") }
//    var sheet_2_6 by rememberSaveable { mutableStateOf("") }
//    var sheet_2_7 by rememberSaveable { mutableStateOf("") }
//    var sheet_3_6 by rememberSaveable { mutableStateOf("") }
//    var sheet_3_7 by rememberSaveable { mutableStateOf("") }
//    var sheet_4_6 by rememberSaveable { mutableStateOf("") }
//    var sheet_4_7 by rememberSaveable { mutableStateOf("") }
//    var sheet_5_6 by rememberSaveable { mutableStateOf("") }
//    var sheet_5_7 by rememberSaveable { mutableStateOf("") }
//    var sheet_6_6 by rememberSaveable { mutableStateOf("") }
//    var sheet_6_7 by rememberSaveable { mutableStateOf("") }


    val tableall = arrayOf(
        table_1_1, table_1_2, table_1_3, table_1_4, table_1_5,table_1_6,table_1_7,
        table_2_1, table_2_2, table_2_3, table_2_4, table_2_5,table_2_6,table_2_7,
        table_3_1, table_3_2, table_3_3, table_3_4, table_3_5,table_3_6,table_3_7,
        table_4_1, table_4_2, table_4_3, table_4_4, table_4_5,table_4_6,table_4_7,
        table_5_1, table_5_2, table_5_3, table_5_4, table_5_5,table_5_6,table_5_7,
        table_6_1, table_6_2, table_6_3, table_6_4, table_6_5,table_6_6,table_6_7,
    )
//
//    val sheetall = arrayOf(
//        sheet_1_1, sheet_1_2, sheet_1_3, sheet_1_4, sheet_1_5,sheet_1_6,sheet_1_7,
//        sheet_2_1, sheet_2_2, sheet_2_3, sheet_2_4, sheet_2_5,sheet_2_6,sheet_2_7,
//        sheet_3_1, sheet_3_2, sheet_3_3, sheet_3_4, sheet_3_5,sheet_3_6,sheet_3_7,
//        sheet_4_1, sheet_4_2, sheet_4_3, sheet_4_4, sheet_4_5,sheet_4_6,sheet_4_7,
//        sheet_5_1, sheet_5_2, sheet_5_3, sheet_5_4, sheet_5_5,sheet_5_6,sheet_5_7,
//        sheet_6_1, sheet_6_2, sheet_6_3, sheet_6_4, sheet_6_5,sheet_6_6,sheet_6_7,
//    )

    val table = arrayOf(
        table_1_1, table_1_2, table_1_3, table_1_4, table_1_5,
        table_2_1, table_2_2, table_2_3, table_2_4, table_2_5,
        table_3_1, table_3_2, table_3_3, table_3_4, table_3_5,
        table_4_1, table_4_2, table_4_3, table_4_4, table_4_5,
        table_5_1, table_5_2, table_5_3, table_5_4, table_5_5,
        table_6_1, table_6_2, table_6_3, table_6_4, table_6_5,
    )

//    val sheet = arrayOf(
//        sheet_1_1, sheet_1_2, sheet_1_3, sheet_1_4, sheet_1_5,
//        sheet_2_1, sheet_2_2, sheet_2_3, sheet_2_4, sheet_2_5,
//        sheet_3_1, sheet_3_2, sheet_3_3, sheet_3_4, sheet_3_5,
//        sheet_4_1, sheet_4_2, sheet_4_3, sheet_4_4, sheet_4_5,
//        sheet_5_1, sheet_5_2, sheet_5_3, sheet_5_4, sheet_5_5,
//        sheet_6_1, sheet_6_2, sheet_6_3, sheet_6_4, sheet_6_5
//    )
  //  var showAlls by remember { mutableStateOf(false) }
    //showAlls = showAll


    var Bianhuaweeks by rememberSaveable { mutableStateOf(
        if(DateTimeManager.weeksBetween > 20) {
            getNewWeek()
        } else DateTimeManager.weeksBetween
    ) }


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
            val json = prefs.getString("json", "")
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


                val text = starttime + "\n" + scheduleid + "\n" + room
                val info =
                    "教师:${person}"+ "  "+
                            "周数:${endtime}"+ "  "+
                            "人数:${periods}"+ "  "+
                            "类型:${lessonType}"


                //  Log.d("变化",Bianhuaweeks.toString())

                if (scheduleList[i].weekIndex == Bianhuaweeks.toInt()) {
                    if (scheduleList[i].weekday == 1) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_1 = text
//                            sheet_1_1 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_1 = text
//                            sheet_2_1 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_1 = text
//                            sheet_3_1 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_1 = text
//                            sheet_4_1 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_1 = text
//                            sheet_5_1 = info
                        }
                    }
                    if (scheduleList[i].weekday == 2) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_2 = text
//                            sheet_1_2 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_2 = text
//                            sheet_2_2 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_2 = text
//                            sheet_3_2 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_2 = text
//                            sheet_4_2 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_2 = text
//                            sheet_5_2 = info
                        }
                    }
                    if (scheduleList[i].weekday == 3) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_3 = text
//                            sheet_1_3 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_3 = text
//                            sheet_2_3 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_3 = text
//                            sheet_3_3 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_3 = text
//                            sheet_4_3 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_3 = text
//                            sheet_5_3 = info
                        }
                    }
                    if (scheduleList[i].weekday == 4) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_4 = text
//                            sheet_1_4 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_4 = text
//                            sheet_2_4 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_4 = text
//                            sheet_3_4 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_4 = text
//                            sheet_4_4 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_4 = text
//                            sheet_5_4 = info
                        }
                    }
                    if (scheduleList[i].weekday == 5) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_5 = text
//                            sheet_1_5 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_5 = text
//                            sheet_2_5 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_5 = text
//                            sheet_3_5 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_5 = text
//                            sheet_4_5 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_5 = text
//                            sheet_5_5 = info
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
            val json =  prefs.getString("json", "")
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
//                            sheet_1_1 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_1 = text
//                            sheet_2_1 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_1 = text
//                            sheet_3_1 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_1 = text
//                            sheet_4_1 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_1 = text
//                            sheet_5_1 = info
                        }
                    }
                    if (scheduleList[i].weekday == 2) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_2 = text
//                            sheet_1_2 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_2 = text
//                            sheet_2_2 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_2 = text
//                            sheet_3_2 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_2 = text
//                            sheet_4_2 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_2 = text
//                            sheet_5_2 = info
                        }
                    }
                    if (scheduleList[i].weekday == 3) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_3 = text
//                            sheet_1_3 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_3 = text
//                            sheet_2_3 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_3 = text
//                            sheet_3_3 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_3 = text
//                            sheet_4_3 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_3 = text
//                            sheet_5_3 = info
                        }
                    }
                    if (scheduleList[i].weekday == 4) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_4 = text
//                            sheet_1_4 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_4 = text
//                            sheet_2_4 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_4 = text
//                            sheet_3_4 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_4 = text
//                            sheet_4_4 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_4 = text
//                            sheet_5_4 = info
                        }
                    }
                    if (scheduleList[i].weekday == 5) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_5 = text
//                            sheet_1_5 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_5 = text
//                            sheet_2_5 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_5 = text
//                            sheet_3_5 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_5 = text
//                            sheet_4_5 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_5 = text
//                            sheet_5_5 = info
                        }
                    }
                    if (scheduleList[i].weekday == 6) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_6 = text
//                            sheet_1_6 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_6 = text
//                            sheet_2_6 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_6 = text
//                            sheet_3_6 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_6 = text
//                            sheet_4_6 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_6 = text
//                            sheet_5_6 = info
                        }
                    }
                    if (scheduleList[i].weekday == 7) {
                        if (scheduleList[i].startTime == 800) {
                            table_1_7 = text
//                            sheet_1_7 = info
                        }
                        if (scheduleList[i].startTime == 1010) {
                            table_2_7 = text
//                            sheet_2_7 = info
                        }
                        if (scheduleList[i].startTime == 1400) {
                            table_3_7 = text
//                            sheet_3_7 = info
                        }
                        if (scheduleList[i].startTime == 1600) {
                            table_4_7 = text
//                            sheet_4_7 = info
                        }
                        if (scheduleList[i].startTime == 1900) {
                            table_5_7 = text
//                            sheet_5_7 = info
                        }
                    }
                }
            }
        } catch (e : Exception) {
            e.printStackTrace()
            Log.d("错误ALL","错误")
        }

    }
//////////////////////////////////////////////////////////////////////////////////

   if(load) {
        val cookie = if (!webVpn) prefs.getString(
            "redirect",
            ""
        ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")
        var num2 = 1
        // val grade = intent.getStringExtra("Grade")
        val ONE = prefs.getString("ONE", "")
        val TGC = prefs.getString("TGC", "")
        val cardvalue = prefs.getString("borrow", "")
        val cookies = "$ONE;$TGC"
        val ticket = prefs.getString("TICKET", "")
        // val jsons = prefs.getString("LoginCommunity",MyApplication.NullLoginCommunity)
        val CommuityTOKEN = prefs.getString("TOKEN", "")
        var a by rememberSaveable { mutableStateOf(0) }
        val job = Job()
        val job2 = Job()
        val scope = CoroutineScope(job)

       if(!webVpn) {
           CoroutineScope(job2).launch {
               val token = prefs.getString("bearer", "")

               //检测若登陆成功（200）则解析出CommunityTOKEN
               val LoginCommunityObserver = Observer<String?> { result ->
                   if (result != null) {
                       if (result.contains("200") && result.contains("token")) {
                           val result = Gson().fromJson(result, LoginCommunityResponse::class.java)
                           val token = result.result.token
                           SharePrefs.saveString("TOKEN", token)
                           if (num2 == 1) {
                               MyToast("Community登陆成功")
                               num2++
                           }
                       }
                   }
               }

               //检测CommunityTOKEN的可用性
               val ExamObserver = Observer<Int> { result ->
                   Log.d("result",(result == 500).toString())
                   if (result == 500) {
                       CoroutineScope(Job()).async {
                           async { vm.GotoCommunity(cookies) }.await()
                           async {
                               delay(1000)
                               ticket?.let { vm.LoginCommunity(it) }
                           }.await()
                           async {
                               Handler(Looper.getMainLooper()).post {
                                   vm.LoginCommunityData.observeForever(
                                       LoginCommunityObserver
                                   )
                               }
                           }
                       }
                   }
               }

               //获取慧新易校
               // val AuthObserver = Observer<String?> { result ->
               //    if (result != null) {
               //        if(result.contains("成功")) MyToast("一卡通登陆成功")
               //          else
               //       }
               //     }


               //检测慧新易校可用性
               val auth = prefs.getString("auth", "")
               if (prefs.getString("auth", "") == "") vm.OneGotoCard("$ONE;$TGC")

               async { vm.OneGotoCard("$ONE;$TGC") }
               async { CommuityTOKEN?.let { vm.Exam(it) } }

               Handler(Looper.getMainLooper()).post { vm.ExamCodeData.observeForever(ExamObserver) }

               //慧新易校获取TOKEN
               //val liushui = prefs.getString("cardliushui", MyApplication.NullBill)
               //if (liushui != null) {


               //
               ///}

               //登录信息门户的接口,还没做重构（懒）
               if (token != null) {
                   if (token.contains("AT") && cardvalue != "未获取") {
                       async { vm.getSubBooks("Bearer $token") }
                       async { vm.getBorrowBooks("Bearer $token") }
                   } else {
                       async {
                           async { vm.OneGoto(cookies) }.await()
                           async {
                               delay(500)
                               vm.getToken()
                           }.await()
                       }
                   }
               }
           }
       }

        val nextBoolean = isNextOpen()
        if (nextBoolean) saveInt("FIRST", 1)


        scope.launch {
//加载其他教务信息////////////////////////////////////////////////////////////////////////////////////////////////////
            // async {
            //   if(webVpn) {
            //     prefs.getString("Password","")?.let { prefs.getString("Username","")?.let { it1 -> LoginClick(vm2, it1, it,true) } }
            //   delay(200)
            // }
            // }.await()
            async {
                val studentIdObserver = Observer<Int> { result ->
                    if (result != 0) {
                        //Log.d("result",result.toString())
                        SharePrefs.saveString("studentId", result.toString())
                        CoroutineScope(Job()).launch {
                            async {
                                grade?.let {
                                    vm.getLessonIds(
                                        cookie!!,
                                        it,
                                        result.toString()
                                    )
                                }
                            }
                            if (nextBoolean) {
                                async {
                                    grade?.let {
                                        vm.getLessonIdsNext(
                                            cookie!!,
                                            it,
                                            result.toString()
                                        )
                                    }
                                }
                            }
                            async { vm.getInfo(cookie!!) }
                            if(prefs.getString("photo","") == null || prefs.getString("photo","") == "")
                            async { cookie?.let { vm.getPhoto(it) } }
                        }
                    } else {
                        ///Log.d("result0",result.toString())
                        /*

                        val studentid = prefs.getInt("STUDENTID",99999)
                         if(studentid != 0) {
                             CoroutineScope(Job()).launch {
                                 async { grade?.let { vm.getLessonIds(cookie!!, it, studentid.toString()) } }
                                 if(nextBoolean) { async { grade?.let { vm.getLessonIdsNext(cookie!!, it, studentid.toString()) }  } }

                                 async { vm.getInfo(cookie!!) }
                                 async { cookie?.let { vm.getPhoto(it) } }
                                 async { vm.getProgram(cookie!!) }
                             }
                         }
                      */
                    }
                }

                val lessonIdObserver = Observer<List<Int>> { result ->
                    if (result.toString() != "") {
                        val lessonIdsArray = JsonArray()
                        result?.forEach { lessonIdsArray.add(JsonPrimitive(it)) }
                        val jsonObject = JsonObject().apply {
                            add("lessonIds", lessonIdsArray)//课程ID
                            addProperty("studentId", vm.studentId.value)//学生ID
                            addProperty("weekIndex", "")
                        }
                        vm.getDatum(cookie!!, jsonObject)
                        vm.studentId.removeObserver(studentIdObserver)
                    }
                }
                val lessonIdObserverNext = Observer<List<Int>> { result ->
                    if (result.toString() != "") {
                        val lessonIdsArray = JsonArray()
                        result?.forEach { lessonIdsArray.add(JsonPrimitive(it)) }
                        val jsonObject = JsonObject().apply {
                            add("lessonIds", lessonIdsArray)//课程ID
                            addProperty("studentId", vm.studentId.value)//学生ID
                            addProperty("weekIndex", "")
                        }
                        vm.getDatumNext(cookie!!, jsonObject)
                        // vm.lessonIdsNext.removeObserver(lessonIdObserver)
                    }
                }
                val datumObserver = Observer<String?> { result ->
                    if (result != null) {
                        if (result.contains("result")) {
                            CoroutineScope(Job()).launch {
                                async { if (showAll) UpdateAll() else Update() }.await()
                                async {
                                    Handler(Looper.getMainLooper()).post {
                                        vm.lessonIds.removeObserver(
                                            lessonIdObserver
                                        )
                                    }
                                }
                                async {
                                    delay(200)
                                    a++
                                    loading = false
                                }
                            }
                        } else MyToast("数据为空,尝试刷新")
                    }
                }

                async { vm.getStudentId(cookie!!) }.await()

                Handler(Looper.getMainLooper()).post {
                    vm.studentId.observeForever(studentIdObserver)
                    vm.lessonIds.observeForever(lessonIdObserver)
                    vm.datumData.observeForever(datumObserver)
                    if (nextBoolean)
                        vm.lessonIdsNext.observeForever(lessonIdObserverNext)
                }
            }
        }

        if (a > 0) job.cancel()
        if (prefs.getString("tip", "0") != "0") loading = false
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //var today by rememberSaveable { mutableStateOf(LocalDate.now()) }
    //val mondayOfCurrentWeek = today.minusDays(today.dayOfWeek.value - 1L)

    val dateList  = getScheduleDate(showAll,today)
    val examList  = examToCalendar()

        Column(
            modifier = Modifier
               // .padding(innerPadding)
                .fillMaxSize()
        ) {
       //     Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
         //   Spacer(modifier = Modifier.height(5.dp))

            AnimatedVisibility(
                visible = loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column() { LoadingUI() }
                }
            }//加载动画居中，3s后消失

            AnimatedVisibility(
                visible = !loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                //在这里插入课程表布局
                Column {
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
                            items(if(showAll)7 else 5) { Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding())) }
                            items(if(showAll)42 else 30) { cell ->
                                var texts = if(showAll)tableall[cell] else table[cell]
                                Card(
                                    shape = MaterialTheme.shapes.extraSmall,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                                    modifier = Modifier
                                        .height(125.dp)
                                        .padding(if (showAll) 1.dp else 2.dp)
                                        .clickable {
//                                            if ((if (showAll) sheetall[cell] else sheet[cell]) != "")
//                                                MyToast(if (showAll) sheetall[cell] else sheet[cell])
//                                            else MyToast("空数据")
                                            val name = parseCourseName(if(showAll)tableall[cell] else table[cell])
                                            if(name != null) {
                                                courseName = name
//                                                Log.d("课程",courseName)
                                                showBottomSheet_totalCourse = true
                                            }
                                        }
                                ) {
                                    //存在待考时
                                    if(examList.isNotEmpty()){
                                        val numa = if(showAll) 7 else 5
                                        val i = cell % numa
                                        val j = cell / numa
                                        val date = dateList[i]
                                        examList.forEach {
                                            if(date == it.day) {
                                                val hour = it.startTime?.substringBefore(":")?.toIntOrNull() ?: 99

                                                if(hour in 7..9 && j == 0) {
                                                    texts = it.startTime + "\n" + it.course + "\n" + it.place
                                                } else if(hour in 10..12 && j == 1) {
                                                    texts = it.startTime + "\n" + it.course + "\n" + it.place
                                                } else if(hour in 14..15  && j == 2) {
                                                    texts = it.startTime + "\n" + it.course + "\n" + it.place
                                                } else if(hour in 16..17  && j == 3) {
                                                    texts = it.startTime + "\n" + it.course + "\n" + it.place
                                                } else if(hour >= 18  && j == 4) {
                                                    texts = it.startTime + "\n" + it.course + "\n" + it.place
                                                }
                                            }
                                        }
                                    }
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState())
                                        //.padding(8.dp)
                                    ) {
                                        Text(text = texts,fontSize = if(showAll)12.sp else 14.sp, textAlign = TextAlign.Center)
                                    }
                                }
                            }
                            item {  Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding())) }
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
                                            onDateChange(today.minusDays(7))
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
                                        Bianhuaweeks = DateTimeManager.Benweeks
                                        if(showAll) UpdateAll() else Update()
                                        onDateChange(LocalDate.now())
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
                            visible = !shouldShowAddButton,
                            enter = scaleIn(),
                            exit = scaleOut(),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(innerPadding)
                                .padding(horizontal = 15.dp, vertical = 15.dp)
                        ) {
                            TextButton(onClick = {  }) {
                                Text(
                                    text = getSemseter(getSemseterCloud()),
                                    style = TextStyle(shadow = Shadow(
                                        color = Color.Gray,
                                        offset = Offset(5.0f,5.0f),
                                        blurRadius = 10.0f
                                    )
                                    )
                                )
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
                                            onDateChange(today.plusDays(7))
                                        }
                                    },
                                ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

}

@RequiresApi(Build.VERSION_CODES.O)
fun getNewWeek() : Long {
    return try {
        val jxglstuJson = prefs.getString("courses","")
        val resultJxglstu = getTotalCourse(jxglstuJson)[0].semester.startDate
        val firstWeekStartJxglstu: LocalDate = LocalDate.parse(resultJxglstu)
        val weeksBetweenJxglstu = ChronoUnit.WEEKS.between(firstWeekStartJxglstu, DateTimeManager.today) + 1
        weeksBetweenJxglstu  //固定本周
    } catch (e : Exception) {
        DateTimeManager.Benweeks
    }
}

