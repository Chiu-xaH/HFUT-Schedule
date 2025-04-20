package com.hfut.schedule.ui.screen.home.search.function.exam

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.DateTimeUtils
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.addToCalendars
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.ui.component.BottomSheetTopBar
import com.hfut.schedule.ui.component.EmptyUI
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.bottomSheetRound
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState

@SuppressLint("SuspiciousIndentation")
@Composable
fun Exam(vm : NetWorkViewModel, ifSaved : Boolean, hazeState: HazeState) {
    var showBottomSheet_Exam by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { Text(text = "考试") },
        overlineContent = { Text(text = "${if(ifSaved) getNewExam().size else getExamJXGLSTU().size} 门")},
        leadingContent = {
            Icon(painterResource(R.drawable.draw), contentDescription = "Localized description",)
        },
        modifier = Modifier.clickable {
            if(ifSaved)  {
                refreshLogin()
            }
            else {
                showBottomSheet_Exam = true
            }
        }
    )

    if (showBottomSheet_Exam) {
        
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheet_Exam = false
            },
            showBottomSheet = showBottomSheet_Exam,
            hazeState = hazeState,
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("考试")
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    if(ifSaved) {
                        if(getExam().size == 0) EmptyUI()
                        else LazyColumn { items(getExam().size) { item -> ExamItems(item,false) } }
                    } else {
                        if(getExamJXGLSTU().isEmpty()) EmptyUI()
                        else LazyColumn { items(getExamJXGLSTU()) { item -> JxglstuExamUI(item,true)} }
                    }
                }
            }
        }
    }
}
@Composable
private fun ExamItems(item : Int,status : Boolean) {

    var date = DateTimeUtils.Date_yyyy_MM_dd
    val todaydate = date?.substring(0, 4) + date?.substring(5, 7)  + date?.substring(8, 10)
    val get = getExam()[item].formatEndTime
    //判断考完试不显示信息

    val examdate = (get?.substring(0,4)+ get?.substring(5, 7) ) + get?.substring(8, 10)

    val st = getExam()[item].formatStartTime

    @Composable
    fun Item() {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Column() {
//                MyCustomCard {
                    StyleCardListItem(
                        headlineContent = { getExam()[item].courseName?.let { Text(text = it) } },
                        overlineContent = { Text(text = st?.substring(5,st.length - 3) + "~" + get?.substring(11,get.length-3)) },
                        supportingContent = { getExam()[item].place?.let { Text(text = it) } },
                        leadingContent = {
                            if(status==true) Icon(painterResource(R.drawable.draw), contentDescription = "Localized description",)
                            else if(examdate.toInt() >= todaydate.toInt()) Icon(painterResource(R.drawable.schedule), contentDescription = "Localized description",)
                            else Icon(Icons.Filled.Check, contentDescription = "Localized description",)
                        },
                        trailingContent = {
                            if(examdate.toInt() < todaydate.toInt()) Text(text = "已结束")
                            else if(examdate.toInt() == todaydate.toInt()) Text(text = "今日")
                            else if(examdate.toInt() > todaydate.toInt()) Text(text = "待考")
                                          },
                        color =  if(examdate.toInt() >= todaydate.toInt())
                            MaterialTheme.colorScheme.errorContainer
                        else null,
                        modifier = Modifier.clickable {},
                    )
//                }
            }
        }
    }

    if(status){
        if(examdate.toInt() >= todaydate.toInt()) Item()
    } else Item()

}

