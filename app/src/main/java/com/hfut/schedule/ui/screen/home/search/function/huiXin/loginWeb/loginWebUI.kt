package com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.huixin.FeeType
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.network.state.reEmptyLiveDta
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.button.BottomButton
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.button.LargeButton
import com.hfut.schedule.ui.component.container.LoadingLargeCard
import com.xah.uicommon.component.status.LoadingUI
import com.hfut.schedule.ui.component.container.TransplantListItem
   
 
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.screen.home.search.function.huiXin.electric.PayFor
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject


private const val HEFEI_TAB = 0
private const val XUANCHENG_TAB = 1
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginWebScaUI(vmUI : UIViewModel, vm : NetWorkViewModel, hazeState: HazeState) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        LoginWebUI(vmUI,vm, hazeState)
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginWebUI(vmUI : UIViewModel, vm : NetWorkViewModel, hazeState: HazeState) {
    val auth = remember { prefs.getString("auth", "") }
    val zjgdUrl = remember { MyApplication.HUI_XIN_URL + "charge-app/?name=pays&appsourse=ydfwpt&id=${FeeType.NET_XUANCHENG.code}&name=pays&paymentUrl=${MyApplication.HUI_XIN_URL}plat&token=" + auth }
    val maxFlow by DataStoreManager.maxFlow.collectAsState(initial = MyApplication.DEFAULT_MAX_FREE_FLOW)

    // 支付用的变量
    var showDialog2 by remember { mutableStateOf(false) }
    var payNumber by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    var json by remember { mutableStateOf("") }
    // 校园网用的变量
    val memoryWeb = prefs.getString("memoryWeb","0")
    var flow by remember { mutableStateOf(vmUI.webValue.value?.flow ?: memoryWeb) }
    var fee by remember { mutableStateOf(vmUI.webValue.value?.fee?: "0") }

    // 百分比
    val percent = try {
        formatDecimal(((flow?.toDouble() ?: 0.0) / (1024 * maxFlow)) * 100,2)
    } catch (_:Exception) {
        "未知"
    }

    val str = try {
        formatDecimal((flow?.toDouble() ?: 0.0) / 1024,2)
    } catch (_:Exception) {
        0.0
    }

    var loading by  remember { mutableStateOf(true)}
    var refresh by  remember { mutableStateOf(true)}


    val titles = remember { listOf("合肥","宣城") }
    val pagerState = rememberPagerState(pageCount = { titles.size }, initialPage =
        when(getCampusRegion()) {
            CampusRegion.XUANCHENG -> XUANCHENG_TAB
            CampusRegion.HEFEI -> HEFEI_TAB
    })

    fun refreshFlow() {
        refresh = true
        loading = true
        CoroutineScope(Job()).launch {
            async { reEmptyLiveDta(vm.infoValue) }.await()
            async { vm.getFee("bearer $auth", FeeType.NET_XUANCHENG) }.await()
            async {
                Handler(Looper.getMainLooper()).post {
                    vm.infoValue.observeForever { result ->
                        if (result != null)
                            if(result.contains("success")&&!result.contains("账号不存在")) {
                                val webInfo = getWebInfo(vm)
                                vmUI.webValue.value = webInfo
                                if (webInfo != null) {
                                    val jsonObject = JSONObject(result)
                                    val dataObject = jsonObject.getJSONObject("map").getJSONObject("data")
                                    dataObject.put("myCustomInfo", "undefined：undefined")
                                    json = dataObject.toString()
//                                    json = webInfo.postJson
                                    flow = webInfo.flow
                                    fee = webInfo.fee
                                    loading = false
                                    refresh = false
                                }
                            }
                    }
                }
            }
        }
    }

    val scope = rememberCoroutineScope()
    val uiState by vm.loginSchoolNetResponse.state.collectAsState()
    var textLogin by  remember { mutableStateOf("登录") }
    var textLogout by  remember { mutableStateOf("注销") }
    LaunchedEffect(Unit) {
        vm.loginSchoolNetResponse.emitPrepare()
    }
    val loadingLogin = uiState is UiState.Loading

    LaunchedEffect(uiState) {
        when(uiState) {
            is UiState.Error -> {
                textLogin = "登录失败"
                textLogout = "注销失败"
            }
            is UiState.Success -> {
                val data = (uiState as UiState.Success).data
                if(data) {
                    textLogin = "已登录"
                    textLogout = "注销"
                } else {
                    textLogout = "已注销"
                    textLogin   = "登录"
                }
            }
            else -> {}
        }
    }

    val context = LocalContext.current

    if(refresh) {
        refreshFlow()
    }
///////////////////////////////充值弹窗/////////////////////////////////
    if(showDialog2)
        Dialog(onDismissRequest = { showDialog2 = false }) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                    OutlinedCard{
                        LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
                            item {
                                Text(text = "选取金额 ￥${payNumber}", modifier = Modifier.padding(10.dp))
                            }
                            item {
                                LazyRow {
                                    items(5) { items ->
                                        IconButton(onClick = {
                                            if (payNumber.length < 3)
                                                payNumber += items.toString()
                                            else Toast.makeText(
                                                MyApplication.context,
                                                "最高999元",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }) { Text(text = items.toString()) }
                                    }
                                }
                            }
                            item {
                                LazyRow {
                                    items(5) { items ->
                                        val num = items + 5
                                        IconButton(onClick = {
                                            if (payNumber.length < 3)
                                                payNumber += num
                                            else Toast.makeText(MyApplication.context, "最高999元", Toast.LENGTH_SHORT).show()
                                        }) { Text(text = num.toString()) }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                    FilledTonalIconButton(
                        onClick = {payNumber = payNumber.replaceFirst(".$".toRegex(), "")},
                        modifier = Modifier.padding(horizontal = 5.dp)
                    ) {
                        Icon(painter = painterResource(R.drawable.backspace), contentDescription = "description")
                    }

                    FilledTonalIconButton(
                        onClick = {
                            showDialog2 = false
                            if(payNumber != "" && payNumber != "0" && payNumber != "00" && payNumber != "000")
                                showBottomSheet = true
                        },
                        modifier = Modifier.padding(horizontal = 5.dp)
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = "description")
                    }

                    FilledTonalButton(
                        onClick = {
                            showDialog2 = false
                            payNumber = "0.01"
                            showBottomSheet = true
                        },
                        modifier = Modifier.padding(horizontal = 5.dp)
                    ) {
                        Text("尝试充值0.01")
                    }
                }
            }
        }

    if (showBottomSheet) {

        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            autoShape = false,
            hazeState = hazeState,
            showBottomSheet = showBottomSheet
//            sheetState = sheetState,
            //    shape = sheetState
        ) {
            Column(
            ) {
                HazeBottomSheetTopBar("支付订单确认", isPaddingStatusBar = false)
//                val info by remember { mutableStateOf("") }
                var int by remember { mutableStateOf(payNumber.toFloat()) }
                if(int > 0) {
                    PayFor(vm,int,"学号 ${getPersonInfo().studentId}",json, FeeType.NET_XUANCHENG,hazeState)
                } else showToast("输入数值")
            }

        }
    }
    val loginUi = @Composable { campus : CampusRegion ->
        DividerTextExpandedWith("登录") {
            if(loadingLogin) {
                LoadingUI()
            } else {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = APP_HORIZONTAL_DP)) {
                    LargeButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        onClick = {
                            scope.launch {
                                vm.loginSchoolNetResponse.clear()
                                vm.loginSchoolNet(campus)
                            }
                        },
                        text = textLogin,
                        icon = R.drawable.login
                    )
                    Spacer(Modifier.width(APP_HORIZONTAL_DP/2))
                    LargeButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        onClick = {
                            scope.launch {
                                vm.loginSchoolNetResponse.clear()
                                vm.logoutSchoolNet(campus)
                            }
                        },
                        text = textLogout,
                        icon = R.drawable.logout,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            if(textLogin == "已登录") {
                Spacer(Modifier.height(APP_HORIZONTAL_DP/2- CARD_NORMAL_DP))
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = APP_HORIZONTAL_DP),
                    shape = MaterialTheme.shapes.medium,
                    onClick = {
                        scope.launch {
                            Starter.startWebView(context,"https://www.bing.cn/")
                        }
                    }
                ) {
                    Text("测试连通性")
                }
            }
            Spacer(Modifier.height(APP_HORIZONTAL_DP/2- CARD_NORMAL_DP*(if(textLogin == "已登录")2 else 1)))
            CardListItem(
                headlineContent = {
                    Text("一键校园网登录已支持磁贴，可在系统控制中心添加")
                },
                leadingContent = {
                    Icon(painterResource(R.drawable.keyboard_command_key),null)
                }
            )
        }
    }
