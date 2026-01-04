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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.jxglstu.CourseBookBean
import com.hfut.schedule.logic.model.jxglstu.CourseSearchResponse
import com.hfut.schedule.logic.model.jxglstu.lessonResponse
import com.hfut.schedule.logic.model.jxglstu.lessons
import com.hfut.schedule.logic.network.repo.hfut.JxglstuRepository
import com.hfut.schedule.logic.network.repo.hfut.UniAppRepository
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.ClipBoardHelper
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.SmallCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.mixedCardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.icon.DepartmentIcons
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.hfut.schedule.ui.component.text.BottomSheetTopBar
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.lesson.JxglstuCourseTableSearch
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.ApiToFailRate
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.permit
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch.ApiForCourseSearch
import com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch.ApiToTeacherSearch
import com.hfut.schedule.ui.style.corner.bottomSheetRound
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.ColumnVertical
import com.xah.uicommon.style.align.RowHorizontal
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.flow.first

enum class TotalCourseDataSource {
    MINE,MINE_NEXT,SEARCH
}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseTotalUI(
    dataSource : TotalCourseDataSource,
    sortType: Boolean,
    vm : NetWorkViewModel,
    hazeState: HazeState,
    ifSaved : Boolean,
    state: LazyListState = rememberLazyListState()
) {
    val courseBookJson by DataStoreManager.courseBookJson.collectAsState(initial = "")
    if(dataSource != TotalCourseDataSource.SEARCH && ifSaved == false) {
        LaunchedEffect(Unit) {
            if(vm.courseBookResponse.state.first() is UiState.Success) return@LaunchedEffect
            val term = SemesterParser.getSemester()
            val cookie = getJxglstuCookie() ?: return@LaunchedEffect
            when(dataSource) {
                TotalCourseDataSource.MINE -> vm.getCourseBook(cookie,term)
                TotalCourseDataSource.MINE_NEXT -> vm.getCourseBook(cookie, SemesterParser.plusSemester(term))
                else -> return@LaunchedEffect
            }
        }
    }
    var courseBookData: Map<Long, CourseBookBean> by remember { mutableStateOf(emptyMap()) }

    val list by produceState(initialValue = emptyList<lessons>(),key1 = dataSource) {
        when(dataSource) {
            TotalCourseDataSource.MINE -> {
                prefs.getString("courses","")?.let { value = JxglstuRepository.parseDatumCourse(it) }
            }
            TotalCourseDataSource.SEARCH -> {
                onListenStateHolder(vm.courseSearchResponse) { data ->
                    value = data
                }
            }
            TotalCourseDataSource.MINE_NEXT -> {
                prefs.getString("coursesNext","")?.let { value = JxglstuRepository.parseDatumCourse(it) }
            }
        }
    }

    LaunchedEffect(courseBookJson) {
//         是JSON
        if(courseBookJson.contains("{")) {
            val data = JxglstuRepository.parseCourseBook(courseBookJson)
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
        if(!isSearch) {
            CustomTextField(
                input = input,
                label = { Text("搜索 学院、课程、代码、类型")},
                leadingIcon = {
                    Icon(painterResource(R.drawable.search),null)
                }
            ) { input = it }
            Spacer(Modifier.height(CARD_NORMAL_DP))
        }
        LazyColumn(state = state) {
            item { TermFirstlyInfo(list) }
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
                    cardModifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null),
                    modifier = Modifier.clickable {
                        showBottomSheet = true
                        numItem = item
                    },
                    index = item
                )
            }
            if(isSearch)
                item { PaddingForPageControllerButton() }
        }
    } else { EmptyIcon() }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun DetailItems(
    lessons: lessons,
    vm : NetWorkViewModel,
    hazeState: HazeState,
    courseBookData : Map<Long, CourseBookBean>,
) {

    var showBottomSheetSchedule by remember { mutableStateOf(false) }

    if(showBottomSheetSchedule) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheetSchedule = false },
            showBottomSheet = showBottomSheetSchedule,
            hazeState = hazeState
        ) {
            var showAll by remember { mutableStateOf(false) }
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("开课课程表 ${lessons.course.nameZh}") {
                        FilledTonalIconButton (onClick = { showAll = !showAll }) {
                            Icon(painter = painterResource(id = if (showAll) R.drawable.collapse_content else R.drawable.expand_content), contentDescription = "")
                        }
                    }
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    JxglstuCourseTableSearch(showAll,vm,hazeState,innerPadding,listOf(lessons)) {
                        showAll = it
                    }
                }
            }
        }
    }

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

    var showBottomSheet_Search by remember { mutableStateOf(false) }

    ApiForCourseSearch(vm,null, lessons.code.substringBefore("--"),showBottomSheet_Search, hazeState = hazeState) {
        showBottomSheet_Search = false
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

    var showBottomSheetClassmates by remember { mutableStateOf(false) }
    if(showBottomSheetClassmates) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheetClassmates = false },
            showBottomSheet = showBottomSheetClassmates,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("同班同学 ${lessons.course.nameZh}")
                },
            ) { innerPadding ->
                Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    ClassmatesScreen(vm,lessons.id.toString())
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
                                    painterResource(R.drawable.kid_star),
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
                    if(lessons.teacherAssignmentList != null) {
                        val teacherNum = lessons.teacherAssignmentList.size
                        if(teacherNum > 0) {
                            PaddingHorizontalDivider(isDashed = true)
                        }

                        val onlyShowName = lessons.teacherAssignmentList.find {
                            !(it.teacher == null && it.age == null)
                        } == null

                        if(onlyShowName) {
                            for (i in 0 until teacherNum step 2) {
                                val teacherList = lessons.teacherAssignmentList[i]
                                RowHorizontal  {
                                    TransplantListItem(
                                        overlineContent = { Text("教师 " + if(teacherNum == 1) "" else (i+1).toString()) },
                                        headlineContent = {
                                            Text( teacherList.person.nameZh )
                                        },
                                        leadingContent = {
                                            Icon(
                                                painterResource(R.drawable.person),
                                                contentDescription = "Localized description",
                                            )
                                        },
                                        modifier = Modifier
                                            .weight(.5f)
                                            .clickable {
                                            permit = 1
                                            teacherTitle = teacherList.person.nameZh
                                            showBottomSheet_Teacher = true
                                        }
                                    )
                                    if(i+1 < teacherNum) {
                                        val teacherList2 = lessons.teacherAssignmentList[i+1]
                                        TransplantListItem(
                                            overlineContent = { Text("教师 " + (i+1+1).toString()) },
                                            headlineContent = {
                                                Text( teacherList2.person.nameZh )
                                            },
                                            leadingContent = {
                                                Icon(
                                                    painterResource(R.drawable.person),
                                                    contentDescription = "Localized description",
                                                )
                                            },
                                            modifier = Modifier
                                                .weight(.5f)
                                                .clickable {
                                                    permit = 1
                                                    teacherTitle = teacherList2.person.nameZh
                                                    showBottomSheet_Teacher = true
                                                }
                                        )
                                    }
                                }
                            }
                        } else {
                            for (i in 0 until teacherNum) {
                                val teacherList = lessons.teacherAssignmentList[i]

                                Row(modifier = Modifier.clickable {
                                    permit = 1
                                    teacherTitle = teacherList.person.nameZh
                                    showBottomSheet_Teacher = true
                                }) {

                                    TransplantListItem(
                                        overlineContent = { Text("教师 " + if(teacherNum == 1) "" else (i+1).toString()) },
                                        headlineContent = {
                                            Text( teacherList.person.nameZh )
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
                                            val t = teacherList.teacher?.title?.nameZh ?: "未知"
                                            ScrollText( t  +" " + (teacherList.teacher?.type?.nameZh ?: ""))
                                        },
                                        overlineContent = {
                                            Text(text =
                                                teacherList.age?.let { age ->
                                                    if(age < 100) {
                                                        "年龄 $age"
                                                    } else {
                                                        "年龄未知"
                                                    }
                                                } ?: "年龄未知"
                                            )
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
                        }

                        if(teacherNum > 0) {
                            PaddingHorizontalDivider(isDashed = true)
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
                            overlineContent = { Text("考察方式") },
                            headlineContent = { Text(lessons.examMode.nameZh) },
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
                                    ClipBoardHelper.copy(code)
                                }
                                .weight(.5f),
                        )
                        TransplantListItem(
                            headlineContent = { Text("教学班对比") },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.group_search),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier
                                .clickable {
                                    showBottomSheet_Search = true
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
                                .clickable {
                                    showBottomSheetClassmates = true
                                }
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
                        val text = if(t1 != null && t2 == null) {
                            t1
                        } else if(t2 != null && t1 == null) {
                            t2
                        } else if(t2 != null && t1 != null) {
                            t1 + "\n" + t2
                        } else {
                            ""
                        }
                        if(!(t1 == null && t2 == null)) {
                            TransplantListItem(
                                overlineContent = { Text("教材") },
                                headlineContent = {
                                    Text(text)
                                },
                                leadingContent = {
                                    Icon(
                                        painterResource(R.drawable.book_5),
                                        contentDescription = "Localized description",
                                    )
                                },
                                modifier = Modifier.clickable {
                                    ClipBoardHelper.copy(text)
                                },
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
                            modifier = Modifier.clickable {
                                showBottomSheetSchedule = true
                            }
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

@Composable
fun ClassmatesScreen(
    vm: NetWorkViewModel,
    lessonId : String
) {
    val uiState by vm.classmatesResp.state.collectAsState()
    val refreshNetwork = suspend m@ {
        var cookie = DataStoreManager.uniAppJwt.first()
        if(cookie.isEmpty() || cookie.isEmpty()) {
            val loginResult = UniAppRepository.login()
            if(loginResult == false) {
                return@m
            }
            cookie = DataStoreManager.uniAppJwt.first()
        }
        vm.classmatesResp.clear()
        vm.getClassmates(lessonId, cookie)
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    var input by remember { mutableStateOf("") }
    CustomTextField(
        input = input,
        leadingIcon = {
            Icon(painterResource(R.drawable.search),null)
        },
        label = { Text("搜索 学号、班级、姓名") }
    ) { input = it }
    Spacer(Modifier.height(CARD_NORMAL_DP))

    CommonNetworkScreen(
        uiState = uiState,
        onReload = refreshNetwork,
    ) {
        val list = (uiState as UiState.Success).data

        // 初始化统计Map
        val classCount = mutableMapOf<String, Int>()
        val genderCount = mutableMapOf<String, Int>()
        val yearCount = mutableMapOf<String, Int>()

        // 一次遍历处理所有统计
        for (item in list) {
            // 统计 className
            classCount[item.className] = classCount.getOrDefault(item.className, 0) + 1

            // 统计 gender
            genderCount[item.gender] = genderCount.getOrDefault(item.gender, 0) + 1

            // 统计年份（取code的前四位）
            val year = item.code.substring(0, 4)
            yearCount[year] = yearCount.getOrDefault(year, 0) + 1
        }

        // 将统计结果转换为 Pair 并排序
        val classes = classCount.entries
            .map { it.value to it.key }
            .sortedByDescending { it.first }

        val genders = genderCount.entries
            .map { it.value to it.key }
            .sortedByDescending { it.first }

        val years = yearCount.entries
            .map { it.value to it.key }
            .sortedByDescending { it.first }

        val filteredList = list.filter {
            it.code.startsWith(input) || it.nameZh.contains(input) || it.className.contains(input) || it.gender.contains(input)
        }

        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP - CARD_NORMAL_DP)){
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    modifier = Modifier.padding(vertical = CARD_NORMAL_DP, horizontal = CARD_NORMAL_DP),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column {
                        TransplantListItem(
                            headlineContent = {
                                Text(
                                    text = "${list.size}人",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 28.sp,
                                    modifier = Modifier.padding(top = APP_HORIZONTAL_DP/6, bottom = 0.dp)
                                )
                            }
                        )
                        // 班级
                        LazyRow() {
                            item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                            items(classes.size) { index ->
                                val item = classes[index]
                                AssistChip(
                                    onClick = { input = item.second },
                                    border = null,
                                    colors = AssistChipDefaults.assistChipColors(containerColor = mixedCardNormalColor()),
                                    label = { Text(item.second) },
                                    trailingIcon = {
                                        Text("x" + item.first.toString() )
                                    },
                                    modifier = Modifier.padding(end = if(index == classes.size-1) 0.dp else CARD_NORMAL_DP*2)
                                )
                            }
                            item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                        }
                        // 男女
                        LazyRow() {
                            item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                            items(genders.size) { index ->
                                val item = genders[index]
                                AssistChip(
                                    onClick = { input = item.second },
                                    border = null,
                                    colors = AssistChipDefaults.assistChipColors(containerColor = mixedCardNormalColor()),
                                    label = { Text(item.second) },
                                    trailingIcon = {
                                        Text("x" + item.first.toString() )
                                    },
                                    modifier = Modifier.padding(end = if(index == genders.size-1) 0.dp else CARD_NORMAL_DP*2)
                                )
                            }
                            item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                        }
                        // 学号前4位
                        LazyRow(modifier = Modifier.padding(bottom = CARD_NORMAL_DP*3)) {
                            item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                            items(years.size) { index ->
                                val item = years[index]
                                AssistChip(
                                    onClick = { input = item.second },
                                    border = null,
                                    colors = AssistChipDefaults.assistChipColors(containerColor = mixedCardNormalColor()),
                                    label = { Text(item.second) },
                                    trailingIcon = {
                                        Text("x" + item.first.toString() )
                                    },
                                    modifier = Modifier.padding(end = if(index == years.size-1) 0.dp else CARD_NORMAL_DP*2)
                                )
                            }
                            item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                        }
                    }
                }
            }
            items(filteredList.size,key = { filteredList[it].code }) { index ->
                val item = filteredList[index]
                SmallCard(modifier = Modifier.padding(horizontal = CARD_NORMAL_DP, vertical = CARD_NORMAL_DP)) {
                    TransplantListItem(
                        headlineContent = {
                            ScrollText(item.nameZh)
                        },
                        overlineContent = {
                            ScrollText(item.code)
                        },
                        supportingContent = {
                            ScrollText(item.className)
                        },
                        trailingContent = {
                            ScrollText(item.gender)
                        }
                    )
                }
            }
        }
    }
}