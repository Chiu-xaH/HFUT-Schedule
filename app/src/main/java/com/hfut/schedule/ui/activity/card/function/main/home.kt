package com.hfut.schedule.ui.activity.card.function.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.logic.enums.CardBarItems
import com.hfut.schedule.logic.utils.DateTimeUtils
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.logic.utils.Starter.refreshLogin
import com.hfut.schedule.ui.activity.card.bills.TodayBills
import com.hfut.schedule.ui.activity.card.bills.main.getBills
import com.hfut.schedule.ui.activity.card.function.CardLimit
import com.hfut.schedule.ui.activity.card.function.SearchBillsUI
import com.hfut.schedule.ui.activity.card.function.SelecctDateRange
import com.hfut.schedule.ui.activity.home.cube.items.subitems.getUserInfo
import com.hfut.schedule.ui.activity.home.focus.funictions.GetZjgdCard
import com.hfut.schedule.ui.activity.home.search.functions.electric.EleUI
import com.hfut.schedule.ui.activity.home.search.functions.loginWeb.LoginWebScaUI
import com.hfut.schedule.ui.activity.home.search.functions.loginWeb.LoginWebUI

import com.hfut.schedule.ui.activity.home.search.functions.shower.ShowerUI
//import com.hfut.schedule.ui.utils.NavigateAndAnimationManager.turnTo
import com.hfut.schedule.ui.utils.NavigateAnimationManager.turnTo
import com.hfut.schedule.ui.utils.components.AppHorizontalDp
import com.hfut.schedule.ui.utils.components.CustomTopBar
import com.hfut.schedule.ui.utils.style.CardForListColor
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.LargeCardColor
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.StyleCardListItem
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.components.WebDialog
import com.hfut.schedule.ui.utils.style.Round
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(innerPadding : PaddingValues, vm : NetWorkViewModel, navController :  NavHostController, vmUI : UIViewModel) {
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
                GetZjgdCard(vm,vmUI)
            }.await()
            async {
                delay(500)
                refreshing = false
                loading = false
               // MyToast("刷新成功")
            }
        }
    })


    var showDialog by remember { mutableStateOf(false) }

    val sheetState_Range = rememberModalBottomSheetState()
    var showBottomSheet_Range by remember { mutableStateOf(false) }

    val sheetState_Search = rememberModalBottomSheetState()
    var showBottomSheet_Search by remember { mutableStateOf(false) }

    val sheetState_Settings = rememberModalBottomSheetState()
    var showBottomSheet_Settings by remember { mutableStateOf(false) }



    val sheetState_ELectric = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_ELectric by remember { mutableStateOf(false) }

    var showBottomSheet_Web by remember { mutableStateOf(false) }
    val sheetState_Web = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    val sheetState_Shower = rememberModalBottomSheetState()
    var showBottomSheet_Shower by remember { mutableStateOf(false) }

    val sheetState_Fee = rememberModalBottomSheetState()
    var showBottomSheet_Fee by remember { mutableStateOf(false) }





    val sheetState_NFC = rememberModalBottomSheetState()
    var showBottomSheet_NFC by remember { mutableStateOf(false) }

    var showBottomSheet_Toady by remember { mutableStateOf(false) }
    val sheetState_Today = rememberModalBottomSheetState()




    val now = SharePrefs.prefs.getString("card_now","00")
    val settle = SharePrefs.prefs.getString("card_settle","00")

    var todaypay = 0.0
    var date = DateTimeUtils.Date_yyyy_MM_dd

    for (item in 0 until getBills(vm).size) {
        val get = getBills(vm)[item].effectdateStr
        val name = getBills(vm)[item].resume
        val todaydate = get?.substringBefore(" ")
        var num = getBills(vm)[item].tranamt.toString()

        //优化0.0X元Bug
        if(num.length == 1)
            num = "00$num"


        num = num.substring(0, num.length - 2) + "." + num.substring(num.length - 2)

        val num_float = num.toFloat()

        if (date == todaydate) {
            if (!name.contains("充值")) todaypay += num_float
        }

    }
    val num = todaypay.toString()
    val bd = BigDecimal(num)
    val str= bd.setScale(2, RoundingMode.HALF_UP).toString()

    val card = SharePrefs.prefs.getString("card","00")

    val test = vmUI.CardValue.value?.balance ?: card
    val name = vmUI.CardValue.value?.name ?: getUserInfo().name
    val nows = vmUI.CardValue.value?.now ?: now
    val settles = vmUI.CardValue.value?.settle ?: settle

    val auth = SharePrefs.prefs.getString("auth","")
    //Log.d("auth",auth.toString())
    val url = MyApplication.ZJGD_URL + "plat/pay" + "?synjones-auth=" + auth

    var showDialog_Huixin by remember { mutableStateOf(false) }

    val urlHuixin = MyApplication.ZJGD_URL + "plat" + "?synjones-auth=" + auth

