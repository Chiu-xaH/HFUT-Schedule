package com.hfut.schedule.ui.activity.home.search.functions.todayCampus

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.DialogProperties
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.DataStoreManager
import com.hfut.schedule.logic.utils.JxglstuParseUtils
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.ui.activity.home.search.functions.life.countFunc
import com.hfut.schedule.ui.utils.components.LoadingUI
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.style.Round
import com.hfut.schedule.ui.utils.components.WebViewScreen
import com.hfut.schedule.ui.utils.components.statusUI
import com.hfut.schedule.ui.utils.components.statusUI2
import com.hfut.schedule.ui.utils.style.ColumnVertical
import com.hfut.schedule.ui.utils.style.RowHorizontal
import com.hfut.schedule.viewmodel.NetWorkViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToadyCampus(ifSaved : Boolean,vm: NetWorkViewModel){
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    TransplantListItem(
//        overlineContent = { Text(text = "今日校园") },
        headlineContent = { Text(text = "学工系统") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.handshake), contentDescription = "") },
        modifier = Modifier.clickable {
//            showBottomSheet = true
            Starter.startLaunchAPK("com.wisedu.cpdaily","今日校园")

        }
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
        ) {
            LoginStu(vm)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayCampusUI(vm : NetWorkViewModel) {

    val stuCookie by DataStoreManager.stuCookieFlow.collectAsState(initial = null)
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    var r by remember { mutableStateOf("") }



    fun refresh() {
        loading = true
        CoroutineScope(Job()).launch {
            async { stuCookie?.let { vm.getStuInfo(it) } }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.stuInfoResponse.observeForever { result ->
                        if (result != null) {
                            countFunc++
                            r = result
                        }
                    }
                }
            }
        }
    }


    if(refresh && countFunc == 0) {
        refresh()
    }
    if(loading) {
        LoadingUI()
    } else {
        Text(r)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginStu(vm : NetWorkViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("学工系统登录") },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            countFunc = 0
            LoginStuUI(vm)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginStuUI(vm : NetWorkViewModel) {
    val ONE = JxglstuParseUtils.casCookies
    val TGC = prefs.getString("TGC", "")
    val cookies = "$ONE;$TGC"

    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
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
                        title = { Text("学工系统") },
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    countFunc = 0
                    TodayCampusUI(vm)
                }
            }
        }
    }


    LaunchedEffect(Unit) {
        if(loading && countFunc == 0) {
            async { countFunc++ }.await()
            async { vm.loginToStu(cookies) }.await()
            launch {
                Handler(Looper.getMainLooper()).post{
                    vm.goToStuResponse.observeForever { result ->
                        if (result != null) {
                            if(result.contains("ticket")) {
                                // 提取ticket
                                vm.stuTicket.value = result.substringAfter("ticket=")
                            }
                        }
                    }
                }
            }
            launch {
                Handler(Looper.getMainLooper()).post{
                    vm.stuTicket.observeForever { result ->
                        if (result != null) {
                            CoroutineScope(Job()).launch { vm.loginRefreshStu(ticket = result, cookie = null) }
                        }
                    }
                }
            }
            launch {
                Handler(Looper.getMainLooper()).post{
                    vm.loginStuResponse.observeForever { result ->
                        if (result != null) {
                            val strs = result.split(";")
                            strs.forEach { item->
                                if(item.contains("_WEU")) {
                                    CoroutineScope(Job()).launch {
                                        async { DataStoreManager.saveStuCookie(item) }.await()
                                        async { loading = false }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if(loading) {
        LoadingUI(text = "正在登录中 请勿关闭")
    } else {
        ColumnVertical {
            statusUI2(Icons.Filled.Check, text = "登录成功")
            Button(
                onClick = {
                    showBottomSheet = true
                }
            ) {
                Text("进入")
            }
        }
    }
}