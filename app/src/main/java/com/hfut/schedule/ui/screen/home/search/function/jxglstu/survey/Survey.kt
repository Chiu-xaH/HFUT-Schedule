package com.hfut.schedule.ui.screen.home.search.function.jxglstu.survey

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.PostMode
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.style.HazeBottomSheet
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Survey(ifSaved : Boolean, vm : NetWorkViewModel, hazeState: HazeState){
    var showBottomSheet by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { Text(text = "评教")},
        leadingContent = { Icon(painter = painterResource(id = R.drawable.verified), contentDescription = "")},
        modifier = Modifier.clickable {
            if(ifSaved) refreshLogin() else
                showBottomSheet = true
        }
    )
    if (showBottomSheet) {
        var refresh by remember { mutableStateOf(false) }
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("评教") {
                        SurveyAllButton(vm) {
                            refresh = !refresh
                        }
                    }
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    SurveyUI(vm,hazeState,refresh)
                }
            }
        }
    }
}
@Composable
fun SurveyAllButton(vm: NetWorkViewModel,refresh : suspend () -> Unit) {
    val surveyListData by vm.surveyListData.state.collectAsState()
    val surveyData by vm.surveyData.state.collectAsState()
    val scope = rememberCoroutineScope()
    val refreshOne: suspend (Int) -> Unit = { id : Int ->
        val cookie = getJxglstuCookie(vm)
        cookie?.let {
            vm.surveyData.clear()
            vm.getSurveyToken(it,id.toString())
            vm.getSurvey(it,id.toString())
        }
    }
    var loading by remember { mutableStateOf(false) }
    FilledTonalButton(
        onClick = {
            // 未评教的教师们
            scope.launch(Dispatchers.IO) {
                val list = (surveyListData as UiState.Success).data.flatMap { it.lessonSurveyTasks }.filter { it.submitted == false }
                if(list.isEmpty()) {
                    showToast("无未完成的评教")
                    refresh()
                    return@launch
                }
                loading = true
                for(task in list) {
                    // 获取下一个教师
                    refreshOne(task.id)
                    val bean = (surveyData as? UiState.Success)?.data
                    if(bean == null) {
                        showToast("失败")
                        loading = false
                        break
                    }
                    // 发送教评
                    postSurvey(vm, PostMode.GOOD,bean)
                }
                loading = false
                refresh()
            }
        },
        enabled = surveyListData is UiState.Success && !loading
    ) {
        if(loading) {
            LoadingIcon()
        } else {
            Text("全部评教(100分)")
        }
    }
}

