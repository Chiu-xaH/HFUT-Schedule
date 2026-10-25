package com.hfut.schedule.ui.screen.home.search.function.community.bus


import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.helper.getCampus
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.datetime.SpecialDay
import com.hfut.schedule.logic.util.sys.datetime.isHolidayOrSpecificWorkDay
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.api.model.response.html.Bus
import com.hfut.schedule.ui.component.button.BUTTON_END_PADDING
import com.hfut.schedule.ui.component.button.BUTTON_PADDING
import com.hfut.schedule.ui.component.button.HazeBottomBarV2
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.button.containerBackDrop
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.dialog.DatePickerDialog
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.hfut.schedule.ui.model.NavigationBarItemData
import com.hfut.schedule.ui.nav.destination.BusDestination
import com.hfut.schedule.ui.nav.destination.NewsApiDestination
import com.hfut.schedule.ui.nav.window.BusDestinationSelectWindow
import com.hfut.schedule.ui.style.color.textFiledAllTransplant
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.rememberHazeBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.nav2Composable
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.util.navigation.currentRouteWithoutArgs
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.sharednav.common.helper.NoneRoundShape
import com.xah.common.logic.state.NetworkUiState
import com.xah.common.logic.util.EMPTY_STRING
import com.xah.common.logic.util.LogUtil
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.CenterScreen
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.container.component.base.SharedContainer
import com.xah.container.component.base.sharedContainer
import com.xah.floating.util.LocalFloatingController
import com.xah.navigation.util.LocalNavController
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Calendar

