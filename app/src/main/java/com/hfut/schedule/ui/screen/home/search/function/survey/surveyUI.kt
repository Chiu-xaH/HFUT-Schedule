package com.hfut.schedule.ui.screen.home.search.function.survey

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import com.hfut.schedule.ui.component.LoadingUI
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.enumeration.PostMode
import com.hfut.schedule.logic.util.parse.SemseterParser.parseSemseter
import com.hfut.schedule.logic.util.parse.SemseterParser.getSemseter
import com.hfut.schedule.logic.util.storage.SharePrefs
import com.hfut.schedule.logic.util.storage.SharePrefs.prefs
import com.hfut.schedule.ui.component.AnimationCardListItem
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.BottomSheetTopBar
import com.hfut.schedule.ui.component.EmptyUI
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.LittleDialog
import com.hfut.schedule.ui.component.MyCustomCard
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.bottomSheetRound
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyUI(vm : NetWorkViewModel, hazeState: HazeState) {
//    var expanded by remember { mutableStateOf(true) }
//    val sheetState = rememberModalBottomSheetState()
//    var showitem by remember { mutableStateOf(false) }
    val scrollstate = rememberLazyListState()
//    val shouldShowAddButton by remember { derivedStateOf { scrollstate.firstVisibleItemScrollOffset == 0 } }


    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(false) }
    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")
    var semsters = getSemseter()
    var semester by remember { mutableStateOf(semsters) }

    LaunchedEffect(semester) {
        refresh = true
    }

    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch {
            async{ cookie?.let { vm.getSurveyList(it, semester) } }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.surveyListData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("forStdLessonSurveySearchVms")) {
                                loading = false
                                refresh = false
                            }
                        }
                    }
                }
            }
        }
    }
    Box {
        AnimatedVisibility(
            visible = loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                LoadingUI()
            }
        }


        AnimatedVisibility(
            visible = !loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            teacherList(vm, hazeState  ,refresh = {refreshch -> refresh = refreshch})
        }


        AnimatedVisibility(
            visible = !loading,
            enter = scaleIn(),
            exit = scaleOut(),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = appHorizontalDp(), vertical = appHorizontalDp())
        ) {
            FloatingActionButton(
                onClick = {
                    semester -= 20
                    refresh = true
                },
            ) { Icon(Icons.Filled.ArrowBack, "Add Button") }
        }


        AnimatedVisibility(
            visible = !loading,
            enter = scaleIn(),
            exit = scaleOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = appHorizontalDp(), vertical = appHorizontalDp())
        ) {
            ExtendedFloatingActionButton(
                onClick = {refresh = true},
            ) { Text(text = parseSemseter(semester),) }
        }

        AnimatedVisibility(
            visible = !loading,
            enter = scaleIn(),
            exit = scaleOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = appHorizontalDp(), vertical = appHorizontalDp())
        ) {
            FloatingActionButton(
                onClick = {
                    semester += 20
                    refresh = true
                },
            ) { Icon(Icons.Filled.ArrowForward, "Add Button") }
        }
    }
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun teacherList(vm : NetWorkViewModel, hazeState: HazeState, refresh : (Boolean) -> Unit) {
    val list =  getSurveyList(vm)
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var postMode by remember { mutableStateOf(PostMode.NORMAL) }
    var showDialog by remember { mutableStateOf(false) }


    var id by remember { mutableStateOf(0) }


    if(showDialog) {
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                val result = selectMode(vm,postMode)
                if(result) {
                    showDialog = false
                    showBottomSheet = false
                    //在这里刷新
                    refresh
                }
            },
            dialogTitle = "确定提交",
            dialogText = "实名制上网,理性填表,不可修改",
            conformText = "提交",
            dismissText = "返回"
        )
    }
    if (showBottomSheet) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet,
            isFullExpand = false
//            sheetState = sheetState,
//            shape = bottomSheetRound(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("发送教评") {
                        Row(modifier = Modifier.padding(horizontal = appHorizontalDp())) {
                            FilledTonalIconButton(
                                onClick = {
                                    showDialog = true
                                    postMode = PostMode.GOOD
                                }) {
                                Icon(
                                    painter = painterResource(R.drawable.thumb_up),
                                    contentDescription = "description"
                                )
                            }
                            FilledTonalIconButton(
                                onClick = {
                                    showDialog = true
                                    // MyToast("下版本开放")
                                    postMode = PostMode.BAD
                                }) {
                                Icon(
                                    painter = painterResource(R.drawable.thumb_down),
                                    contentDescription = "description"
                                )
                            }
                        }
                    }
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    surveyInfo(id,vm)
                }
            }
        }
    }

    if(list.size != 0)
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
                                SharePrefs.saveInt("teacherID",id)
                                showBottomSheet = true
                            } else showToast("已评教")
                        },
                        index = item
                    )
//                }
            }
            item {
                Spacer(modifier = Modifier.height(85.dp))
            }
        }
    else {
        Column {

            //      Scaffold {
            EmptyUI()
            Spacer(modifier = Modifier.height(85.dp))
        }

        //      }
    }
}


