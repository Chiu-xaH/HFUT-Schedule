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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.collectAsState
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
import com.hfut.schedule.logic.model.jxglstu.CourseBookBean
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.model.jxglstu.CourseSearchResponse
import com.hfut.schedule.logic.model.jxglstu.lessonResponse
import com.hfut.schedule.logic.model.jxglstu.lessons
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.SemseterParser
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.permit
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.ApiToFailRate
import com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch.ApiToTeacherSearch
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.text.BottomSheetTopBar
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.status.EmptyUI
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.style.bottomSheetRound
import com.hfut.schedule.ui.component.text.ScrollText
import com.hfut.schedule.ui.component.icon.DepartmentIcons
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.screen.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.divider.ScrollHorizontalDivider
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.style.ColumnVertical
import com.hfut.schedule.ui.style.HazeBottomSheet
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.flow.first
import kotlin.text.contains

enum class TotalCourseDataSource {
    MINE,MINE_NEXT,SEARCH
}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseTotalUI(dataSource : TotalCourseDataSource, sortType: Boolean, vm : NetWorkViewModel, hazeState: HazeState,ifSaved : Boolean) {
    val courseBookJson by DataStoreManager.courseBookJson.collectAsState(initial = "")
    if(dataSource != TotalCourseDataSource.SEARCH && ifSaved == false) {
        LaunchedEffect(Unit) {
            if(vm.courseBookResponse.state.first() is UiState.Success) return@LaunchedEffect
            val term = SemseterParser.getSemseter()
            val cookie = getJxglstuCookie(vm) ?: return@LaunchedEffect
            when(dataSource) {
                TotalCourseDataSource.MINE -> vm.getCourseBook(cookie,term)
                TotalCourseDataSource.MINE_NEXT -> vm.getCourseBook(cookie, SemseterParser.plusSemseter(term))
                else -> return@LaunchedEffect
            }
        }
    }
    var courseBookData: Map<Long, CourseBookBean> by remember { mutableStateOf(emptyMap()) }

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

    LaunchedEffect(courseBookJson) {
//         是JSON
        if(courseBookJson.contains("{")) {
            val data = vm.parseCourseBook(courseBookJson)
            courseBookData = data
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
                    DetailItems(sortList[numItem],vm,hazeState,courseBookData)
                }
            }
        }
    }
    if(list.isNotEmpty()) {
        val state = rememberLazyListState()
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
        LazyColumn(state = state) {
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
fun DetailItems(lessons: lessons, vm : NetWorkViewModel, hazeState: HazeState,courseBookData : Map<Long, CourseBookBean>) {

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
                        .fillMaxSize()
                ) {
                    ApiToFailRate(lessons.course.nameZh,vm, hazeState =hazeState ,innerPadding)
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
                    modifier = Modifier.fillMaxSize()
                ) {
                    ApiToTeacherSearch(teacherTitle,vm,innerPadding)
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
                            headlineContent = { lessons.courseType.nameZh.let { Text(it) } },
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
                        lessons.scheduleWeeksInfo?.let {
                            TransplantListItem(
                                overlineContent = { Text("周数") },
                                headlineContent = { ScrollText(it) },
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
                    }

                    Row {
                        lessons.stdCount?.let {
                            TransplantListItem(
                                overlineContent = { Text("人数" ) },
                                headlineContent = { Text(it.toString()) },
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
                        }
                        lessons.course.credits?.let {
                            TransplantListItem(
                                overlineContent = { Text("学分") },
                                headlineContent = { Text(it.toString()) },
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

                    }
                    val teacherNum = lessons.teacherAssignmentList?.size ?: 0
                    for (i in 0 until teacherNum) {
                        val teacherList = lessons.teacherAssignmentList?.get(i)
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
                                        ScrollText( t  +" " + (teacherList.teacher.type?.nameZh ?: ""))
                                    }
                                },
                                overlineContent = {
                                    if (teacherList != null) {
                                        Text(text =  if(teacherList.age  != null)  "年龄 " + teacherList.age else "年龄未知")
                                    }
                                },
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
                        var department = lessons.openDepartment.nameZh
                        if(department.contains("（")) department = department.substringBefore("（")
                        TransplantListItem(
                            overlineContent = { Text("开设学院") },
                            headlineContent = { Text(department) },
                            leadingContent = {
                                DepartmentIcons(name = department)
                            },
                            modifier = Modifier
                                .clickable {}
                                .weight(.5f),
                        )
                        TransplantListItem(
                            overlineContent = { Text("考察方式 ${lessons.examMode.nameZh.toString() }") },
                            headlineContent = { ScrollText(if(lessons.planExamWeek != null)"预计第${lessons.planExamWeek.toString()}周" else "无时间") },
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
                        val code = lessons.code
                        val classes = if(code.contains("--")) code.substringAfter("--") else null
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
                            overlineContent = classes?.let{ { Text("当前为" + it + "班") } },
                            headlineContent = { Text("全部教学班") },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.group_search),
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
                            headlineContent = { Text(text = AppNavRoute.FailRate.label) },

                            leadingContent = {
                                Icon(
                                    painterResource(AppNavRoute.FailRate.icon),
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
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.groups),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier
                                .clickable { showToast("请前往 合工大教务 微信公众号") }
                                .weight(.5f),
                        )
                    }
                    lessons.nameZh?.let {
                        TransplantListItem(
                            overlineContent = { Text("班级") },
                            headlineContent = { Text(it) },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.sensor_door),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier.clickable {},
                        )
                    }
                    courseBookData[lessons.course.id]?.let {
                        var t1 : String? = if(it.textbook.isEmpty() || it.textbook.isBlank()) {
                            null
                        } else {
                            "基本教材: " + it.textbook.replace("<br/>"," ").replace("<br>"," ")
                        }
                        var t2 : String? = if(it.specialTextbook.isEmpty() || it.specialTextbook.isBlank()) {
                            null
                        } else {
                            "辅助教材: " + it.specialTextbook.replace("<br/>"," ").replace("<br>"," ")
                        }
                        if(!(t1 == null && t2 == null)) {
                            TransplantListItem(
                                overlineContent = { Text("教材") },
                                headlineContent = {
                                    Text(
                                        if(t1 != null && t2 == null) {
                                            t1
                                        } else if(t2 != null && t1 == null) {
                                            t2
                                        } else if(t2 != null && t1 != null) {
                                            t1 + "\n" + t2
                                        } else {
                                            ""
                                        }
                                    )
                                },
                                leadingContent = {
                                    Icon(
                                        painterResource(R.drawable.book),
                                        contentDescription = "Localized description",
                                    )
                                },
                                modifier = Modifier.clickable {},
                            )
                        }
                    }
                    lessons.scheduleText.dateTimePlacePersonText.textZh?.let {
                        TransplantListItem(
                            overlineContent = { Text("上课安排") },
                            headlineContent = { Text(it) },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.schedule),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier.clickable {}
                        )
                    }
                    lessons.remark?.let {
                        TransplantListItem(
                            overlineContent = { Text("备注") },
                            headlineContent = { Text(it) },
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
        item { Spacer(Modifier.height(APP_HORIZONTAL_DP/2)) }
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