private enum class BusDate(val type : String,val description: String) {
    WORK_DAY("0","周一至周五"),
    SATURDAY("1","周六"),
    SUNDAY("2","周日")
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BusScreen(
    vm : NetWorkViewModel,
) {
    val hazeState = rememberHazeBlur()
    val dates = remember { BusDate.entries }
    var startInput by remember { mutableStateOf(EMPTY_STRING) }
    var endInput by remember { mutableStateOf(EMPTY_STRING) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val backdrop = rememberLayerBackdrop()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(BusDestination.title.asString()) },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    },
                    actions = {
                        LiquidButton(
                            onClick = {
                                scope.launch {
                                    Starter.startWebUrlInner(context, Constant.HFUT_URL + "xcxx.htm")
                                }
                            },
                            backdrop = backdrop,
                            isCircle = true,
                            modifier = Modifier.padding(end = BUTTON_END_PADDING)
                        ) {
                            Icon(painterResource(R.drawable.net),null)
                        }
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = APP_HORIZONTAL_DP),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
                        modifier = Modifier
                            .containerBackDrop(backdrop, MaterialTheme.shapes.medium)
                            .weight(.5f),
                        value = startInput,
                        onValueChange = {
                            startInput = it
                        },
                        label = { Text("起点") },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = textFiledAllTransplant(),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        modifier = Modifier
                            .containerBackDrop(backdrop, MaterialTheme.shapes.medium)
                            .weight(.5f),
                        value = endInput,
                        onValueChange = {
                            endInput = it
                        },
                        label = { Text("终点") },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = textFiledAllTransplant(),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp-CARD_NORMAL_DP))
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .backDropSource(backdrop)
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            val uiState by vm.busResponse.state.collectAsState()
            val refreshNetwork : suspend () -> Unit = m@ {
                if(uiState is NetworkUiState.Success) {
                    return@m
                }
                prefs.getString("TOKEN","")?.let {
                    vm.busResponse.clear()
                    vm.getBus(it)
                }
            }
            LaunchedEffect(Unit) {
                refreshNetwork()
            }
            CommonNetworkScreen(uiState, onReload = refreshNetwork) {
                val list = (uiState as NetworkUiState.Success).data.filter {
                    it.from.contains(startInput) && it.to.contains(endInput)
                }
                LazyColumn {
                    item { InnerPaddingHeight(innerPadding,true) }
                    items(list.size, key = { it }) { index ->
                        val item = list[index]
                        with(item) {
                            val date = dates.find { it.type == type }?.description ?: type
                            val pointList = stop.split("  ").filter { it.isNotEmpty() && it.isNotBlank() }.toMutableList()
                            // 向首部添加from 尾部添加to
                            pointList.add(0, from)
                            pointList.add(to)
                            // 用线路图显示
                            CustomCard(color = cardNormalColor()) {
                                TransplantListItem(
                                    headlineContent = {
                                        Text("$date $time")
                                    },
                                    supportingContent = {
                                        Text("上车地点 : $place")
                                    },
                                    trailingContent = {
                                        Text(count.toString() + "辆")
                                    },
                                    leadingContent = {
                                        Icon(painterResource(R.drawable.schedule),null)
                                    },
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = APP_HORIZONTAL_DP)
                                        .padding(bottom = APP_HORIZONTAL_DP),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // 第一行：横线贯穿全宽，圆点平均分布
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box {
                                            val transition = rememberInfiniteTransition(label = "scroll")
                                            var barWidthPx by remember { mutableStateOf(0f) }

                                            val offsetX by transition.animateFloat(
                                                initialValue = -1f,
                                                targetValue = 2f, // 扫完整条 + 额外一点
                                                animationSpec = infiniteRepeatable(
                                                    animation = tween(durationMillis = 3000, easing = LinearEasing),
                                                    repeatMode = RepeatMode.Restart
                                                ),
                                                label = "offsetX"
                                            )



                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(10.dp)
                                                    .onSizeChanged {
                                                        barWidthPx = it.width.toFloat()
                                                    }
                                                    .background(
//                                                            Brush.linearGradient(
//                                                                colors = listOf(
//                                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
//                                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
//                                                                ),
//                                                            ),
                                                        Brush.linearGradient(
                                                            colors = listOf(
                                                                MaterialTheme.colorScheme.primary.copy(
                                                                    alpha = 0.25f
                                                                ),
                                                                MaterialTheme.colorScheme.primary.copy(
                                                                    alpha = 0.55f
                                                                ),
                                                                MaterialTheme.colorScheme.primary.copy(
                                                                    alpha = 0.25f
                                                                ),
                                                            ),
                                                            start = Offset(
                                                                barWidthPx * offsetX,
                                                                0f
                                                            ),
                                                            // 0.5f为渐变宽度
                                                            end = Offset(
                                                                barWidthPx * (offsetX + 0.5f),
                                                                0f
                                                            ) // 始终保持横跨一段宽度
                                                        ),
                                                        shape = CircleShape
                                                    ),
                                            )
//                                                Box(
//                                                    Modifier
//                                                        .fillMaxWidth()
//                                                        .height(10.dp)
//                                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = .5f), shape = CircleShape)
//                                                )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                pointList.forEachIndexed { index, _ ->
                                                    val isPort = index == 0 || index == pointList.size-1
                                                    // 圆点
                                                    Box(
                                                        Modifier
                                                            .size(10.dp)
                                                            .clip(CircleShape)
                                                            .background(MaterialTheme.colorScheme.primary)
                                                    )
                                                    if (index != pointList.lastIndex) {
                                                        Box(Modifier
                                                            .weight(1f)
                                                            .background(Color.Transparent))
                                                    }
                                                }
                                            }

                                        }
                                    }


                                    // 第二行：竖排文字，平均分布到圆点下方
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        pointList.forEachIndexed { index,point ->
                                            val isPort = index == 0 || index == pointList.size-1
                                            Text(
                                                text = point.toCharArray().joinToString("\n"),
//                                                    fontWeight = if(isPort) FontWeight.Bold else FontWeight.Normal,
                                                color = MaterialTheme.colorScheme.primary,
                                                style = MaterialTheme.typography.labelMedium,
                                                lineHeight = 16.sp,
                                                modifier = Modifier.padding(top = CARD_NORMAL_DP*2)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item { InnerPaddingHeight(innerPadding,false) }
                }
            }
        }
    }
}

enum class BusBarItems(val page : Int) {
    NOW(0),SEARCH(1)
}

private val items = listOf(
    NavigationBarItemData(
        BusBarItems.NOW.name,
        "出行",
        R.drawable.schedule,
        R.drawable.schedule_filled,
    ),
    NavigationBarItemData(
        BusBarItems.SEARCH.name,
        "所有安排",
        R.drawable.directions_bus,
        R.drawable.directions_bus_filled,
    )
)

