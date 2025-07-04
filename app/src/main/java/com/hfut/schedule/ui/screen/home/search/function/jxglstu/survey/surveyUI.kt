package com.hfut.schedule.ui.screen.home.search.function.jxglstu.survey

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.jxglstu.forStdLessonSurveySearchVms
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.SemseterParser.getSemseter
import com.hfut.schedule.logic.util.parse.SemseterParser.parseSemseter
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.AnimationCardListItem
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.DepartmentIcons
import com.hfut.schedule.ui.component.EmptyUI
import com.hfut.schedule.ui.component.custom.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.PaddingForPageControllerButton
 
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyUI(vm : NetWorkViewModel, hazeState: HazeState,code : String?= null) {

    var semester by remember { mutableIntStateOf(getSemseter()) }

    val uiState by vm.surveyListData.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        val cookie = getJxglstuCookie(vm)
        cookie?.let {
            vm.surveyListData.clear()
            vm.getSurveyList(it,semester)
        }
    }

    LaunchedEffect(semester) {
        refreshNetwork()
    }

    val scope = rememberCoroutineScope()
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        Box(modifier = Modifier.fillMaxSize()) {
            CourseSurveyListUI(vm,hazeState = hazeState, scope,code,refresh = refreshNetwork)
            FloatingActionButton(
                onClick = { semester -= 20 },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP)
                ,
            ) { Icon(Icons.Filled.ArrowBack, "Add Button") }


            ExtendedFloatingActionButton(
                onClick = { scope.launch { refreshNetwork() } },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP)
            ) { Text(text = parseSemseter(semester),) }

            FloatingActionButton(
                onClick = { semester += 20 },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP)
            ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
        }
    }
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseSurveyListUI(vm : NetWorkViewModel, hazeState: HazeState, scope: CoroutineScope, code : String?, refresh : suspend () -> Unit) {
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
    val filteredList = list.filter { code == null || it.code == code }


    if(filteredList.isNotEmpty())
        LazyColumn {
            items(filteredList.size) { item ->
                val listItem = filteredList[item]
                AnimationCardListItem(
                    leadingContent = { DepartmentIcons(listItem.openDepartment.nameZh) },
                    trailingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "") },
                    headlineContent = {
                        Text(listItem.course.nameZh)
                    },
                    overlineContent = { Text(listItem.code) },
                    modifier = Modifier.clickable {
                        data = listItem
                        showBottomSheet = true
                    },
                    index = item
                )
            }
            item {
                PaddingForPageControllerButton()
            }
        }
    else {
        Column {
            EmptyUI()
            PaddingForPageControllerButton()
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
                AnimationCardListItem(
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
                    index = item
                )
            }
            item {
                Spacer(Modifier.height(APP_HORIZONTAL_DP).navigationBarsPadding())
            }
        }
    else {
        Column {
            EmptyUI()
        }
    }
}


