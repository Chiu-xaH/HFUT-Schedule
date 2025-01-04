package com.hfut.schedule.ui.activity.home.calendar.communtiy

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.beans.Community.CourseTotalResponse
import com.hfut.schedule.logic.beans.Community.courseDetailDTOList
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.activity.home.search.functions.totalCourse.DetailItems
import com.hfut.schedule.ui.activity.home.search.functions.totalCourse.getTotalCourse
import com.hfut.schedule.ui.utils.components.MyCard
import com.hfut.schedule.ui.utils.style.Round


@RequiresApi(Build.VERSION_CODES.O)
fun getCouses(Week : Int,friendUserName : String? = null) : Array<Array<List<courseDetailDTOList>>> {
    val dayArray = Array(7) { Array<List<courseDetailDTOList>>(12) { emptyList() } }
    val json = SharePrefs.prefs.getString(if(friendUserName == null) "Course" else "Course${friendUserName}", MyApplication.NullTotal)
    val result = Gson().fromJson(json, CourseTotalResponse::class.java).result.courseBasicInfoDTOList
    for (i in 0 until result.size){
        val Name = result[i].courseName
        val List = result[i].courseDetailDTOList
        for(j in 0 until List.size) {
            val section = List[j].section
            val weekCount = List[j].weekCount
            val week = List[j].week
            weekCount.forEach { item ->
                if(item == Week) {
                    List[j].name = Name
                    dayArray[week - 1][section - 1] = listOf(List[j])
                }
            }
        }
    }
    return dayArray
}

@RequiresApi(Build.VERSION_CODES.O)
fun getCourseINFO(weekday : Int,Week : Int,friendUserName : String? = null) : MutableList<List<courseDetailDTOList>> {
    val new = mutableListOf<List<courseDetailDTOList>>()
    return try {
        if(weekday <= 7) {
            val days = getCouses(Week,friendUserName)[weekday - 1]
            for (i in days.indices){
                if(days[i].isNotEmpty())
                    days[i].forEach { new.add(days[i]) }
            }
            new
        } else new
    } catch (e : Exception) {
        e.printStackTrace()
        new
    }
}


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailInfos(sheet : courseDetailDTOList,isFriend : Boolean = false,vm: NetWorkViewModel) {
    val sheetState_totalCourse = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_totalCourse by remember { mutableStateOf(false) }
//    val json = SharePrefs.prefs.getString("courses","")
    var courseName by remember { mutableStateOf("") }
//    val list = getTotalCourse(json)
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

    LazyColumn {
        item{
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Column {
                    MyCard {
                        ListItem(
                            headlineContent = { sheet.place?.let { Text(it) } },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.near_me),
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                        ListItem(
                            headlineContent = { Text(sheet.classTime ) },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.schedule),
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                        ListItem(
                                headlineContent = { Text(sheet.teacher ) },
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.person),
                                contentDescription = "Localized description",
                            )
                        }
                        )
                        ListItem(
                            headlineContent = { Text("周 ${sheet.week} 第 ${sheet.section.toString()} 节" ) },
                            supportingContent = { Text(text = "周数 ${sheet.weekCount.toString().replace("[","").replace("]","")} ")},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.calendar),
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                    }
                    if(!isFriend)
                    MyCard{
                        ListItem(
                            headlineContent = { Text( "更多信息") },
                            leadingContent = {
                                Icon(
                                    Icons.Filled.ArrowForward,
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier.clickable {
                                courseName = sheet.name
                                showBottomSheet_totalCourse = true
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
//根据课程名跨接口查找唯一课程信息
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailApi(isNext : Boolean = false,courseName : String,vm : NetWorkViewModel) {
    //用法
//    val sheetState_totalCourse = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//    var showBottomSheet_totalCourse by remember { mutableStateOf(false) }
//    var courseName by remember { mutableStateOf("") }
//    if (showBottomSheet_totalCourse) {
//        ModalBottomSheet(
//            onDismissRequest = {
//                showBottomSheet_totalCourse = false
//            },
//            sheetState = sheetState_totalCourse,
//            shape = Round(sheetState_totalCourse)
//        ) {
//            CourseDetailApi(courseName = courseName, vm = vm)
//        }
//    }
    val json = SharePrefs.prefs.getString(if(!isNext)"courses" else "coursesNext","")
    val list = getTotalCourse(json)
    var numItem by remember { mutableStateOf(0) }
    for(i in list.indices) {
        if(list[i].course.nameZh == courseName) {
            numItem = i
            break
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(getTotalCourse(json)[numItem].course.nameZh) }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            DetailItems(getTotalCourse(json)[numItem],json, vm)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}