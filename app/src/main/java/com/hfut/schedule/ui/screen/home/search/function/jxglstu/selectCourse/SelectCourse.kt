package com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import com.hfut.schedule.ui.component.status.LoadingUI
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.enumeration.SelectType
import com.hfut.schedule.logic.model.jxglstu.SelectCourseInfo
import com.hfut.schedule.logic.model.jxglstu.SelectPostResponse
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.screen.home.updateCourses
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.ApiToFailRate
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.permit
import com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch.ApiToTeacherSearch
import com.hfut.schedule.ui.component.container.AnimationCustomCard
 
import com.hfut.schedule.ui.component.container.cardNormalColor
  
import com.hfut.schedule.ui.component.text.BottomSheetTopBar
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.status.CenterScreen
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.status.EmptyUI
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.style.bottomSheetRound
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.webview.WebDialog
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.style.ColumnVertical
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.viewmodel.ui.UIViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCourse(ifSaved : Boolean, vm : NetWorkViewModel, hazeState: HazeState, vmUI: UIViewModel) {
    var showBottomSheet by remember { mutableStateOf(false) }
//    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showBottomSheet_info by remember { mutableStateOf(false) }
//    val sheetState_info = rememberModalBottomSheetState()
    val webVpnCookie by DataStoreManager.webVpnCookie.collectAsState(initial = "")

    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else MyApplication.WEBVPN_COOKIE_HEADER + webVpnCookie
    WebDialog(
        showDialog,
        { showDialog = false },
        url = if(vm.webVpn) MyApplication.JXGLSTU_WEBVPN_URL else MyApplication.JXGLSTU_URL + "for-std/course-table",
        title = "教务系统",
        cookie = cookie
    )

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
//            sheetState = sheetState,
//            shape = bottomSheetRound(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("选课") {
                        Row() {
                            FilledTonalIconButton(onClick = { showBottomSheet_info = true }, ) {
                                Icon(painter = painterResource(id = R.drawable.info), contentDescription = "")
                            }
                            FilledTonalButton(onClick = {
                                showDialog = true
                            }) {
                                Text(text = "冲突预览")
                            }
                            Spacer(modifier = Modifier.width(5.dp))
                            FilledTonalButton(onClick = {
                                scope.launch{ updateCourses(vm, vmUI) }
                                showToast("已刷新课表与课程汇总")
                            }) {
                                Text(text = "刷新课表")
                            }
                        }
                    }
                },
            ) { innerPadding ->
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()) {
                    selectCourseListLoading(vm, hazeState = hazeState)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
    if (showBottomSheet_info) {
        HazeBottomSheet(
            onDismissRequest = {
                showBottomSheet_info = false
                scope.launch{ updateCourses(vm,vmUI) }
                               },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_info
//            sheetState = sheetState_info,
//            shape = bottomSheetRound(sheetState_info)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("功能说明")
                },
            ) { innerPadding ->
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()) {
                    SelectShuoming()
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    TransplantListItem(
        headlineContent = { Text(text = "选课") },
        leadingContent = {
            Icon(painter = painterResource(id = R.drawable.ads_click), contentDescription = "")
        },
        modifier = Modifier.clickable {
            if(!ifSaved) showBottomSheet = true
            else refreshLogin()
        }
    )
}
@Composable
fun SelectShuoming() {
//    MyCustomCard {
        StyleCardListItem(
            headlineContent = { Text(text = "免责声明") },
            supportingContent = { Text(text = "本应用不承担选课失败造成的后果\n请登录官方系统,确保一定选课成功")}
        )
//    }
//    MyCustomCard {
        StyleCardListItem(
            headlineContent = { Text(text = "功能说明") },
            supportingContent = { Text(text = "点击右侧按钮选退课\n点击卡片查看详细课程信息\n退课后列表并不会更新,请手动刷新\n选退课时请核对课程代码\n若持续加载可能为教务服务器问题\n选退课完成后前往课表与课程汇总,会显示出新课程(也可在右上角手动刷新)")}
        )
//    }
}

@Composable
fun selectCourseListLoading(vm : NetWorkViewModel, hazeState: HazeState) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
//    val webVpnCookie by DataStoreManager.webVpnCookie.collectAsState(initial = "")
//
//    val cookie = if (!vm.webVpn) prefs.getString(
//        "redirect",
//        ""
//    ) else MyApplication.WEBVPN_COOKIE_HEADER + webVpnCookie

    val scope = rememberCoroutineScope()
    if(refresh) {
        loading = true

        CoroutineScope(Job()).launch{
            val cookie = getJxglstuCookie(vm)
            async { cookie?.let { vm.verify(cookie) } }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.verifyData.observeForever { result ->
                       // Log.d("sss",result.toString())
                        if (result != null) {
                            if (result.contains("302")) {
                                scope.launch {
                                    cookie?.let { vm.getSelectCourse(it) }
                                }
                            }
                        }
                    }
                }
            }
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.selectCourseData.observeForever { result ->
                        if (result != null) {
                            if (result.contains("[")) {
                               loading = false
                                refresh = false
                            }
                        }
                    }
                }
            }
        }
    }


    if(loading) {
        LoadingUI()
    } else {
        SelectCourseList(vm, hazeState = hazeState)
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCourseList(vm: NetWorkViewModel, hazeState: HazeState) {
    val list = getSelectCourseList(vm)
    var courseId by remember { mutableIntStateOf(0) }
    var nameTitle by remember { mutableStateOf("选课") }
    var showBottomSheet by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }

    var showBottomSheet_selected by remember { mutableStateOf(false) }

    if (showBottomSheet_selected) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_selected = false },
            showBottomSheet = showBottomSheet_selected,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("已选课程")
                },
            ) { innerPadding ->
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()) {
                    HaveSelectedCourseLoad(vm, courseId,hazeState)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar(nameTitle) {
                        FilledTonalButton(onClick = {
                            showBottomSheet_selected = true
                        },) {
                            Text(text = "退课")
                        }
                    }
                },) { innerPadding ->
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()) {
                    SelectCourseInfoLoad(courseId,vm, hazeState =hazeState )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
    val ui = @Composable {
        AnimationCardListItem(
            index = list.size,
            headlineContent = {
                Text("手动输入代号查看被隐藏掉的选课入口")
            },
            supportingContent = {
                Column {
                    Text("一些不符合自身条件的选课入口例如异地校区不会显示，但可以通过输入代号进入，代号位于右上角\n免责声明：只供看，不要乱选课，后果自负")
                    Spacer(Modifier.height(APP_HORIZONTAL_DP/2))
                    Row {
                        TextField(
                            modifier = Modifier
                                .weight(1f),
                            value = input,
                            onValueChange = { input = it },
                            label = { Text("输入数字代号") },
                            trailingIcon = {
                                IconButton(onClick = {
                                    input.toIntOrNull()?.let { i ->
                                        courseId = i
                                        SharedPrefs.saveString("courseIDS", i.toString())
                                        nameTitle = "入口$i"
                                        showBottomSheet = true
                                    } ?: showToast("必须为数字")
                                }) {
                                    Icon(Icons.Default.ArrowForward,null)
                                }
                            },
                            shape = MaterialTheme.shapes.medium,
                            colors = textFiledTransplant(),
                        )
                    }
                }
            }
        )
    }

    if(list.isNotEmpty()) {
        LazyColumn {
            items(list.size) { item ->
                val data = list[item]
                var expand by remember { mutableStateOf(false) }
                with(data) {
                    AnimationCustomCard (Modifier.clickable {
                        courseId = id
                        SharedPrefs.saveString("courseIDS", id.toString())
                        nameTitle = name
                        showBottomSheet = true
                    },
                        index = item,
                        hasElevation = false,
                        containerColor = cardNormalColor()
                    ) {
                        TransplantListItem(
                            headlineContent = { Text(text = name) },
                            overlineContent = { Text(text = selectDateTimeText)},
                            trailingContent = {
                                ColumnVertical {
                                    FilledTonalIconButton(onClick = { expand = !expand }) {
                                        Icon(painter = painterResource(id = if(expand) R.drawable.collapse_content else R.drawable.expand_content), contentDescription = "")
                                    }
                                    Text("代号 $id")
                                }

                            },
                        )
                        AnimatedVisibility(
                            visible = expand,
                            enter = slideInVertically(
                                initialOffsetY = { -40 }
                            ) + expandVertically(
                                expandFrom = Alignment.Top
                            ) + scaleIn(
                                transformOrigin = TransformOrigin(0.5f, 0f)
                            ) + fadeIn(initialAlpha = 0.3f),
                            exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(targetScale = 1.2f)
                        ) {
                            Column {
                                TransplantListItem(
                                    headlineContent = { Text(text = "选课公告") },
                                    supportingContent = {
                                        Text(text = bulletin)
                                    }
                                )
                                TransplantListItem(
                                    headlineContent = { Text(text = "选课规则") },
                                    supportingContent = {
                                        for (i in addRulesText) {
                                            Text(text = "$i ")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

            }
            item {
                ui()
            }
        }
    } else {
        CenterScreen {
            Column {
                EmptyUI("当前无选课")
                Spacer(Modifier.height(APP_HORIZONTAL_DP))
                ui()
            }
        }
    }
}

@Composable
fun SelectCourseInfoLoad(courseId : Int, vm: NetWorkViewModel, hazeState: HazeState) {
    var input by remember { mutableStateOf("") }
    val uiState by vm.selectCourseInfoData.state.collectAsState()

    val refreshNetwork: suspend () -> Unit = {
        val cookie = getJxglstuCookie(vm)
        cookie?.let {
            vm.selectCourseInfoData.clear()
            vm.getSelectCourseInfo(it,courseId)
        }
    }

    LaunchedEffect(Unit) {
        refreshNetwork()
    }

    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = APP_HORIZONTAL_DP),
                    value = input,
                    onValueChange = {
                        input = it
                    },
                    label = { Text("搜索 名称、代码、类型" ) },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {

                            }) {
                            Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = textFiledTransplant(),
                )
            }
            Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
            SelectCourseInfo(vm,courseId,input, hazeState =hazeState )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectCourseInfo(vm: NetWorkViewModel,courseId : Int, search : String = "", hazeState: HazeState) {
    val uiState by vm.selectCourseInfoData.state.collectAsState()
    val list = (uiState as UiState.Success).data
    var lessonId by remember { mutableIntStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("课程详情") }
    var num by remember { mutableIntStateOf(0) }
    var showBottomSheet_info by remember { mutableStateOf(false) }
    val webVpnCookie by DataStoreManager.webVpnCookie.collectAsState(initial = "")

    val cookie =
        if (!vm.webVpn) prefs.getString(
            "redirect",
            ""
        ) else MyApplication.WEBVPN_COOKIE_HEADER + webVpnCookie

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            autoShape = false,
            isFullExpand = false,
            hazeState = hazeState,
            showBottomSheet = showBottomSheet
        ) {
            Column() {
                HazeBottomSheetTopBar("选课结果", isPaddingStatusBar = false)
                selectCourseResultLoad(vm,courseId,lessonId, SelectType.add.name)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
    val searchList = mutableListOf<SelectCourseInfo>()
    list.forEach { item ->
        if(item.code.contains(search) || item.course.nameZh.contains(search) || item.nameZh.contains(search) || item.remark?.contains(search) == true) {
            searchList.add(item)
        }
    }


    if (showBottomSheet_info) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_info = false },
            showBottomSheet = showBottomSheet_info,
            hazeState = hazeState,
            autoShape = false
        ) {
            Column {
                HazeBottomSheetTopBar(name) {
                    FilledTonalButton(onClick = {
                        showBottomSheet = true
                    }) {
                        Text(text = "选课")
                    }
                }
                courseInfo(num,searchList,vm, hazeState =hazeState )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    LazyColumn {
        items(searchList.size) {item ->
            val lists = searchList[item]
            var stdCount by remember { mutableStateOf("0") }
            LaunchedEffect(lists.id,webVpnCookie) {
                if(vm.webVpn) {
                    if(webVpnCookie.isEmpty()) return@LaunchedEffect
                }
                async { cookie?.let { vm.getSCount(it,lists.id) } }.await()
                async {
                    Handler(Looper.getMainLooper()).post{
                        vm.stdCountData.observeForever { result ->
                            if (result != null) {
                                if (result.contains(lists.id.toString())) {
                                    val data = parseDynamicJson(result)
                                    stdCount = data.toString().substringAfter("=").substringBefore("}")
                                }
                            }
                        }
                    }
                }
            }
//            MyCustomCard {
                val limit = lists.limitCount
                val isFull = stdCount.toInt() >= lists.limitCount
                val remark = lists.remark
                StyleCardListItem(
                    headlineContent = { Text(text = lists.course.nameZh, fontWeight = FontWeight.Bold) },
                    overlineContent = { Text(text =   "已选 " + stdCount + " / " + limit + " | ${lists.code}")},
                    supportingContent = { Text(text = lists.nameZh  + if(remark != null && remark != "") "\n${remark}" else "")},
                    trailingContent = {

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            FilledTonalIconButton(
                                onClick = {
                                    lessonId = lists.id
                                    showBottomSheet = true
                                },
                                colors = if(!isFull) IconButtonDefaults.filledTonalIconButtonColors() else IconButtonDefaults.filledTonalIconButtonColors(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                            ) { Icon(painter = painterResource(id = R.drawable.add_2), contentDescription = "") }
                            if(isFull) {
                                Text("已满")
                            }
                        }
                                      },
                    modifier = Modifier.clickable {
                        showBottomSheet_info = true
                        name = lists.course.nameZh
                        num = item
                        lessonId = lists.id
                    },
                    color = if(isFull) {
                         MaterialTheme.colorScheme.errorContainer
                    } else {
                        null
                    }
                )
//            }
        }
    }
}

fun parseDynamicJson(jsonString: String): Map<String, Int> {
    val gson = Gson()
    val mapType = object : TypeToken<Map<String, Int>>() {}.type
    return gson.fromJson(jsonString, mapType)
}
@Composable
fun selectCourseResultLoad(vm : NetWorkViewModel, courseId : Int, lessonId : Int, type : String) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    var statusText by remember { mutableStateOf("提交中") }
    var statusBoolean by remember { mutableStateOf(false) }
//    val webVpnCookie by DataStoreManager.webVpnCookie.collectAsState(initial = "")

//    val cookie = if (!vm.webVpn) prefs.getString(
//        "redirect",
//        ""
//    ) else MyApplication.WEBVPN_COOKIE_HEADER + webVpnCookie
    val scope = rememberCoroutineScope()

    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch{
            val cookie = getJxglstuCookie(vm)
            async { cookie?.let { vm.getRequestID(it,lessonId.toString(),courseId.toString(), type) } }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.requestIdData.observeForever { result ->
                        if (result != null) {
                            scope.launch {
                                cookie?.let { vm.postSelect(it,result) }

                            }
                        }
                    }
                }
            }
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.selectResultData.observeForever { result ->
                        if (result != null) {
                            if (result.contains("{")) {
                                loading = false
                                refresh = false
                                try {
                                    val data = Gson().fromJson(result, SelectPostResponse::class.java)
                                    val status = data.success
                                    statusBoolean = status
                                    statusText = if(status) {
                                        "成功"
                                    } else {
                                        data.errorMessage?.textZh ?: "失败"
                                    }
                                } catch (e : Exception) {
                                    statusBoolean = false
                                    statusText = "失败"
                                }
                            }
                        }
                    }
                }
            }
        }
    }




    if(!loading) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                Icon( if(statusBoolean) Icons.Filled.Check else Icons.Filled.Close, contentDescription = "",Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                Text(text = statusText, color = MaterialTheme.colorScheme.primary)
            }
        }
    } else {
        LoadingUI()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun courseInfo(num : Int, lists : List<SelectCourseInfo>, vm: NetWorkViewModel, hazeState: HazeState) {
    val data = lists[num]

    val sheetState_FailRate = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_FailRate by remember { mutableStateOf(false) }

    if (showBottomSheet_FailRate) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet_FailRate = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_FailRate
//            sheetState = sheetState_FailRate,
//            shape = bottomSheetRound(sheetState_FailRate)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("挂科率 ${data.course.nameZh}")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    ApiToFailRate(data.course.nameZh,vm, hazeState = hazeState)
                }
            }
        }
    }

    val sheetState_Teacher = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_Teacher by remember { mutableStateOf(false) }

    var teacherTitle by remember { mutableStateOf("") }

    if (showBottomSheet_Teacher) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_Teacher = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_Teacher,
