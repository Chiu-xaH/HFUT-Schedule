package com.hfut.schedule.ui.activity.home.search.functions.totalCourse

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.beans.Jxglstu.CourseSearchResponse
import com.hfut.schedule.logic.beans.Jxglstu.lessonResponse
import com.hfut.schedule.logic.beans.Jxglstu.lessons
import com.hfut.schedule.logic.utils.DateTimeManager
import com.hfut.schedule.logic.utils.DateTimeManager.TimeState.*
import com.hfut.schedule.ui.activity.home.search.functions.failRate.permit
import com.hfut.schedule.ui.activity.home.search.functions.failRate.ApiToFailRate
import com.hfut.schedule.ui.activity.home.search.functions.teacherSearch.ApiToTeacherSearch
import com.hfut.schedule.ui.utils.components.EmptyUI
import com.hfut.schedule.ui.utils.components.LoadingUI
import com.hfut.schedule.ui.utils.components.MyCard
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.RotatingIcon
import com.hfut.schedule.ui.utils.style.Round
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.components.DepartmentIcons

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseTotalUI(json : String?,isSearch : Boolean,sortType: Boolean,vm : NetWorkViewModel) {

    val list = getTotalCourse(json)
    if(sortType)
        list.sortBy { it.scheduleWeeksInfo?.substringBefore("~")?.toIntOrNull() }
    else list.sortBy { it.course.credits }

    var numItem by remember { mutableStateOf(0) }
   // var sortType by remember { mutableStateOf(true) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
                        title = { Text(list[numItem].course.nameZh) },
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    DetailItems(list[numItem],vm)
                }
            }
        }
    }
    if(getTotalCourse(json).size != 0) {
        LazyColumn {
            item{ SemsterInfo(json) }
            items(list.size) { item ->
                val weeksInfo = list[item].scheduleWeeksInfo
                val startWeek = weeksInfo?.substringBefore("~")
                val endWeek = weeksInfo?.substringAfter("~")?.substringBefore("周")

                var state : DateTimeManager.TimeState? = null
                if(startWeek != null && endWeek != null && !isSearch) {
                    val week = DateTimeManager.weeksBetween
                    if(week in startWeek.toInt()..endWeek.toInt()) {
                        state = DateTimeManager.TimeState.ONGOING
                    } else if(week < startWeek.toInt()) {
                        state = DateTimeManager.TimeState.NOT_STARTED
                    } else if(week > endWeek.toInt()) {
                        state = DateTimeManager.TimeState.ENDED
                    }
                }
                val infiniteTransition = rememberInfiniteTransition(label = "")
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = .5f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(500, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ), label = ""
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Column() {
                        MyCard {
                            ListItem(
                                headlineContent = {  Text(list[item].course.nameZh) },
                                overlineContent = { ScrollText(text = "学分 ${list[item].course.credits}" + if(list[item].scheduleWeeksInfo != null) " | $weeksInfo" else "") },
                                trailingContent = {
                                    when(state) {
                                        ONGOING -> Text("开课中", modifier = Modifier.alpha(alpha))
                                        NOT_STARTED -> "未开课"
                                        ENDED -> Icon(Icons.Filled.Check,null)
                                        null -> Icon(Icons.Filled.ArrowForward,null)
                                    }
                                },
                                //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                                leadingContent = {
                                    list[item].openDepartment.nameZh.let { DepartmentIcons(name = it) }
                                                 },
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


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun DetailItems(lessons: lessons,vm : NetWorkViewModel) {

    val lists = lessons

    val sheetState_FailRate = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_FailRate by remember { mutableStateOf(false) }

    if (showBottomSheet_FailRate) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_FailRate = false },
            sheetState = sheetState_FailRate,
            shape = Round(sheetState_FailRate)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("挂科率 ${lessons.course.nameZh}") }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    ApiToFailRate(lessons.course.nameZh,vm)
                }
            }
        }
    }

    val sheetState_Teacher = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_Teacher by remember { mutableStateOf(false) }

    var teacherTitle by remember { mutableStateOf("") }

    if (showBottomSheet_Teacher) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Teacher = false },
            sheetState = sheetState_Teacher,
            shape = Round(sheetState_Teacher)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("教师检索 " + teacherTitle) }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    ApiToTeacherSearch(teacherTitle,vm)
                }
            }
        }
    }

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
                            headlineContent = { lists.courseType.nameZh?.let { ScrollText(it) } },

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
                    val teacherNum = lists.teacherAssignmentList?.size ?: 0
                    for (i in 0 until teacherNum) {
                        val teacherList = lists.teacherAssignmentList?.get(i)
                        Row(modifier = Modifier.clickable {
                            if (teacherList != null) {
                                permit = 1
                                teacherTitle = teacherList.teacher.person?.nameZh.toString()
                                showBottomSheet_Teacher = true
                            }
                        }) {
                            ListItem(
                                overlineContent = { Text("教师 " + if(teacherNum == 1) "" else (i+1).toString()) },
                                headlineContent = {
                                    if (teacherList != null) {
                                        ScrollText( teacherList.teacher.person?.nameZh.toString() )
                                    }
                                },
                                leadingContent = {
                                    Icon(
                                        painterResource(R.drawable.person),
                                        contentDescription = "Localized description",
                                    )
                                },
                                modifier = Modifier
                                    .weight(.5f),
                            )
                            ListItem(
                                headlineContent = {
                                    if (teacherList != null) {
                                        ScrollText( teacherList.teacher.title?.nameZh.toString()  +" " + (teacherList.teacher?.type?.nameZh ?: ""))
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
                                    .weight(.5f),
                            )
                        }
                    }

                    Row {
                        var department = lists.openDepartment.nameZh.toString()
                        if(department.contains("（")) department = department.substringBefore("（")
                        ListItem(
                            overlineContent = { Text("开设学院") },
                            headlineContent = { ScrollText(department) },

                            //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                            leadingContent = {
                                lists.openDepartment.nameZh?.let { DepartmentIcons(name = it) }
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
                            modifier = Modifier
                                .clickable { MyToast("请前往 合工大教务 微信公众号") }
                                .weight(.5f),
                        )
                    }
                    Row {
                        ListItem(
                            headlineContent = { Text(text = "挂科率查询") },

                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.monitoring),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier
                                .clickable {
                                    permit = 1
                                    showBottomSheet_FailRate = true
                                }
                                .weight(.5f),
                        )
                        ListItem(
                            headlineContent = { Text("添加到收藏") },
                            trailingContent = {
                            },
                            //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                            leadingContent = {
                                Icon(
                                    painterResource(id = R.drawable.star),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier
                                .clickable { MyToast("正在开发") }
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