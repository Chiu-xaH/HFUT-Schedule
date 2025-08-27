package com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.SelectType
import com.hfut.schedule.logic.model.jxglstu.SelectCourseInfo
import com.hfut.schedule.logic.model.jxglstu.SelectPostResponse
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.showToast
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.hfut.schedule.ui.component.container.AnimationCustomCard
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.xah.uicommon.style.align.CenterScreen
import com.hfut.schedule.ui.component.status.EmptyUI
import com.xah.uicommon.component.status.LoadingUI
import com.hfut.schedule.ui.component.text.BottomSheetTopBar
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
   
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.ApiToFailRate
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.permit
import com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch.ApiToTeacherSearch
import com.hfut.schedule.ui.screen.home.updateCourses
import com.xah.uicommon.style.align.ColumnVertical
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.corner.bottomSheetRound
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.ui.util.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.xah.transition.component.containerShare
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SelectCourseScreen(
    vm: NetWorkViewModel,
    vmUI : UIViewModel,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val route = remember { AppNavRoute.SelectCourse.route }
    val scope = rememberCoroutineScope()
    val cookie by produceState(initialValue = "") {
        value = getJxglstuCookie(vm) ?: ""
    }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val url = if(vm.webVpn) MyApplication.JXGLSTU_WEBVPN_URL else MyApplication.JXGLSTU_URL + "for-std/course-table"
    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = route,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.topBarBlur(hazeState),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.SelectCourse.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(navController,animatedContentScope,route, AppNavRoute.SelectCourse.icon)
                    },
                    actions = {
                        Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                            FilledTonalButton(onClick = {
                                navController.navigateForTransition(
                                    AppNavRoute.WebView,
                                    AppNavRoute.WebView.withArgs(
                                        url = url,
                                        title = "教务系统",
                                        cookies = cookie
                                    ))
                            },
                                modifier = Modifier.containerShare(sharedTransitionScope,animatedContentScope=animatedContentScope, route = AppNavRoute.WebView.shareRoute(url))
                            ) {
                                Text(text = "冲突预览")
                            }
                            Spacer(modifier = Modifier.width(CARD_NORMAL_DP))
                            FilledTonalButton(onClick = {
                                scope.launch{ updateCourses(vm, vmUI) }
                                showToast("已刷新课表与课程汇总")
                            }) {
                                Text(text = "刷新课表")
                            }
                        }
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.hazeSource(hazeState).fillMaxSize()
            ) {
                SelectCourseListLoading(vm, hazeState = hazeState,innerPadding)
            }
        }
    }
}


@Composable
private fun SelectCourseListLoading(vm : NetWorkViewModel, hazeState: HazeState,innerPadding : PaddingValues) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }

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
        CenterScreen {
            LoadingUI()
        }
    } else {
        SelectCourseList(vm, hazeState = hazeState,innerPadding)
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectCourseList(vm: NetWorkViewModel, hazeState: HazeState,innerPadding : PaddingValues) {
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
                    Text("一些不符合自身条件的选课入口例如异地校区不会显示，但可以通过输入代号进入，代号位于右上角")
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
            item { InnerPaddingHeight(innerPadding,true) }
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
            item { InnerPaddingHeight(innerPadding,false) }
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
private fun SelectCourseInfoLoad(courseId : Int, vm: NetWorkViewModel, hazeState: HazeState) {
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

    val cookie by produceState<String?>(initialValue = null) {
        value = getJxglstuCookie(vm)
    }


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
                SelectCourseResultLoad(vm,courseId,lessonId, SelectType.add.name)
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
                CourseInfo(num,searchList,vm, hazeState =hazeState )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    LazyColumn {
        items(searchList.size) {item ->
            val lists = searchList[item]
            var stdCount by remember { mutableStateOf("0") }
            LaunchedEffect(lists.id) {
                if(cookie == null) return@LaunchedEffect
                async { vm.getSCount(cookie!!,lists.id) }.await()
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
            CardListItem(
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

private fun parseDynamicJson(jsonString: String): Map<String, Int> {
    val gson = Gson()
    val mapType = object : TypeToken<Map<String, Int>>() {}.type
    return gson.fromJson(jsonString, mapType)
}
@Composable
fun SelectCourseResultLoad(vm : NetWorkViewModel, courseId : Int, lessonId : Int, type : String) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    var statusText by remember { mutableStateOf("提交中") }
    var statusBoolean by remember { mutableStateOf(false) }

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
private fun CourseInfo(num : Int, lists : List<SelectCourseInfo>, vm: NetWorkViewModel, hazeState: HazeState) {
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
                        .fillMaxSize()
                ) {
                    ApiToFailRate(data.course.nameZh,vm, hazeState = hazeState,innerPadding)
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
                    ApiToTeacherSearch(teacherTitle,vm,innerPadding)
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
            headlineContent = { Text(text = AppNavRoute.FailRate.label) },
            leadingContent = { Icon(painterResource(AppNavRoute.FailRate.icon), contentDescription = "Localized description",) },
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
private fun HaveSelectedCourseLoad(vm: NetWorkViewModel, courseId: Int, hazeState: HazeState) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }

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
        HaveSelectedCourse(vm, courseId, hazeState = hazeState)
    } else {
        LoadingUI()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HaveSelectedCourse(vm: NetWorkViewModel, courseId : Int, hazeState: HazeState) {
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

                CourseInfo(num,lists,vm, hazeState = hazeState)
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
                    SelectCourseResultLoad(vm,courseId,lessonId, SelectType.drop.name)
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
            CardListItem(
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