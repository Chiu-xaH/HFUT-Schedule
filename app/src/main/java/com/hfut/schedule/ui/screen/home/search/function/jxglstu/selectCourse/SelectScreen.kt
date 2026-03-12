package com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse


import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.SelectType
import com.hfut.schedule.logic.model.jxglstu.SelectCourseInfo
import com.hfut.schedule.logic.network.util.StatusCode
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.ClipBoardHelper
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.BUTTON_PADDING
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.button.containerBackDrop
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.RefreshIndicator
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.destination.DropCoursesDestination
import com.hfut.schedule.ui.destination.SelectCoursesDetailDestination
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.ApiToFailRate
import com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch.ApiToTeacherSearch
import com.hfut.schedule.ui.screen.home.updateCourses
import com.hfut.schedule.ui.style.color.textFiledAllTransplant
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.state.GlobalUIStateHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.container.container.SharedContainer
import com.xah.container.container.sharedContainer
import com.xah.mirror.util.rememberShaderState
import com.xah.navigation.utils.LocalNavController
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.CenterScreen
import com.xah.uicommon.style.align.ColumnVertical
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.xah.uicommon.util.LogUtil
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
@Composable
fun SelectCourseScreen(
    vm: NetWorkViewModel,
) {
    val context = LocalContext.current
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scope = rememberCoroutineScope()
    val cookie by produceState(initialValue = "") {
        value = getJxglstuCookie() ?: ""
    }
    val uiState by vm.selectCourseData.state.collectAsState()
    val refreshNetwork : suspend(Boolean) -> Unit =  m@ { skip : Boolean ->
        if(skip && uiState is UiState.Success) {
            return@m
        }
        val cookie = getJxglstuCookie()
        if(cookie == null) {
            return@m
        }
        val result = vm.verify(cookie)
        if(result != StatusCode.REDIRECT.code) {
            showToast("验证出现问题")
        }
        vm.selectCourseData.clear()
        vm.getSelectCourse(cookie)
    }
    val refreshing = uiState is UiState.Loading
    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            refreshNetwork(false)
        }
    })
    val backDrop = rememberLayerBackdrop()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val url = if(GlobalUIStateHolder.webVpn) MyApplication.JXGLSTU_WEBVPN_URL else MyApplication.JXGLSTU_URL + "for-std/course-table"
    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState),
                colors = topBarTransplantColor(),
                title = { Text(stringResource(AppNavRoute.SelectCourses.label)) },
                navigationIcon = {
                    TopBarNavigationIcon()
                },
                actions = {
                    Row(modifier = Modifier.padding(end = APP_HORIZONTAL_DP)) {
                        LiquidButton (
                            onClick = {
                                scope.launch{
                                    updateCourses(vm, context)
                                    showToast("已刷新课表与课程汇总")
                                }
                            },
                            backdrop = backDrop,
                            isCircle = true
                        ) {
                            Icon(painterResource(R.drawable.event_repeat),null)
                        }
                        Spacer(Modifier.width(BUTTON_PADDING))
                        LiquidButton (
                            onClick = {
                                scope.launch {
                                    Starter.startWebView(
                                        context,
                                        url = url,
                                        title = "教务系统",
                                        cookie = cookie
                                    )
                                }
                            },
                            backdrop = backDrop
                        ) {
                            Text(text = "冲突预览")
                        }
//                        Spacer(Modifier.width(BUTTON_PADDING))
//                        LiquidButton (
//                            onClick = {
//                                navController.navigateForTransition(AppNavRoute.NewsApi,toRoute)
//                            },
//                            modifier = Modifier.containerShare(route = toRoute, MaterialTheme.shapes.large),
//                            backdrop = backDrop
//                        ) {
//                            Text(text = "选课公告")
//                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .backDropSource(backDrop)
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            LaunchedEffect(Unit) {
                refreshNetwork(true)
            }

            Box(modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
                RefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter).zIndex(1f).padding(innerPadding))
                CommonNetworkScreen(uiState, onReload = { refreshNetwork(false) }) {
                    SelectCourseList(vm,innerPadding)
                }
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SelectCourseDetailScreen(
    vm: NetWorkViewModel,
    courseId : Int,
    title : String,
) {
    val navController = LocalNavController.current
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val backDrop = rememberLayerBackdrop()
    var input by rememberSaveable { mutableStateOf("") }
    val refreshNetwork: suspend () -> Unit = {
        val cookie = getJxglstuCookie()
        cookie?.let {
            vm.selectCourseInfoData.clear()
            vm.getSelectCourseInfo(it,courseId)
        }
    }
    var refreshCount by remember { mutableIntStateOf(0) }

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(title) },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    },
                    actions = {
                        val dest = DropCoursesDestination(
                            courseId,
                            title
                        )
                        Row(modifier = Modifier.padding(end = APP_HORIZONTAL_DP)) {
                            LiquidButton(
                                onClick = {
                                    refreshCount++
                                    showToast("已刷新人数")
                                },
                                isCircle = true,
                                backdrop = backDrop
                            ) {
                                Icon(painterResource(R.drawable.rotate_right), null)
                            }
                            Spacer(Modifier.width(BUTTON_PADDING))
                            SharedContainer(
                                key = dest.key,
                                shape = CircleShape,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                LiquidButton(
                                    shape = RectangleShape,
                                    backdrop = backDrop,
                                    onClick = {
                                        navController.push(dest)
                                    },
                                ) {
                                    Text(text = "退课", maxLines = 1)
                                }
                            }
                        }
                    }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
                        colors = textFiledAllTransplant(),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = APP_HORIZONTAL_DP)
                            .containerBackDrop(backDrop, MaterialTheme.shapes.medium)
                        ,
                        value = input,
                        onValueChange = {
                            input = it
                        },
                        label = { Text("搜索 名称、代码、类型、教师") },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = {}) {
                                Icon(
                                    painter = painterResource(R.drawable.search),
                                    contentDescription = "description"
                                )
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
//                        colors = textFiledTransplant(),
                    )
                }
                Spacer(Modifier.height(CARD_NORMAL_DP))
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .backDropSource(backDrop)
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            val uiState by vm.selectCourseInfoData.state.collectAsState()

            LaunchedEffect(Unit) {
                refreshNetwork()
            }

            CommonNetworkScreen(uiState, onReload = refreshNetwork) {
                SelectCourseInfo(vm,courseId,input, hazeState =hazeState ,innerPadding,refreshCount)
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DropCourseScreen(
    vm: NetWorkViewModel,
    courseId : Int,
    title : String,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = {
                        Text("$title : 退课")
                    },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    },
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            HaveSelectedCourseLoad(vm, courseId,hazeState,innerPadding)
        }
    }
