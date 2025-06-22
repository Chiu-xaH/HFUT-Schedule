package com.hfut.schedule.ui.screen.home.calendar.communtiy

import android.annotation.SuppressLint
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.DetailItems
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getTotalCourse
import com.hfut.schedule.ui.component.cardNormalColor
import com.hfut.schedule.ui.component.custom.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.MyCustomCard
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.style.HazeBottomSheet
import dev.chrisbanes.haze.HazeState


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailInfos(sheet : courseDetailDTOList, isFriend : Boolean = false, vm: NetWorkViewModel, hazeState: HazeState) {
    var showBottomSheet_totalCourse by remember { mutableStateOf(false) }
    var courseName by remember { mutableStateOf("") }
    if (showBottomSheet_totalCourse) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheet_totalCourse = false
            },
            showBottomSheet = showBottomSheet_totalCourse,
            hazeState = hazeState
        ) {
            CourseDetailApi(courseName = courseName, vm = vm, hazeState = hazeState)
        }
    }

    LazyColumn {
        item{
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Column {
                    MyCustomCard(hasElevation = false, containerColor = cardNormalColor()) {
                        TransplantListItem(
                            headlineContent = { sheet.place?.let { Text(it) } },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.near_me),
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                        TransplantListItem(
                            headlineContent = { Text(sheet.classTime ) },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.schedule),
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                        TransplantListItem(
                            headlineContent = { Text(sheet.teacher) },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.person),
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                        TransplantListItem(
                            headlineContent = { Text(sheet.campus_dictText ) },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.local_library),
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                        TransplantListItem(
                            headlineContent = { Text("周 ${sheet.week} 第 ${sheet.section} 节" ) },
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
                        StyleCardListItem(
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
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
//根据课程名跨接口查找唯一课程信息
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailApi(isNext : Boolean = false, courseName : String, vm : NetWorkViewModel, hazeState: HazeState) {
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
    val json = SharedPrefs.prefs.getString(if(!isNext)"courses" else "coursesNext","")
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
        containerColor = Color.Transparent,
        topBar = {
            HazeBottomSheetTopBar(getTotalCourse(json)[numItem].course.nameZh)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            DetailItems(getTotalCourse(json)[numItem], vm, hazeState =hazeState )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}