//status参数判断是聚焦还是界面，若为聚焦则解析显示未考的，若为界面都显示
@Composable
fun JxglstuExamUI(item : Map<String,String>,status : Boolean) {
    //时隔一年修补这里的Bug
    val newDate = DateTimeUtils.Date_yyyy_MM_dd
    val newToday = newDate.replace("-","").toLongOrNull() ?: 0
    val examDate = item["日期时间"]
    val examDateNum = examDate?.substringBefore(" ")?.replace("-","")?.toLongOrNull() ?: 0


    val activity = LocalActivity.current
//    var date = DateTimeManager.Date_MM_dd
//    val todaydate = (date?.substring(0, 2) ) + date?.substring(3, 5)
//    val get = item["日期时间"]
//    val examdate = (get?.substring(5, 7) ) + get?.substring(8, 10)
    //判断考完试不显示信息
    //  Log.d("exam",examdate)
    //Log.d("today",todaydate)
    if(status) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Column() {
//                MyCustomCard{
                    StyleCardListItem(
                        headlineContent = {  Text(text = "${item["课程名称"]}") },
                        overlineContent = { examDate?.let { Text(text = it) } },
                        supportingContent = { Text(text = "${item["考场"]}") },
                        leadingContent = {
                            if(examDateNum >= newToday)
                                Icon(painterResource(R.drawable.schedule), contentDescription = "Localized description",)
                            else Icon(Icons.Filled.Check, contentDescription = "Localized description",)
                        },
                        trailingContent = {
                            if(examDateNum < newToday) Text(text = "已结束")
                            else if(examDateNum == newToday) Text("今日")
                        },
                        modifier = Modifier.clickable {},
                    )
//                }
            }
        }
    } else {
        if(examDateNum >= newToday) {
            //如果是今天考试，那么判断考试结束后不显示 待做

            val course = item["课程名称"]
            val time = item["日期时间"]
            val place  = item["考场"]

            val year = time?.substringBefore("-")
            val month = time?.substring(5,7)
            val day = time?.substring(8,10)
            val startTimeHour = time?.substringAfter(" ")?.substringBefore(":")
            val startTimeMinute = time?.substringAfter(":")?.substringBefore("~")
            val endTimeHour = time?.substringAfter("~")?.substringBefore(":")
            val endTimeMinute = time?.substringAfter("~")?.substringAfter(":")

            //如果是今天

          //  Log.d("打印测试","${year}年 ${month}月 ${day}日 起始 ${startTimeHour}时 ${startTimeMinute}分 结束 ${endTimeHour}时 ${endTimeMinute}分")

            //今天 && 已经考完
            if("$month-$day" == DateTimeUtils.Date_MM_dd
                && DateTimeUtils.compareTime("$endTimeHour:$endTimeMinute") == DateTimeUtils.TimeState.ENDED) {

            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Column() {
//                        MyCustomCard {
                            StyleCardListItem(
                                headlineContent = {  Text(text = "$course") },
                                overlineContent = { Text(text = "${time?.substringAfter("-")}") },
                                supportingContent = { Text(text = "$place") },
                                leadingContent = {
//                                if("$month-$day" == DateTimeManager.Date_MM_dd) {
//                                    Icon(painterResource(R.drawable.warning), contentDescription = "Localized description",)
//                                } else {
//
//                                }
                                    Icon(painterResource(R.drawable.draw), contentDescription = "Localized description",)
                                },
                                modifier = Modifier.clickable {},
                                trailingContent = {
                                    if("$month-$day" == DateTimeUtils.Date_MM_dd) {
                                        Text("今日")
                                    } else {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            FilledTonalIconButton(
                                                colors = IconButtonDefaults.filledTonalIconButtonColors(MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                                                onClick = {
                                                    try {
                                                        val startDateList = year?.let { month?.let { it1 ->
                                                            day?.let { it2 ->
                                                                startTimeHour?.let { it3 ->
                                                                    startTimeMinute?.let { it4 ->
                                                                        listOf(it.toInt(),
                                                                            it1.toInt(), it2.toInt(), it3.toInt(), it4.toInt())
                                                                    }
                                                                }
                                                            }
                                                        } }
                                                        val endDateList = year?.let { month?.let { it1 ->
                                                            day?.let { it2 ->
                                                                endTimeHour?.let { it3 ->
                                                                    endTimeMinute?.let { it4 ->
                                                                        listOf(it.toInt(),
                                                                            it1.toInt(), it2.toInt(), it3.toInt(), it4.toInt())
                                                                    }
                                                                }
                                                            }
                                                        } }
                                                        activity?.let { it0 -> course?.let { place?.let { it1 -> startDateList?.let { it2 -> endDateList?.let { it3 -> addToCalendars(it2, it3, it1, it,"考试", it0) } } } } }
                                                    } catch (e : Exception) {
                                                        e.printStackTrace()
                                                    }
                                                }) {
                                                Icon(painter = painterResource(id = R.drawable.event_upcoming), contentDescription = "")
                                            }
                                            time?.substringBefore(" ")?.let {
//                                                Row(horizontalArrangement = Arrangement.Center) {
                                                    Text("${DateTimeUtils.daysBetween(it)}天")
//                                                }
                                            }
                                        }
                                    }
                                },
                                color = MaterialTheme.colorScheme.errorContainer
                            )
//                        }
                    }
                }
            }
        }
    }
}