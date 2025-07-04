package com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse

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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.model.jxglstu.CourseSearchResponse
import com.hfut.schedule.logic.model.jxglstu.lessonResponse
import com.hfut.schedule.logic.model.jxglstu.lessons
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.permit
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.ApiToFailRate
import com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch.ApiToTeacherSearch
import com.hfut.schedule.ui.component.AnimationCardListItem
import com.hfut.schedule.ui.component.custom.BottomSheetTopBar
import com.hfut.schedule.ui.component.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.custom.CustomTextField
import com.hfut.schedule.ui.component.EmptyUI
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.style.bottomSheetRound
import com.hfut.schedule.ui.component.custom.ScrollText
import com.hfut.schedule.ui.component.DepartmentIcons
import com.hfut.schedule.ui.component.custom.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.onListenStateHolder
import com.hfut.schedule.ui.style.ColumnVertical
import com.hfut.schedule.ui.style.HazeBottomSheet
import dev.chrisbanes.haze.HazeState
import kotlin.text.contains

enum class TotalCourseDataSource {
    MINE,MINE_NEXT,SEARCH
}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseTotalUI(dataSource : TotalCourseDataSource, sortType: Boolean, vm : NetWorkViewModel, hazeState: HazeState) {

    val list by produceState(initialValue = emptyList<lessons>(),key1 = dataSource) {
        when(dataSource) {
            TotalCourseDataSource.MINE -> {
                prefs.getString("courses","")?.let { value = vm.parseDatumCourse(it) }
            }
            TotalCourseDataSource.SEARCH -> {
                onListenStateHolder(vm.courseSearchResponse) { data ->
                    value = data
                }
            }
            TotalCourseDataSource.MINE_NEXT -> {
                prefs.getString("coursesNext","")?.let { value = vm.parseDatumCourse(it) }
            }
        }
    }

    val isSearch = dataSource == TotalCourseDataSource.SEARCH

    var input by remember { mutableStateOf("") }
    val sortList =  list
        .filter {
            input.isBlank() || it.course.nameZh.contains(input) || it.course.courseType.nameZh.contains(input) || it.openDepartment.nameZh.contains(input) || it.code.contains(input) ||( it.remark?.contains(input) == true)
        }
        .let { filtered ->
            if (sortType) {
                filtered.sortedBy { it.scheduleWeeksInfo?.substringBefore("~")?.toIntOrNull() }
            } else {
                filtered.sortedBy { it.course.credits }
            }
        }

//        if(sortType) {
//        list.sortedBy { it.scheduleWeeksInfo?.substringBefore("~")?.toIntOrNull() }
//    } else {
//        list.sortedBy { it.course.credits }
//    }


    var numItem by remember { mutableIntStateOf(0) }

    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar(sortList[numItem].course.nameZh)
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    DetailItems(sortList[numItem],vm,hazeState)
                }
            }
        }
    }
    if(list.isNotEmpty()) {
        if(!isSearch) {
            CustomTextField(
                input = input,
                label = { Text("筛选 学院、课程、代码、类型")},
                leadingIcon = {
                    Icon(painterResource(R.drawable.search),null)
                }
            ) { input = it }
            Spacer(Modifier.height(CARD_NORMAL_DP))
        }
        LazyColumn {
            item { TermFirstlyInfo(list,isSearch) }
            items(sortList.size, key = { sortList[it].code }) { item ->
                val data = sortList[item]
                val weeksInfo = data.scheduleWeeksInfo

                val code = data.code
                AnimationCardListItem(
                    headlineContent = {  Text(data.course.nameZh) },
                    overlineContent = { ScrollText(text =
                        "学分 ${data.course.credits}" +
                                (if(data.scheduleWeeksInfo != null) " | $weeksInfo" else "") +
                                (if(isSearch && code.contains("--")) " | " + code.substringAfter("--") + "班" else "")
                    ) },
                    trailingContent = {
                        val type = data.courseType.nameZh

                        ColumnVertical() {
                            if(type.contains("选修") || type.contains("慕课") || type.contains("公选")) {
                                Text("选修")
                            } else if(type.contains("实践")) {
                                Text("实践")
                            }
                            if(!type.contains("实践")) {
                                if(data.scheduleWeeksInfo == null && data.scheduleText.dateTimePlacePersonText.textZh == null) {
                                    Text("非教室")
                                }
                            }
                        }
                    },
                    leadingContent = {
                        data.openDepartment.nameZh.let { DepartmentIcons(name = it) }
                    },
                    modifier = Modifier.clickable {
                        showBottomSheet = true
                        numItem = item
                    },
                    cardModifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null),
                    index = item
                )
            }
            if(isSearch)
                item { PaddingForPageControllerButton() }
        }
    } else { EmptyUI() }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun DetailItems(lessons: lessons, vm : NetWorkViewModel, hazeState: HazeState) {

    val lists = lessons

    val sheetState_FailRate = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_FailRate by remember { mutableStateOf(false) }

    if (showBottomSheet_FailRate) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_FailRate = false },
            sheetState = sheetState_FailRate,
            shape = bottomSheetRound(sheetState_FailRate)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    BottomSheetTopBar("挂科率 ${lessons.course.nameZh}")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    ApiToFailRate(lessons.course.nameZh,vm, hazeState =hazeState )
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
            shape = bottomSheetRound(sheetState_Teacher)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    BottomSheetTopBar("教师检索 $teacherTitle")
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
                        TransplantListItem(
                            overlineContent = { Text("类型") },
                            headlineContent = { lists.courseType.nameZh.let { Text(it) } },

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
                        if(lists.scheduleWeeksInfo != null)
                            TransplantListItem(
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
                        if(lists.stdCount != null)
                            TransplantListItem(
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
                        if(lists.course.credits != null)
                            TransplantListItem(
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
                            TransplantListItem(
                                overlineContent = { Text("教师 " + if(teacherNum == 1) "" else (i+1).toString()) },
                                headlineContent = {
                                    if (teacherList != null) {
                                        Text( teacherList.teacher.person?.nameZh.toString() )
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
                            TransplantListItem(
                                headlineContent = {
                                    if (teacherList != null) {
                                        val t = teacherList.teacher.title?.nameZh ?: "未知"
                                        ScrollText( t  +" " + (teacherList.teacher?.type?.nameZh ?: ""))
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
                        var department = lists.openDepartment.nameZh
                        if(department.contains("（")) department = department.substringBefore("（")
                        TransplantListItem(
                            overlineContent = { Text("开设学院") },
                            headlineContent = { Text(department) },

                            //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                            leadingContent = {
                                DepartmentIcons(name = department)
                            },
                            modifier = Modifier
                                .clickable {}
                                .weight(.5f),
                        )
                        TransplantListItem(
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
                        val code = lists.code
                        val classes = if(code.contains("--")) code.substringAfter("--") else null
                        val classCode = if(code.contains("--")) code.substringBefore("--") else null
                        TransplantListItem(
                            overlineContent = { Text("课程代码--教学班") },
                            headlineContent = { Text(code) },

                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.tag),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier
                                .clickable {
                                    ClipBoardUtils.copy(code)
                                }
                                .weight(.5f),
                        )
                        TransplantListItem(
                            overlineContent = classes?.let{ { Text(it + "班") } },
                            headlineContent = { Text("教学班对比") },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.compare_arrows),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier
                                .clickable {
                                    showToast("正在开发")
                                }
                                .weight(.5f),
                        )
                    }
                    Row {
                        TransplantListItem(
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
                        TransplantListItem(
                            headlineContent = { Text("同班同学") },
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
                                .clickable { showToast("请前往 合工大教务 微信公众号") }
                                .weight(.5f),
                        )
//                        ListItem(
//                            headlineContent = { Text("添加到收藏") },
//                            trailingContent = {
//                            },
//                            //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
//                            leadingContent = {
//                                Icon(
//                                    painterResource(id = R.drawable.star),
//                                    contentDescription = "Localized description",
//                                )
//                            },
//                            modifier = Modifier
//                                .clickable { MyToast("正在开发") }
//                                .weight(.5f),
//                        )
                    }
                    if(lists.nameZh != null)
                        TransplantListItem(
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
                        TransplantListItem(
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
                        TransplantListItem(
                            overlineContent = { Text("备注") },
                            headlineContent = { Text(lists.remark) },
                            trailingContent = {
                            },
                            //supportingContent = { Text(text = "班级 " + getCourse()[item].className)},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.info),
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
    val list = mutableListOf<lessons>()

    try {
        if (json != null) {
            if(json.contains("lessonIds")) {
                val result = Gson().fromJson(json,lessonResponse::class.java).lessons
                return result.toMutableList()
            }
            else {
                val result = Gson().fromJson(json,CourseSearchResponse::class.java).data
                for (i in result.indices) {
                    val courses = result[i].lesson
                    list.add(courses)
                }
                return list
            }
        } else return list
    } catch (e : Exception) {
        return list
    }
}