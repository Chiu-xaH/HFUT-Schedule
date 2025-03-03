package com.hfut.schedule.ui.activity.home.cube.items.subitems

import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.logic.beans.SearchEleResponse
import com.hfut.schedule.logic.beans.zjgd.FeeResponse
import com.hfut.schedule.logic.beans.zjgd.FeeType
import com.hfut.schedule.logic.utils.DateTimeUtils
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.data.SharePrefs.saveString
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.ui.activity.home.focus.funictions.GetZjgdCard
import com.hfut.schedule.ui.activity.home.focus.funictions.TodayUI
import com.hfut.schedule.ui.activity.home.focus.funictions.getTodayNet
import com.hfut.schedule.ui.activity.home.search.functions.electric.Electric
import com.hfut.schedule.ui.activity.home.search.functions.loginWeb.LoginWeb
import com.hfut.schedule.ui.activity.home.search.functions.loginWeb.WebInfo
import com.hfut.schedule.ui.activity.home.search.functions.loginWeb.getWebInfoOld
import com.hfut.schedule.ui.activity.home.search.functions.card.SchoolCardItem
import com.hfut.schedule.ui.activity.home.search.functions.loginWeb.getWebInfo
import com.hfut.schedule.ui.activity.home.search.functions.shower.getInGuaGua
import com.hfut.schedule.ui.utils.components.AppHorizontalDp
import com.hfut.schedule.ui.utils.components.CardNormalColor
import com.hfut.schedule.ui.utils.components.CardNormalDp
import com.hfut.schedule.ui.utils.components.CustomTopBar
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.StyleCardListItem
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.style.Round
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusCardSettings() {

    var showBottomSheet by remember { mutableStateOf(false) }
    var sheetState = rememberModalBottomSheetState()

    val switch_ele = SharePrefs.prefs.getBoolean("SWITCHELE", true)
    var showEle by remember { mutableStateOf(switch_ele) }
    SharePrefs.saveBoolean("SWITCHELE", true, showEle)

    val switch_web = SharePrefs.prefs.getBoolean("SWITCHWEB", true)
    var showWeb by remember { mutableStateOf(switch_web) }
    SharePrefs.saveBoolean("SWITCHWEB", true, showWeb)

    val switch_card = SharePrefs.prefs.getBoolean("SWITCHCARD", true)
    var showCard by remember { mutableStateOf(switch_card) }
    SharePrefs.saveBoolean("SWITCHCARD", true, showCard)

    val switch_today = SharePrefs.prefs.getBoolean("SWITCHTODAY", true)
    var showToday by remember { mutableStateOf(switch_today) }
    SharePrefs.saveBoolean("SWITCHTODAY", true, showToday)


    val switch_card_add = SharePrefs.prefs.getBoolean("SWITCHCARDADD", true)
    var showCardAdd by remember { mutableStateOf(switch_card_add) }
    SharePrefs.saveBoolean("SWITCHCARDADD", true, showCardAdd)

    val switch_countDown = SharePrefs.prefs.getBoolean("SWITCHCOUNTDOWN", false)
    var showCountDown by remember { mutableStateOf(switch_countDown) }
    SharePrefs.saveBoolean("SWITCHCOUNTDOWN", false, showCountDown)

    val switch_shortCut = SharePrefs.prefs.getBoolean("SWITCHSHORTCUT", false)
    var showShortCut by remember { mutableStateOf(switch_shortCut) }
    SharePrefs.saveBoolean("SWITCHSHORTCUT", false, showShortCut)


//    MyCustomCard{
    StyleCardListItem(headlineContent = { Text(text = "打开开关则会在APP启动时自动获取信息,并显示在聚焦即时卡片内，如需减少性能开销可按需开启或关闭") }, leadingContent = { Icon(
            painter = painterResource(id = R.drawable.info),
            contentDescription = ""
        )})
//    }


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
        headlineContent = { Text(text = "倒计时")} ,
        leadingContent = { Icon(painter = painterResource(id = R.drawable.schedule), contentDescription = "")},
        trailingContent = { Switch(checked = showCountDown, onCheckedChange = {showch -> showCountDown = showch},enabled = false)}
    )
    TransplantListItem(
        headlineContent = { Text(text = "绩点排名")} ,
        leadingContent = { Icon(painter = painterResource(id = R.drawable.filter_vintage), contentDescription = "")},
        trailingContent = { Switch(checked = false, onCheckedChange = {}, enabled = false)}
    )
    TransplantListItem(
        headlineContent = { Text(text = "预留项")} ,
        leadingContent = { Icon(painter = painterResource(id = R.drawable.add_circle), contentDescription = "")},
        modifier = Modifier.clickable { showBottomSheet = true },
        trailingContent = { Switch(checked = showShortCut, onCheckedChange = {showch -> showShortCut = showch},enabled = false)}
    )


    if(showBottomSheet && showShortCut) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    CustomTopBar("选择你想放在聚焦首页的项")
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
fun FocusCard(vmUI : UIViewModel, vm : NetWorkViewModel, refreshing : Boolean) {
    val showEle = prefs.getBoolean("SWITCHELE",true)
    val showToday = prefs.getBoolean("SWITCHTODAY",true)
    val showWeb = prefs.getBoolean("SWITCHWEB",true)
    val showCard = prefs.getBoolean("SWITCHCARD",true)
    val showCountDown = prefs.getBoolean("SWITCHCOUNTDOWN",false)
    val showShortCut = prefs.getBoolean("SWITCHSHORTCUT",false)

    val scale = animateFloatAsState(
        targetValue = if (refreshing) 0.95f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )
    val blurSize by animateDpAsState(
        targetValue = if (refreshing) 10.dp else 0.dp, label = ""
        ,animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
    )

    CoroutineScope(Job()).launch {
        async {
            if(showWeb)
                getWebInfoFromZJGD(vm,vmUI)
        }
        async {
            if(showEle)
                getEleNew(vm, vmUI)
        }
        async {
            if(showToday)
                getTodayNet(vm)
        }
        async {
            if(showCard)
                GetZjgdCard(vm,vmUI)
        }
    }
    if(showCard || showEle || showToday || showWeb)
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = AppHorizontalDp(), vertical = CardNormalDp()), horizontalArrangement = Arrangement.Center) {

        MyCustomCard(modifier = Modifier
            .fillMaxWidth()
            .scale(scale.value),
            containerColor = CardNormalColor(),
            hasElevation = false
        ) {
            Column(modifier = Modifier.blur(blurSize)) {
                if(showCard || showToday)
                    Row {
                        if(showCard)
                            Box(modifier = Modifier.weight(.5f)) {
                                SchoolCardItem(vmUI,true)
                            }
                        if(showToday)
                            Box(modifier = Modifier
                                .weight(.5f)) {
                                TodayUI()
                            }
                    }
                if(showWeb || showEle)
                    Row {
                        if(showEle)
                            Box(modifier = Modifier.weight(.5f)) {
                                Electric(vm,true,vmUI)
                            }
                        if(showWeb)
                            Box(modifier = Modifier
                                .weight(.5f)) {
                                LoginWeb(vmUI,true,vm)
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
                if(DateTimeUtils.formattedTime_Hour.toInt() in 22 until 25) {
                    Row(
                        modifier = Modifier.clickable {
                            getInGuaGua(vm)
                        }
                    ) {
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

        }
    }
}
//废弃旧的方法
fun getWeb(vmUI : UIViewModel)  {
    CoroutineScope(Job()).launch {
        Handler(Looper.getMainLooper()).post{
            vmUI.infoValue.observeForever { result ->
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
fun getEle(vm : NetWorkViewModel, vmUI : UIViewModel) {
    val BuildingsNumber = prefs.getString("BuildNumber", "0")
    val RoomNumber = prefs.getString("RoomNumber", "")
    val EndNumber = prefs.getString("EndNumber", "")

    var input = "300$BuildingsNumber$RoomNumber$EndNumber"
    var jsons = "{ \"query_elec_roominfo\": { \"aid\":\"0030000000007301\", \"account\": \"24027\",\"room\": { \"roomid\": \"${input}\", \"room\": \"${input}\" },  \"floor\": { \"floorid\": \"\", \"floor\": \"\" }, \"area\": { \"area\": \"\", \"areaname\": \"\" }, \"building\": { \"buildingid\": \"\", \"building\": \"\" },\"extdata\":\"info1=\" } }"

    CoroutineScope(Job()).launch {
        async { vm.searchEle(jsons) }.await()
        async {
            Handler(Looper.getMainLooper()).post{
                vm.ElectricData.observeForever { result ->
                    if (result?.contains("query_elec_roominfo") == true) {
                        var msg = Gson().fromJson(result, SearchEleResponse::class.java).query_elec_roominfo.errmsg
                        if(msg.contains("剩余金额")) {
                            val bd = BigDecimal(msg.substringAfter("剩余金额").substringAfter(":"))
                            vmUI.electricValue.value =  bd.setScale(2, RoundingMode.HALF_UP).toString()
                            saveString("memoryEle",vmUI.electricValue.value)
                        }
                    }
                }
            }
        }
    }
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
                vm.ElectricData.observeForever { result ->
                    if (result?.contains("success") == true) {
                        val data = Gson().fromJson(result,FeeResponse::class.java).map.showData
                        for ((key, value) in data) {
                            val bd = BigDecimal(value.substringAfter("剩余金额:"))
                            vmUI.electricValue.value = bd.setScale(2, RoundingMode.HALF_UP).toString()
                            saveString("memoryEle",vmUI.electricValue.value)
                        }
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