//            sheetState = sheetState_Teacher,
//            shape = bottomSheetRound(sheetState_Teacher)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("教师检索 $teacherTitle")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    ApiToTeacherSearch(teacherTitle,vm)
                }
            }
        }
    }


    Row {
        TransplantListItem(
            overlineContent = { Text(text = "学分")},
            headlineContent = { Text(text = data.course.credits.toString()) },
            leadingContent = { Icon(painter = painterResource(id = R.drawable.filter_vintage), contentDescription = "")},
            modifier = Modifier.weight(.5f)
        )
        TransplantListItem(
            overlineContent = { Text(text = "考试形式")},
            headlineContent = { Text(text = data.examMode.nameZh) },
            leadingContent = { Icon(painter = painterResource(id = R.drawable.draw), contentDescription = "")},
            modifier = Modifier.weight(.5f)
        )
    }
    Row {
        TransplantListItem(
            overlineContent = { Text(text = data.nameZh)},
            headlineContent = { Text(text = data.courseType.nameZh) },
            leadingContent = { Icon(painter = painterResource(id = R.drawable.calendar_view_month), contentDescription = "")},
            modifier = Modifier.weight(.5f)
        )
        TransplantListItem(
            headlineContent = { Text(text = "挂科率查询") },
            leadingContent = { Icon(painterResource(R.drawable.monitoring), contentDescription = "Localized description",) },
            modifier = Modifier
                .clickable {
                    permit = 1
                    showBottomSheet_FailRate = true
                }
                .weight(.5f),
        )
    }
    val teachers = data.teachers
    for(i in teachers.indices step 2) {
        val item = teachers[i]
        Row {
            TransplantListItem(
                overlineContent = { Text(text = "教师 ${i+1}")},
                headlineContent = { Text(text = item.nameZh) },
                leadingContent = { Icon(painter = painterResource(id = R.drawable.person), contentDescription = "")},
                modifier = Modifier
                    .weight(.5f)
                    .clickable {
                        teacherTitle = item.nameZh
                        showBottomSheet_Teacher = true
                    }
            )
            if(i+1 < teachers.size) {
                val item2 = teachers[i+1]
                TransplantListItem(
                    overlineContent = { Text(text = "教师 ${i+2}")},
                    headlineContent = { Text(text = item2.nameZh) },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.person), contentDescription = "")},
                    modifier = Modifier
                        .weight(.5f)
                        .clickable {
                            teacherTitle = item2.nameZh
                            showBottomSheet_Teacher = true
                        }
                )
            }
        }
    }

    Row {
        TransplantListItem(
            overlineContent = { Text(text = "代码")},
            headlineContent = { Text(text = data.code) },
            leadingContent = { Icon(painter = painterResource(id = R.drawable.tag), contentDescription = "")},
            modifier = Modifier.weight(.5f)
        )
    }
    data.dateTimePlace.textZh?.let {
        TransplantListItem(
            overlineContent = { Text(text = "安排")},
            headlineContent = { Text(text = it)  },
            leadingContent = { Icon(painter = painterResource(id = R.drawable.calendar), contentDescription = "")}
        )
    }
}

