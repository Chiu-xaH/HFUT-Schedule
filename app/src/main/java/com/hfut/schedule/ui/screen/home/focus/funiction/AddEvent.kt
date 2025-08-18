package com.hfut.schedule.ui.screen.home.focus.funiction

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.activity.BackEventCompat
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomEventDTO
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.database.util.CustomEventMapper
import com.hfut.schedule.logic.model.SupabaseEventOutput
import com.hfut.schedule.logic.util.network.state.reEmptyLiveDta
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.addToCalendars
import com.hfut.schedule.logic.util.sys.parseToDateTime
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.dialog.DateRangePickerModal
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.hfut.schedule.ui.component.dialog.TimeRangePickerDialog
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.navigationBarHeightPadding
import com.hfut.schedule.ui.component.status.LoadingUI
import com.hfut.schedule.ui.component.text.BottomTip
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.EventCampus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getEventCampus
import com.hfut.schedule.ui.screen.supabase.home.getInsertedEventId
import com.hfut.schedule.ui.screen.supabase.login.loginSupabaseWithCheck
import com.hfut.schedule.ui.style.RowHorizontal
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

private enum class ShareRoutes {
    BUTTON,SURFACE
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AddEventFloatButton(
    isSupabase : Boolean,
    isVisible: Boolean,
    vmUI : UIViewModel,
    innerPaddings: PaddingValues,
    vm: NetWorkViewModel
) {
    // 懒加载
    var showSurface by remember { mutableStateOf(false) }

    var showAddUI by remember { mutableStateOf(false) }
    // 容器转换动画
//    val isCenterAnimation by DataStoreManager.motionAnimationTypeFlow.collectAsState(initial = false)
    val boundsTransform by remember { mutableStateOf(
        BoundsTransform { _, _ ->
            spring(
                dampingRatio = Spring.DampingRatioLowBouncy*1.15f,
                stiffness = StiffnessMediumLow,
                visibilityThreshold = Rect.VisibilityThreshold
            )
        }
    ) }
    // 通知父布局开始进行模糊和缩放，同时暂时关闭topBar和bottomBar的实时模糊
    LaunchedEffect(showAddUI) {
        if(isSupabase) {
            vmUI.isAddUIExpandedSupabase = showAddUI
        } else {
            vmUI.isAddUIExpanded = showAddUI
        }
        if(showAddUI) {
            // 进入
            showSurface = false
            delay(AppAnimationManager.ANIMATION_SPEED * 1L)
            showSurface = true
        } else {
            // 退出
            showSurface = false
        }
    }
    SharedTransitionLayout {
        AnimatedContent(
            targetState = showAddUI,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = AppAnimationManager.ANIMATION_SPEED)) togetherWith fadeOut(animationSpec = tween(durationMillis = AppAnimationManager.ANIMATION_SPEED*2))
            },
            label = ""
        ) { targetShowAddUI ->
            // 这里是 AnimatedContentScope 的作用域
            if (targetShowAddUI) {
                SurfaceUI(
                    isSupabase,
                    animatedContentScope = this,
                    showSurface = showSurface,
                    showChange = { showAddUI = it },
                    boundsTransform,
                    vm
                )
            } else {
                ButtonUI(
                    isVisible = isVisible,
                    innerPaddings = innerPaddings,
                    animatedContentScope = this,
                    showChange = { showAddUI = it },
                    boundsTransform
                )
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.ButtonUI(
    isVisible: Boolean,
    innerPaddings : PaddingValues,
    animatedContentScope: AnimatedContentScope,
    showChange : (Boolean) -> Unit,
    boundsTransform: BoundsTransform
) {
    if (isVisible) {
        FloatingActionButton(
            modifier = Modifier
                .padding(bottom = innerPaddings.calculateBottomPadding()-navigationBarHeightPadding)

//                .padding(innerPaddings)
                .padding(horizontal = APP_HORIZONTAL_DP, vertical = APP_HORIZONTAL_DP)
                .sharedBounds(
                    boundsTransform = boundsTransform,
                    enter = AppAnimationManager.fadeAnimation.enter,
                    exit = AppAnimationManager.fadeAnimation.exit,
                    sharedContentState = rememberSharedContentState(key = "container"),
                    animatedVisibilityScope = animatedContentScope,
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                ),
            elevation =  FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
            onClick = { showChange(true) },
        ) { Icon(Icons.Filled.Add, "Add Button") }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.SurfaceUI(
    isSupabase : Boolean,
    animatedContentScope: AnimatedContentScope,
    showSurface : Boolean,
    showChange: (Boolean) -> Unit,
    boundsTransform: BoundsTransform,
    vm: NetWorkViewModel
) {
    val jwt by DataStoreManager.supabaseJwt.collectAsState(initial = "")
    val refreshToken by DataStoreManager.supabaseRefreshToken.collectAsState(initial = "")

    var loading by remember { mutableStateOf(false) }

    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
    var useBackHandler by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if(useBackHandler == false) {
            delay(AppAnimationManager.ANIMATION_SPEED*1L)
            useBackHandler = true
        }
    }
    var scale by remember { mutableFloatStateOf(1f) }
    if(useBackHandler && enablePredictive) {
        PredictiveBackHandler() { progress: Flow<BackEventCompat> ->
            // code for gesture back started
            try {
                progress.collect { backEvent ->
                    // code for progress
                    scale = 1f - (0.075f * backEvent.progress)
                }
                // code for completion
                scale = 0f
                showChange(false)
            } catch (e: CancellationException) {
                // code for cancellation
                scale = 1f
            }
        }
    } else {
        BackHandler {
            showChange(false)
        }
    }

    val scope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .sharedBounds(
                enter = AppAnimationManager.fadeAnimation.enter,
                exit = AppAnimationManager.fadeAnimation.exit,
                sharedContentState = rememberSharedContentState(key = "container"),
                animatedVisibilityScope = animatedContentScope,
                boundsTransform = boundsTransform,
                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
            )
            .clip(FloatingActionButtonDefaults.shape),
        topBar = {
            Column {
                TopAppBar(
                    colors = topBarTransplantColor(),
                    title = { Text("添加") },
                    actions = {
                        if(getPersonInfo().username != null && !isSupabase)
                            FilledTonalButton(onClick = {
                                scope.launch {
                                    loading = true
                                    loginSupabaseWithCheck(jwt,refreshToken,vm)
                                    loading = false
                                }
                            }, modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                                Text("云端共建")
                            }

                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { showChange(false) }
                        ) {
                            Icon(Icons.Filled.Close,null,tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }
        },
    ) { innerPadding ->
        AnimatedVisibility(
            modifier = Modifier
                .scale(scale)

            ,
            visible = showSurface,
            enter  = fadeIn(),
            exit = fadeOut(tween(durationMillis = 0))
        ) {
            Column(modifier = Modifier
                .padding(innerPadding)
                .background(Color.Transparent)) {
                if(loading) {
                    LoadingUI("正在核对登录")
                } else {
                    AddEventUI(vm,isSupabase,showChange)
                }
            }
        }
//        if(showSurface) {
//            AnimatedVisibility(
//                visible = true,
//                enter  = fadeIn(),
//                exit = fadeOut()
//            ) {
//                Column(modifier = Modifier
//                    .padding(innerPadding)
//                    .background(Color.Transparent)) {
//                    if(loading) {
//                        LoadingUI("正在核对登录")
//                    } else {
//                        AddEventUI(vm,isSupabase,showChange)
//                    }
//                }
//            }
//        }
    }
}

@Composable
fun AddEventUI(vm: NetWorkViewModel,isSupabase : Boolean,showChange: (Boolean) -> Unit) {
    var enabled by remember { mutableStateOf(false) }

    val activity = LocalActivity.current
    var isScheduleType by remember { mutableStateOf(true) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var remark by remember { mutableStateOf("") }

    var time by remember { mutableStateOf(Pair("","")) }
    var date by remember { mutableStateOf(Pair("","")) }

    var showSelectDateDialog by remember { mutableStateOf(false) }
    var showSelectTimeDialog by remember { mutableStateOf(false) }
    var showSupabaseDialog by remember { mutableStateOf(false) }


    val scope = rememberCoroutineScope()

    LaunchedEffect(date,time) {
        remark = if(isScheduleType) {
            if(date.first == date.second) { // 当天日程
                "${date.first.substringAfter("-")} " +
                        // 同时间
                        if(time.first == time.second) {
                            time.first
                        } else {
                            time.first + " ~ " + time.second
                        }
            } else {
                "${date.first.substringAfter("-") + " " + time.first} ~ ${date.second.substringAfter("-") + " " + time.second}"
            }
        } else {
            date.second.substringAfter("-") + " " + time.second + " 截止"
        }
    }
    var campus by remember { mutableStateOf(getEventCampus()) }

    // 向上回传数据
    LaunchedEffect(title,time,date,remark) {
        enabled = title.isNotBlank() && title.isNotEmpty() && time.first.isNotEmpty() && time.second.isNotEmpty() && date.first.isNotEmpty() && date.second.isNotEmpty() && remark.isNotBlank() && remark.isNotEmpty()
    }

    val jwt by DataStoreManager.supabaseJwt.collectAsState(initial = "")
    var isClone by remember { mutableStateOf(true) }
    val classList = remember { mutableStateListOf<String>() }
    var updateLoading by remember { mutableStateOf(false) }
    val typeIcon = @Composable {
        Icon(painterResource(if(isScheduleType) R.drawable.calendar else R.drawable.net),null)
    }

    if(showSelectDateDialog)
        DateRangePickerModal(isScheduleType,onSelected = { date = it }) { showSelectDateDialog = false }
    if(showSelectTimeDialog)
        TimeRangePickerDialog(isScheduleType,onSelected = { time = it }) { showSelectTimeDialog = false }


    LaunchedEffect(updateLoading) {
        if(isSupabase && updateLoading) {
            val entity = parseToDateTime(startDate = date.first, startTime = time.first, endDate = date.second, endTime = time.second)?.let {
                SupabaseEventOutput(
                    name = title,
                    dateTime = it,
                    type = if(isScheduleType) CustomEventType.SCHEDULE else CustomEventType.NET_COURSE,
                    description = description.let { desp -> if(desp.isNotEmpty() && desp.isNotBlank()) desp else null },
                    timeDescription = remark,
                    applicableClasses = classList.sorted(),
                    url = null,
                    campus = campus
                )
            }
            if(enabled && entity != null) {
                // 添加到数据库
                async { reEmptyLiveDta(vm.supabaseAddResp) }.await()
                async { updateLoading = true }.await()
                launch { vm.supabaseAdd(jwt, entity) }
            }

            Handler(Looper.getMainLooper()).post {
                vm.supabaseAddResp.observeForever { result ->
                    if (result != null) {
                        if(result.first) {
                            showToast("上传成功 请下拉刷新")
                            // 克隆
                            if(isClone) {
                                scope.launch {
                                    async {
                                        val entity = parseToDateTime(startDate = date.first, startTime = time.first, endDate = date.second, endTime = time.second)?.let {
                                            CustomEventDTO(
                                                title = title,
                                                dateTime = it,
                                                type = if(isScheduleType) CustomEventType.SCHEDULE else CustomEventType.NET_COURSE,
                                                description = description.let { desp -> if(desp.isNotEmpty() && desp.isNotBlank()) desp else null },
                                                remark = remark,
                                                supabaseId = getInsertedEventId(vm)
                                            )
                                        }
                                        if(enabled && entity != null) {
                                            // 添加到数据库
                                            DataBaseManager.customEventDao.insert(CustomEventMapper.dtoToEntity(entity))
                                        }
                                    }.await()
                                    // 关闭
                                    launch { updateLoading = false }
                                    launch { showChange(false) }
                                }
                            }
                        } else {
                            showToast("上传失败")
                        }
                    }
                }
            }
        }
    }

    if(showSupabaseDialog) {
        LittleDialog(
            onConfirmation = {
                updateLoading = true
                showSupabaseDialog = false
            },
            onDismissRequest = { showSupabaseDialog = false },
            dialogText = "是否核对好信息无误?提交后若有问题可删除重新添加；上传的内容请遵守需符合规范，不得出现谎骗、低俗等内容"
        )
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                if(!isSupabase) {
                    scope.launch {
                        async {
                            val entity = parseToDateTime(startDate = date.first, startTime = time.first, endDate = date.second, endTime = time.second)?.let {
                                CustomEventDTO(
                                    title = title,
                                    dateTime = it,
                                    type = if(isScheduleType) CustomEventType.SCHEDULE else CustomEventType.NET_COURSE,
                                    description = description.let { desp -> if(desp.isNotEmpty() && desp.isNotBlank()) desp else null },
                                    remark = remark
                                )
                            }
                            if(enabled && entity != null) {
                                // 添加到数据库
                                DataBaseManager.customEventDao.insert(CustomEventMapper.dtoToEntity(entity))
                                showToast("执行完成 请检查是否显示")
                            }
                        }.await()
                        // 关闭
                        launch { showChange(false) }
                    }
                } else {
                    if(enabled)
                        showSupabaseDialog = true
                    else
                        showChange(false)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = APP_HORIZONTAL_DP)
                .align(Alignment.BottomCenter)
                .zIndex(2f)
        ) {
            if(!updateLoading)
                Text(if(enabled) "添加" else "关闭")
            else
                LoadingIcon()
        }
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            DividerTextExpandedWith("预览") {
                StyleCardListItem(
                    headlineContent = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    leadingContent = typeIcon,
                    overlineContent = { Text(remark,maxLines = 1,overflow = TextOverflow.Ellipsis) },
                    supportingContent =  if(description.isNotBlank() && description.isNotEmpty()) { { Text(description,maxLines = 1,overflow = TextOverflow.Ellipsis) } } else null,
                    trailingContent = { FilledTonalIconButton(
                        onClick = {
                            scope.launch {
                                activity?.let { addToCalendars(startDate = date.first, startTime = time.first, endDate = date.second, endTime = time.second, description, title,null, it) }
                            }
                        }, enabled = enabled
                    ) { Icon(painterResource(R.drawable.event_upcoming),null) } },
                    modifier = Modifier.clickable { openOperation(description) }
                )
            }
            DividerTextExpandedWith("配置") {
                StyleCardListItem(
                    headlineContent = { Text("类型: " + if(isScheduleType) "日程" else "网课" ) },
                    supportingContent = { Text(if(isScheduleType) "日程类型旨在用户自行添加额外的课程、实验、会议等，强调线下活动、有始有终；\n添加后，在未开始时位于其他事项，进行期间会显示为重要事项" else "网课类型旨在用户自行添加需要在截止日期之前的网络作业、实验报告等，强调线上活动、无始有终，相比日程类型只注意结束时间(即DeadLine)；\n添加后，除了当天即将到达截止时位于重要事项，其余均位于其他事项" ) },
                    leadingContent = {
                        FilledTonalIconButton(
                            onClick = { isScheduleType = !isScheduleType },
                            content = typeIcon
                        )
                    } ,
                    modifier = Modifier.clickable {
                        isScheduleType = !isScheduleType
                        date = Pair("","")
                        time = Pair("","")
                    }
                )
                Spacer(Modifier.height(5.dp))
                CustomTextField(input = title, label = { Text("标题") },singleLine = false) { title = it }
                Spacer(Modifier.height(5.dp + CARD_NORMAL_DP))
                CustomTextField(input = description, label = { Text("备注(可空 可填写网址,地点,位置等)") },singleLine = false) { description = it }
                Spacer(Modifier.height(5.dp ))
                MyCustomCard(containerColor = cardNormalColor()) {
                    TransplantListItem(
                        headlineContent = { Text((if(isScheduleType)"开始 ${date.first + " " + time.first}\n" else "") + "结束 ${date.second + " " + time.second}") },
                        leadingContent = { Icon(painterResource(R.drawable.schedule),null) }
                    )
                    PaddingHorizontalDivider()
                    Row(modifier = Modifier.align(Alignment.End)) {
                        Text(
                            text = if(isScheduleType)"选择日期范围" else "选择截止日期",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.Bottom)
                                .padding(
                                    horizontal = APP_HORIZONTAL_DP,
                                    vertical = APP_HORIZONTAL_DP - 5.dp
                                )
                                .clickable {
                                    showSelectDateDialog = true
                                }
                        )
                        Text(
                            text = if(isScheduleType)"选择时间范围" else "选择截止时间",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.Top)
                                .padding(
                                    horizontal = APP_HORIZONTAL_DP,
                                    vertical = APP_HORIZONTAL_DP - 5.dp
                                )
                                .clickable {
                                    showSelectTimeDialog = true
                                }
                        )
                    }
                }
                Spacer(Modifier.height(5.dp))
                CustomTextField(input = remark, label = { Text("自定义时间显示") }, singleLine = false) { remark = it }
                Spacer(Modifier.height(5.dp - CARD_NORMAL_DP*0f))

                if(isSupabase) {
                    var isEditMode by remember { mutableStateOf(false) }
                    var input by remember { mutableStateOf("") }
                    var id by remember { mutableIntStateOf(-1) }
                    var showDelDialog by remember { mutableStateOf(false) }
                    var showAddDialog by remember { mutableStateOf(false) }

                    if(showDelDialog) {
                        LittleDialog(
                            onConfirmation = {
                                if(id >= 0) {
                                    classList.removeAt(id)
                                }
                                showDelDialog = false
                            },
                            onDismissRequest = { showDelDialog = false },
                            dialogText = "要删除此项吗"
                        )
                    }
                    if(showAddDialog) {
                        Dialog(
                            onDismissRequest = { showAddDialog = false }
                        ) {
                            Column(modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surface)) {
                                Column(modifier = Modifier.padding(APP_HORIZONTAL_DP)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        TextField(
                                            modifier = Modifier
                                                .weight(1f),
//                                                .padding(horizontal = APP_HORIZONTAL_DP),
                                            value = input,
                                            onValueChange = { input = it },
                                            singleLine = true,
                                            shape = MaterialTheme.shapes.medium,
                                            colors = textFiledTransplant(isColorCopy = false),
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))

                                    Row(modifier = Modifier
                                        .fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                                        FilledTonalButton(onClick = {
                                            showAddDialog = false
                                        },modifier = Modifier
                                            .weight(.5f)
                                        ) {
                                            Text(text = "取消")
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Button(onClick = {
                                            classList.add(input)
                                            showAddDialog = false
                                        },modifier = Modifier
                                            .weight(.5f)
                                        ) {
                                            Text(text = "保存")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    LaunchedEffect(Unit) { getPersonInfo().classes?.let { classList.add(it) } }
                    LaunchedEffect(showAddDialog) {
                        if(!showAddDialog)
                            input = ""
                    }

                    Spacer(Modifier.height(5.dp - CARD_NORMAL_DP*2f))
                    MyCustomCard(containerColor = cardNormalColor()) {
                        TransplantListItem(
                            headlineContent = { Text("适用范围" ) },
                            supportingContent = {
                                Text("为保证统一规范，必须按 查询中心-个人信息-班级 输入班级名，例如'计算机29-9班’而不是‘计科29-9班’，不添加/清空班级则表示对所有人可见" )
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.target),null)
                            } ,
                            modifier = Modifier.clickable {
                            }
                        )
                        PaddingHorizontalDivider()
                        TransplantListItem(
                            headlineContent = { Text("适用校区") },
                            supportingContent = {
                                Column {
                                    Row {
                                        FilterChip(onClick = { campus = EventCampus.HEFEI }, label = { Text("合肥") }, selected = campus == EventCampus.HEFEI)
                                        Spacer(Modifier.width(10.dp))
                                        FilterChip(onClick = { campus = EventCampus.XUANCHENG }, label = { Text("宣城") }, selected = campus == EventCampus.XUANCHENG)
                                        Spacer(Modifier.width(10.dp))
                                        FilterChip(onClick = { campus = EventCampus.DEFAULT }, label = { Text("所有人可见") }, selected = campus == EventCampus.DEFAULT)
                                    }
                                }

                            }
                        )
                        PaddingHorizontalDivider()
                        TransplantListItem(
                            headlineContent = { Text("适用班级") },
                            supportingContent = {
                                if(classList.isEmpty()) {
                                    RowHorizontal {
                                        Text("所有人可见")
                                    }
                                } else {
                                    Column {
                                        for(index in classList.indices step 2) {
                                            Row {
                                                AssistChip(
                                                    onClick = {
                                                        id = index
                                                        showDelDialog = true
                                                    },
                                                    label = { Text(classList[index]) },
                                                    leadingIcon = if(isEditMode) { { Icon(Icons.Filled.Close, null) } } else null
                                                )

                                                if(index+1 != classList.size) {
                                                    Spacer(Modifier.width(APP_HORIZONTAL_DP))
                                                    AssistChip(
                                                        onClick = {
                                                            id = index+1
                                                            showDelDialog = true
                                                        },
                                                        label = { Text(classList[index+1]) },
                                                        leadingIcon = if(isEditMode) { { Icon(Icons.Filled.Close, null) } } else null
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        )
                        Row(modifier = Modifier.align(Alignment.End)) {
                            Text(
                                text = "添加",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .align(Alignment.Bottom)
                                    .padding(
                                        horizontal = APP_HORIZONTAL_DP,
                                        vertical = APP_HORIZONTAL_DP - 5.dp
                                    )
                                    .clickable {
                                        showAddDialog = true
                                    }
                            )
                            Text(
                                text = if(!isEditMode) "编辑" else "完成",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .align(Alignment.Top)
                                    .padding(
                                        horizontal = APP_HORIZONTAL_DP,
                                        vertical = APP_HORIZONTAL_DP - 5.dp
                                    )
                                    .clickable {
                                        isEditMode = !isEditMode
                                    }
                            )
                            Text(
                                text = "清空",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .align(Alignment.Bottom)
                                    .padding(
                                        horizontal = APP_HORIZONTAL_DP,
                                        vertical = APP_HORIZONTAL_DP - 5.dp
                                    )
                                    .clickable {
                                        classList.clear()
                                    }
                            )
                            Text(
                                text = "排序(自动)",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .align(Alignment.Bottom)
                                    .padding(
                                        horizontal = APP_HORIZONTAL_DP,
                                        vertical = APP_HORIZONTAL_DP - 5.dp
                                    )
                                    .clickable {
                                        val sorted = classList.sorted() // 生成排序后的副本
                                        classList.clear()               // 清空原列表
                                        classList.addAll(sorted)        // 添加排序后的元素
                                    }
                            )
                        }
                    }
                    Spacer(Modifier.height(5.dp - CARD_NORMAL_DP))
                    StyleCardListItem(
                        headlineContent = { Text("同时克隆卡片至本地")},
                        supportingContent = { Text(if(isClone)"上传卡片时，同时添加入本地聚焦当中" else "仅共享卡片 自己无需使用")},
                        trailingContent = { Switch(checked = isClone, onCheckedChange = { ch -> isClone = ch }) },
                        modifier = Modifier.clickable { isClone = !isClone }
                    )
                    Spacer(Modifier.height(5.dp))
                    BottomTip("结果将上传至云端,仅持有校园邮箱用户可访问")
                } else {
                    BottomTip("结果将保存在本地，若需共享请进入云端共建")
                }
            }

            Spacer(Modifier.height(40.dp + APP_HORIZONTAL_DP))
        }
    }
}
