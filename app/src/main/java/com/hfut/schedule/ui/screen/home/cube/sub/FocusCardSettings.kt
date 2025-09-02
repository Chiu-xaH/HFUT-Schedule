package com.hfut.schedule.ui.screen.home.cube.sub

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.util.insertSafely
import com.hfut.schedule.logic.model.zjgd.FeeResponse
import com.hfut.schedule.logic.model.zjgd.FeeType
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.datetime.isHoliday
import com.hfut.schedule.logic.util.sys.datetime.isSpecificWorkDay
import com.hfut.schedule.logic.util.sys.datetime.isSpecificWorkDayTomorrow
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.mixedCardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.text.BottomSheetTopBar
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.multi.CourseType
import com.hfut.schedule.ui.screen.home.focus.funiction.TodayUI
import com.hfut.schedule.ui.screen.home.search.function.huiXin.card.SchoolCardItem
import com.hfut.schedule.ui.screen.home.search.function.huiXin.electric.Electric
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.LoginWeb
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getWebInfo
import com.hfut.schedule.ui.screen.home.search.function.huiXin.shower.getInGuaGua
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.ui.component.status.CustomSwitch
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.corner.bottomSheetRound
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.transition.component.containerShare
import com.xah.transition.util.TransitionPredictiveBackHandler
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusCardSettings(innerPadding : PaddingValues,navController: NavHostController) {
    var scale by remember { mutableFloatStateOf(1f) }
    TransitionPredictiveBackHandler(navController) {
        scale = it
    }
    var showBottomSheet by remember { mutableStateOf(false) }
    var sheetState = rememberModalBottomSheetState()

    val switch_ele = prefs.getBoolean("SWITCHELE", getCampusRegion() == CampusRegion.XUANCHENG)
    var showEle by remember { mutableStateOf(switch_ele) }
    SharedPrefs.saveBoolean("SWITCHELE", true, showEle)

    val switch_web = prefs.getBoolean("SWITCHWEB", getCampusRegion() == CampusRegion.XUANCHENG)
    var showWeb by remember { mutableStateOf(switch_web) }
    SharedPrefs.saveBoolean("SWITCHWEB", true, showWeb)

    val switch_card = prefs.getBoolean("SWITCHCARD", true)
    var showCard by remember { mutableStateOf(switch_card) }
    SharedPrefs.saveBoolean("SWITCHCARD", true, showCard)

    val switch_today = prefs.getBoolean("SWITCHTODAY", true)
    var showToday by remember { mutableStateOf(switch_today) }
    SharedPrefs.saveBoolean("SWITCHTODAY", true, showToday)


    val switch_card_add = prefs.getBoolean("SWITCHCARDADD", true)
    var showCardAdd by remember { mutableStateOf(switch_card_add) }
    SharedPrefs.saveBoolean("SWITCHCARDADD", true, showCardAdd)

    val switch_countDown = prefs.getBoolean("SWITCHCOUNTDOWN", false)
    var showCountDown by remember { mutableStateOf(switch_countDown) }
    SharedPrefs.saveBoolean("SWITCHCOUNTDOWN", false, showCountDown)

    val switch_shortCut = prefs.getBoolean("SWITCHSHORTCUT", false)
    var showShortCut by remember { mutableStateOf(switch_shortCut) }
    SharedPrefs.saveBoolean("SWITCHSHORTCUT", false, showShortCut)

    val showShower by DataStoreManager.enableShowFocusShower.collectAsState(initial = true)
    val showWeather by DataStoreManager.enableShowFocusWeatherWarn.collectAsState(initial = true)
//    val showSpecial by DataStoreManager.showFocusSpecial.collectAsState(initial = true)

    val scope = rememberCoroutineScope()


    Column(modifier = Modifier.verticalScroll(rememberScrollState()).scale(scale)) {
        InnerPaddingHeight(innerPadding,true)
        CardListItem(
            headlineContent = { Text(text = "打开开关则会在APP冷启动或刷新时自动获取数据,并显示在聚焦首页第一张卡片内") },
            leadingContent = { Icon(painter = painterResource(id = R.drawable.info), contentDescription = "",)},
            color = MaterialTheme.colorScheme.surface
        )

        DividerTextExpandedWith("预加载数据") {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text(text = "一卡通")} ,
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.credit_card), contentDescription = "")},
                    trailingContent = {
                        Row {
                            Switch(checked = showCardAdd, onCheckedChange = {showch -> showCardAdd = showch}, thumbContent = { Icon(painter = painterResource(id = R.drawable.add), contentDescription = "")})
                            Spacer(modifier = Modifier.width(10.dp))
                            Switch(checked = showCard, onCheckedChange = {showch -> showCard = showch})
                        }
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "寝室电费")} ,
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.flash_on), contentDescription = "")},
                    trailingContent = {
                        Switch(checked = showEle, onCheckedChange = {showch -> showEle = showch })
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "校园网")} ,
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.net), contentDescription = "")},
                    trailingContent = { Switch(checked = showWeb, onCheckedChange = {showch -> showWeb = showch})}
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "聚焦通知")} ,
                    supportingContent = { Text(text = "明日早八,临近课程,催还图书,临近考试")},
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.sentiment_very_satisfied), contentDescription = "")},
                    trailingContent = { Switch(checked = showToday, onCheckedChange = {showch -> showToday = showch})}
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "洗浴(需要时显示)")} ,
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.bathtub), contentDescription = "")},
                    trailingContent = { Switch(checked = showShower, onCheckedChange = { scope.launch { DataStoreManager.saveFocusShowShower(!showShower) } })}
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "气象预警(需要时显示)")} ,
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.warning), contentDescription = "")},
                    trailingContent = { Switch(checked = showWeather, onCheckedChange = { scope.launch { DataStoreManager.saveFocusShowWeatherWarn(!showWeather) } })}
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "调休提示(需要时显示)")} ,
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.beach_access), contentDescription = "")},
                    trailingContent = { Switch(checked = true, onCheckedChange = { }, enabled = false)}
                )
            }
        }
        InnerPaddingHeight(innerPadding,false)
    }



    if(showBottomSheet && showShortCut) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = bottomSheetRound(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    BottomSheetTopBar("选择你想放在聚焦首页的项")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}


@SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun FocusCard(
    vmUI : UIViewModel,
    vm : NetWorkViewModel,
    hazeState: HazeState,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val showEle = prefs.getBoolean("SWITCHELE",getCampusRegion() == CampusRegion.XUANCHENG)
    val showToday = prefs.getBoolean("SWITCHTODAY",true)
    val showWeb = prefs.getBoolean("SWITCHWEB",getCampusRegion() == CampusRegion.XUANCHENG)
    val showCard = prefs.getBoolean("SWITCHCARD",true)
    var loading by remember { mutableStateOf(false) }
    val showShower by DataStoreManager.enableShowFocusShower.collectAsState(initial = true)
    val showWeather by DataStoreManager.enableShowFocusWeatherWarn.collectAsState(initial = true)
    val route = remember { AppNavRoute.Life.route }
    val context = LocalContext.current
    if(showCard || showEle || showToday || showWeb)
        CustomCard(
            color = mixedCardNormalColor(),
            modifier = if(showWeather) {
                Modifier.containerShare(
                    sharedTransitionScope,
                    animatedContentScope = animatedContentScope,
                    route = route,
                    roundShape = MaterialTheme.shapes.medium,
                )
            } else Modifier,
        ) {
            Column() {
                if(showCard || showToday)
                    Row {
                        if(showCard)
                            Box(modifier = Modifier.weight(.5f)) {
                                SchoolCardItem(vmUI,true)
                            }
                        if(showToday)
                            Box(modifier = Modifier
                                .weight(.5f)) {
                                TodayUI(hazeState,vm)
                            }
                    }
                if(showWeb || showEle)
                    Row {
                        if(showEle)
                            Box(modifier = Modifier.weight(.5f)) {
                                Electric(vm,true,vmUI,hazeState)
                            }
                        if(showWeb)
                            Box(modifier = Modifier
                                .weight(.5f)) {
                                LoginWeb(vmUI,true,vm,hazeState)
                            }
                    }
                if(DateTimeManager.Time_Hour.toInt() in 22 until 25 && showShower) {
                    Row(
                        modifier = Modifier.clickable {
                            getInGuaGua(vm, context) { loading = it }
                        }
                    ) {
                        if(loading) {
                            TransplantListItem(
                                headlineContent = { Text(text = "正在核对登录") },
                                leadingContent = {
                                    LoadingIcon()
                                }
                            )
                        } else {
                            Box(modifier = Modifier.weight(.5f)) {
                                TransplantListItem(
                                    headlineContent = { Text(text = "晚上好") },
                                    overlineContent = { Text(text = "洗去一身疲惫吧") },
                                    leadingContent = {
                                        Icon(painterResource(id = R.drawable.dark_mode), contentDescription = "")
                                    }
                                )
                            }
                            Box(modifier = Modifier.weight(.5f)) {
                                TransplantListItem(headlineContent = { Text(text = "洗浴") }, leadingContent = {
                                    Icon(painterResource(id = R.drawable.bathtub), contentDescription = "")
                                }, overlineContent = { Text(text = "推荐") }
                                )
                            }
                        }
                    }
                }
                if(showWeather) {
                    val uiStateWarn by vm.weatherWarningData.state.collectAsState()
                    AnimatedVisibility(
                        visible = uiStateWarn is UiState.Success,
                        exit = AppAnimationManager.fadeAnimation.exit,
                        enter = AppAnimationManager.fadeAnimation.enter
                    ) {
                        val list = (uiStateWarn as UiState.Success).data
                        AnimatedVisibility(
                            visible = list.isNotEmpty(),
                            exit = AppAnimationManager.fadeAnimation.exit,
                            enter = AppAnimationManager.fadeAnimation.enter
                        ) {
                            with(list[0]) {
                                TransplantListItem(
                                    headlineContent = { Text(title) },
                                    overlineContent = { Text(typeName)},
                                    leadingContent = { Icon(painterResource(R.drawable.warning),null)},
                                    modifier = Modifier.clickable {
                                        navController.navigateForTransition(AppNavRoute.Life,route)
                                    },
                                )
                            }
                        }
                    }
                }
                Special(vmUI,hazeState)
            }
        }
}