@Composable
fun HaveSelectedCourseLoad(vm: NetWorkViewModel, courseId: Int, hazeState: HazeState) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
//    val webVpnCookie by DataStoreManager.webVpnCookie.collectAsState(initial = "")

//    val cookie = if (!vm.webVpn) prefs.getString(
//        "redirect",
//        ""
//    ) else MyApplication.WEBVPN_COOKIE_HEADER + webVpnCookie


    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch{
            val cookie = getJxglstuCookie(vm)
            async { cookie?.let { vm.getSelectedCourse(it, prefs.getString("courseIDS",null).toString())} }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.selectedData.observeForever { result ->
                        if (result != null) {
                            if (result.contains("{")) {
                                loading = false
                                refresh = false
                            }
                        }
                    }
                }
            }
        }
    }





    if(!loading) {
        haveSelectedCourse(vm, courseId, hazeState = hazeState)
    } else {
        LoadingUI()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun haveSelectedCourse(vm: NetWorkViewModel, courseId : Int, hazeState: HazeState) {
    val lists = getSelectedCourse(vm)
    var name by remember { mutableStateOf("课程详情") }
    var num by remember { mutableStateOf(0) }
    val sheetState_info = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_info by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
 //   var courseId by remember { mutableStateOf(0) }
    var lessonId by remember { mutableStateOf(0) }

    if (showBottomSheet_info) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_info = false },
            sheetState = sheetState_info,
//            shape = Round(sheetState_info)
        ) {
            Column {
                BottomSheetTopBar(name) {
                    FilledTonalButton(onClick = { showDialog = true }) {
                        Text(text = "退课")
                    }
                }

                courseInfo(num,lists,vm, hazeState = hazeState)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

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
                    BottomSheetTopBar("调课结果")
                },
            ) { innerPadding ->
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()) {
                    selectCourseResultLoad(vm,courseId,lessonId, SelectType.drop.name)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    if(showDialog) {
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                showBottomSheet = true
                showDialog = false
                             },
            dialogTitle = "警告",
            dialogText = "请再次确定,是否退掉 ${name}\n若为培养计划内的公共或专业课,如需再选需在教务完全关闭之前选择!否则无法再修改",
            conformText = "确定",
            dismissText = "取消"
        )
    }

    LazyColumn {
        items(lists.size) {item ->
            val names =  lists[item].course.nameZh
//            MyCustomCard {
            StyleCardListItem(
                    headlineContent = { Text(text = names)  },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.category), contentDescription = "")},
                    trailingContent = { FilledTonalIconButton(onClick = {
                        lessonId = lists[item].id
                        name = names
                        showDialog = true
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = "")
                    }},
                    modifier = Modifier.clickable {
                        showBottomSheet_info = true
                        num = item
                        name = names
                        lessonId = lists[item].id
                    }
                )
//            }
        }
    }
}