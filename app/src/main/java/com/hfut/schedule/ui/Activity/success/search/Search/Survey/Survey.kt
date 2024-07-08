package com.hfut.schedule.ui.Activity.success.search.Search.Survey

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.Enums.PostMode
import com.hfut.schedule.logic.datamodel.MyAPIResponse
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.SaveInt
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.success.search.Search.More.Login
import com.hfut.schedule.ui.UIUtils.LittleDialog
import com.hfut.schedule.ui.UIUtils.MyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Survey(ifSaved : Boolean,vm : LoginSuccessViewModel){
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(text = "教师评教")},
        leadingContent = { Icon(painter = painterResource(id = R.drawable.verified), contentDescription = "")},
        modifier = Modifier.clickable {
            if(ifSaved) Login() else
                showBottomSheet = true
        }
    )
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
                        title = { Text("教评") },
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    SurveyUI(vm)
                }
            }
        }
    }
}
@Composable
fun SurveyUI(vm : LoginSuccessViewModel) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    val cookie = SharePrefs.prefs.getString("redirect", "")
    val semester = Gson().fromJson(prefs.getString("my",MyApplication.NullMy),MyAPIResponse::class.java).semesterId
    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch{
            async{ cookie?.let { vm.getSurveyList(it, semester.toInt()) } }.await()
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
            CircularProgressIndicator()
        }
    }


    AnimatedVisibility(
        visible = !loading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        teacherList(vm, refresh = {refreshch -> refresh = refreshch})
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun teacherList(vm : LoginSuccessViewModel,refresh : (Boolean) -> Unit) {
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
            conformtext = "提交",
            dismisstext = "返回"
        )
    }
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
                        title = { Text("开始教评") },
                        actions = {
                            Row(modifier = Modifier.padding(horizontal = 15.dp)) {
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
                    )
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

    LazyColumn {
        items(list.size) { item ->
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ){
                ListItem(
                    headlineContent = { Text(text = list[item].teacher.person.nameZh)},
                    leadingContent = { Icon(painterResource(R.drawable.person), contentDescription = "Localized description",) },
                    trailingContent = { if(!list[item].submitted) Icon(Icons.Filled.ArrowForward, contentDescription = "") else Text(text = "已评")},
                    modifier = Modifier.clickable {
                        if(!list[item].submitted) {
                            id = list[item].id
                            SaveInt("teacherID",id)
                            showBottomSheet = true
                        } else MyToast("已评教")
                    },
                )
            }
        }
    }
}