fun getWebInfoFromHuiXin(vm: NetWorkViewModel, vmUI : UIViewModel)  {
    val auth = prefs.getString("auth","")
    CoroutineScope(Job()).launch {
        async { vm.getFee("bearer $auth",FeeType.NET_XUANCHENG) }.await()
        async {
            Handler(Looper.getMainLooper()).post{
                vm.infoValue.observeForever { result ->
                    if (result != null)
                        if(result.contains("success")&&!result.contains("账号不存在")) {
                            vmUI.webValue.value = getWebInfo(vm)
                        }
                }
            }
        }
    }
}

fun getElectricFromHuiXin(vm : NetWorkViewModel, vmUI : UIViewModel) {
    val BuildingsNumber = prefs.getString("BuildNumber", "0")
    val RoomNumber = prefs.getString("RoomNumber", "")
    val EndNumber = prefs.getString("EndNumber", "")

    var input = "300$BuildingsNumber$RoomNumber$EndNumber"
    val auth = prefs.getString("auth","")

    CoroutineScope(Job()).launch {
        async { vm.getFee("bearer $auth", FeeType.ELECTRIC_XUANCHENG, room = input) }.await()
        async {
            Handler(Looper.getMainLooper()).post{
                vm.electricData.observeForever { result ->
                    if (result?.contains("success") == true) {
                        try {
                            val data = Gson().fromJson(result,FeeResponse::class.java).map.showData
                            for ((_, value) in data) {
//                                val bd = BigDecimal(value.substringAfter("剩余金额:"))
                                vmUI.electricValue.value = formatDecimal(value.substringAfter("剩余金额:").toDouble(),2)
                                saveString("memoryEle",vmUI.electricValue.value)
                            }
                        } catch (_:Exception) { }
                    }
                }
            }
        }
    }
}

