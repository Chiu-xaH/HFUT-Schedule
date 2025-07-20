package com.hfut.schedule.ui.screen.home.search.function.school.student

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.community.getTodayCampusApps
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.network.state.CasInHFUT
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.container.SmallCard
import com.hfut.schedule.ui.screen.home.search.function.other.life.countFunc
import com.hfut.schedule.ui.component.text.BottomSheetTopBar
import com.hfut.schedule.ui.component.status.LoadingUI
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.style.bottomSheetRound
import com.hfut.schedule.ui.component.status.StatusUI2
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.network.URLImage
import com.hfut.schedule.ui.component.screen.CustomTabRow
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.text.ScrollText
import com.hfut.schedule.ui.style.ColumnVertical
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToadyCampus(hazeState : HazeState,vm: NetWorkViewModel){
    var showBottomSheet by remember { mutableStateOf(false) }
    TransplantListItem(
        headlineContent = { Text(text = "学工系统") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.handshake), contentDescription = "") },
        modifier = Modifier.clickable {
            showBottomSheet = true
        }
    )

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState,
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("学工系统") {
                        Row {
                            FilledTonalButton(onClick = {
                                Starter.startLaunchAPK("com.wisedu.cpdaily","今日校园")
                            }) {
                                Text("今日校园")
                            }
                            Spacer(Modifier.width(APP_HORIZONTAL_DP/3))
                            FilledTonalButton(onClick = {
                                Starter.startWebUrl(MyApplication.STU_URL)
                            }) {
                                Text("学工系统")
                            }
                        }
                    }
                },) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    StuAppsScreen(vm)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
