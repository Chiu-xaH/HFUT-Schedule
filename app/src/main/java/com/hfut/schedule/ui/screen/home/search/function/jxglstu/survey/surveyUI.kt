package com.hfut.schedule.ui.screen.home.search.function.jxglstu.survey

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.UiState
import com.hfut.schedule.logic.util.parse.SemseterParser.getSemseter
import com.hfut.schedule.logic.util.parse.SemseterParser.parseSemseter
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.AnimationCardListItem
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.EmptyUI
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyUI(vm : NetWorkViewModel, hazeState: HazeState) {

    var semester by remember { mutableIntStateOf(getSemseter()) }

    val uiState by vm.surveyListData.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        val cookie = if (!vm.webVpn) prefs.getString(
            "redirect",
            ""
        ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")

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
            TeacherSurveyListUI(vm,hazeState = hazeState, refresh = refreshNetwork)
            FloatingActionButton(
                onClick = { semester -= 20 },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = appHorizontalDp(), vertical = appHorizontalDp())
                ,
            ) { Icon(Icons.Filled.ArrowBack, "Add Button") }


            ExtendedFloatingActionButton(
                onClick = { scope.launch { refreshNetwork() } },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = appHorizontalDp(), vertical = appHorizontalDp())
            ) { Text(text = parseSemseter(semester),) }

            FloatingActionButton(
                onClick = { semester += 20 },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(horizontal = appHorizontalDp(), vertical = appHorizontalDp())
            ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
        }
    }
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherSurveyListUI(vm : NetWorkViewModel, hazeState: HazeState, refresh : suspend () -> Unit) {
//    val list =  getSurveyList(vm)
    val uiState by vm.surveyListData.state.collectAsState()
    val list = (uiState as UiState.Success).data
    var showBottomSheet by remember { mutableStateOf(false) }
    var id by remember { mutableIntStateOf(0) }

    val scope = rememberCoroutineScope()
    if (showBottomSheet) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet,
            isFullExpand = true,
            autoShape = false
//            sheetState = sheetState,
//            shape = bottomSheetRound(sheetState)
        ) {
            Column {
                HazeBottomSheetTopBar("发送教评", isPaddingStatusBar = false)
                SurveyInfoUI(id,vm) {
                    showBottomSheet = false
                    scope.launch { refresh() }
                }
            }
        }
    }

    if(list.isNotEmpty())
        LazyColumn {
            items(list.size) { item ->
//                MyCustomCard{
                    AnimationCardListItem(
                        headlineContent = { list[item].teacher.person?.let { Text(text = it.nameZh) } },
                        leadingContent = { Icon(painterResource(R.drawable.person), contentDescription = "Localized description",) },
                        trailingContent = { if(!list[item].submitted) Icon(Icons.Filled.ArrowForward, contentDescription = "") else Text(text = "已评") },
                        modifier = Modifier.clickable {
                            if(!list[item].submitted) {
                                id = list[item].id
                                SharedPrefs.saveInt("teacherID",id)
                                showBottomSheet = true
                            } else showToast("已评教")
                        },
                        index = item
                    )
//                }
            }
            item {
                PaddingForPageControllerButton()
            }
        }
    else {
        Column {

            //      Scaffold {
            EmptyUI()
            PaddingForPageControllerButton()
        }

        //      }
    }
}