//    val switch_startUri = SharePrefs.prefs.getBoolean("SWITCHSTARTURI",true)

//    if (showDialog) {
//        androidx.compose.ui.window.Dialog(
//            onDismissRequest = { showDialog = false },
//            properties = DialogProperties(usePlatformDefaultWidth = false)
//        ) {
//            Scaffold(
//                modifier = Modifier.fillMaxSize(),
//                topBar = {
//                    TopAppBar(
//                        colors = TopAppBarDefaults.mediumTopAppBarColors(
//                            containerColor = Color.Transparent,
//                            titleContentColor = MaterialTheme.colorScheme.primary,
//                        ),
//                        actions = {
//                            Row{
//                                IconButton(onClick = { Starter.startWebUrl(url) }) { Icon(painterResource(id = R.drawable.net), contentDescription = "") }
//                                IconButton(onClick = { showDialog = false }) { Icon(painterResource(id = R.drawable.close), contentDescription = "") }
//                            }
//                        },
//                        title = { Text("付款码") }
//                    )
//                },
//            ) { innerPadding ->
//                Column(
//                    modifier = Modifier
//                        .padding(innerPadding)
//                        .fillMaxSize()
//                ) {
//                    WebViewScreen(url)
//                }
//            }
//        }
//    }

    WebDialog(showDialog,{ showDialog = false },url,"付款码")
    WebDialog(showDialog_Huixin,{ showDialog_Huixin = false },urlHuixin,"慧新易校")
//    if (showDialog_Huixin) {
//        if(switch_startUri) {
//            androidx.compose.ui.window.Dialog(
//                onDismissRequest = { showDialog_Huixin = false },
//                properties = DialogProperties(usePlatformDefaultWidth = false)
//            ) {
//                Scaffold(
//                    modifier = Modifier.fillMaxSize(),
//                    topBar = {
//                        TopAppBar(
//                            colors = TopAppBarDefaults.mediumTopAppBarColors(
//                                containerColor = Color(0xFFFDCA31),
//                                titleContentColor = Color.White,
//                            ),
//                            actions = {
//                                Row{
//                                    IconButton(onClick = { Starter.startWebUrl(urlHuixin) }) { Icon(
//                                        painterResource(id = R.drawable.net), contentDescription = "", tint = Color.White
//                                    ) }
//                                    IconButton(onClick = { showDialog_Huixin = false }) { Icon(
//                                        painterResource(id = R.drawable.close), contentDescription = "", tint = Color.White) }
//                                }
//                            },
//                            title = { Text("慧新易校") }
//                        )
//                    },
//                ) { innerPadding ->
//                    Column(
//                        modifier = Modifier
//                            .padding(innerPadding)
//                            .fillMaxSize()
//                    ) {
//                        WebViewScreen(urlHuixin)
//                    }
//                }
//            }
//        } else {
//            Starter.startWebUrl(urlHuixin)
//        }
//    }

    if(showBottomSheet_Fee) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_Fee = false
            },
            sheetState = sheetState_Fee,
            shape = Round(sheetState_Fee)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    CustomTopBar("缴费与查询")
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
                            headlineContent = { Text(text = "网费") },
                            leadingContent = {
                                Icon(painterResource(id = R.drawable.net), contentDescription = "")
                            },
                            modifier = Modifier.clickable { showBottomSheet_Web= true }
                        )
//                    }
//                    MyCustomCard {
                        StyleCardListItem(
                            headlineContent = { Text(text = "洗浴") },
                            leadingContent = {
                                Icon(painterResource(id = R.drawable.bathtub), contentDescription = "")
                            },
                            modifier = Modifier.clickable { showBottomSheet_Shower = true }
                        )
//                    }
                    StyleCardListItem(
                        headlineContent = { Text(text = "合肥校区缴费") },
                        overlineContent = { Text("慧新易校平台") },
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
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_ELectric = false
            },
            sheetState = sheetState_ELectric,