@Composable
fun Special(vmUI: UIViewModel,hazeState : HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val isSpecificWorkDay = remember { isSpecificWorkDay() }
    val isSpecificWorkDayTomorrow = remember { isSpecificWorkDayTomorrow() }

    val isHoliday = remember { isHoliday() }
    var isTomorrow by remember { mutableStateOf(false) }
    var targetDate by remember { mutableStateOf<String?>(null) }

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState,
            isFullExpand = true
        ) {
            ChangeCourseUI(isTomorrow) {
                showBottomSheet = it
            }
        }
    }
    LaunchedEffect(showBottomSheet) {
        if(showBottomSheet == false) {
            vmUI.specialWOrkDayChange++
        }
    }


    if(isHoliday) {
        Row(modifier = Modifier
//            .background(MaterialTheme.colorScheme.surfaceContainer)
//            .zIndex(2f)
            .clickable {

            }) {
            TransplantListItem(
                headlineContent = { ScrollText(text = "节假日休息(已隐藏今日课程)" ) },
                overlineContent = { ScrollText(text = "今天") },
                leadingContent = { Icon(painter = painterResource(R.drawable.beach_access) , contentDescription = "")},
            )
        }
    }
    if(isSpecificWorkDay) {
        LaunchedEffect(showBottomSheet) {
            targetDate = DataBaseManager.specialWorkDayDao.search(DateTimeManager.Date_yyyy_MM_dd)?.targetDate
        }
        PaddingHorizontalDivider()
        Row(modifier = Modifier
//            .background(MaterialTheme.colorScheme.errorContainer)
//            .zIndex(2f)
            .clickable {
                isTomorrow = false
                showBottomSheet = true
            }) {
            TransplantListItem(
                headlineContent = { ScrollText(text = "有调休上课" ) },
                overlineContent = { ScrollText(text = "今天") },
                leadingContent = { Icon(painter = painterResource(R.drawable.warning) , contentDescription = "")},
                modifier = Modifier.weight(.5f)
            )
            TransplantListItem(
                headlineContent = { ScrollText(text = "上${ targetDate?.substringAfter("-","") ?: "--" }课程" ) },
                overlineContent = { ScrollText(text = "选择今天上哪天的课") },
                modifier = Modifier.weight(.5f)
            )
        }
    } else if(isSpecificWorkDayTomorrow) {
        LaunchedEffect(showBottomSheet) {
            targetDate = DataBaseManager.specialWorkDayDao.search(DateTimeManager.tomorrow_YYYY_MM_DD)?.targetDate
        }
        PaddingHorizontalDivider()
        Row(modifier = Modifier
//            .background(MaterialTheme.colorScheme.surfaceContainer)
//            .zIndex(2f)
            .clickable {
                isTomorrow = true
                showBottomSheet = true
            }) {
            TransplantListItem(
                headlineContent = { ScrollText(text = "有调休上课" ) },
                overlineContent = { ScrollText(text = "明天") },
                leadingContent = { Icon(painter = painterResource(R.drawable.schedule) , contentDescription = "")},
                modifier = Modifier.weight(.5f)
            )
            TransplantListItem(
                headlineContent = { ScrollText(text = "上${ targetDate?.substringAfter("-","") ?: "--" }课程" ) },
                overlineContent = { ScrollText(text = "选择明天将上哪天的课") },
                modifier = Modifier.weight(.5f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeCourseUI(isTomorrow : Boolean,onDismiss : (Boolean) -> Unit) {
    val date = remember { if(isTomorrow) DateTimeManager.tomorrow_YYYY_MM_DD else DateTimeManager.Date_yyyy_MM_dd }
    var targetDate by remember { mutableStateOf<String?>(null) }

    val state = rememberDatePickerState()

    val scope = rememberCoroutineScope()
    LaunchedEffect(state.selectedDateMillis) {
        targetDate = try {
            DateTimeManager.simpleFormatter_YYYY_MM_DD.format(state.selectedDateMillis)
        } catch (e : Exception) { null }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            HazeBottomSheetTopBar("${date.substringAfter("-")}将要实行${targetDate?.substringAfter("-") ?: ""}") {
                if(state.selectedDateMillis != null) {
                    FilledTonalButton(
                        onClick = {
                            // 设置
                            scope.launch {
                                targetDate?.let {
                                    DataBaseManager.specialWorkDayDao.insertSafely(originDate = date, targetDate = it)
                                    onDismiss(false)
                                }
                            }
                        },
                    ) {
                        Text(text = "完成")
                    }
                } else {
                    FilledTonalButton(
                        onClick = {
                            // 恢复默认
                            scope.launch {
                                DataBaseManager.specialWorkDayDao.delete(date)
                                onDismiss(false)
                            }
                        },
                    ) {
                        Text(text = "恢复默认")
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
            if(prefs.getInt("SWITCH_DEFAULT_CALENDAR", CourseType.JXGLSTU.code) != CourseType.JXGLSTU.code)
                CardListItem(
                    headlineContent = { Text("检测到聚焦使用的是社区课表数据源，若需使聚焦首页显示调休课程功能生效，请在设置日期后，前往 选项-应用行为-默认课程表 切换为教务")},
                    leadingContent = { Icon(painterResource(R.drawable.info),null)}
                )
            else
                CardListItem(
                    headlineContent = { Text("设置完成后聚焦首页将会显示为设置日期的课程安排")},
                    leadingContent = { Icon(painterResource(R.drawable.info),null)}
                )
            DatePicker(state = state,
                modifier = Modifier.weight(1f), title = { Text(text = "")},
                colors = DatePickerDefaults.colors(containerColor = Color.Transparent),
            )
        }
    }
}

