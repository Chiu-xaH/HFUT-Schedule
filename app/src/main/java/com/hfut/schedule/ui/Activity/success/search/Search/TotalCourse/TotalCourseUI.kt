package com.hfut.schedule.ui.Activity.success.search.Search.TotalCourse

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import com.hfut.schedule.R
import com.hfut.schedule.logic.datamodel.Jxglstu.CourseSearchResponse
import com.hfut.schedule.logic.datamodel.Jxglstu.courseType
import com.hfut.schedule.logic.datamodel.Jxglstu.lessonResponse
import com.hfut.schedule.logic.datamodel.Jxglstu.lessons
import com.hfut.schedule.logic.datamodel.Jxglstu.teacher
import com.hfut.schedule.logic.datamodel.Jxglstu.teacherAssignmentList2
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.UIUtils.EmptyUI
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.UIUtils.Round
import com.hfut.schedule.ui.UIUtils.ScrollText
import com.hfut.schedule.ui.UIUtils.courseIcons

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseTotalUI(json : String?,isSearch : Boolean) {


    var numItem by remember { mutableStateOf(0) }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    //val json = prefs.getString("courses","")
    if (showBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {

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
                ) {
                    DetailItems(numItem,json)
                }
            }
        }
    }
    if(getTotalCourse(json).size != 0) {
        LazyColumn {

            item{ SemsterInfo(json) }
            items(getTotalCourse(json).size) { item ->
                val list = getTotalCourse(json)[item]
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Column() {
                        Card(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 3.dp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 15.dp,
                                    vertical = 5.dp
                                ),
                            shape = MaterialTheme.shapes.medium,
                        ){

                            ListItem(
                                headlineContent = {  Text(list.course.nameZh) },
                                overlineContent = { ScrollText(text = "学分 ${list.course.credits}" + if(list.scheduleWeeksInfo != null) " | ${list.scheduleWeeksInfo}" else "") },
                                trailingContent = { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "")},
                                //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                                leadingContent = { courseIcons(name = list.openDepartment.nameZh) },
                                modifier = Modifier.clickable {
                                    showBottomSheet = true
                                    numItem = item
                                },
                            )
                        }
                    }
                }
            }
            if(isSearch)
            item { Spacer(modifier = Modifier.height(85.dp)) }
        }
    } else { EmptyUI() }
}