private const val TAB_LEFT = 0
private const val TAB_RIGHT = 1
@Composable
fun StuAppsScreen(vm : NetWorkViewModel) {
    val refreshNetwork : suspend () -> Unit = {
        prefs.getString("TOKEN","")?.let {
            vm.stuAppsResponse.clear()
            vm.getStuApps(it)
        }
    }
    val titles = remember { listOf("Community源(动态)","今日校园源(静态)") }
    val todayCampusTip by DataStoreManager.todayCampusTip.collectAsState(initial = true)
    val paperState = rememberPagerState(pageCount = { titles.size })
    val scope = rememberCoroutineScope()
    val size = remember { 30.dp }
    var input by remember { mutableStateOf("") }
    val uiState by vm.stuAppsResponse.state.collectAsState()
    val localList = remember { getTodayCampusApps() }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }

    Column {
        CustomTabRow(paperState,titles)
        HorizontalPager(state = paperState) { pager ->
            Column(modifier = Modifier.fillMaxSize()) {
                if(todayCampusTip) {
                    StyleCardListItem(
                        leadingContent = {
                            Icon(painterResource(R.drawable.info),null)
                        },
                        headlineContent = { Text(
                            when(pager) {
                                TAB_RIGHT -> {
                                    "本使用源由开发者从今日校园APP整理的功能，若有新增的功能请联系开发者"
                                }
                                TAB_LEFT -> {
                                    "本使用源自动托给Community源动态获取，但是功能不是特别全..."
                                }
                                else -> ""
                            }
                        )},
                        trailingContent = {
                            IconButton(onClick = {
                                scope.launch{ DataStoreManager.saveTodayCampusTip(false) }
                            }) {
                                Icon(painterResource(R.drawable.close),null)
                            }
                        }
                    )
                    Spacer(Modifier.height(CARD_NORMAL_DP))
                }
                CustomTextField(
                    input = input,
                    label = { Text("检索功能") },
                    leadingIcon = { Icon(painterResource(R.drawable.search),null) },
                ) {
                    input = it
                }
                Spacer(Modifier.height(CARD_NORMAL_DP))

                when(pager) {
                    TAB_RIGHT -> {
                        if(input.isNotEmpty() || input.isNotBlank()) {
                            val data = localList.flatMap { it.apps }.filter { it.name.contains(input) }
                            LazyVerticalGrid(columns = GridCells.Fixed(2),modifier = Modifier.padding(horizontal = 12.dp)) {
                                items(data.size, key = { it }) { index ->
                                    val item = data[index]
                                    with(item) {
                                        val canUse = openUrl.startsWith(MyApplication.STU_URL)
                                        SmallCard(modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp)) {
                                            TransplantListItem(
                                                leadingContent = {
                                                    URLImage(iconUrl, width = size, height = size)
                                                },
                                                headlineContent = { ScrollText(name, textDecoration = if(canUse) TextDecoration.None else TextDecoration.LineThrough) },
                                                modifier = Modifier.clickable {
                                                    if(canUse)
                                                        Starter.startWebUrl(openUrl)
                                                    else
                                                        showToast("暂不支持，请使用今日校园")
                                                }
                                            )
                                        }
                                    }
                                }
                                items(2) {
                                    Spacer(Modifier.height(APP_HORIZONTAL_DP))
                                }
                            }
                        } else {
                            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                for(i in localList) {
                                    val list = i.apps
                                    if(list.isNotEmpty()) {
                                        DividerTextExpandedWith(i.categoryName) {
                                            for(j in list.indices step 2) {
                                                val item1 = list[j]
                                                Row(Modifier.padding(horizontal = 12.dp)) {
                                                    with(item1) {
                                                        val canUse = openUrl.startsWith(MyApplication.STU_URL)
                                                        SmallCard(modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp).weight(.5f)) {
                                                            TransplantListItem(
                                                                leadingContent = {
                                                                    URLImage(iconUrl, width = size, height = size)
                                                                },
                                                                headlineContent = { ScrollText(name,textDecoration = if(canUse) TextDecoration.None else TextDecoration.LineThrough) },
                                                                modifier = Modifier.clickable {
                                                                    if(canUse)
                                                                        Starter.startWebUrl(openUrl)
                                                                    else
                                                                        showToast("暂不支持，请使用今日校园")
                                                                }
                                                            )
                                                        }
                                                    }
                                                    if(j + 1 < i.apps.size) {
                                                        val item2 = list[j+1]
                                                        with(item2) {
                                                            val canUse = openUrl.startsWith(MyApplication.STU_URL)
                                                            SmallCard(modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp).weight(.5f)) {
                                                                TransplantListItem(
                                                                    leadingContent = {
                                                                        URLImage(iconUrl, width = size, height = size)
                                                                    },
                                                                    headlineContent = { ScrollText(name, textDecoration = if(canUse) TextDecoration.None else TextDecoration.LineThrough) },
                                                                    modifier = Modifier.clickable {
                                                                        if(canUse)
                                                                            Starter.startWebUrl(openUrl)
                                                                        else
                                                                            showToast("暂不支持，请使用今日校园")
                                                                    }
                                                                )
                                                            }
                                                        }
                                                    } else {
                                                        Spacer(Modifier.width(1.dp).weight(.5f))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    TAB_LEFT -> {
                        CommonNetworkScreen(uiState, onReload = refreshNetwork) {
                            val data = (uiState as UiState.Success).data.filter { it.name.contains(input) }
                            LazyVerticalGrid(columns = GridCells.Fixed(2),modifier = Modifier.padding(horizontal = 12.dp)) {
                                items(data.size, key = { it }) { index ->
                                    val item = data[index]
                                    with(item) {
                                        SmallCard(modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp)) {
                                            TransplantListItem(
                                                leadingContent = {
                                                    URLImage(logo, width = size, height = size)
                                                },
                                                headlineContent = { ScrollText(name) },
                                                modifier = Modifier.clickable {
                                                    url?.let {
                                                        Starter.startWebUrl(it)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                                items(2) {
                                    Spacer(Modifier.height(APP_HORIZONTAL_DP))
                                }
                            }
                        }
                    }
                }
            }
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
        containerColor = Color.Transparent,
        topBar = {
            BottomSheetTopBar("学工系统登录")
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
    val ONE = CasInHFUT.casCookies
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
            shape = bottomSheetRound(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    BottomSheetTopBar("学工系统")
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
            StatusUI2(Icons.Filled.Check, text = "登录成功")
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