// 新数据源
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BusScreenV2(
    vm : NetWorkViewModel,
) {
    val hazeState = rememberHazeBlur()
    var startInput by rememberSaveable() { mutableStateOf<String?>(null) }
    var endInput by rememberSaveable() { mutableStateOf<String?>(null) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val backdrop = rememberLayerBackdrop()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
//    var busDate by rememberSaveable() { mutableStateOf<BusDate?>(null) }
    val busNavController = rememberNavController()
    val targetPage = when(busNavController.currentRouteWithoutArgs()) {
        BusBarItems.NOW.name -> BusBarItems.NOW
        else -> BusBarItems.SEARCH
    }
    val floatingController = LocalFloatingController.current

    val uiState by vm.busResponseV2.state.collectAsState()
    val refreshNetwork : suspend () -> Unit = m@ {
        if(uiState is NetworkUiState.Success) {
            return@m
        }
        vm.busResponseV2.clear()
        vm.getBusV2()
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    LaunchedEffect(targetPage) {
        when(targetPage) {
            BusBarItems.NOW -> {
                if(startInput == null && endInput == null) {
                    startInput = getCampus()?.description
                }
            }
            BusBarItems.SEARCH -> {
//                startInput = null
//                endInput = null
            }
        }
    }

    var date by rememberSaveable { mutableStateOf(DateTimeManager.Date_yyyy_MM_dd) }
    var showSelectDateDialog by remember { mutableStateOf(false) }
    if(showSelectDateDialog)
        DatePickerDialog(onConfirm = { date = it },allowSelectPrevious = false) { showSelectDateDialog = false }

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(BusDestination.title.asString()) },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    },
                    actions = {
                        Row(
                            modifier = Modifier.padding(end = BUTTON_END_PADDING)
                        ) {
                            when(targetPage) {
                                BusBarItems.NOW -> {
                                    LiquidButton(
                                        onClick = {
                                            showSelectDateDialog = true
                                        },
                                        isCircle = false,
                                        backdrop = backdrop
                                    ) {
                                        Text(date)
                                    }
                                }
                                BusBarItems.SEARCH -> {
                                    /*
                                    LiquidButton(
                                        onClick = {
                                            busDate = when(busDate) {
                                                null -> BusDate.WORK_DAY
                                                BusDate.WORK_DAY -> BusDate.SATURDAY
                                                BusDate.SATURDAY -> BusDate.SUNDAY
                                                BusDate.SUNDAY -> null
                                            }
                                        },
                                        backdrop = backdrop,
                                        isCircle = false,
                                    ) {
                                        Text(busDate?.description ?: "全部")
                                    }
                                    Spacer(Modifier.width(BUTTON_PADDING))
                                     */
                                    LiquidButton(
                                        onClick = {
                                            scope.launch {
                                                Starter.startWebUrlInner(context, Constant.HFUT_URL + "xcxx.htm")
                                            }
                                        },
                                        backdrop = backdrop,
                                        isCircle = true,
                                    ) {
                                        Icon(painterResource(R.drawable.net),null)
                                    }
                                }
                            }
                        }
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = APP_HORIZONTAL_DP),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val startWindow = BusDestinationSelectWindow(
                        true,startInput,endInput
                    ) {
                        startInput = it
                    }
                    SharedContainer(
                        shape = MaterialTheme.shapes.medium,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        key = startWindow.key,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            modifier = Modifier
                                .height(56.dp)
                                .containerBackDrop(backdrop, MaterialTheme.shapes.medium)
                                .clickable {
                                    floatingController.push(startWindow)
                                },
                            shape = NoneRoundShape,
                            color = Color.Transparent,
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    startInput ?: "起点",
                                    maxLines = 1,
                                    color = if(startInput == null) Color.Gray else Color.Unspecified,
                                    modifier = Modifier
                                        .padding(start = APP_HORIZONTAL_DP)
                                        .align(Alignment.CenterStart)
                                )
                                /*
                                if(targetPage == BusBarItems.NOW) {
                                    NoPadding {
                                        IconButton (
                                            onClick = {
                                                showDevelopingToast()
                                            },
                                            modifier = Modifier
                                                .padding(end = BUTTON_PADDING)
                                                .size(BUTTON_HEIGHT)
                                                .align(Alignment.CenterEnd)
                                        ) {
                                            Icon(
                                                painterResource(R.drawable.near_me),
                                                null
                                            )
                                        }
                                    }
                                }

                                 */
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(BUTTON_PADDING))
                    LiquidButton(
                        onClick = {
                            val temp = startInput
                            startInput = endInput
                            endInput = temp
                        },
                        backdrop = backdrop,
                        isCircle = true
                    ) {
                        Icon(
                            painterResource(R.drawable.swap_horiz),
                            null,
                            tint = if(startInput == null && endInput == null) Color.Gray else LocalContentColor.current,
                        )
                    }
                    Spacer(modifier = Modifier.width(BUTTON_PADDING))
                    val endWindow = BusDestinationSelectWindow(
                        false,endInput,startInput
                    ) {
                        endInput = it
                    }
                    SharedContainer(
                        shape = MaterialTheme.shapes.medium,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        key = endWindow.key,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            modifier = Modifier
                                .height(56.dp)
                                .containerBackDrop(backdrop, MaterialTheme.shapes.medium)
                                .clickable {
                                    floatingController.push(endWindow)
                                },
                            shape = NoneRoundShape,
                            color = Color.Transparent,
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    endInput ?: "终点",
                                    maxLines = 1,
                                    color = if(endInput == null) Color.Gray else Color.Unspecified,
                                    modifier = Modifier
                                        .padding(start = APP_HORIZONTAL_DP)
                                        .align(Alignment.CenterStart)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp-CARD_NORMAL_DP))
                if(uiState is NetworkUiState.Error) {
                    GoToHolidayUI(false,date)
                }
            }
        },
        bottomBar = {
            HazeBottomBarV2(
                hazeState,
                backdrop,
                items,
                busNavController
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = busNavController,
            startDestination = BusBarItems.NOW.name,
            enterTransition = {
                AppAnimationManager.centerAnimation.enter
            },
            exitTransition = {
                AppAnimationManager.centerAnimation.exit
            },
            modifier = Modifier
                .hazeSource(state = hazeState)
                .backDropSource(backdrop)
        ) {
            nav2Composable(BusBarItems.NOW.name) {
                BusNowScreen(vm,innerPadding,startInput,endInput,date,refreshNetwork)
            }
            nav2Composable(BusBarItems.SEARCH.name) {
                BusSearchScreen(vm,innerPadding,null,startInput,endInput,refreshNetwork)
            }
        }
    }
}


