package com.hfut.schedule.ui.screen.home.focus.funiction

import android.annotation.SuppressLint
import androidx.activity.BackEventCompat
import androidx.activity.compose.BackHandler
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
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.core.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomEventType
import com.hfut.schedule.logic.database.repository.CustomEventRepository
import com.hfut.schedule.logic.database.util.CustomEventMapper
import com.hfut.schedule.logic.model.enumeration.LocalEvent
import com.hfut.schedule.logic.network.repo.UniAppRepository
import com.hfut.schedule.logic.util.helper.getCampus
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.JumpTransitionEffectWallpaper
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showDevelopingToast
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.api.util.getUinAppCampusId
import com.hfut.schedule.ui.component.button.BUTTON_END_PADDING
import com.hfut.schedule.ui.component.button.BUTTON_PADDING
import com.hfut.schedule.ui.component.button.BottomTextButtonGroup
import com.hfut.schedule.ui.component.button.CardBottomButton
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.dialog.DatePickerDialog
import com.hfut.schedule.ui.component.dialog.DateRangePickerDialog
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.hfut.schedule.ui.component.dialog.TimeRangePickerDialog
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.divider.defaultDividerColor
import com.hfut.schedule.ui.component.icon.BrushIcon
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.input.WheelPicker
import com.hfut.schedule.ui.component.status.StatusIcon
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.nav.destination.AddEventDestination
import com.hfut.schedule.ui.nav.destination.SupabaseDestination
import com.hfut.schedule.ui.nav.destination.SupabaseLoginDestination
import com.hfut.schedule.ui.screen.home.calendar.common.dateToWeek
import com.hfut.schedule.ui.screen.home.calendar.common.numToChinese
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.EventCampus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getEventCampus
import com.hfut.schedule.ui.screen.supabase.login.loginSupabaseWithCheck
import com.hfut.schedule.ui.style.appNavigationBarPadding
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.rememberHazeBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.layout.measureDpSize
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.sharednav.common.helper.NoneRoundShape
import com.xah.common.logic.model.Campus
import com.xah.common.logic.state.NetworkUiState
import com.xah.common.logic.util.EMPTY_STRING
import com.xah.common.logic.util.isNotEmptyAndBlank
import com.xah.common.ui.component.status.CustomSingleChoiceRow
import com.xah.common.ui.component.status.LoadingUI
import com.xah.common.ui.component.text.BottomTip
import com.xah.common.ui.component.text.ScrollText
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.CenterScreen
import com.xah.common.ui.style.align.ColumnVertical
import com.xah.common.ui.style.align.RowHorizontal
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.common.ui.style.padding.navigationBarHeightPadding
import com.xah.common.ui.util.text
import com.xah.navigation.model.action.LaunchMode
import com.xah.navigation.util.LocalNavController
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.util.UUID
import kotlin.Pair
import kotlin.coroutines.cancellation.CancellationException

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AddEventFloatButton(
    isSupabase : Boolean,
    isVisible: Boolean,
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
            label = EMPTY_STRING
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

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    vm : NetWorkViewModel,
    eventId : Int = -1,
    enableCopy : Boolean = false,
    isSupabase : Boolean = false
) {
    val navController = LocalNavController.current
    val jwt by DataStoreManager.supabaseJwt.collectAsState(initial = EMPTY_STRING)
    val refreshToken by DataStoreManager.supabaseRefreshToken.collectAsState(initial = EMPTY_STRING)

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val backDrop = rememberLayerBackdrop()
    val hazeState = rememberHazeBlur()

    if(showDialog) {
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                if(eventId >= 0) {
                    scope.launch {
                        async { DataBaseManager.customEventDao.del(eventId) }.await()
                        launch { showDialog = false }
                        launch(Dispatchers.Main) {
                            navController.pop()
                        }
                    }
                } else {
                    showToast("id错误")
                }
            },
            dialogText = "要删除此项吗",
        )
    }

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                modifier = Modifier.topBarBlur(hazeState),
                scrollBehavior = scrollBehavior,
                colors = topBarTransplantColor(),
                title = {
                    val platformText = remember(isSupabase) {
                        if(isSupabase) {
                            "云端"
                        } else {
                            EMPTY_STRING
                        }
                    }
                    Text(
                        if(eventId <= 0) "添加${platformText}日程"
                        else if(enableCopy) "复制${platformText}日程"
                        else "修改${platformText}日程"
                    )
                },
                navigationIcon = {
                    TopBarNavigationIcon()
                },
                actions = {
                    if(eventId <= 0) {
                        LiquidButton(
                            onClick = {
                                showDevelopingToast()
                            },
                            isCircle = true,
                            backdrop = backDrop
                        ) {
                            BrushIcon(icon = R.drawable.wand_stars)
                        }
                        if(!isSupabase) {
                            LiquidButton(
                                onClick = {
                                    navController.push(SupabaseDestination, effect = JumpTransitionEffectWallpaper())
                                },
                                modifier = Modifier
                                    .padding(end = APP_HORIZONTAL_DP)
                                    .padding(start = BUTTON_PADDING)
                                ,
                                isCircle = true,
                                backdrop = backDrop
                            ) {
                                Icon(painterResource(R.drawable.cloud),null)
                            }
                        }
                    } else if(!enableCopy) {
                        LiquidButton(
                            onClick = {
                                navController.push(
                                    AddEventDestination(eventId, enableCopy = true),
                                    launchMode = LaunchMode.Replace(),
                                )
                            },
                            isCircle = true,
                            backdrop = backDrop
                        ) {
                            Icon(painterResource(R.drawable.copy_all),null)
                        }
                        Spacer(Modifier.width(BUTTON_PADDING))
                        LiquidButton(
                            onClick = {
                                showDialog = true
                            },
                            modifier = Modifier.padding(end = BUTTON_END_PADDING),
                            isCircle = true,
                            backdrop = backDrop,
                        ) {
                            Icon(painterResource(R.drawable.delete),null, tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        val canUse by produceState<Boolean?>(initialValue = null) {
            value = if(isSupabase) loginSupabaseWithCheck(jwt,refreshToken,vm,context) else true
        }
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .backDropSource(backDrop)
        ) {
            when(canUse) {
                null -> {
                    CenterScreen {
                        LoadingUI("正在核对登录(登录账号才可贡献日程)")
                    }
                }
                true -> {
                    AddEventUI(vm,isSupabase,eventId,enableCopy,innerPadding) {
                        navController.pop()
                    }
                }
                false -> {
                    CenterScreen {
                        ColumnVertical {
                            StatusIcon(R.drawable.login,text("未登录或状态失效"))
                            Spacer(Modifier.height(APP_HORIZONTAL_DP))
                            Button(onClick = {
                                navController.push(SupabaseLoginDestination, effect = JumpTransitionEffectWallpaper())
                            }) {
                                Text("刷新登录状态")
                            }
                        }
                    }
                }
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
                .padding(bottom = innerPaddings.calculateBottomPadding() - navigationBarHeightPadding)
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
        ) { Icon(painterResource(R.drawable.add), "Add Button") }
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
    val jwt by DataStoreManager.supabaseJwt.collectAsState(initial = EMPTY_STRING)
    val refreshToken by DataStoreManager.supabaseRefreshToken.collectAsState(initial = EMPTY_STRING)

//    var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
    var useBackHandler by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if(useBackHandler == false) {
            delay(AppAnimationManager.ANIMATION_SPEED*1L)
            useBackHandler = true
        }
//        launch {
//            loading = true
//            loginSupabaseWithCheck(jwt,refreshToken,vm,context)
//            loading = false
//        }
//        //                            scope.launch {

//                            }
    }
    val navController = LocalNavController.current
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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
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
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text("添加") },
                    actions = {
                        if(!isSupabase) {
                            FilledTonalButton(onClick = {
                                navController.push(SupabaseDestination, effect = JumpTransitionEffectWallpaper())
                            }, modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                                Text("云端共建")
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { showChange(false) }
                        ) {
                            Icon(painterResource(R.drawable.arrow_back),null,tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }
        },
    ) { innerPadding ->
        AnimatedVisibility(
            modifier = Modifier.scale(scale),
            visible = showSurface,
            enter  = fadeIn(),
            exit = fadeOut(tween(durationMillis = 0))
        ) {
            val canUse by produceState<Boolean?>(initialValue = null) {
                value = if(isSupabase) loginSupabaseWithCheck(jwt,refreshToken,vm,context) else true
            }
            Column(modifier = Modifier
                .padding(innerPadding)
                .background(Color.Transparent)) {
                when(canUse) {
                    null -> {
                        CenterScreen {
                            LoadingUI("正在核对登录(登录账号才可贡献日程)")
                        }
                    }
                    true -> {
                        CenterScreen {
                            StatusIcon(
                                R.drawable.build,
                                text("上传日程功能正改造升级，敬请期待")
                            )
                        }
                        return@Column
                        AddEventUI(vm,isSupabase,showChange = showChange, innerPadding = innerPadding)
                    }
                    false -> {
                        CenterScreen {
                            ColumnVertical {
                                StatusIcon(R.drawable.login,text("未登录或状态失效"))
                                Spacer(Modifier.height(APP_HORIZONTAL_DP))
                                Button(onClick = {
                                    navController.push(SupabaseLoginDestination, effect = JumpTransitionEffectWallpaper())
                                }) {
                                    Text("刷新登录状态")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun generateRemark(
    isScheduleType : Boolean,
    date : Pair<String,String>,
    time : Pair<String, String>
) : String {
    return if(isScheduleType) {
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddEventUI(
    vm: NetWorkViewModel,
    isSupabase : Boolean,
    eventId: Int = -1,
    enableCopy : Boolean = false,
    innerPadding : PaddingValues,
    showChange: (Boolean) -> Unit
) {
    val groupId = remember { UUID.randomUUID().toString() }
    var enabled by remember { mutableStateOf(false) }
    val editedData by produceState(initialValue = emptyList()) {
        if(eventId <= 0) {
            return@produceState
        }
        value = CustomEventRepository.getGroupById(eventId).map {
            CustomEventMapper.entityToDto(it)
        }
    }
    var isScheduleType by remember { mutableStateOf(true) }
    var title by remember { mutableStateOf(EMPTY_STRING) }
    val timeParts = remember { mutableStateListOf(TimePart()) }

    // 外置数据填入
    LaunchedEffect(editedData) {
        if(editedData.isEmpty()) {
            return@LaunchedEffect
        }
        val head = editedData[0]
        isScheduleType = head.type == CustomEventType.SCHEDULE
        title = head.title

        timeParts.clear()
        editedData.forEach {
            val preDateTime = it.dateTime
            val preStart = preDateTime.start
            val preEnd = preDateTime.end
            val bean = TimePart(
                description = it.description ?: EMPTY_STRING,
                remark = it.remark,
                time = Pair(
                    "${parseTimeItem(preStart.hour)}:${parseTimeItem(preStart.minute)}",
                    "${parseTimeItem(preEnd.hour)}:${parseTimeItem(preEnd.minute)}"
                ),
                date = Pair(
                    "${preStart.year}-${parseTimeItem(preStart.month)}-${parseTimeItem(preStart.day)}",
                    "${preEnd.year}-${parseTimeItem(preEnd.month)}-${parseTimeItem(preEnd.day)}"
                )
            )
            timeParts.add(bean)
        }
    }

    var showSupabaseDialog by remember { mutableStateOf(false) }


    val scope = rememberCoroutineScope()

    var campus by remember { mutableStateOf(getEventCampus()) }

    val jwt by DataStoreManager.supabaseJwt.collectAsState(initial = EMPTY_STRING)
    var isClone by remember { mutableStateOf(true) }
    val classList = remember { mutableStateListOf<String>() }
    var updateLoading by remember { mutableStateOf(false) }
    val typeIcon = @Composable {
        Icon(painterResource(if(isScheduleType) R.drawable.calendar else R.drawable.timer),null)
    }

    /* TODO P2 数据回填至Model
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
                                                supabaseId = getInsertedEventId(vm),
                                                groupId = null
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

     */

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
    var bottomHeight by remember { mutableStateOf(0.dp) }
    val color = MaterialTheme.colorScheme.surface
    val useCreate = editedData.isEmpty() || enableCopy
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .measureDpSize { _, h ->
                    bottomHeight = h
                }
                .align(Alignment.BottomCenter)
                .zIndex(2f)
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to color.copy(alpha = 0f),
                            0.25f to color.copy(alpha = 0.65f),
                            0.50f to color.copy(alpha = 0.80f),
                            0.75f to color.copy(alpha = 0.95f),
                            1.0f to color.copy(alpha = 1f),
                        )
                    )
                )
                .appNavigationBarPadding()
        ){
            /* TODO P3 堆叠UI
            OutlinedCard(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP, vertical = CARD_NORMAL_DP),
            ) {
                TransplantListItem(
                    headlineContent = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    leadingContent = typeIcon,
                    overlineContent = { Text(remark,maxLines = 1,overflow = TextOverflow.Ellipsis) },
                    supportingContent =  if(description.isNotBlank() && description.isNotEmpty()) { { Text(description,maxLines = 1,overflow = TextOverflow.Ellipsis) } } else null,
                )
            }
             */
            Button(
                onClick = {
                    /* TODO 点击事件
                    if(!isSupabase) {
                        scope.launch {
                            async {
                                val entity = parseToDateTime(startDate = date.first, startTime = time.first, endDate = date.second, endTime = time.second)?.let {
                                    CustomEventDTO(
                                        title = title,
                                        dateTime = it,
                                        type = if(isScheduleType) CustomEventType.SCHEDULE else CustomEventType.NET_COURSE,
                                        description = description.let { desp -> if(desp.isNotEmpty() && desp.isNotBlank()) desp else null },
                                        remark = remark,
                                        supabaseId = null,
                                        groupId = null
                                    )
                                }
                                if(enabled && entity != null) {
                                    // 添加到数据库
                                    val targetEntity = CustomEventMapper.dtoToEntity(entity)
                                    val result = async {
                                        if (useCreate) {
                                            DataBaseManager.customEventDao.insert(targetEntity).toInt()
                                        } else {
                                            val sId = editedData!!.supabaseId
                                            DataBaseManager.customEventDao.update(targetEntity.copy(id = eventId, supabaseId = sId))
                                        }
                                    }.await()
                                    if(result <= 0) {
                                        showToast("执行失败")
                                    } else {
                                        showToast("执行成功")
                                    }
                                }
                            }.await()
                            // 关闭
                            launch { showChange(false) }
                        }
                    } else {
                        if(enabled)
                            showSupabaseDialog = true
                    }
                     */
                },
//                shape = MaterialTheme.shapes.medium,
                enabled = updateLoading || enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = APP_HORIZONTAL_DP)

            ) {
                if(!updateLoading)
                    Text(
                        if(useCreate) "添加" else "更新"
                    )
                else
                    LoadingIcon()
            }

        }

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            InnerPaddingHeight(innerPadding,true)
            CustomSingleChoiceRow(
                options = LocalEvent.entries,
                selected = if(isScheduleType) LocalEvent.SCHEDULE else LocalEvent.DEADLINE,
            ) {
                isScheduleType = when(it) {
                    LocalEvent.DEADLINE -> false
                    LocalEvent.SCHEDULE -> true
                }
            }
            CardListItem(
                cardModifier = Modifier.padding(bottom = CARD_NORMAL_DP),
                headlineContent = {
                    Text(
                        if(isScheduleType)
                            "日程类型旨在用户自行添加额外的课程、实训、实验、班会等，强调线下活动、有始有终;添加后，将同时显示在课程表方格中，在进行期间会显示为重要事项，否则在其他事项中"
                        else
                            "DDL类型旨在用户自行添加需要在截止日期之前的作业、实验报告等，强调线上活动，相比日程类型只需注意结束时间;添加后，当剩余72h内时将会显示为重要事项，否则在其他事项中",
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                modifier = Modifier.clickable {
                    isScheduleType = !isScheduleType
                },
                leadingContent = {
                    FilledTonalIconButton(
                        onClick = {
                            isScheduleType = !isScheduleType
                        },
                        content = typeIcon
                    )
                }
            )
            Spacer(Modifier.height(CARD_NORMAL_DP))
            CustomTextField(input = title, label = { Text("标题") },singleLine = false) { title = it }
            if(isScheduleType) {
                DividerTextExpandedWith("时间段") {
                    LaunchedEffect(Unit) {
                        snapshotFlow {
                            timeParts.map { it.enabled }
                        }.collect { enabledList ->
                            enabled = enabledList.all { it } && title.isNotEmptyAndBlank()
                        }
                    }
                    // TODO bug手动刷新才预填充
                    timeParts.forEachIndexed { index, item ->
                        var thisDescription by remember { mutableStateOf(item.description ?: EMPTY_STRING) }
                        var thisTime by remember { mutableStateOf(item.time) }
                        var thisDate by remember { mutableStateOf(item.date) }
                        val thisRemark by produceState(initialValue = EMPTY_STRING, key1 = thisDate, key2 = thisTime) {
                            // remark自动生成
                            value = generateRemark(true,thisDate,thisTime)
                        }
                        var thisShowSelectDateDialog by remember { mutableStateOf(false) }
                        var thisShowSelectTimeDialog by remember { mutableStateOf(false) }

                        if(thisShowSelectDateDialog)
                            EventDatePickerDialog(
                                isScheduleType,
                                onSelected = {
                                    val d = (it as EventDatePickerSelectedResult.Date).date
                                    thisDate = Pair(d,d)
                                }
                            ) {
                                thisShowSelectDateDialog = false
                            }
                        if(thisShowSelectTimeDialog)
                            TimeRangePickerDialog(isScheduleType,onSelected = { thisTime = it }, defaultValue = thisTime) { thisShowSelectTimeDialog = false }

                        // 每一个TImePart都是合法的
                        LaunchedEffect(thisTime,thisDate) {
                            item.enabled = thisTime.first.isNotEmpty() && thisTime.second.isNotEmpty() && thisDate.first.isNotEmpty() && thisDate.second.isNotEmpty() && thisRemark.isNotEmptyAndBlank()
                        }

                        CustomCard(color = cardNormalColor()) {
                            Column {
                                var thisDisplaySelector by remember { mutableStateOf(false) }
                                CustomTextField(
                                    input = thisDescription,
                                    label = { Text("备注(可为空)") },
                                    singleLine = false,
                                    shape = NoneRoundShape,
                                    colors = TextFieldDefaults.colors().copy(
                                        focusedIndicatorColor = if(!thisDisplaySelector) Color.Transparent else defaultDividerColor(),
                                        unfocusedIndicatorColor = if(!thisDisplaySelector) Color.Transparent else defaultDividerColor(),
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent
                                    ),
                                    trailingIcon = {
                                        IconButton(
                                            onClick = {
                                                thisDisplaySelector = !thisDisplaySelector
                                            }
                                        ) {
                                            val animatedImageVector = AnimatedImageVector.animatedVectorResource(id = R.drawable.ic_anim_expand)
                                            val painter = rememberAnimatedVectorPainter(animatedImageVector, thisDisplaySelector)
                                            Icon(painter, null)
                                        }
                                    },
                                    modifier = Modifier,
                                ) {
                                    thisDescription = it
                                }
                                AnimatedVisibility(
                                    visible = thisDisplaySelector,
                                    enter = slideInVertically(
                                        initialOffsetY = { -40 }
                                    ) + expandVertically(
                                        expandFrom = Alignment.Top
                                    ) + scaleIn(
                                        transformOrigin = TransformOrigin(0.5f, 0f)
                                    ) + fadeIn(initialAlpha = 0.3f),
                                    exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(targetScale = 1.2f)
                                ) {
                                    BuildingsSelector(vm) {
                                        thisDescription = it
                                    }
                                }
                            }
                            PaddingHorizontalDivider(isDashed = true)
                            TransplantListItem(
                                headlineContent = {
                                    val thisWeekInfoStart by produceState<Pair<Int, Int>?>(initialValue = null,key1 = thisDate) {
                                        value = dateToWeek(thisDate.first)
                                    }
                                    val thisTimeStr = if(thisTime.first.isEmpty() || thisTime.second.isEmpty()) {
                                        "----"
                                    } else if(thisTime.first == thisTime.second) {
                                        thisTime.first
                                    } else {
                                        thisTime.first + "~" + thisTime.second
                                    }
                                    Text(
                                        thisDate.second.ifEmpty { "----" } +  (thisWeekInfoStart?.let { " (第${it.first}周 周${numToChinese(it.second)})" } ?: EMPTY_STRING) +" " + thisTimeStr
                                    )
                                },
                                leadingContent = { Icon(painterResource(R.drawable.schedule),null) }
                            )
                            BottomTextButtonGroup(
                                listOf(
//                            CardBottomButton("清空",isScheduleType && dates.isNotEmpty(), errorColor = true) {
//                                dates.clear()
//                            },
                                    CardBottomButton("选择日期") {
                                        thisShowSelectDateDialog = true
                                    },
                                    CardBottomButton("选择时间") {
                                        thisShowSelectTimeDialog = true
                                    },
//                                CardBottomButton("复制", show = false) {
//                                    showDevelopingToast()
//                                },
                                    CardBottomButton("删除", errorColor = true, show = timeParts.size != 1) {
                                        timeParts.removeAt(index)
                                    }
                                )
                            )
                        }
                    }
                    FilledTonalButton (
                        onClick = {
                            timeParts.add(TimePart())
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = cardNormalColor())
                    ) {
                        Text("添加")
                    }
                }
            } else {
                var thisTime by remember {
                    mutableStateOf(
                        if(timeParts.isEmpty()) {
                            Pair(EMPTY_STRING,EMPTY_STRING)
                        } else {
                            timeParts[0].time
                        }
                    )
                }
                var thisDate by remember {
                    mutableStateOf(
                        if(timeParts.isEmpty()) {
                            Pair(EMPTY_STRING,EMPTY_STRING)
                        } else {
                            timeParts[0].date
                        }
                    )
                }
                var thisDescription by remember {
                    mutableStateOf(
                        if(timeParts.isEmpty()) {
                            EMPTY_STRING
                        } else {
                            timeParts[0].description ?: EMPTY_STRING
                        }
                    )
                }
                val thisRemark by produceState(initialValue = EMPTY_STRING, key1 = thisDate, key2 = thisTime) {
                    // remark自动生成
                    value = generateRemark(false,thisDate,thisTime)
                }
                var thisShowSelectDateDialog by remember { mutableStateOf(false) }
                var thisShowSelectTimeDialog by remember { mutableStateOf(false) }

                if(thisShowSelectDateDialog)
                    EventDatePickerDialog(
                        false,
                        onSelected = {
                            thisDate = (it as EventDatePickerSelectedResult.Range).range
                        }
                    ) {
                        thisShowSelectDateDialog = false
                    }
                if(thisShowSelectTimeDialog)
                    TimeRangePickerDialog(false,onSelected = { thisTime = it }, defaultValue = thisTime) { thisShowSelectTimeDialog = false }

                val weekInfoEnd by produceState<Pair<Int, Int>?>(initialValue = null,key1 = thisDate) {
                    value = dateToWeek(thisDate.second)
                }

                // 每一个TImePart都是合法的
                LaunchedEffect(title,thisTime,thisDate) {
                    enabled = title.isNotEmptyAndBlank() && thisTime.first.isNotEmpty() && thisTime.second.isNotEmpty() && thisDate.first.isNotEmpty() && thisDate.second.isNotEmpty() && thisRemark.isNotEmptyAndBlank()
                }

                Spacer(Modifier.height(CARD_NORMAL_DP*2))
                CustomCard(color = cardNormalColor()) {
                    var thisDisplaySelector by remember { mutableStateOf(false) }
                    CustomTextField(
                        input = thisDescription,
                        label = { Text("备注(可为空)") },
                        singleLine = false,
                        shape = NoneRoundShape,
                        colors = TextFieldDefaults.colors().copy(
                            focusedIndicatorColor = if(!thisDisplaySelector) Color.Transparent else defaultDividerColor(),
                            unfocusedIndicatorColor = if(!thisDisplaySelector) Color.Transparent else defaultDividerColor(),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    thisDisplaySelector = !thisDisplaySelector
                                }
                            ) {
                                val animatedImageVector = AnimatedImageVector.animatedVectorResource(id = R.drawable.ic_anim_expand)
                                val painter = rememberAnimatedVectorPainter(animatedImageVector, thisDisplaySelector)
                                Icon(painter, null)
                            }
                        },
                        modifier = Modifier,
                    ) {
                        thisDescription = it
                    }
                    AnimatedVisibility(
                        visible = thisDisplaySelector,
                        enter = slideInVertically(
                            initialOffsetY = { -40 }
                        ) + expandVertically(
                            expandFrom = Alignment.Top
                        ) + scaleIn(
                            transformOrigin = TransformOrigin(0.5f, 0f)
                        ) + fadeIn(initialAlpha = 0.3f),
                        exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(targetScale = 1.2f)
                    ) {
                        BuildingsSelector(vm) {
                            thisDescription = it
                        }
                    }
                }
                Spacer(Modifier.height(CARD_NORMAL_DP))
                CustomCard(color = cardNormalColor()) {
                    TransplantListItem(
                        headlineContent = {
                            Text(
                                "结束 ${thisDate.second +  (weekInfoEnd?.let { " (第${it.first}周 周${numToChinese(it.second)})" } ?: EMPTY_STRING) +" " + thisTime.second}"
                            )
                        },
                        leadingContent = { Icon(painterResource(R.drawable.schedule),null) }
                    )
                    BottomTextButtonGroup(
                        listOf(
                            CardBottomButton("选择截止日期") {
                                thisShowSelectDateDialog = true
                            },
                            CardBottomButton("选择截止时间") {
                                thisShowSelectTimeDialog = true
                            },
                        )
                    )
                }
            }

            Spacer(Modifier.height(CARD_NORMAL_DP*2))

            if(isSupabase) {
                var isEditMode by remember { mutableStateOf(false) }
                var input by remember { mutableStateOf(EMPTY_STRING) }
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

                LaunchedEffect(Unit) { getPersonInfo().className?.let { classList.add(it) } }
                LaunchedEffect(showAddDialog) {
                    if(!showAddDialog) {
                        input = EMPTY_STRING
                    }
                }

                Spacer(Modifier.height(5.dp - CARD_NORMAL_DP*2f))
                CustomCard(color = cardNormalColor()) {
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
                                                leadingIcon = if(isEditMode) { { Icon(painterResource(R.drawable.close), null) } } else null
                                            )

                                            if(index+1 != classList.size) {
                                                Spacer(Modifier.width(APP_HORIZONTAL_DP))
                                                AssistChip(
                                                    onClick = {
                                                        id = index+1
                                                        showDelDialog = true
                                                    },
                                                    label = { Text(classList[index+1]) },
                                                    leadingIcon = if(isEditMode) { { Icon(painterResource(R.drawable.close), null) } } else null
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
                CardListItem(
                    headlineContent = { Text("同时克隆卡片至本地")},
                    supportingContent = { Text(if(isClone)"上传卡片时，同时添加入本地聚焦当中" else "仅共享卡片 自己无需使用")},
                    trailingContent = { Switch(checked = isClone, onCheckedChange = { ch -> isClone = ch }) },
                    modifier = Modifier.clickable { isClone = !isClone }
                )
                Spacer(Modifier.height(5.dp))
                BottomTip("结果将上传至云端,仅持有校园邮箱用户可访问")
            }
            InnerPaddingHeight(innerPadding,false)
            Spacer(Modifier.height(bottomHeight + APP_HORIZONTAL_DP).appNavigationBarPadding())
        }
    }
}

private class OuterApp

@Composable
private fun BuildingsSelector(
    vm : NetWorkViewModel,
    modifier : Modifier = Modifier,
    onSelected : (String) -> Unit
) {
    var campus by remember { mutableStateOf<Any?>(getCampus() ?: OuterApp()) }
    var selectedBuildings by remember { mutableStateOf<String?>(null) }

    val chipsUiState by vm.uniAppBuildingsResp.state.collectAsState()
    val refreshNetworkChips = suspend m@ {
        if(chipsUiState is NetworkUiState.Success) {
            return@m
        }
        var jwt = DataStoreManager.uniAppJwt.first()
        if(jwt.isEmpty() || jwt.isEmpty()) {
            val loginResult = UniAppRepository.login()
            if(!loginResult) {
                return@m
            }
            jwt = DataStoreManager.uniAppJwt.first()
        }
        vm.uniAppBuildingsResp.clear()
        vm.getBuildings(token = jwt)
    }

    LaunchedEffect(campus) {
        if(campus is OuterApp) {
            return@LaunchedEffect
        }
        refreshNetworkChips()
    }

    LaunchedEffect(selectedBuildings) {
        selectedBuildings?.let { onSelected(it) }
    }

    val ddlList = remember {
        listOf(
            Starter.AppPackages.RAIN_CLASSROOM.appName,
            Starter.AppPackages.CHAO_XING.appName,
            Starter.AppPackages.MOOC.appName,
            Starter.AppPackages.TODAY_CAMPUS.appName,
            "U校园"
        )
    }

    Column(modifier = modifier) {
        val finalList : List<Any> = remember { Campus.entries + OuterApp() }

        Row(
            Modifier.padding(horizontal = APP_HORIZONTAL_DP)
        ) {
            WheelPicker(
                data = finalList,
                initialSelectedIndex = when(campus) {
                    Campus.TXL -> 0
                    Campus.FCH -> 1
                    Campus.XC -> 2
                    else -> 3
                },
                modifier = Modifier.weight(.5f),
                enableInfiniteScroll = true,
                selectedColor = MaterialTheme.colorScheme.surface,
                selectedShape = FilterChipDefaults.shape,
                onSelect = { _,content ->
                    selectedBuildings = EMPTY_STRING
                    campus = content
                }
            ) { content ->
                Text(
                    if(content is Campus) {
                        content.description + "校区"
                    } else {
                        "其它"
                    }
                )
            }
            if(campus is OuterApp) {
                Spacer(Modifier.width(APP_HORIZONTAL_DP/2))
                WheelPicker(
                    data = ddlList,
                    initialSelectedIndex = 0,
                    modifier = Modifier.weight(.5f),
                    enableInfiniteScroll = false,
                    selectedColor = MaterialTheme.colorScheme.surface,
                    selectedShape = FilterChipDefaults.shape,
                    onSelect = { _,content ->
                        selectedBuildings = content
                    }
                ) {
                    ScrollText(it)
                }
                return
            }
            if(chipsUiState is NetworkUiState.Success) {
                val buildingList = (chipsUiState as NetworkUiState.Success).data
                    .filter {
                        if(campus is Campus) {
                            getUinAppCampusId(campus as Campus) == it.campusAssoc
                        } else {
                            true
                        }
                    }
                    .map { it.nameZh }
                Spacer(Modifier.width(APP_HORIZONTAL_DP/2))
                WheelPicker(
                    data = buildingList,
                    modifier = Modifier.weight(.5f),
                    enableInfiniteScroll = false,
                    selectedColor = MaterialTheme.colorScheme.surface,
                    selectedShape = FilterChipDefaults.shape,
                    onSelect = { _,content ->
                        selectedBuildings = content
                    }
                ) {
                    ScrollText(it)
                }
            }
        }
        // 建筑
        if(chipsUiState is NetworkUiState.Loading) {
            BottomTip("正在获取建筑列表")
            Spacer(modifier = Modifier.height(CARD_NORMAL_DP*3))
        } else if(chipsUiState is NetworkUiState.Error) {
            BottomTip("获取建筑列表失败")
            Spacer(modifier = Modifier.height(CARD_NORMAL_DP*3))
        }
    }
}


private sealed class EventDatePickerSelectedResult {
    data class Range(
        val range : Pair<String, String>
    ) : EventDatePickerSelectedResult()
/*
    data class Dates(
        val dates : List<String>
    ) : EventDatePickerSelectedResult()
 */
    data class Date(
        val date : String
    ) : EventDatePickerSelectedResult()
}

@Deprecated("即将抛弃范围选取的交互，此函数将会被删除")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TemporaryEventDatePickerDialog(
    isRangeOrSingle : Boolean = false,
    onSelected: (Pair<String, String>) -> Unit,
    onDismiss: () -> Unit
) {
    if(isRangeOrSingle) {
        DateRangePickerDialog(onSelected,onDismiss)
    } else {
        var startDate by remember { mutableStateOf<Long?>(null) }
        var startDateString by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            val todayStartMillis = DateTimeManager.getToday()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            startDate = todayStartMillis
            startDateString = DateTimeManager.simpleFormatter_YYYY_MM_DD.format(startDate)
        }

        DatePickerDialog(
            "截止",
            onConfirm = {
                startDateString?.let { s ->
                    onSelected(Pair(s, it))
                }
            },
            onDismiss = onDismiss
        )
    }
}

/**
 * @param isRangeOrSingle 为true选择日期范围，返回Pair<String, String>起止日期；为false选择单日期，返回Pair<String, String>的second为其值
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventDatePickerDialog(
    isRangeOrSingle : Boolean = false,
    onSelected: (EventDatePickerSelectedResult) -> Unit,
    onDismiss: () -> Unit
) {
    if(isRangeOrSingle) {
        DatePickerDialog(
            onDismiss = onDismiss,
            onConfirm = {
                onSelected(EventDatePickerSelectedResult.Date(it))
            }
        )
        /*
        DatesPickerDialog(
            initialDates = emptyList(),
            onDismiss = onDismiss,
            onConfirm = {
                onSelected(EventDatePickerSelectedResult.Dates(it))
            }
        )
         */
    } else {
        var startDate by remember { mutableStateOf<Long?>(null) }
        var startDateString by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            val todayStartMillis = DateTimeManager.getToday()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            startDate = todayStartMillis
            startDateString = DateTimeManager.simpleFormatter_YYYY_MM_DD.format(startDate)
        }

        DatePickerDialog(
            "截止",
            onConfirm = {
                startDateString?.let { s ->
                    onSelected(EventDatePickerSelectedResult.Range(Pair(s, it)))
                }
            },
            onDismiss = onDismiss
        )
    }
}

private data class TimePart(
    val description : String? = null,
    val remark : String? = null,
    val date : Pair<String,String> = Pair(EMPTY_STRING,EMPTY_STRING),
    val time : Pair<String,String> = Pair(EMPTY_STRING,EMPTY_STRING),
    val id : String = UUID.randomUUID().toString()
) {
    var enabled : Boolean by mutableStateOf(false)
}