//            shape = Round(sheetState_ELectric)
        ) {
            EleUI(vm = vm)
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
            LoginWebScaUI(vmUI, vm)
        }
    }

    if (showBottomSheet_Shower) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_Shower = false
            },
            sheetState = sheetState_Shower,
            shape = Round(sheetState_Shower)
        ) {
            ShowerUI(vm)
        }
    }

    if(showBottomSheet_NFC) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_NFC = false
            },
            sheetState = sheetState_NFC,
            shape = Round(sheetState_NFC)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    CustomTopBar("NFC复制说明")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    TransplantListItem(headlineContent = { Text(text = "实体卡芯片自带加密,复制后手机仅可在图书馆储物柜刷取，其余场景均不可用，包括但不限于宿舍门禁、寝室门禁、消费终端等") })
                }
            }
        }
    }

    if(showBottomSheet_Range) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_Range = false
            },
            sheetState = sheetState_Range,
            shape = Round(sheetState_Range)
        ) { SelecctDateRange(vm) }
    }

    if (showBottomSheet_Search) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_Search = false
            },
            sheetState = sheetState_Search,
            shape = Round(sheetState_Search)
        ) { SearchBillsUI(vm) }
    }

    if (showBottomSheet_Settings) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_Settings = false
            },
            sheetState = sheetState_Settings,
            shape = Round(sheetState_Settings)
        ) { CardLimit(vm,vmUI) }
    }

    if(showBottomSheet_Toady) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Toady = false },
            sheetState = sheetState_Today,
            shape = Round(sheetState_Today)
        ){
            TodayBills(vm)
        }
    }


    val scale = animateFloatAsState(
        targetValue = if (refreshing) 0.9f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )
    val scale2 = animateFloatAsState(
        targetValue = if (refreshing) 0.97f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )
    val blurSize by animateDpAsState(
        targetValue = if (refreshing) 10.dp else 0.dp, label = ""
        ,animationSpec = tween(MyApplication.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
    )
    Box(modifier = Modifier
        .fillMaxHeight()
        .pullRefresh(states)) {
        LazyColumn {
            item {
                //////////////////////////////////////////////////////////////////////////////
                Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
                Spacer(modifier = Modifier.height(5.dp))

                DividerTextExpandedWith(text = "校园卡",false) {
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = AppHorizontalDp()),
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(scale2.value)
                            .padding(horizontal = AppHorizontalDp(), vertical = 5.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(LargeCardColor())
                    ) {
                        Column(modifier = Modifier
                            .blur(blurSize)
                            .scale(scale.value)) {
                            TransplantListItem(
                                headlineContent = { Text(text = "$name 校园一卡通") },
                                trailingContent = {
                                    FilledTonalIconButton(onClick = {
                                        scope.launch {
                                            async {
                                                refreshing = true
                                                GetZjgdCard(vm,vmUI)
                                            }.await()
                                            async {
                                                delay(500)
                                                refreshing = false
                                                // MyToast("刷新成功")
                                            }
                                        }
                                    }) {
                                        Icon(painter = painterResource(id = R.drawable.rotate_right), contentDescription = "")
                                    }
                                }
                            )
                            TransplantListItem(headlineContent = { Text(text = "￥$test", fontSize = 28.sp) },
                                trailingContent = {
                                    Text(text = "待圈存 ￥${settles}\n卡余额 ￥${nows}")
                                })
                            Row {
                                TransplantListItem(
                                    modifier = Modifier
                                        .weight(.5f)
                                        .clickable {
                                            if (test != null) {
                                                //余额不足//未登录//正常
                                                if (test != "00" && test!!.toDouble() < 10) {
                                                    Starter.startAppUrl(MyApplication.ALIPAY_CARD_URL)
                                                } else if (test == "00") {
                                                    refreshLogin()
                                                } else {
                                                    MyToast("未检测出问题,若实体卡仍异常请咨询有关人士")
                                                }
                                            }
                                        },
                                    headlineContent = {
                                        if (test != null)
                                            Text(text = if(test != "00" && test.toDouble() < 10) "余额不足"  else if(test == "00") "未登录" else "正常")
                                    },
                                    overlineContent = { Text(text = "状态")},
                                    leadingContent = {
                                        if (test != null) {
                                            Icon(painter =
                                            painterResource(id =
                                            if(test != "00" && test!!.toDouble() < 10) R.drawable.add_circle  else if(test == "00") R.drawable.login else R.drawable.check_circle
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
                        modifier = Modifier.clickable { turnTo(navController,CardBarItems.BILLS.name) }
                    )
                    TransplantListItem(
                        headlineContent = { Text(text = "统计") },
                        supportingContent = { Text(text = "按时间段归纳统计消费")},
                        leadingContent = { Icon(painter = painterResource(id = R.drawable.leaderboard), contentDescription = "") },
                        modifier = Modifier.clickable { turnTo(navController,CardBarItems.COUNT.name) }
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
                        modifier = Modifier.clickable { MyToast("暂未开发") }
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

                Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
            }
        }
        PullRefreshIndicator(refreshing, states,
            Modifier
                .align(Alignment.TopCenter)
                .padding(innerPadding))
    }
}

@Composable
fun limitRow(vmUI : UIViewModel) {
    val limit by remember { mutableStateOf(vmUI.CardValue.value?.autotrans_limite ?: prefs.getString("card_limit","0")) }
    val amt by remember { mutableStateOf(vmUI.CardValue.value?.autotrans_amt?: prefs.getString("card_amt","0")) }


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