//    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun SelectCourseList(
    vm: NetWorkViewModel,
    innerPadding : PaddingValues,
//    navController : NavHostController,
 ) {
    val navController = LocalNavController.current
    val uiState by vm.selectCourseData.state.collectAsState()
    val list = (uiState as UiState.Success).data
    var input by remember { mutableStateOf("") }

    val ui = @Composable {
        CardListItem(
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
                                        navController.push(
                                            SelectCoursesDetailDestination(
                                                i,
                                                "入口$i"
                                            )
                                        )
                                    } ?: showToast("必须为数字")
                                }) {
                                    Icon(painterResource(R.drawable.arrow_forward),null)
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
                    val dest = SelectCoursesDetailDestination(
                        id,
                        name
                    )
                    CustomCard (
                        modifier = Modifier
                            .clickable {
                                navController.push(
                                    SelectCoursesDetailDestination(
                                        id,
                                        name
                                    )
                                )
                            }
                            .sharedContainer(
                                dest.key,
                                MaterialTheme.shapes.medium,
                                cardNormalColor()
                            ),
                        shape = RectangleShape,
                        color = cardNormalColor()
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
                                PaddingHorizontalDivider()
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
                EmptyIcon("当前无选课")
                Spacer(Modifier.height(APP_HORIZONTAL_DP))
                ui()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectCourseInfo(vm: NetWorkViewModel,courseId : Int, search : String = "", hazeState: HazeState,innerPadding: PaddingValues,refreshCount : Int) {
    val uiState by vm.selectCourseInfoData.state.collectAsState()
    val list = (uiState as UiState.Success).data
        .let {
            if(search.isEmpty() || search.isBlank()) {
                it
            } else {
                it.filter { item ->
                    item.code.contains(search,ignoreCase = true) ||
                            item.course.nameZh.contains(search) ||
                            item.nameZh.contains(search) ||
                            item.remark?.contains(search) == true ||
                            item.teachers.toString().contains(search)
                }
            }
        }
        .sortedBy { it.code }

    var lessonId by remember { mutableIntStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("课程详情") }
    var num by remember { mutableIntStateOf(0) }
    var showBottomSheet_info by remember { mutableStateOf(false) }

    val cookie by produceState<String?>(initialValue = null) {
        value = getJxglstuCookie()
    }
    val state = rememberLazyListState()

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet
        ) {
            Column() {
                HazeBottomSheetTopBar("选课结果", isPaddingStatusBar = false)
                SelectCourseResultLoad(vm,courseId,lessonId, SelectType.add.name)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showBottomSheet_info) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_info = false },
            showBottomSheet = showBottomSheet_info,
        ) {
            Column {
                HazeBottomSheetTopBar(name, isPaddingStatusBar = false) {
                    FilledTonalButton(onClick = {
                        showBottomSheet = true
                    }) {
                        Text(text = "选课")
                    }
                }
                CourseInfo(num,list,vm, hazeState =hazeState )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    LazyColumn(state=state) {
        item { InnerPaddingHeight(innerPadding,true) }
        items(list.size, key = { list[it].id }) { item ->
            val lists = list[item]
            var stdCount by remember { mutableStateOf("0") }
            LaunchedEffect(lists.id,refreshCount) {
                if(cookie == null) return@LaunchedEffect
                async { stdCount = "0" }.await()
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
        item { InnerPaddingHeight(innerPadding,false) }
    }
}

private fun parseDynamicJson(jsonString: String): Map<String, Int> {
    try {
        val gson = Gson()
        val mapType = object : TypeToken<Map<String, Int>>() {}.type
        return gson.fromJson(jsonString, mapType)
    } catch (e : Exception) {
        LogUtil.error(e)
        return emptyMap()
    }
}
@Composable
fun SelectCourseResultLoad(vm : NetWorkViewModel, courseId : Int, lessonId : Int, type : String) {
    val refreshNetwork = suspend m@ {
        val cookie = getJxglstuCookie() ?: return@m
        vm.requestIdData.clear()
        vm.getRequestID(cookie,lessonId,courseId, type)
        val result = (vm.requestIdData.state.value as? UiState.Success)?.data ?: return@m
        vm.selectResultData.clear()
        vm.postSelect(cookie,result)
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    val uiState by vm.selectResultData.state.collectAsState()
    Column {
        CommonNetworkScreen(uiState, onReload = refreshNetwork, isFullScreen = false) {
            val data = (uiState as UiState.Success).data
            ColumnVertical(modifier = Modifier.fillMaxWidth()) {
                Icon( if(data.first) painterResource(R.drawable.check) else painterResource(R.drawable.close), contentDescription = "",Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
                Text(text = data.second, color = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(Modifier.height(APP_HORIZONTAL_DP))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseInfo(num : Int, lists : List<SelectCourseInfo>, vm: NetWorkViewModel, hazeState: HazeState) {
    val data = lists[num]

    var showBottomSheet_FailRate by remember { mutableStateOf(false) }

    if (showBottomSheet_FailRate) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet_FailRate = false },
            showBottomSheet = showBottomSheet_FailRate
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
                    ApiToFailRate(data.course.nameZh,vm,innerPadding,data.code)
                }
            }
        }
    }

    var showBottomSheet_Teacher by remember { mutableStateOf(false) }

    var teacherTitle by remember { mutableStateOf("") }

    if (showBottomSheet_Teacher) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_Teacher = false },
            showBottomSheet = showBottomSheet_Teacher,
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
            headlineContent = { Text(text = data.courseType.nameZh) },
            leadingContent = { Icon(painter = painterResource(id = R.drawable.kid_star), contentDescription = "")},
            modifier = Modifier.weight(.5f)
        )
        TransplantListItem(
            headlineContent = { Text(text = stringResource(AppNavRoute.FailRate.label)) },
            leadingContent = { Icon(painterResource(AppNavRoute.FailRate.icon), contentDescription = "Localized description",) },
            modifier = Modifier
                .clickable {
                    showBottomSheet_FailRate = true
                }
                .weight(.5f),
        )
    }
    TransplantListItem(
        headlineContent = { Text(text = data.nameZh)},
        overlineContent = { Text("班级")},
        leadingContent = { Icon(painter = painterResource(id = R.drawable.sensor_door), contentDescription = "")},
        modifier = Modifier.clickable {
            ClipBoardHelper.copy(data.nameZh)
        }
    )
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
            modifier = Modifier.weight(.5f).clickable {
                ClipBoardHelper.copy(data.code)
            }
        )
    }
    data.dateTimePlace.textZh?.let {
        TransplantListItem(
            overlineContent = { Text(text = "安排")},
            headlineContent = { Text(text = it)  },
            modifier = Modifier.clickable {},
            leadingContent = { Icon(painter = painterResource(id = R.drawable.calendar), contentDescription = "")}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HaveSelectedCourseLoad(vm: NetWorkViewModel, courseId: Int, hazeState: HazeState,innerPadding: PaddingValues) {
    val uiState by vm.selectedData.state.collectAsState()
    val refreshNetwork = suspend m@ {
        vm.selectedData.clear()
        val cookie = getJxglstuCookie() ?: return@m
        vm.getSelectedCourse(cookie,courseId)
    }

    var name by remember { mutableStateOf("课程详情") }
    var num by remember { mutableIntStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var lessonId by remember { mutableStateOf(0) }

    LaunchedEffect(showBottomSheet) {
        if(showBottomSheet == false) {
            refreshNetwork()
        }
    }

    if (showBottomSheet) {
        HazeBottomSheet (
            showBottomSheet = showBottomSheet,
//            isFullScreen = false,
            onDismissRequest = { showBottomSheet = false },
        ) {
            Column(modifier = Modifier) {
                HazeBottomSheetTopBar("调课结果", isPaddingStatusBar = false)
                SelectCourseResultLoad(vm,courseId,lessonId, SelectType.drop.name)
                Spacer(modifier = Modifier
                    .height(APP_HORIZONTAL_DP)
                    .navigationBarsPadding())
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
            hazeState = hazeState,
            dialogText = "请再次确定,是否退掉${name}?需在教务完全关闭之前完成调整,否则无法再修改!",
        )
    }

    val state = rememberLazyListState()
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        val lists = (uiState as UiState.Success).data
        var showBottomSheet_info by remember { mutableStateOf(false) }
        if (showBottomSheet_info) {
            HazeBottomSheet (
                showBottomSheet = showBottomSheet_info,
//                isFullScreen = false,
                onDismissRequest = { showBottomSheet_info = false },
            ) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    HazeBottomSheetTopBar(name, isPaddingStatusBar = false) {
                        FilledTonalButton(onClick = { showDialog = true }) {
                            Text(text = "退课")
                        }
                    }
                    CourseInfo(num,lists,vm, hazeState = hazeState)
                    Spacer(modifier = Modifier
                        .height(APP_HORIZONTAL_DP)
                        .navigationBarsPadding())
                }
            }
        }

        LazyColumn(state=state) {
            item { InnerPaddingHeight(innerPadding,true) }
            items(lists.size, key = { it }) {item ->
                val names =  lists[item].course.nameZh
                CardListItem(
                    headlineContent = { Text(text = names)  },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.category), contentDescription = "")},
                    trailingContent = { FilledTonalIconButton(onClick = {
                        lessonId = lists[item].id
                        name = names
                        showDialog = true
                    }) {
                        Icon(painterResource(R.drawable.close), contentDescription = "")
                    }},
                    modifier = Modifier.clickable {
                        showBottomSheet_info = true
                        num = item
                        name = names
                        lessonId = lists[item].id
                    }
                )
            }
            item { InnerPaddingHeight(innerPadding,false) }
        }
    }
}


