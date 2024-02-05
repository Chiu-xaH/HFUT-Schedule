package com.hfut.schedule.ui.ComposeUI.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.datamodel.Community.LoginCommunityResponse
import com.hfut.schedule.logic.datamodel.Community.courseDetailDTOList
import com.hfut.schedule.logic.datamodel.Jxglstu.datumResponse
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.UIUtils.MyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class, ExperimentalAnimationApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun CalendarScreen(vm : LoginSuccessViewModel,grade : String) {

    var loading by remember { mutableStateOf(true) }

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

    var showAll by remember { mutableStateOf(false) }

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




    var Bianhuaweeks by rememberSaveable { mutableStateOf(GetDate.weeksBetween) }


    val dayweek = GetDate.dayweek


    var chinesenumber  = GetDate.chinesenumber

    when (dayweek) {
        1 -> chinesenumber = "一"
        2 -> chinesenumber = "二"
        3 -> chinesenumber = "三"
        4 -> chinesenumber = "四"
        5 -> chinesenumber = "五"
        6 ->  chinesenumber = "六"
        0 ->  chinesenumber = "日"
    }
    //填充UI与更新
    fun Update() {
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
        val json = prefs.getString("json", "")
        // Log.d("测试",json!!)
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

            if (scheduleList[i].weekIndex == Bianhuaweeks.toInt()) {
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
    }
    fun UpdateAll(data : Boolean) {
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

        val json = if(!data) prefs.getString("json", "") else vm.datumData.value
        // Log.d("测试",json!!)
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

            if (scheduleList[i].weekIndex == Bianhuaweeks.toInt()) {
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
    }
//////////////////////////////////////////////////////////////////////////////////
    val cookie = prefs.getString("redirect", "")

   // val grade = intent.getStringExtra("Grade")
    val ONE = prefs.getString("ONE","")
    val TGC = prefs.getString("TGC","")
    val cardvalue = prefs.getString("borrow","")
    val cookies = ONE + ";" +TGC
    val ticket = prefs.getString("TICKET","")
    val jsons = prefs.getString("LoginCommunity",MyApplication.NullLoginCommunity)
    val job = Job()
    val scope = CoroutineScope(job)
    scope.apply {
        launch {

            launch {
                val token = prefs.getString("bearer", "")
                // token?.let { Log.d("token", it) }

                val liushui = prefs.getString("cardliushui", MyApplication.NullBill)
                if (liushui != null) {
                    if (prefs.getString(
                            "auth",
                            ""
                        ) == null || liushui.contains("操作成功") == false
                    ) {
                        val ONE = prefs.getString("ONE", "")
                        val TGC = prefs.getString("TGC", "")
                        vm.OneGotoCard(ONE + ";" + TGC)
                    }
                }
                val CommuityTOKEN = prefs.getString("TOKEN", "")

                if (CommuityTOKEN != null) {
                    if (CommuityTOKEN.contains("ey") == false) {
                        async {
                            async { vm.GotoCommunity(cookies) }.await()
                            async {
                                delay(1000)
                                ticket?.let { vm.LoginCommunity(it) }
                            }.await()
                            async {
                                delay(1000)
                                if (jsons != null) {
                                    if (jsons.contains("200")) {
                                        val result = Gson().fromJson(
                                            jsons,
                                            LoginCommunityResponse::class.java
                                        )
                                        val token = result.result.token
                                        SharePrefs.Save("TOKEN", token)
                                        //   vm.CommuityTOKEN?.let { Log.d("sss", it) }
                                    }
                                }
                            }
                        }
                    }
                }



                if (token != null) {
                    if (token.contains("AT") && cardvalue != "未获取") {
                        // async { vm.getCard("Bearer $token") }
                        async { vm.getSubBooks("Bearer $token") }
                        async { vm.getBorrowBooks("Bearer $token") }

                    } else {
                        async {
                            async { vm.OneGoto(cookies) }.await()
                            async {
                                delay(500)
                                vm.getToken()
                            }.await()
                            launch {
                                delay(2900)
                                //  async { vm.getCard("Bearer " + vm.token.value) }
                                async { vm.getBorrowBooks("Bearer " + vm.token.value) }
                                async { vm.getSubBooks("Bearer " + vm.token.value) }
                            }
                        }
                    }
                }
            }

//加载其他信息/////////////////////////////////////////////////////
            if (prefs.getString("tip", "0") == "0") {
                launch {
                    val studentIdObserver = Observer<Int> { result ->
                        if (result != 99999) {
                            SharePrefs.Save("studentId", result.toString())
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
                                async { vm.getInfo(cookie!!) }
                                async { vm.getProgram(cookie!!) }
                            }
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
                    val datumObserver = Observer<String?> { result ->
                        if (result != null) {
                            if (result.contains("result")) {
                                CoroutineScope(Job()).launch {
                                    async { UpdateAll(true) }.await()
                                    async {
                                        Handler(Looper.getMainLooper()).post {
                                            vm.lessonIds.removeObserver(
                                                lessonIdObserver
                                            )
                                        }
                                    }
                                    async {
                                        delay(200)
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
                    }
                }
            } else {
                launch {
                    async { UpdateAll(false) }.await()
                    async {
                        delay(200)
                        loading = false
                    }
                }
            }
        }
    }


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

    var today by rememberSaveable { mutableStateOf(LocalDate.now()) }
    val mondayOfCurrentWeek = today.minusDays(today.dayOfWeek.value - 1L)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("今天  第${GetDate.Benweeks}周  周${chinesenumber}  ${GetDate.Date_MM_dd}") },
                actions = {
                    IconButton(onClick = { showAll = !showAll }) { Icon(painter = painterResource(id = if (showAll) R.drawable.collapse_content else R.drawable.expand_content), contentDescription = "") }
                }
              //  actions = { IconButton(onClick = {}) { Icon(Icons.Filled.Refresh, contentDescription = "主页") } }
            )
        },) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            AnimatedVisibility(
                visible = loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column() { CircularProgressIndicator() }
                }
            }//加载动画居中，3s后消失

            AnimatedVisibility(
                visible = !loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                //在这里插入课程表布局


                LazyVerticalGrid(columns = GridCells.Fixed(5),modifier = Modifier.padding(horizontal = 10.dp)){
                    items(5) { item ->
                        if (GetDate.Benweeks > 0)
                            Text(
                                text = mondayOfCurrentWeek.plusDays(item.toLong()).toString()
                                    .substringAfter("-") ,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        else Text(
                            text = "未开学",
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }


                Box( modifier = Modifier
                    .fillMaxHeight()
                ) {
                    val scrollstate = rememberLazyGridState()
                    val shouldShowAddButton = scrollstate.firstVisibleItemScrollOffset == 0

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
                                            MyToast(if(showAll)sheetall[cell] else sheet[cell])
                                        else MyToast("空数据")
                                    }
                            ) {
                                Text(text = if(showAll)tableall[cell] else table[cell],fontSize = if(showAll)12.sp else 14.sp, textAlign = TextAlign.Center)
                            }
                        }
                    }
                    androidx.compose.animation.AnimatedVisibility(
                        visible = shouldShowAddButton,
                        enter = scaleIn(),
                        exit = scaleOut(),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 15.dp, vertical = 100.dp)
                    ) {
                        if (shouldShowAddButton) {
                            FloatingActionButton(
                                onClick = {
                                    if (Bianhuaweeks > 1) {
                                        Bianhuaweeks-- - 1
                                        today = today.minusDays(7)
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
                            .padding(horizontal = 15.dp, vertical = 100.dp)
                    ) {
                        if (shouldShowAddButton) {
                            ExtendedFloatingActionButton(
                                onClick = {
                                    Bianhuaweeks = GetDate.Benweeks
                                    today = LocalDate.now()
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
                            .padding(horizontal = 15.dp, vertical = 100.dp)
                    ) {
                        if (shouldShowAddButton) {
                            FloatingActionButton(
                                onClick = {
                                    if (Bianhuaweeks < 20) {
                                        Bianhuaweeks++ + 1
                                        today = today.plusDays(7)
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