//////////////////////////////布局区///////////////////////////////////
    Column() {
        HazeBottomSheetTopBar("校园网", isPaddingStatusBar = false) {
            if(pagerState.currentPage == XUANCHENG_TAB) {
                Row {
                    FilledTonalIconButton(
                        onClick = {
                            refreshFlow()
                        },
                    ) {
                        Icon(painterResource(R.drawable.rotate_right),null)
                    }
                    FilledTonalButton(
                        onClick = {
                            scope.launch {
                                Starter.startWebView(context,url = zjgdUrl, title = "慧新易校")
                            }
                        },
                    ) {
                        Text("官方充值")
                    }
                }
            }
        }
        CustomTabRow(pagerState,titles)
        HorizontalPager(state = pagerState,modifier = Modifier.animateContentSize()) { page ->
            when(page) {
                HEFEI_TAB -> {
                    Column {
                        loginUi(CampusRegion.HEFEI)
                        DividerTextExpandedWith(text = "使用说明", defaultIsExpanded = false) {
                            CustomCard(color = cardNormalColor()) {
                                TransplantListItem(
                                    headlineContent = { Text(text = "认证初始密码位于 查询中心-个人信息-密码信息") },
                                    supportingContent = {
                                        Text("如您修改了一卡通默认密码，请前往 选项-网络-一卡通密码 填入新的密码才可登陆校园网")
                                    },
                                    leadingContent = {
                                        Icon(painter = painterResource(id = R.drawable.key), contentDescription = "")
                                    }
                                )
                                PaddingHorizontalDivider()
                                TransplantListItem(
                                    headlineContent = { Text(text = "部分内网需校园网或WebVpn打开") },
                                    supportingContent = {
                                        Text(text = "学校提供平台供外网访问内网网址,位于 查询中心-WebVpn")
                                    },
                                    leadingContent = {
                                        Icon(painter = painterResource(id = R.drawable.vpn_key), contentDescription = "")
                                    }
                                )
                            }
                        }
                    }
                }
                XUANCHENG_TAB -> {
                    Column {
                        LoadingLargeCard(
                            title = "已用 $str GiB",
                            loading = loading,
                            prepare = false,
                            rightTop =  {
                                Text(text = "$flow MiB")
                            }
                        ) {
                            Row {
                                TransplantListItem(
                                    headlineContent = { Text(text = "余额 ￥${fee}") },
                                    overlineContent = { Text("1GiB / ￥1") },
                                    leadingContent = { Icon(painter = painterResource(id = R.drawable.paid), contentDescription = "")},
                                    modifier = Modifier.weight(.5f)
                                )
                                TransplantListItem(
                                    overlineContent = { Text(text = "月免费 ${maxFlow}GiB") },
                                    headlineContent = { Text(text = "已用 $percent%", fontWeight = FontWeight.Bold)},
                                    leadingContent = { Icon(painterResource(R.drawable.percent), contentDescription = "Localized description",) },
                                    modifier = Modifier.weight(.5f)
                                )
                            }
                            BottomButton(
                                onClick = {
                                    showDialog2 = true
                                },
                                enable = loading == false,
                                text = "快速充值"
                            )
                        }

                        loginUi(CampusRegion.XUANCHENG)

                        DividerTextExpandedWith(text = "使用说明", defaultIsExpanded = false) {
                            CustomCard(color = cardNormalColor()) {
                                TransplantListItem(
                                    headlineContent = { Text(text = "认证初始密码位于 查询中心-个人信息-密码信息") },
                                    supportingContent = {
                                        Text("如您修改了一卡通默认密码，请前往 选项-网络-一卡通密码 填入新的密码才可登陆校园网")
                                    },
                                    leadingContent = {
                                        Icon(painter = painterResource(id = R.drawable.key), contentDescription = "")
                                    }
                                )
                                PaddingHorizontalDivider()
                                TransplantListItem(
                                    headlineContent = { Text(text = "部分内网需校园网或WebVpn打开") },
                                    supportingContent = {
                                        Text(text = "学校提供平台供外网访问内网网址,位于 查询中心-WebVpn")
                                    },
                                    leadingContent = {
                                        Icon(painter = painterResource(id = R.drawable.vpn_key), contentDescription = "")
                                    }
                                )
                                PaddingHorizontalDivider()
                                TransplantListItem(
                                    headlineContent = { Text(text = "免费时期") },
                                    supportingContent = {
                                        Text(text = "法定节假日与寒暑假不限额度不限时间，其余时间限额月${maxFlow}GiB，每日熄灯期间禁用；合肥校区不限额")
                                    },
                                    leadingContent = {
                                        Icon(painter = painterResource(id = R.drawable.paid), contentDescription = "")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// Compose 嵌套用lambda
// 普通函数 嵌套用嵌套函数
