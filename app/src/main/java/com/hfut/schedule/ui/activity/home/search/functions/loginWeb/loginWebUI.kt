package com.hfut.schedule.ui.activity.home.search.functions.loginWeb

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.beans.zjgd.FeeType
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.data.reEmptyLiveDta
import com.hfut.schedule.logic.utils.parse.formatDecimal
import com.hfut.schedule.ui.activity.home.search.functions.electric.PayFor
import com.hfut.schedule.ui.activity.home.search.functions.person.getPersonInfo
import com.hfut.schedule.ui.activity.home.search.functions.transfer.Campus
import com.hfut.schedule.ui.activity.home.search.functions.transfer.getCampus
import com.hfut.schedule.ui.utils.components.appHorizontalDp
import com.hfut.schedule.ui.utils.components.BottomButton
import com.hfut.schedule.ui.utils.components.BottomSheetTopBar
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.HazeBottomSheetTopBar
import com.hfut.schedule.ui.utils.components.LoadingLargeCard
import com.hfut.schedule.ui.utils.components.showToast
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.components.WebDialog
import com.hfut.schedule.ui.utils.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginWebScaUI(vmUI : UIViewModel, vm : NetWorkViewModel,hazeState: HazeState) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        LoginWebUI(vmUI,vm, hazeState)
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginWebUI(vmUI : UIViewModel, vm : NetWorkViewModel,hazeState: HazeState) {
    val auth = prefs.getString("auth","")
    val zjgdUrl = MyApplication.HUIXIN_URL + "charge-app/?name=pays&appsourse=ydfwpt&id=${FeeType.WEB.code}&name=pays&paymentUrl=${MyApplication.HUIXIN_URL}plat&token=" + auth
    var showDialogWeb by remember { mutableStateOf(false) }
    WebDialog(showDialogWeb, url = zjgdUrl, title = "慧新易校",showChanged = { showDialogWeb = false }, showTop = false)
    // 支付用的变量
    var showDialog2 by remember { mutableStateOf(false) }
    var payNumber by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var json by remember { mutableStateOf("") }
    // 校园网用的变量
    val isXuancheng = getCampus() == Campus.XUANCHENG
    val memoryWeb = SharePrefs.prefs.getString("memoryWeb","0")
    var flow by remember { mutableStateOf(vmUI.webValue.value?.flow ?: memoryWeb) }
    var fee by remember { mutableStateOf(vmUI.webValue.value?.fee?: "0") }

    // 百分比
    val percent = try {
        val bd2 = BigDecimal(((flow?.toDouble() ?: 0.0) / (1024 * MyApplication.MAX_FREE_FLOW)) * 100)
        bd2.setScale(2, RoundingMode.HALF_UP).toString()
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




    var textLogin by  remember { mutableStateOf("登录") }
    var textLogout by  remember { mutableStateOf("注销") }


    fun refresh() {
        CoroutineScope(Job()).launch {
            async {
                vmUI.getWebInfo()
            }.await()
            async {
                Handler(Looper.getMainLooper()).post {
                    vmUI.resultValue.observeForever { result ->
                        if (result != null) {
                            if(result.contains("登录成功") && !result.contains("已使用")) {
                                vmUI.getWebInfo()
                                textLogin = "已登录"
                                textLogout = "注销"
                                // textStatus = "已登录"
                            } else if(result.contains("已使用")) {
                                textLogout = "已注销"
                                //  textStatus = "已注销"
                                textLogin   = "登录"
                            }
                        }
                    }
                }
            }
        }
    }

    fun refreshFlow() {
        refresh = true
        loading = true
        CoroutineScope(Job()).launch {
            async { reEmptyLiveDta(vm.infoValue) }.await()
            async { vm.getFee("bearer $auth", FeeType.WEB) }.await()
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

    refresh()

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
                    PayFor(vm,int,"学号 ${getPersonInfo().username}",json, FeeType.WEB,hazeState)
                } else showToast("输入数值")
            }

        }
    }
