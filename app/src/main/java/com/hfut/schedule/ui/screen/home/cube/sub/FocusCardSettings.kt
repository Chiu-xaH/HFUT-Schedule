package com.hfut.schedule.ui.screen.home.cube.sub

import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.zjgd.FeeResponse
import com.hfut.schedule.logic.model.zjgd.FeeType
import com.hfut.schedule.logic.util.network.SimpleUiState
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.DateTimeUtils
import com.hfut.schedule.ui.component.BottomSheetTopBar
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.MyCustomCard
import com.hfut.schedule.ui.component.RotatingIcon
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.cardNormalColor
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.component.onListenStateHolder
import com.hfut.schedule.ui.screen.home.focus.funiction.TodayUI
import com.hfut.schedule.ui.screen.home.search.function.card.SchoolCardItem
import com.hfut.schedule.ui.screen.home.search.function.electric.Electric
import com.hfut.schedule.ui.screen.home.search.function.life.LifeUIS
import com.hfut.schedule.ui.screen.home.search.function.loginWeb.LoginWeb
import com.hfut.schedule.ui.screen.home.search.function.loginWeb.getWebInfo
import com.hfut.schedule.ui.screen.home.search.function.loginWeb.getWebInfoOld
import com.hfut.schedule.ui.screen.home.search.function.shower.getInGuaGua
import com.hfut.schedule.ui.screen.home.search.function.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.transfer.getCampus
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.bottomSheetRound
import com.hfut.schedule.ui.util.NavigateAnimationManager
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusCardSettings(innerPadding : PaddingValues) {

    var showBottomSheet by remember { mutableStateOf(false) }
    var sheetState = rememberModalBottomSheetState()

    val switch_ele = SharedPrefs.prefs.getBoolean("SWITCHELE", getCampus() == Campus.XUANCHENG)
    var showEle by remember { mutableStateOf(switch_ele) }
    SharedPrefs.saveBoolean("SWITCHELE", true, showEle)

    val switch_web = SharedPrefs.prefs.getBoolean("SWITCHWEB", getCampus() == Campus.XUANCHENG)
    var showWeb by remember { mutableStateOf(switch_web) }
    SharedPrefs.saveBoolean("SWITCHWEB", true, showWeb)

    val switch_card = SharedPrefs.prefs.getBoolean("SWITCHCARD", true)
    var showCard by remember { mutableStateOf(switch_card) }
    SharedPrefs.saveBoolean("SWITCHCARD", true, showCard)

    val switch_today = SharedPrefs.prefs.getBoolean("SWITCHTODAY", true)
    var showToday by remember { mutableStateOf(switch_today) }
    SharedPrefs.saveBoolean("SWITCHTODAY", true, showToday)


    val switch_card_add = SharedPrefs.prefs.getBoolean("SWITCHCARDADD", true)
    var showCardAdd by remember { mutableStateOf(switch_card_add) }
    SharedPrefs.saveBoolean("SWITCHCARDADD", true, showCardAdd)

    val switch_countDown = SharedPrefs.prefs.getBoolean("SWITCHCOUNTDOWN", false)
    var showCountDown by remember { mutableStateOf(switch_countDown) }
    SharedPrefs.saveBoolean("SWITCHCOUNTDOWN", false, showCountDown)

    val switch_shortCut = SharedPrefs.prefs.getBoolean("SWITCHSHORTCUT", false)
    var showShortCut by remember { mutableStateOf(switch_shortCut) }
    SharedPrefs.saveBoolean("SWITCHSHORTCUT", false, showShortCut)

    val showShower by DataStoreManager.showFocusShower.collectAsState(initial = true)
    val showWeather by DataStoreManager.showFocusWeatherWarn.collectAsState(initial = true)
    val scope = rememberCoroutineScope()


    Column(modifier = Modifier.padding(innerPadding)) {
        StyleCardListItem(headlineContent = { Text(text = "打开开关则会在APP启动时自动获取信息,并显示在聚焦即时卡片内，如需减少性能开销可按需开启或关闭") }, leadingContent = { Icon(
            painter = painterResource(id = R.drawable.info),
            contentDescription = ""
        )})


        Spacer(modifier = Modifier.height(5.dp))

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
        TransplantListItem(
            headlineContent = { Text(text = "寝室电费")} ,
            leadingContent = { Icon(painter = painterResource(id = R.drawable.flash_on), contentDescription = "")},
            trailingContent = {
                Switch(checked = showEle, onCheckedChange = {showch -> showEle = showch })
            }
        )
        TransplantListItem(
            headlineContent = { Text(text = "校园网")} ,
            leadingContent = { Icon(painter = painterResource(id = R.drawable.net), contentDescription = "")},
            trailingContent = { Switch(checked = showWeb, onCheckedChange = {showch -> showWeb = showch})}
        )
        TransplantListItem(
            headlineContent = { Text(text = "聚焦通知")} ,
            supportingContent = { Text(text = "明日早八,临近课程,催还图书,临近考试")},
            leadingContent = { Icon(painter = painterResource(id = R.drawable.sentiment_very_satisfied), contentDescription = "")},
            trailingContent = { Switch(checked = showToday, onCheckedChange = {showch -> showToday = showch})}
        )
        TransplantListItem(
            headlineContent = { Text(text = "洗浴(需要时显示)")} ,
            leadingContent = { Icon(painter = painterResource(id = R.drawable.bathtub), contentDescription = "")},
            trailingContent = { Switch(checked = showShower, onCheckedChange = { scope.launch { DataStoreManager.saveFocusShowShower(!showShower) } })}
        )
        TransplantListItem(
            headlineContent = { Text(text = "气象预警(需要时显示)")} ,
            leadingContent = { Icon(painter = painterResource(id = R.drawable.warning), contentDescription = "")},
            trailingContent = { Switch(checked = showWeather, onCheckedChange = { scope.launch { DataStoreManager.saveFocusShowWeatherWarn(!showWeather) } })}
        )
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
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FocusCard(vmUI : UIViewModel, vm : NetWorkViewModel, hazeState: HazeState) {
    val showEle = prefs.getBoolean("SWITCHELE",getCampus() == Campus.XUANCHENG)
    val showToday = prefs.getBoolean("SWITCHTODAY",true)
    val showWeb = prefs.getBoolean("SWITCHWEB",getCampus() == Campus.XUANCHENG)
    val showCard = prefs.getBoolean("SWITCHCARD",true)
    val showCountDown = prefs.getBoolean("SWITCHCOUNTDOWN",false)
    val showShortCut = prefs.getBoolean("SWITCHSHORTCUT",false)
    var loading by remember { mutableStateOf(false) }
    val showShower by DataStoreManager.showFocusShower.collectAsState(initial = true)
    val showWeather by DataStoreManager.showFocusWeatherWarn.collectAsState(initial = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("生活服务")
                },) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    LifeUIS(vm)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
    if(showCard || showEle || showToday || showWeb)
        MyCustomCard(
            containerColor = cardNormalColor(),
            hasElevation = false
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
                                TodayUI(hazeState)
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
                if(showCountDown || showShortCut)
                    Row {
                        if(showCountDown)
                            Box(modifier = Modifier.weight(.5f)) {
                                countDownUI()
                            }
                        if(showShortCut)
                            Box(modifier = Modifier
                                .weight(.5f)) {
                                shortCut()
                            }
                    }
                if(DateTimeUtils.Time_Hour.toInt() in 22 until 25 && showShower) {
                    Row(
                        modifier = Modifier.clickable {
                            getInGuaGua(vm) { loading = it }
                        }
                    ) {
                        if(loading) {
                            TransplantListItem(
                                headlineContent = { Text(text = "正在核对登录") },
                                leadingContent = {
                                    RotatingIcon(R.drawable.progress_activity)
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
                        visible = uiStateWarn is SimpleUiState.Success,
                        exit = NavigateAnimationManager.fadeAnimation.exit,
                        enter = NavigateAnimationManager.fadeAnimation.enter
                    ) {
                        val list = (uiStateWarn as SimpleUiState.Success).data
                        AnimatedVisibility(
                            visible = list.isNotEmpty(),
                            exit = NavigateAnimationManager.fadeAnimation.exit,
                            enter = NavigateAnimationManager.fadeAnimation.enter
                        ) {
                            with(list[0]) {
                                TransplantListItem(
                                    headlineContent = { Text(title) },
                                    overlineContent = { Text(typeName)},
                                    leadingContent = { Icon(painterResource(R.drawable.warning),null)},
                                    modifier = Modifier.clickable { showBottomSheet = true }
                                )
                            }
                        }
                    }
                }
            }
        }
}
//废弃旧的方法
fun getWeb(vm : NetWorkViewModel,vmUI: UIViewModel)  {
    CoroutineScope(Job()).launch {
        Handler(Looper.getMainLooper()).post{
            vm.infoWebValue.observeForever { result ->
                if (result != null)
                    if(result.contains("flow")) {
                        vmUI.webValue.value = getWebInfoOld(result)
                        saveString("memoryWeb", vmUI.webValue.value?.flow)
                    }
            }
        }
    }
}

fun getWebInfoFromZJGD(vm: NetWorkViewModel, vmUI : UIViewModel)  {
    val auth = prefs.getString("auth","")
    CoroutineScope(Job()).launch {
        async { vm.getFee("bearer $auth",FeeType.WEB) }.await()
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


//废弃旧的方法
suspend fun getEle(vm : NetWorkViewModel, vmUI : UIViewModel) = withContext(Dispatchers.IO) {
    val BuildingsNumber = prefs.getString("BuildNumber", "0")
    val RoomNumber = prefs.getString("RoomNumber", "")
    val EndNumber = prefs.getString("EndNumber", "")

    var input = "300$BuildingsNumber$RoomNumber$EndNumber"
    var jsons = "{ \"query_elec_roominfo\": { \"aid\":\"0030000000007301\", \"account\": \"24027\",\"room\": { \"roomid\": \"${input}\", \"room\": \"${input}\" },  \"floor\": { \"floorid\": \"\", \"floor\": \"\" }, \"area\": { \"area\": \"\", \"areaname\": \"\" }, \"building\": { \"buildingid\": \"\", \"building\": \"\" },\"extdata\":\"info1=\" } }"
    vm.electricOldData.clear()
    vm.searchEle(jsons)
    onListenStateHolder(vm.electricOldData) { data ->
        vmUI.electricValue.value = data
        saveString("memoryEle",vmUI.electricValue.value)
    }
//    withContext(Dispatchers.Main) {
//        val state = .state.first { it !is SimpleUiState.Loading }
//        when (state) {
//            is SimpleUiState.Success -> {
//                val data = state.data
//                
//            }
//            is SimpleUiState.Error -> {
//                showToast("错误 " + state.exception?.message)
//            }
//            else -> {}
//        }
//    }
}
fun getEleNew(vm : NetWorkViewModel, vmUI : UIViewModel) {
    val BuildingsNumber = prefs.getString("BuildNumber", "0")
    val RoomNumber = prefs.getString("RoomNumber", "")
    val EndNumber = prefs.getString("EndNumber", "")

    var input = "300$BuildingsNumber$RoomNumber$EndNumber"
    val auth = prefs.getString("auth","")

    CoroutineScope(Job()).launch {
        async { vm.getFee("bearer $auth", FeeType.ELECTRIC, room = input) }.await()
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
fun countDownUI() {
    TransplantListItem(
        headlineContent = { Text(text = "添加") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.schedule), contentDescription = "")},
        overlineContent = { Text(text = "倒计时") }
    )
}
@Composable
fun shortCut() {
    TransplantListItem(
        headlineContent = { Text(text = "捷径") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.add_circle), contentDescription = "")},
        overlineContent = { Text(text = "选择要放入的选项") }
    )
}