@SuppressLint("SuspiciousIndentation")
@Composable
fun DetailItems(item : Int,json: String?) {

    val lists = getTotalCourse(json)[item]

    LazyColumn {
        item{
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Column() {

                    Row {
                        if(lists.stdCount != null)
                        ListItem(
                            overlineContent = { Text("人数" ) },
                            headlineContent = { Text(lists.stdCount.toString()) },

                            //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.group),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier
                                .clickable {}
                                .weight(.5f),
                        )
                        if(lists.scheduleWeeksInfo != null)
                        ListItem(
                            overlineContent = { Text("周数") },
                            headlineContent = { ScrollText(lists.scheduleWeeksInfo) },

                            //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.calendar),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier
                                .clickable {}
                                .weight(.5f),
                        )
                    }
                    Row {
                        ListItem(
                            overlineContent = { Text("类型") },
                            headlineContent = { ScrollText(lists.courseType.nameZh) },

                            //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.hotel_class),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier
                                .clickable {}
                                .weight(.5f),
                        )
                        if(lists.course.credits != null)
                        ListItem(
                            overlineContent = { Text("学分") },
                            headlineContent = { Text(lists.course.credits.toString()) },

                            //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.filter_vintage),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier
                                .clickable {}
                                .weight(.5f),
                        )
                    }
                    Row {
                        val teacherList =  if(lists.teacherAssignmentList?.isNotEmpty() == true) lists.teacherAssignmentList?.get(0) else teacherAssignmentList2(
                            teacher(courseType("无信息"),
                                courseType("无信息"),
                                null
                            ),null)
                        ListItem(
                            overlineContent = { ScrollText("教师(默认展示第一位)") },
                            headlineContent = {
                                if (teacherList != null) {
                                    ScrollText( teacherList.teacher.person.nameZh.toString() )
                                }
                            },
                            //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.person),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier
                                .clickable {}
                                .weight(.5f),
                        )
                        ListItem(
                            headlineContent = {
                                if (teacherList != null) {
                                    ScrollText( teacherList.teacher.title.nameZh.toString()  +" " + (teacherList?.teacher?.type?.nameZh ?: ""))
                                }
                            },
                            overlineContent = {
                                if (teacherList != null) {
                                    Text(text =  if(teacherList.age  != null)  "年龄 " + teacherList.age else "年龄未知")
                                }
                            },

                            //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.info),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier
                                .clickable {}
                                .weight(.5f),
                        )

                    }
                    Row {
                        ListItem(
                            overlineContent = { Text("开设学院") },
                            headlineContent = { ScrollText(lists.openDepartment.nameZh.toString()) },

                            //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                            leadingContent = {
                                courseIcons(name = lists.openDepartment.nameZh)
                            },
                            modifier = Modifier
                                .clickable {}
                                .weight(.5f),
                        )
                        ListItem(
                            overlineContent = { Text("考察方式 ${lists.examMode.nameZh.toString() }") },
                            headlineContent = { ScrollText(if(lists.planExamWeek != null)"预计第${lists.planExamWeek.toString()}周" else "无时间") },

                            //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.draw),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier
                                .clickable {}
                                .weight(.5f),
                        )
                    }
                    Row {
                        ListItem(
                            overlineContent = { Text("课程代码") },
                            headlineContent = { ScrollText(lists.code) },

                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.tag),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier
                                .clickable {}
                                .weight(.5f),
                        )
                        ListItem(
                            overlineContent = { Text("同班同学") },
                            headlineContent = { Text("查看") },
                            trailingContent = {
                            },
                            //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                            leadingContent = {
                                Icon(
                                    Icons.Filled.ArrowForward,
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier.clickable { MyToast("请前往 合工大教务 微信公众号") }.weight(.5f),
                        )
                    }
                    if(lists.nameZh != null)
                        ListItem(
                            overlineContent = { Text("班级") },
                            headlineContent = { Text(lists.nameZh.toString()) },

                            //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.sensor_door),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier.clickable {},
                        )

                    if(lists.scheduleText.dateTimePlacePersonText.textZh != null)
                    ListItem(
                        overlineContent = { Text("上课安排") },
                        headlineContent = { Text(lists.scheduleText.dateTimePlacePersonText.textZh ) },

                        //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.schedule),
                                contentDescription = "Localized description",
                            )
                        },
                        modifier = Modifier.clickable {}
                    )

                    if(lists.remark != null)
                        ListItem(
                            overlineContent = { Text("备注") },
                            headlineContent = { Text(lists.remark) },
                            trailingContent = {
                            },
                            //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.calendar),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier.clickable {},
                        )
                }
            }
        }
    }
}

fun getTotalCourse(json : String?): MutableList<lessons>  {
    var list = mutableListOf<lessons>()

    try {
        if (json != null) {
            if(json.contains("lessonIds")) {
                var result = Gson().fromJson(json,lessonResponse::class.java).lessons


                for (i in result.indices) {
                    val courses = result[i]
                    list.add(lessons(courses.nameZh,courses.remark,courses.scheduleText,courses.stdCount,courses.course,courses.courseType,courses.openDepartment,courses.examMode,courses.scheduleWeeksInfo,courses.planExamWeek,courses.teacherAssignmentList,courses.semester,courses.code))
                }
                return list
            }
            else {
                val result = Gson().fromJson(json,CourseSearchResponse::class.java).data
                for (i in result.indices) {
                    val courses = result[i].lesson
                    list.add(lessons(courses.nameZh,courses.remark,courses.scheduleText,courses.stdCount,courses.course,courses.courseType,courses.openDepartment,courses.examMode,courses.scheduleWeeksInfo,courses.planExamWeek,courses.teacherAssignmentList,courses.semester,courses.code))
                }
                return list
            }
        } else return list
    } catch (e : Exception) {
        return list
    }
}