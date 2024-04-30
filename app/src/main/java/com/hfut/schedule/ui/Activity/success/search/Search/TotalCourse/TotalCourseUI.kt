package com.hfut.schedule.ui.Activity.success.search.Search.TotalCourse

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.hfut.schedule.logic.datamodel.Jxglstu.lessonResponse
import com.hfut.schedule.logic.datamodel.Jxglstu.lessons
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.UIUtils.EmptyUI
import com.hfut.schedule.ui.UIUtils.ScrollText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseTotalUI() {


    var numItem by remember { mutableStateOf(0) }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text(getTotalCourse()[numItem].course.nameZh) }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    DetailItems(numItem)
                }
            }
        }
    }
    if(getTotalCourse().size != 0) {
        LazyColumn {
            item { Another() }
            items(getTotalCourse().size) { item ->
                val list = getTotalCourse()[item]
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
        }
    } else { EmptyUI() }
}


@Composable
fun courseIcons(name : String) {
    if(name.contains("计算机")) Icon(painterResource(R.drawable.computer), contentDescription = "Localized description",)
    else if(name.contains("资源与环境工程")) Icon(painterResource(R.drawable.eco), contentDescription = "Localized description",)
    else if(name.contains("数学")) Icon(painterResource(R.drawable.function), contentDescription = "Localized description",)
    else if(name.contains("外国语")) Icon(painterResource(R.drawable.translate), contentDescription = "Localized description",)
    else if(name.contains("马克思主义")) Icon(painterResource(R.drawable.neurology), contentDescription = "Localized description",)
    else if(name.contains("机械工程")) Icon(painterResource(R.drawable.settings), contentDescription = "Localized description",)
    else if(name.contains("材料科学与化工")) Icon(painterResource(R.drawable.hive), contentDescription = "Localized description",)
    else if(name.contains("电气与自动化工程")) Icon(painterResource(R.drawable.flash_on), contentDescription = "Localized description",)
    else if(name.contains("土木与水利工程")) Icon(painterResource(R.drawable.domain), contentDescription = "Localized description",)
    else if(name.contains("化学与化工")) Icon(painterResource(R.drawable.science), contentDescription = "Localized description",)
    else if(name.contains("经济")) Icon(painterResource(R.drawable.paid), contentDescription = "Localized description",)
    else if(name.contains("文法")) Icon(painterResource(R.drawable.gavel), contentDescription = "Localized description",)
    else if(name.contains("管理")) Icon(Icons.Filled.Menu, contentDescription = "Localized description",)
    else if(name.contains("仪器科学与光电工程")) Icon(painterResource(R.drawable.biotech), contentDescription = "Localized description",)
    else if(name.contains("建筑与艺术")) Icon(painterResource(R.drawable.interests), contentDescription = "Localized description",)
    else if(name.contains("食品与生物工程")) Icon(painterResource(R.drawable.genetics), contentDescription = "Localized description",)
    else if(name.contains("微电子")) Icon(painterResource(R.drawable.mist), contentDescription = "Localized description",)
    else if(name.contains("物理")) Icon(painterResource(R.drawable.category), contentDescription = "Localized description",)
    else if(name.contains("汽车与交通工程")) Icon(painterResource(R.drawable.directions_car), contentDescription = "Localized description",)
    else if(name.contains("软件")) Icon(painterResource(R.drawable.code), contentDescription = "Localized description",)
    else if(name.contains("体育部")) Icon(painterResource(R.drawable.directions_run), contentDescription = "Localized description",)
    else if(name.contains("国际教育")) Icon(painterResource(R.drawable.publics), contentDescription = "Localized description",)
    else Icon(painterResource(R.drawable.calendar_view_month), contentDescription = "Localized description",)
}
@Composable
fun Another() {
    var num = 0.0
    for(i in 0 until getTotalCourse().size) {
        val credit = getTotalCourse()[i].course.credits
        if (credit != null) {
            num += credit
        }
    }
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
                   /// overlineContent = { Text(text = "")},
                    headlineContent = {  Text("学分合计 $num") },
                    leadingContent = { Icon(
                        painterResource(R.drawable.category),
                        contentDescription = "Localized description",
                    ) },
                    modifier = Modifier.clickable {},
                    colors = ListItemDefaults.colors(MaterialTheme.colorScheme.primaryContainer)
                )
            }
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun DetailItems(item : Int) {

    val lists = getTotalCourse()[item]

    LazyColumn {
        item{
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Column() {

                    Row {
                        if(lists.stdCount != null)
                        ListItem(
                            overlineContent = { Text("人数") },
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
                        if(lists.courseType.nameZh != null)
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
                        val teacherList =  lists.teacherAssignmentList[0]
                        ListItem(
                            overlineContent = { ScrollText("教师(默认展示第一位)") },
                            headlineContent = { ScrollText( teacherList.teacher.person.nameZh.toString() ) },
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
                            headlineContent = { ScrollText( teacherList.teacher.title.nameZh.toString()  +" " + (teacherList?.teacher?.type?.nameZh ?: "")) },
                            overlineContent = { Text(text =  if(teacherList.age  != null)  "年龄 " + teacherList.age else "年龄未知") },

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
                            overlineContent = { Text("考察方式") },
                            headlineContent = { ScrollText(lists.examMode.nameZh.toString()) },

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

fun getTotalCourse(): MutableList<lessons>  {
    var list = mutableListOf<lessons>()
    val json = prefs.getString("courses","")
    return try {
        val result = Gson().fromJson(json,lessonResponse::class.java).lessons
        for (i in result.indices) {
            val courses = result[i]
            list.add(lessons(courses.nameZh,courses.remark,courses.scheduleText,courses.stdCount,courses.course,courses.courseType,courses.openDepartment,courses.examMode,courses.scheduleWeeksInfo,courses.teacherAssignmentList))
        }
        list
    } catch (e : Exception) {
        list
    }
}