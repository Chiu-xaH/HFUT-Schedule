package com.hfut.schedule.ui.screen.card.function.main


import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.CardBarItems
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.screen.RefreshIndicator
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.webview.WebDialog

import com.hfut.schedule.ui.component.container.largeCardColor
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.card.bill.TodayBills
import com.hfut.schedule.ui.screen.card.function.CardLimit
import com.hfut.schedule.ui.screen.card.function.SearchBillsUI
import com.hfut.schedule.ui.screen.card.function.SelecctDateRange
import com.hfut.schedule.ui.screen.home.focus.funiction.initCardNetwork
import com.hfut.schedule.ui.screen.home.search.function.huiXin.electric.EleUI
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.LoginWebScaUI
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.screen.home.search.function.huiXin.shower.ShowerUI
import com.hfut.schedule.ui.screen.home.search.function.huiXin.washing.WashingUI
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.style.appBlur
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.navigateAndSave
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun loadTodayPay(vm: NetWorkViewModel) : State<String> = produceState(initialValue = "--") {
    var total = 0.0
    try {
        val uiState = vm.huiXinBillResult.state.first { it !is UiState.Loading }
        if(uiState is UiState.Success) {
            val list = uiState.data.records
            for (item in list) {
                val get = item.effectdateStr
                val name = item.resume
                val todayDate = get.substringBefore(" ")
                var num = item.tranamt.toString()
                // 优化0.0X元Bug
                if(num.length == 1)
                    num = "00$num"
                num = num.substring(0, num.length - 2) + "." + num.substring(num.length - 2)
                if (DateTimeManager.Date_yyyy_MM_dd == todayDate) {
                    if (!name.contains("充值")) total += num.toFloat()
                }
            }
            value = formatDecimal(total,2)
        }
    } catch (_ : Exception) { value = formatDecimal(total,2) }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(innerPadding : PaddingValues, vm : NetWorkViewModel, navController :  NavHostController, vmUI : UIViewModel, hazeState: HazeState) {
    //刷新
    var refreshing by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    // 用协程模拟一个耗时加载
    val scope = rememberCoroutineScope()
    var states = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            async {
                refreshing = true
                loading = true
            }.await()
            async { initCardNetwork(vm,vmUI) }.await()
            async {
                delay(500)
                refreshing = false
                loading = false
            }
        }
    })

    var showDialog by remember { mutableStateOf(false) }
    var showBottomSheet_Range by remember { mutableStateOf(false) }
    var showBottomSheet_Search by remember { mutableStateOf(false) }
    var showBottomSheet_Settings by remember { mutableStateOf(false) }
    var showBottomSheet_ELectric by remember { mutableStateOf(false) }
    var showBottomSheet_Web by remember { mutableStateOf(false) }
    val sheetState_Web = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sheetState_Shower = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sheetState_Washing = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_Shower by remember { mutableStateOf(false) }
    var showBottomSheet_Washing by remember { mutableStateOf(false) }
    var showBottomSheet_Fee by remember { mutableStateOf(false) }
    var showBottomSheet_NFC by remember { mutableStateOf(false) }
    var showBottomSheet_Toady by remember { mutableStateOf(false) }

    val cardValue by remember { derivedStateOf { vmUI.cardValue } }
    val str by loadTodayPay(vm)

    var text by remember { mutableStateOf(cardValue?.balance ?: prefs.getString("card","00")) }
    var name by remember { mutableStateOf(cardValue?.name ?: getPersonInfo().name) }
    var nows by remember { mutableStateOf(cardValue?.now ?: prefs.getString("card_now","00")) }
    var settles by remember { mutableStateOf(cardValue?.settle ?: prefs.getString("card_settle","00")) }

    val auth = prefs.getString("auth","")
    val url by remember { mutableStateOf(MyApplication.HUIXIN_URL + "plat/pay" + "?synjones-auth=" + auth) }

    var showDialog_Huixin by remember { mutableStateOf(false) }

    val urlHuixin = MyApplication.HUIXIN_URL + "plat" + "?synjones-auth=" + auth

    LaunchedEffect(cardValue) {
        text = cardValue?.balance ?: prefs.getString("card","00")
        name = cardValue?.name ?: getPersonInfo().name
        nows = cardValue?.now ?: prefs.getString("card_now","00")
        settles = cardValue?.settle ?: prefs.getString("card_settle","00")
    }

    WebDialog(showDialog,{ showDialog = false },url,"付款码")
    WebDialog(showDialog_Huixin,{ showDialog_Huixin = false },urlHuixin,"慧新易校")


    if(showBottomSheet_Fee) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheet_Fee = false
            },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_Fee,
            isFullExpand = false
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("缴费与查询", isPaddingStatusBar = false)
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
//                    MyCustomCard {
                        StyleCardListItem(
                            headlineContent = { Text(text = "电费") },
                            leadingContent = {
                                Icon(painterResource(id = R.drawable.flash_on), contentDescription = "")
                            },
                            modifier = Modifier.clickable { showBottomSheet_ELectric = true }
                        )
