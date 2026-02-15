package com.hfut.schedule.ui.screen.home.search.function.jxglstu.survey

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.jxglstu.forStdLessonSurveySearchVms
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.SemesterParser.getSemester
import com.hfut.schedule.logic.util.parse.SemesterParser.parseSemester
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.xah.uicommon.style.APP_HORIZONTAL_DP

import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.icon.DepartmentIcons
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
 
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CardListItem

import com.hfut.schedule.ui.component.container.CustomCard
import com.xah.uicommon.style.align.CenterScreen
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyUI(vm : NetWorkViewModel, hazeState: HazeState,refresh : Boolean,innerPadding : PaddingValues,code : String?= null) {

    var semester by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(Unit) {
        semester = getSemester()
    }
    val uiState by vm.surveyListData.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        if(semester != null) {
            val cookie = getJxglstuCookie()
            cookie?.let {
                vm.surveyListData.clear()
                vm.getSurveyList(it, semester!!)
            }
        }
    }

    LaunchedEffect(semester,refresh) {
        if(semester != null)
            refreshNetwork()
    }
    val listState = rememberLazyListState()

    val scope = rememberCoroutineScope()
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        Box(modifier = Modifier.fillMaxSize()) {
            CourseSurveyListUI(listState,vm,hazeState = hazeState, scope,code,refresh = refreshNetwork,innerPadding)
            semester?.let { page ->
                val currentSemester by produceState<Int?>(initialValue = null) {
                    value = getSemester()
                }
                PageController(
                    listState = listState,
                    currentPage = page,
                    onNextPage = { semester = it },
                    onPreviousPage = { semester = it },
                    gap = 20,
                    text = parseSemester(page),
                    range = Pair(null,null),
                    resetPage = currentSemester ?: -1
                )
            }
        }
    }
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseSurveyListUI(
    state : LazyListState,
    vm : NetWorkViewModel,
    hazeState: HazeState,
    scope: CoroutineScope,
    code : String?,
    refresh : suspend () -> Unit,
    innerPadding : PaddingValues
) {
    val uiState by vm.surveyListData.state.collectAsState()
    val list = (uiState as UiState.Success).data
    var showBottomSheet by remember { mutableStateOf(false) }

    var data by remember { mutableStateOf<forStdLessonSurveySearchVms?>(null) }
    if (showBottomSheet && data != null) {
        with(data!!) {
            HazeBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                hazeState = hazeState,
                showBottomSheet = showBottomSheet,
                isFullExpand = true,
                autoShape = false
            ) {
                Column {
                    HazeBottomSheetTopBar(course.nameZh, isPaddingStatusBar = false)
                    TeacherSurveyListUI(this@with,vm,hazeState,scope,refresh)
                }
            }
        }
    }
    val filteredList = list.filter { code == null || it.code == code }.sortedByDescending { item ->
        val tasks = item.lessonSurveyTasks
        if (tasks.isEmpty()) 0.0
        else tasks.count { it.submitted } / tasks.size.toDouble()
    }.reversed()


    var showBottomSheet_start by remember { mutableStateOf(false) }
    var tId by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }

    if (showBottomSheet_start) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet_start = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_start,
            isFullExpand = true,
            autoShape = false
        ) {
            Column {
                HazeBottomSheetTopBar("评教 $name", isPaddingStatusBar = false)
                SurveyInfoUI(tId,vm,scope) {
                    showBottomSheet_start = false
                    scope.launch { refresh() }
                }
            }
        }
    }


    if(filteredList.isNotEmpty())
        LazyColumn(state = state ) {
            item { InnerPaddingHeight(innerPadding,true) }
            items(filteredList.size) { item ->
                val listItem = filteredList[item]
                val teachers = listItem.lessonSurveyTasks
                val submittedMap = teachers.map { it.submitted }
                val submittedCount = submittedMap.filter { it == true }.size
                val allSubmitted = submittedCount == submittedMap.size
                CustomCard(
                    color = cardNormalColor(),
                ) {
                    Column {
                        TransplantListItem(
                            leadingContent = { DepartmentIcons(listItem.openDepartment.nameZh) },
                            headlineContent = {
                                Text(listItem.course.nameZh,textDecoration = if(allSubmitted) TextDecoration.LineThrough else TextDecoration.None)
                            },
                            trailingContent = { Text( if(!allSubmitted) submittedCount.toString() + "/" + submittedMap.size else "已评")},
                            overlineContent = { Text(listItem.code,textDecoration = if(allSubmitted) TextDecoration.LineThrough else TextDecoration.None) },
                            modifier = Modifier.clickable {
                                data = listItem
                                showBottomSheet = true
                            },
                        )
                        PaddingHorizontalDivider()
                        for(i in teachers.indices step 2) {
                            val t1 = teachers[i]
                            Row {
                                with(t1) {
                                    val isSubmitted = submitted
                                    val tName = teacher.person?.nameZh
                                    TransplantListItem(
                                        headlineContent = { tName?.let { Text(text = it, textDecoration = if(isSubmitted) TextDecoration.LineThrough else TextDecoration.None,fontWeight = if (!isSubmitted) FontWeight.Bold else FontWeight.Light) } },
                                        leadingContent = { Icon(painterResource(if(!isSubmitted)R.drawable.person else R.drawable.check),null) },
                                        modifier = Modifier.weight(.5f).clickable {
                                            if(!isSubmitted) {
                                                name = tName ?: ""
                                                tId = id
//                                                SharedPrefs.saveInt("teacherID",id)
                                                showBottomSheet_start = true
                                            } else showToast("已评教")
                                        },
                                    )
                                }
                                if(i+1 < teachers.size) {
                                    val t2 = teachers[i+1]
                                    with(t2) {
                                        val isSubmitted = submitted
                                        val tName = teacher.person?.nameZh
                                        TransplantListItem(
                                            headlineContent = { tName?.let { Text(text = it,textDecoration = if(isSubmitted) TextDecoration.LineThrough else TextDecoration.None,fontWeight = if (!isSubmitted) FontWeight.Bold else FontWeight.Light) } },
                                            leadingContent = { Icon(painterResource(if(!isSubmitted)R.drawable.person else R.drawable.check),null) },
                                            modifier = Modifier.weight(.5f).clickable {
                                                if(!isSubmitted) {
                                                    name = tName ?: ""
                                                    tId = id
//                                                    SharedPrefs.saveInt("teacherID",id)
                                                    showBottomSheet_start = true
                                                } else showToast("已评教")
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item {
                PaddingForPageControllerButton()
            }
            item { InnerPaddingHeight(innerPadding,false) }
        }
    else {
        CenterScreen {
            EmptyIcon()
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeacherSurveyListUI(data : forStdLessonSurveySearchVms,vm : NetWorkViewModel, hazeState: HazeState,  scope: CoroutineScope,refresh : suspend () -> Unit) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var id by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }

    if (showBottomSheet) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet,
            isFullExpand = true,
            autoShape = false
        ) {
            Column {
                HazeBottomSheetTopBar("评教 $name", isPaddingStatusBar = false)
                SurveyInfoUI(id,vm,scope) {
                    showBottomSheet = false
                    scope.launch { refresh() }
                }
            }
        }
    }
    val list = data.lessonSurveyTasks

    if(list.isNotEmpty())
        LazyColumn {
            items(list.size) { item ->
                val listItem = list[item]
                val isSubmitted = listItem.submitted
                val tName = listItem.teacher.person?.nameZh
                CardListItem(
                    headlineContent = { tName?.let { Text(text = it) } },
                    leadingContent = { Icon(painterResource(R.drawable.person),null) },
                    trailingContent = { if(!isSubmitted) Icon(Icons.Filled.ArrowForward, contentDescription = "") else Text(text = "已评") },
                    modifier = Modifier.clickable {
                        if(!isSubmitted) {
                            name = tName ?: ""
                            id = list[item].id
                            SharedPrefs.saveInt("teacherID",id)
                            showBottomSheet = true
                        } else showToast("已评教")
                    },
                )
            }
            item {
                Spacer(Modifier.height(APP_HORIZONTAL_DP).navigationBarsPadding())
            }
        }
    else {
        Column {
            EmptyIcon()
        }
    }
}