private fun getBusDate(dateStr : String) : BusDate? {
    try {
        val date = LocalDate.parse(dateStr)
        return when (date.dayOfWeek) {
            DayOfWeek.SATURDAY -> BusDate.SATURDAY
            DayOfWeek.SUNDAY -> BusDate.SUNDAY
            else -> BusDate.WORK_DAY
        }
    } catch (e : Exception) {
        LogUtil.error(e)
        return null
    }
}


@Composable
private fun GoToHolidayUI(
    bottomPadding : Boolean,
    date: String
) {
    val navController = LocalNavController.current
    val isHolidayOrSpecificWorkDay = remember(date) { isHolidayOrSpecificWorkDay(date) }

    if(isHolidayOrSpecificWorkDay != SpecialDay.NORMAL) {
        val dest = remember { NewsApiDestination(NewsApiDestination.Keyword.HOLIDAY_SCHEDULE.keyword) }
        Column {
            CardListItem(
                headlineContent = {
                    val typeStr = when(isHolidayOrSpecificWorkDay) {
                        SpecialDay.HOLIDAY -> "假期"
                        SpecialDay.WORK_DAY -> "调休工作日"
                        else -> ""
                    }
                    Text("此日期为$typeStr，点击可跳转至调休通知公告，以实际安排为准")
                },
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.stream),
                        null
                    )
                },
                modifier = Modifier.clickable {
                    navController.push(dest)
                },
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = NoneRoundShape,
                cardModifier = Modifier.sharedContainer(
                    key = dest.key,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
            )
            if(bottomPadding) {
                Spacer(modifier = Modifier.height(8.dp-CARD_NORMAL_DP))
            }
        }
    }
}

@Composable
private fun BusNowScreen(
    vm : NetWorkViewModel,
    innerPadding : PaddingValues,
    startInput : String?,
    endInput : String?,
    date : String,
    refreshNetwork : suspend () -> Unit
) {
    val uiState by vm.busResponseV2.state.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        CommonNetworkScreen(uiState, onReload = refreshNetwork) {
            val result = remember(uiState,date) {
                (uiState as NetworkUiState.Success).data
                    .filter { map ->
                        getBusDate(date)?.let {
                            map.key == it.description
                        } ?: true
                    }
                    .flatMap { it.value }
                    .sortedBy { it.time }
            }

            var refresh by remember { mutableStateOf(0) }
            // 整分钟对齐刷新
            LaunchedEffect(Unit) {
                while (true) {
                    val now = Calendar.getInstance()
                    val second = now.get(Calendar.SECOND)
                    val millis = now.get(Calendar.MILLISECOND)
                    delay((60 - second) * 1000L - millis)
                    refresh++
                }
            }

            var hasPassed by remember { mutableStateOf(false) }
            val list = remember(startInput,endInput,date,refresh) {
                hasPassed = false
                result
                    .filter {
                        it.from.contains(startInput ?: EMPTY_STRING) &&
                        it.to.contains(endInput ?: EMPTY_STRING)
                    }
                    .filter {
                        if(date == DateTimeManager.Date_yyyy_MM_dd) {
                            val state = DateTimeManager.getTimeStateReal(it.time,it.time).first
                            if(state == DateTimeManager.TimeState.NOT_STARTED) {
                                true
                            } else {
                                hasPassed = true
                                false
                            }
                        } else {
                            true
                        }
                    }
            }

            if(list.isEmpty()) {
                CenterScreen {
                    EmptyIcon(
                        if(date == DateTimeManager.Date_yyyy_MM_dd && hasPassed) {
                            "今天的运营已结束"
                        } else {
                            "暂无班车安排"
                        }
                    )
                }
                return@CommonNetworkScreen
            }
            LazyColumn {
                item { InnerPaddingHeight(innerPadding,true) }
                item {
                    GoToHolidayUI(true,date)
                }
                items(list.size, key = { it }) { index ->
                    val item = list[index]
                    with(item) {
                        CustomCard(color = cardNormalColor()) {
                            TransplantListItem(
                                headlineContent = {
                                    Text("$from → $to")
                                },
                                overlineContent = {
                                    Text("$place | $time | ${count}辆")
                                },
//                                trailingContent = {
//                                    // 不足30min时显示 X分钟后发车
//                                    // 否则不显示内容
//                                    Text("10分钟后发车")
//                                },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.directions_bus),null)
                                },
                            )
                            BusPathUI(this)
                        }
                    }
                }
                item { InnerPaddingHeight(innerPadding,false) }
            }
        }
    }
}