//////////////////////////////布局区///////////////////////////////////
    Column() {
        HazeBottomSheetTopBar("校园网" + if(getCampus() != Campus.XUANCHENG) "-宣城校区" else "", isPaddingStatusBar = false) {
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
                        showDialogWeb = true
                    },
                ) {
                    Text("官方充值")
                }
            }
        }
        DividerTextExpandedWith(text = "数据",false) {
            LoadingLargeCard(
                title = "已用 $str GB",
                loading = loading,
                rightTop =  {
                    Text(text = "$flow MB")
                }
            ) {
                if(isXuancheng) {
                    Row {
                        TransplantListItem(
                            headlineContent = { Text(text = "余额 ￥${fee}") },
                            overlineContent = { Text("1GB / ￥1") },
                            leadingContent = { Icon(painter = painterResource(id = R.drawable.paid), contentDescription = "")},
                            modifier = Modifier.weight(.5f)
                        )
                        TransplantListItem(
                            overlineContent = { Text(text = "月免费额度 ${MyApplication.MAX_FREE_FLOW}GB") },
                            headlineContent = { Text(text = "已用 $percent%", fontWeight = FontWeight.Bold)},
                            leadingContent = { Icon(painterResource(R.drawable.percent), contentDescription = "Localized description",) },
                            modifier = Modifier.weight(.5f)
                        )
                    }
                    BottomButton(
                        onClick = {
                            if(!loading) {
                                showDialog2 = true
                            }
                        },
                        text = "快速充值"
                    )
//                    Divider()
//                    Box(Modifier.background(MaterialTheme.colorScheme.secondaryContainer)) {
//                        FilledTonalButton(
//                            onClick = {
//                                if(!loading) {
//                                    showDialog2 = true
//                                }
//                            },
//                            shape = RoundedCornerShape(0.dp),
//                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.Transparent),
//                            modifier = Modifier.fillMaxSize(),
//                        ) {
//                            Text("快速充值")
//                        }
//                    }
                } else {
                    Divider()
                    Row {
                        Box(modifier = Modifier.background(MaterialTheme.colorScheme.primary).fillMaxWidth().weight(.5f)) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(0.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                onClick = {
                                    vmUI.loginWeb()
                                    vmUI.loginWeb2()
                                }
                            ) {
                                Text(textLogin)
                            }
                        }
                        Box(modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer).fillMaxWidth().weight(.5f)) {
                            FilledTonalButton(
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.Transparent),
                                shape = RoundedCornerShape(0.dp),
                                onClick = {
                                    vmUI.loginWeb()
                                    vmUI.loginWeb2()
                                }
                            ) {
                                Text(textLogout)
                            }
                        }
                    }
                }
            }


            Spacer(Modifier.height(10.dp))
            if(isXuancheng) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = appHorizontalDp())) {
                    Button(
                        modifier = Modifier.fillMaxWidth().weight(.5f),
                        onClick = {
                            vmUI.loginWeb()
                            vmUI.loginWeb2()
                        }
                    ) {
                        Text(textLogin)
                    }
                    Spacer(Modifier.width(10.dp))
                    FilledTonalButton(
                        modifier = Modifier.fillMaxWidth().weight(.5f),
                        onClick = {
                            vmUI.logoutWeb()
                        }
                    ) {
                        Text(textLogout)
                    }
                }
            }
            if(textLogin == "已登录") {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = appHorizontalDp()),
                    onClick = {
                        Starter.startWebUrl("https://cn.bing.com/")
                    }
                ) {
                    Text("测试连通性")
                }
            }
        }
        DividerTextExpandedWith(text = "使用说明", defaultIsExpanded = false) {
            TransplantListItem(
                headlineContent = { Text(text = "认证初始密码位于 查询中心-个人信息-密码信息") },
                leadingContent = {
                    Icon(painter = painterResource(id = R.drawable.key), contentDescription = "")
                }
            )
            TransplantListItem(
                headlineContent = { Text(text = "部分内网必须连接校园网打开") },
                supportingContent = {
                    Text(text = "学校提供WEBVPN供外网访问部分内网地址,可在 查询中心-网址导航 打开")
                },
                leadingContent = {
                    Icon(painter = painterResource(id = R.drawable.vpn_key), contentDescription = "")
                }
            )
            TransplantListItem(
                headlineContent = { Text(text = "免费时期") },
                supportingContent = {
                    Text(text = "宣城校区法定节假日与寒暑假不限额度，其余时间限额月50GB；合肥校区不限额")
                },
                leadingContent = {
                    Icon(painter = painterResource(id = R.drawable.paid), contentDescription = "")
                }
            )
        }
    }
}