//                    }
//                    MyCustomCard{
                        StyleCardListItem(
                            headlineContent = { Text(text = "网费"  ) },
                            leadingContent = {
                                Icon(painterResource(id = R.drawable.net), contentDescription = "")
                            },
                            modifier = Modifier.clickable { showBottomSheet_Web= true }
                        )
//                    }
//                    MyCustomCard {
                        StyleCardListItem(
                            headlineContent = { Text(text = "洗浴" ) },
                            leadingContent = {
                                Icon(painterResource(id = R.drawable.bathtub), contentDescription = "")
                            },
                            modifier = Modifier.clickable { showBottomSheet_Shower = true }
                        )
//                    }
                    StyleCardListItem(
                        headlineContent = { Text(text = "洗衣机") },
                        leadingContent = {
                            Icon(painterResource(id = R.drawable.local_laundry_service), contentDescription = "")
                        },
                        modifier = Modifier.clickable {
                            showBottomSheet_Washing = true
                        }
                    )

                    StyleCardListItem(
                        headlineContent = { Text("慧新易校平台") },
                        leadingContent = {
                            Icon(Icons.Default.ArrowForward, contentDescription = "")
                        },
                        modifier = Modifier.clickable { showDialog_Huixin = true }
                    )
                }
            }
        }
    }

    if (showBottomSheet_ELectric) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheet_ELectric = false
            },
            showBottomSheet = showBottomSheet_ELectric,
            hazeState = hazeState,
            autoShape = false
//            sheetState = sheetState_ELectric,
//            shape = Round(sheetState_ELectric)
        ) {
            EleUI(vm = vm,hazeState)
        }
    }

    if (showBottomSheet_Web) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_Web = false
            },
            sheetState = sheetState_Web,
//            shape = Round(sheetState_Web)
        ) {
            LoginWebScaUI(vmUI, vm,hazeState)
        }
    }

    if (showBottomSheet_Shower) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_Shower = false
            },
            sheetState = sheetState_Shower,
        ) {
            ShowerUI(vm,hazeState = hazeState)
        }
    }
    if (showBottomSheet_Washing) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_Washing = false
            },
            sheetState = sheetState_Washing,
        ) {
            WashingUI(vm,hazeState)
        }
    }

    if(showBottomSheet_NFC) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheet_NFC = false
            },

            showBottomSheet = showBottomSheet_NFC,
            hazeState = hazeState,
            isFullExpand = false
//            sheetState = sheetState_NFC,
//            shape = bottomSheetRound(sheetState_NFC)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("NFC复制说明")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    StyleCardListItem(headlineContent = { Text(text = "实体卡芯片自带加密,复制后手机仅可在图书馆储物柜刷取，其余场景均不可用，包括但不限于宿舍门禁、寝室门禁、消费终端等") })
                }
            }
        }
    }

    if(showBottomSheet_Range) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheet_Range = false
            },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_Range
//            sheetState = sheetState_Range,
//            shape = bottomSheetRound(sheetState_Range)
        ) { SelecctDateRange(vm) }
    }

    if (showBottomSheet_Search) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheet_Search = false
            },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_Search
//            sheetState = sheetState_Search,
//            shape = bottomSheetRound(sheetState_Search)
        ) { SearchBillsUI(vm) }
    }

    if (showBottomSheet_Settings) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheet_Settings = false
            },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_Settings,
            isFullExpand = false
