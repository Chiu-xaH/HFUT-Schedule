package com.hfut.schedule.ui.screen.home.cube.sub

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.util.insertSafely
import com.xah.common.logic.model.CampusRegion
import com.hfut.schedule.logic.util.helper.getCampusRegion
import com.hfut.schedule.network.api.model.response.json.huixin.HuiXinFeeResponse
import com.hfut.schedule.network.api.model.response.json.huixin.HuiXinFeeType
import com.hfut.schedule.logic.util.parse.roundOffString
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.datetime.isHoliday
import com.hfut.schedule.logic.util.sys.datetime.isSpecificWorkDay
import com.hfut.schedule.logic.util.sys.datetime.isSpecificWorkDayTomorrow
import com.hfut.schedule.network.core.GsonInstance
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.model.choice.GradeAutoCheckMode
import com.hfut.schedule.ui.nav.destination.GradeDestination
import com.hfut.schedule.ui.nav.destination.LifeDestination
import com.hfut.schedule.ui.nav.destination.NewsApiDestination
import com.hfut.schedule.ui.screen.home.calendar.multi.CourseType
import com.hfut.schedule.ui.screen.home.focus.funiction.TodayUI
import com.hfut.schedule.ui.screen.home.search.function.huiXin.card.SchoolCardItem
import com.hfut.schedule.ui.screen.home.search.function.huiXin.electric.Electric
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.LoginWeb
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getWebInfo
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.ui.util.state.GlobalEventHolder
import com.hfut.schedule.ui.util.state.GlobalUiStateHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.sharednav.common.helper.NoneRoundShape
import com.xah.common.logic.state.NetworkUiState
import com.xah.common.logic.util.LogUtil
import com.xah.common.ui.component.status.CustomSingleChoiceRow
import com.xah.common.ui.component.text.ScrollText
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.container.component.base.SharedContainer
import com.xah.navigation.util.LocalNavController
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusCardSettings(innerPadding : PaddingValues) {

    val enableShowFocusToday by DataStoreManager.enableShowFocusToday.collectAsState(true)
    val enableShowFocusElectric by DataStoreManager.enableShowFocusElectric.collectAsState(true)
    val enableShowFocusSchoolNet by DataStoreManager.enableShowFocusSchoolNet.collectAsState(true)
    val enableShowFocusSchoolCard by DataStoreManager.enableShowFocusSchoolCard.collectAsState(true)
    val enableShowFocusSchoolCardAddButton by DataStoreManager.enableShowFocusSchoolCardAddButton.collectAsState(true)
    val enableShowFocusGrade by DataStoreManager.enableShowFocusGrade.collectAsState(GradeAutoCheckMode.ONLY_VACATION.code)
    val enableShowFocusWeatherWarn by DataStoreManager.enableShowFocusWeatherWarn.collectAsState(initial = false)
    val electricUseHefei by DataStoreManager.useHefeiElectric.collectAsState(initial = getCampusRegion() == CampusRegion.HEFEI)

    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
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
                            Switch(
                                checked = enableShowFocusSchoolCardAddButton,
                                onCheckedChange = {
                                    scope.launch {
                                        DataStoreManager.saveEnableFocusSchoolCardAddButton(!enableShowFocusSchoolCardAddButton)
                                    }
                                },
                                thumbContent = { Icon(painter = painterResource(id = R.drawable.add), contentDescription = "")}
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Switch(
                                checked = enableShowFocusSchoolCard,
                                onCheckedChange = {
                                    scope.launch {
                                        DataStoreManager.saveEnableFocusSchoolCard(!enableShowFocusSchoolCard)
                                    }
                                }
                            )
                        }
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "寝室电费")} ,
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.flash_on), contentDescription = "")},
                    supportingContent = {
                        Row( ) {
                            FilterChip(
                                onClick = {
                                    scope.launch { DataStoreManager.saveUseHefeiElectric(true) }
                                },
                                selected = electricUseHefei,
                                label = { Text("合肥校区") }
                            )
                            Spacer(Modifier.width(CARD_NORMAL_DP*2))
                            FilterChip(
                                onClick = {
                                    scope.launch { DataStoreManager.saveUseHefeiElectric(false) }
                                },
                                selected = !electricUseHefei,
                                label = { Text("宣城校区") }
                            )
                        }
                    },
                    trailingContent = {
                        Switch(
                            checked = enableShowFocusElectric,
                            onCheckedChange = {
                                scope.launch {
                                    DataStoreManager.saveEnableFocusElectric(!enableShowFocusElectric)
                                }
                            }
                        )
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "校园网")} ,
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.net), contentDescription = "")},
                    trailingContent = {
                        Switch(
                            checked = enableShowFocusSchoolNet,
                            onCheckedChange = {
                                scope.launch {
                                    DataStoreManager.saveEnableFocusSchoolNet(!enableShowFocusSchoolNet)
                                }
                            }
                        )
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "聚焦通知")} ,
                    supportingContent = { Text(text = "明日早八,临近课程,催还图书,临近考试")},
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.sentiment_very_satisfied), contentDescription = "")},
                    trailingContent = {
                        Switch(
                            checked = enableShowFocusToday,
                            onCheckedChange = {
                                scope.launch {
                                    DataStoreManager.saveEnableShowFocusToday(!enableShowFocusToday)
                                }
                            }
                        )
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "气象预警")} ,
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.warning), contentDescription = "")},
                    trailingContent = { Switch(checked = enableShowFocusWeatherWarn, onCheckedChange = { scope.launch { DataStoreManager.saveFocusShowWeatherWarn(!enableShowFocusWeatherWarn) } })}
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "调休提示")} ,
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.beach_access), contentDescription = "")},
                    trailingContent = { Switch(checked = true, onCheckedChange = { }, enabled = false)}
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "成绩单")} ,
                    supportingContent = { Text(text = "检查是否出现新的成绩")},
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.article), contentDescription = "")},
                )
                CustomSingleChoiceRow<GradeAutoCheckMode>(
                    selected = enableShowFocusGrade,
                    modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP)
                ) {
                    scope.launch {
                        DataStoreManager.saveEnableShowFocusGrade(it)
                    }
                }
            }
        }
        InnerPaddingHeight(innerPadding,false)
    }
}


@SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun FocusCard(
    vm : NetWorkViewModel,
    hazeState: HazeState,
) {
    val navController = LocalNavController.current
    val showToday by DataStoreManager.enableShowFocusToday.collectAsState(true)
    val showEle by DataStoreManager.enableShowFocusElectric.collectAsState(true)
    val showWeb by DataStoreManager.enableShowFocusSchoolNet.collectAsState(true)
    val showCard by DataStoreManager.enableShowFocusSchoolCard.collectAsState(true)
    val showWeather by DataStoreManager.enableShowFocusWeatherWarn.collectAsState(initial = false)
    val gradeCount by GlobalEventHolder.gradeCountCheckCallback.flow.collectAsState(initial = null)

    if(showCard || showEle || showToday || showWeb)
        CustomCard(
            color = cardNormalColor(),
        ) {
            Column() {
                if(showCard || showToday)
                    Row {
                        if(showCard)
                            Box(modifier = Modifier.weight(.5f)) {
                                SchoolCardItem(true)
                            }
                        if(showToday)
                            Box(modifier = Modifier
                                .weight(.5f)) {
                                TodayUI(vm)
                            }
                    }
                if(showWeb || showEle)
                    Row {
                        if(showEle)
                            Box(modifier = Modifier.weight(.5f)) {
                                Electric(vm,true,hazeState)
                            }
                        if(showWeb)
                            Box(modifier = Modifier
                                .weight(.5f)) {
                                LoginWeb(true,vm,hazeState)
                            }
                    }
                if(gradeCount != null && gradeCount != 0) {
                    val dest = GradeDestination(ifSaved = false)
                    AnimatedVisibility(
                        visible = true,
                        exit = AppAnimationManager.fadeAnimation.exit,
                        enter = AppAnimationManager.fadeAnimation.enter
                    ) {
                        SharedContainer(
                            key = null,
//                            key = dest.key,
                            shape = NoneRoundShape,
                            containerColor = cardNormalColor()
                        ) {
                            TransplantListItem(
                                colors = cardNormalColor(),
                                headlineContent = {
                                    Text(
                                        if(gradeCount == 0) {
                                            "无新出成绩"
                                        } else {
                                            "新出${gradeCount}门"
                                        }
                                    )
                                },
                                overlineContent = { Text("成绩单")},
                                leadingContent = {
                                    Icon(
                                        painterResource(id = R.drawable.article),
                                        contentDescription = "",
                                    )
                                },
                                modifier = Modifier.clickable {
                                    GlobalEventHolder.gradeCountCheckCallback.clear()
                                    navController.push(dest)
                                },
                            )
                        }
                    }
                }
                Special()
                if(showWeather) {
                    val uiStateWarn by vm.weatherWarningData.state.collectAsState()
                    AnimatedVisibility(
                        visible = uiStateWarn is NetworkUiState.Success,
                        exit = AppAnimationManager.fadeAnimation.exit,
                        enter = AppAnimationManager.fadeAnimation.enter
                    ) {
                        val list = (uiStateWarn as NetworkUiState.Success).data
                        AnimatedVisibility(
                            visible = list.isNotEmpty(),
                            exit = AppAnimationManager.fadeAnimation.exit,
                            enter = AppAnimationManager.fadeAnimation.enter
                        ) {
                            with(list[0]) {
                                SharedContainer(
                                    key = LifeDestination.key,
                                    shape = MaterialTheme.shapes.medium.copy(
                                        topStart = CornerSize(0.dp),
                                        topEnd = CornerSize(0.dp),
                                    ),
                                    containerColor = cardNormalColor()
                                ) {
                                    TransplantListItem(
                                        colors = cardNormalColor(),
                                        headlineContent = { Text(title) },
                                        overlineContent = { Text(typeName)},
                                        leadingContent = { Icon(painterResource(R.drawable.temp_preferences_eco),null)},
                                        modifier = Modifier.clickable {
                                            navController.push(LifeDestination)
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
}


suspend fun getWebInfoFromHuiXin(vm: NetWorkViewModel) = withContext(Dispatchers.IO) {
    val auth = prefs.getString("auth","")
    async { vm.getFee("bearer $auth",HuiXinFeeType.NET_XUANCHENG) }.await()
    async {
        Handler(Looper.getMainLooper()).post{
            vm.infoValue.observeForever { result ->
                if(result != null && result.contains("success")&&!result.contains("账号不存在")) {
                    GlobalUiStateHolder.webValue.value = getWebInfo(vm)
                }
            }
        }
    }
}

suspend fun getElectricFromHuiXin(vm : NetWorkViewModel) = withContext(Dispatchers.IO) {
    val useHefei = DataStoreManager.useHefeiElectric.first()
    val auth = prefs.getString("auth","")
    if(useHefei) {
        val bean = DataStoreManager.getHefeiElectric()
        if(bean == null) {
            withContext(Dispatchers.Main) {
                GlobalUiStateHolder.electricValue.value = "--"
            }
            saveString("memoryEle","0.0")
            return@withContext
        }
        async { vm.getFee("bearer $auth", HuiXinFeeType.ELECTRIC_HEFEI_UNDERGRADUATE, room = bean.roomNumber, building = bean.buildingNumber) }.await()
        async {
            Handler(Looper.getMainLooper()).post{
                vm.hefeiElectric.observeForever { result ->
                    if (result?.contains("success") == true) {
                        try {
                            val data = GsonInstance.fromJson(result,HuiXinFeeResponse::class.java).map.showData
                            for ((_, value) in data) {
                                GlobalUiStateHolder.electricValue.value = value
                                saveString("memoryEle",GlobalUiStateHolder.electricValue.value)
                            }
                        } catch (e:Exception) {
                            LogUtil.error(e)
                        }
                    }
                }
            }
        }
    } else {
        val BuildingsNumber = prefs.getString("BuildNumber", "0")
        val RoomNumber = prefs.getString("RoomNumber", "")
        val EndNumber = prefs.getString("EndNumber", "")

        var input = "300$BuildingsNumber$RoomNumber$EndNumber"
        async { vm.getFee("bearer $auth", HuiXinFeeType.ELECTRIC_XUANCHENG, room = input) }.await()
        async {
            Handler(Looper.getMainLooper()).post{
                vm.electricData.observeForever { result ->
                    if (result?.contains("success") == true) {
                        try {
                            val data = GsonInstance.fromJson(result,HuiXinFeeResponse::class.java).map.showData
                            for ((_, value) in data) {
                                GlobalUiStateHolder.electricValue.value = value.substringAfter("剩余金额:").toDouble().roundOffString(2)
                                saveString("memoryEle",GlobalUiStateHolder.electricValue.value)
                            }
                        } catch (e:Exception) {
                            LogUtil.error(e)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Special(
) {
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
        ) {
            ChangeCourseUI(isTomorrow) {
                showBottomSheet = it
            }
        }
    }
    LaunchedEffect(showBottomSheet) {
        if(!showBottomSheet) {
            GlobalEventHolder.specialWorkDayChangeCallback.emit(Unit)
        }
    }


    if(isHoliday) {
        Row(modifier = Modifier
            .clickable {

            }) {
            TransplantListItem(
                headlineContent = { ScrollText(text = "节假日休息" ) },
                overlineContent = { ScrollText(text = "已隐藏今日课程") },
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
            .clickable {
                isTomorrow = false
                showBottomSheet = true
            }) {
            val d = targetDate?.substringAfter("-","")
            TransplantListItem(
                leadingContent = { Icon(painter = painterResource(R.drawable.swap_vert) , contentDescription = "")},
                headlineContent = { ScrollText(text = "上${ d ?: "--" }课程" ) },
                overlineContent = { ScrollText(text = "今天有调休上课") },
            )
        }
    } else if(isSpecificWorkDayTomorrow) {
        LaunchedEffect(showBottomSheet) {
            targetDate = DataBaseManager.specialWorkDayDao.search(DateTimeManager.tomorrow_YYYY_MM_DD)?.targetDate
        }
        PaddingHorizontalDivider()
        Row(modifier = Modifier
            .clickable {
                isTomorrow = true
                showBottomSheet = true
            }) {
            val d = targetDate?.substringAfter("-","")
            TransplantListItem(
                leadingContent = { Icon(painter = painterResource(R.drawable.exposure_plus_1) , contentDescription = "")},
                headlineContent = { ScrollText(text = "上${d ?: "--" }课程" ) },
                overlineContent = { ScrollText(text = "明天有调休上课") },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeCourseUI(
    isTomorrow : Boolean,
    onDismiss : (Boolean) -> Unit
) {
    val navController = LocalNavController.current
    val date = remember { if(isTomorrow) DateTimeManager.tomorrow_YYYY_MM_DD else DateTimeManager.Date_yyyy_MM_dd }
    var targetDate by remember { mutableStateOf<String?>(null) }

    val state = rememberDatePickerState()

    val scope = rememberCoroutineScope()
    LaunchedEffect(state.selectedDateMillis) {
        targetDate = try {
            DateTimeManager.simpleFormatter_YYYY_MM_DD.format(state.selectedDateMillis)
        } catch (e : Exception) {
            LogUtil.error(e)
            null
        }
    }
    val defaultCalendar by DataStoreManager.defaultCalendar.collectAsState(initial = CourseType.JXGLSTU.code)

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
            if(defaultCalendar == CourseType.COMMUNITY.code)
                CardListItem(
                    headlineContent = { Text("目前默认数据源被设置为智慧社区，若需使调休功能生效，请在设置日期后，前往 选项-应用行为-默认课程表 切换为非智慧社区")},
                    leadingContent = { Icon(painterResource(R.drawable.info),null)}
                )
            else
                CardListItem(
                    headlineContent = { Text("设置完成后聚焦首页将会显示为设置日期的课程安排")},
                    leadingContent = { Icon(painterResource(R.drawable.info),null)}
                )
            CardListItem(
                headlineContent = { Text("查询学校调休安排")},
                modifier = Modifier.clickable {
                    navController.push(
                        NewsApiDestination.create(NewsApiDestination.Keyword.HOLIDAY_SCHEDULE)
                    )
                },
                leadingContent = { Icon(painterResource(NewsApiDestination.ICON),null)}
            )
            DatePicker(state = state,
                modifier = Modifier.weight(1f), title = { Text(text = "")},
                colors = DatePickerDefaults.colors(containerColor = Color.Transparent),
            )
        }
    }
}