@Composable
private fun BusSearchScreen(
    vm : NetWorkViewModel,
    innerPadding : PaddingValues,
    busDate : BusDate?,
    startInput : String?,
    endInput : String?,
    refreshNetwork : suspend () -> Unit,
) {
    val uiState by vm.busResponseV2.state.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        CommonNetworkScreen(uiState, onReload = refreshNetwork) {
            val result = (uiState as NetworkUiState.Success).data

            val list = result
                .let { l ->
                    busDate?.description?.let {
                        l[it]
                    }
                        ?: l.flatMap { it.value }
                }
                .filter {
                    it.from.contains(startInput ?: EMPTY_STRING) && it.to.contains(endInput ?: EMPTY_STRING)
                }

            if(list.isEmpty()) {
                CenterScreen {
                    EmptyIcon("无符合条件的校车安排")
                }
                return@CommonNetworkScreen
            }
            LazyColumn {
                item { InnerPaddingHeight(innerPadding,true) }
                items(list.size, key = { it }) { index ->
                    val item = list[index]
                    with(item) {
                        CustomCard(color = cardNormalColor()) {
                            TransplantListItem(
                                headlineContent = {
                                    Text("$week $time")
                                },
                                supportingContent = {
                                    Text("上车地点 : $place")
                                },
                                trailingContent = {
                                    Text(count.toString() + "辆")
                                },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.schedule),null)
                                },
                            )
                            BusPathUI(this)
                        }
                    }
                }
                item { InnerPaddingHeight(innerPadding,false) }
            }
        }
    }
}

@Composable
private fun BusPathUI(
    bus : Bus
) = with(bus) {
    val pointList by produceState(initialValue = emptyList(), key1 = bus) {
        val list = stops.toMutableList()
        // 向首部添加from 尾部添加to
        list.add(0, from)
        list.add(to)
        value = list.toList()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = APP_HORIZONTAL_DP)
            .padding(bottom = APP_HORIZONTAL_DP),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                val transition = rememberInfiniteTransition(label = "scroll")
                var barWidthPx by remember { mutableStateOf(0f) }

                val offsetX by transition.animateFloat(
                    initialValue = -1f,
                    targetValue = 2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 3000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "offsetX"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .onSizeChanged {
                            barWidthPx = it.width.toFloat()
                        }
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.25f
                                    ),
                                    MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.55f
                                    ),
                                    MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.25f
                                    ),
                                ),
                                start = Offset(
                                    barWidthPx * offsetX,
                                    0f
                                ),
                                end = Offset(
                                    barWidthPx * (offsetX + 0.5f),
                                    0f
                                )
                            ),
                            shape = CircleShape
                        ),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pointList.forEachIndexed { index, _ ->
                        val isPort = index == 0 || index == pointList.size-1
                        // 圆点
                        Box(
                            Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        if (index != pointList.lastIndex) {
                            Box(Modifier
                                .weight(1f)
                                .background(Color.Transparent))
                        }
                    }
                }

            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            pointList.forEachIndexed { index,point ->
                Text(
                    text = point.toCharArray().joinToString("\n"),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = CARD_NORMAL_DP*2)
                )
            }
        }
    }
}