//            sheetState = sheetState_Settings,
//            shape = bottomSheetRound(sheetState_Settings)
        ) { CardLimit(vm,vmUI) }
    }

    if(showBottomSheet_Toady) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_Toady = false },
//            sheetState = sheetState_Today,
//            shape = bottomSheetRound(sheetState_Today),
            isFullExpand = false,
            showBottomSheet = showBottomSheet_Toady,
            hazeState = hazeState
        ){
            TodayBills(vm)
        }
    }

    val scale = animateFloatAsState(
        targetValue = if (refreshing) 0.9f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(AppAnimationManager.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )
    val scale2 = animateFloatAsState(
        targetValue = if (refreshing) 0.97f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(AppAnimationManager.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )

    Box(modifier = Modifier
        .fillMaxHeight()
        .pullRefresh(states)) {
        LazyColumn {
            item {
                //////////////////////////////////////////////////////////////////////////////
                InnerPaddingHeight(innerPadding,true)
                Spacer(modifier = Modifier.height(5.dp))

                DividerTextExpandedWith(text = "校园卡",false) {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = APP_HORIZONTAL_DP),
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(scale2.value)
                            .padding(horizontal = APP_HORIZONTAL_DP, vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(largeCardColor())
                    ) {
                        Column(modifier = appBlur(loading).scale(scale.value)
                            ) {
                            TransplantListItem(
                                headlineContent = { Text(text = "$name 校园一卡通") },
                                trailingContent = {
                                    FilledTonalIconButton(onClick = {
                                        scope.launch {
                                            async {
                                                refreshing = true
                                            }.await()
                                            async {
                                                delay(500)
                                                refreshing = false
                                            }
                                        }
                                    }) {
                                        Icon(painter = painterResource(id = R.drawable.rotate_right), contentDescription = "")
                                    }
                                }
                            )
                            TransplantListItem(headlineContent = { Text(text = "￥$text", fontSize = 28.sp) },
                                trailingContent = {
                                    Text(text = "待圈存 ￥${settles}\n卡余额 ￥${nows}")
                                })
                            Row {
                                TransplantListItem(
                                    modifier = Modifier
                                        .weight(.5f)
                                        .clickable {
                                            if (text != null) {
                                                //余额不足//未登录//正常
                                                if (text != "00" && text!!.toDouble() < 10) {
                                                    Starter.startAppUrl(MyApplication.ALIPAY_CARD_URL)
                                                } else if (text == "00") {
                                                    refreshLogin()
                                                } else {
                                                    showToast("未检测出问题,若实体卡仍异常请咨询有关人士")
                                                }
                                            }
                                        },
                                    headlineContent = {
                                        if (text != null)
                                            Text(text = if(text != "00" && text!!.toDouble() < 10) "余额不足"  else if(text == "00") "未登录" else "正常")
                                    },
                                    overlineContent = { Text(text = "状态")},
                                    leadingContent = {
                                        if (text != null) {
                                            Icon(painter =
                                            painterResource(id =
                                            if(text != "00" && text!!.toDouble() < 10) R.drawable.add_circle  else if(text == "00") R.drawable.login else R.drawable.check_circle
                                            )  , contentDescription = "")
                                        }
                                    })
                                TransplantListItem(
                                    modifier = Modifier
                                        .weight(.5f)
                                        .clickable { showBottomSheet_Toady = true },
                                    headlineContent = { Text(text = "￥$str",fontWeight = FontWeight.Bold) },
                                    overlineContent = { Text(text = "今日支出")},
                                    leadingContent = { Icon(painter = painterResource(id = R.drawable.send_money), contentDescription = "")})
                            }
                            //  limitRow(vmUI)
                        }
                    }
                }

                DividerTextExpandedWith(text = "功能") {
                    TransplantListItem(
                        headlineContent = { Text(text = "账单") },
                        supportingContent = { Text(text = "按消费先后查看交易记录")},
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.receipt_long), contentDescription = "") },
                        modifier = Modifier.clickable { navController.navigateAndSave(CardBarItems.BILLS.name) }
                    )
                    TransplantListItem(
                        headlineContent = { Text(text = "统计") },
                        supportingContent = { Text(text = "按时间段归纳统计消费")},
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.leaderboard), contentDescription = "") },
                        modifier = Modifier.clickable { navController.navigateAndSave(CardBarItems.COUNT.name) }
                    )
                    TransplantListItem(
                        headlineContent = { Text(text = "充值") },
                        supportingContent = { Text(text = "跳转至支付宝校园卡页面")},
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.add_card), contentDescription = "")},
                        modifier = Modifier.clickable { Starter.startAppUrl(MyApplication.ALIPAY_CARD_URL) }
                    )
                    TransplantListItem(
                        headlineContent = { Text(text = "搜索") },
                        supportingContent = { Text(text = "仅检索标题")},
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.search), contentDescription = "")},
                        modifier = Modifier.clickable { showBottomSheet_Search = true }
                    )
                    TransplantListItem(
                        headlineContent = { Text(text = "限额") },
                        supportingContent = { Text(text = "超出设置的日额度后需在支付机输入密码")},
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.block), contentDescription = "")},
                        modifier = Modifier.clickable { showBottomSheet_Settings = true }
                    )
                    TransplantListItem(
                        headlineContent = { Text(text = "卡状态") },
                        supportingContent = { Text(text = "挂失 解挂 冻结")},
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.pie_chart), contentDescription = "")},
                        modifier = Modifier.clickable { showToast("暂未开发") }
                    )
                    TransplantListItem(
                        headlineContent = { Text(text = "付款码") },
                        supportingContent = { Text(text = "在支持扫码的食堂支付机使用以替代实体卡")},
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.barcode), contentDescription = "")},
                        modifier = Modifier.clickable { showDialog = true }
                    )
                    TransplantListItem(
                        headlineContent = { Text(text = "范围支出") },
                        supportingContent = { Text(text = "手动点选范围查询总消费")},
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.settings_ethernet), contentDescription = "")},
                        modifier = Modifier.clickable { showBottomSheet_Range = true }
                    )
                    TransplantListItem(
                        headlineContent = { Text(text = "网电缴费") },
                        supportingContent = { Text(text = "查询网费、宿舍电费、洗浴使用情况并缴费")},
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.corporate_fare), contentDescription = "")},
                        modifier = Modifier.clickable { showBottomSheet_Fee = true }
                    )
                    TransplantListItem(
                        headlineContent = { Text(text = "慧新易校") },
                        supportingContent = { Text(text = "跳转到慧新易校平台进行充值、查询等")},
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.handshake), contentDescription = "")},
                        modifier = Modifier.clickable { showDialog_Huixin = true }
                    )
                    TransplantListItem(
                        headlineContent = { Text(text = "实体卡复制") },
                        supportingContent = { Text(text = "对实体卡使用设备NFC复制功能")},
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.contactless), contentDescription = "")},
                        modifier = Modifier.clickable { showBottomSheet_NFC = true }
                    )
                }
                InnerPaddingHeight(innerPadding,false)
            }
        }
        RefreshIndicator(refreshing, states,
            Modifier
                .align(Alignment.TopCenter)
                .padding(innerPadding))
    }
}

@Composable
fun limitRow(vmUI : UIViewModel) {
    val cardValue by remember { derivedStateOf { vmUI.cardValue } }

    val limit by remember { mutableStateOf(cardValue?.autotrans_limite ?: 0) }
    val amt by remember { mutableStateOf(cardValue?.autotrans_amt?: 0) }


    Row {
        TransplantListItem(
            modifier = Modifier
                .weight(.5f)
                .clickable { },
            headlineContent = { Text(text = "￥${limit}") },
            overlineContent = { Text(text = "自动转账限额")},
            leadingContent = { Icon(painter = painterResource(id = R.drawable.block), contentDescription = "")})
        TransplantListItem(
            modifier = Modifier
                .weight(.5f)
                .clickable { },
            headlineContent = { Text(text = "￥${amt}") },
            overlineContent = { Text(text = "自动转账金额")},
            leadingContent = { Icon(painter = painterResource(id = R.drawable.do_not_disturb_on), contentDescription = "")})
    }
}

