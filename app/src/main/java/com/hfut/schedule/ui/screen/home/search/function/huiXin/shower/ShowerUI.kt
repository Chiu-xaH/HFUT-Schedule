package com.hfut.schedule.ui.screen.home.search.function.huiXin.shower

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.zjgd.FeeType
import com.hfut.schedule.logic.model.zjgd.ShowerFeeResponse
import com.hfut.schedule.logic.util.network.state.reEmptyLiveDta
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.Starter.loginGuaGua
import com.hfut.schedule.logic.util.sys.Starter.startGuaGua
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.screen.CustomTabRow
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.container.LoadingLargeCard
import com.hfut.schedule.ui.component.status.LoadingUI
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.webview.WebDialog
 
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.home.search.function.huiXin.electric.PayFor
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.math.BigDecimal

fun getInGuaGua(vm: NetWorkViewModel,onResult : (Boolean) -> Unit) {

    lateinit var guaguaUserInfoObserver: Observer<String?> // 延迟初始化观察者

    guaguaUserInfoObserver = Observer { result ->
        if(result != null) {
            onResult(false)
            if (result.contains("成功")) {
                saveString("GuaGuaPersonInfo", result)
                vm.guaGuaUserInfo.removeObserver(guaguaUserInfoObserver) // 正常移除观察者
                startGuaGua()
            } else if (result.contains("error")) {
                vm.guaGuaUserInfo.removeObserver(guaguaUserInfoObserver) // 正常移除观察者
                loginGuaGua()
            }
        }
    }

    CoroutineScope(Job()).launch {
        async { reEmptyLiveDta(vm.guaGuaUserInfo) }.await()
        async { onResult(true) }.await()
        async { vm.getGuaGuaUserInfo() }.await()
        launch {
            Handler(Looper.getMainLooper()).post {
                vm.guaGuaUserInfo.observeForever(guaguaUserInfoObserver)
            }
        }
    }
}
private const val HEFEI_TAB = 0
private const val XUANCHENG_TAB = 1
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowerUI(vm : NetWorkViewModel, isInGuagua : Boolean = false, hazeState: HazeState) {
    val titles = remember { listOf("合肥","宣城") }

    val pagerState = rememberPagerState(pageCount = { titles.size }, initialPage =
        when(getCampus()) {
            Campus.XUANCHENG -> XUANCHENG_TAB
            Campus.HEFEI -> HEFEI_TAB
        }
    )
    val auth = prefs.getString("auth","")
    var showDialogWeb by remember { mutableStateOf(false) }
    WebDialog(showDialogWeb, url = MyApplication.HUIXIN_URL + "charge-app/?name=pays&appsourse=ydfwpt&id=${ if(pagerState.currentPage == XUANCHENG_TAB)FeeType.SHOWER_XUANCHENG.code else FeeType.SHOWER_HEFEI.code}&name=pays&paymentUrl=${MyApplication.HUIXIN_URL}plat&token=" + auth, title = "慧新易校",showChanged = { showDialogWeb = false }, showTop = false)
    val savedPhoneNumber = prefs.getString("PhoneNumber","")
    var phoneNumber by remember { mutableStateOf(savedPhoneNumber ?: "") }
    var balance by remember { mutableStateOf(0) }
    var givenBalance by remember { mutableStateOf(0) }
    var studentID by remember { mutableStateOf("") }


    var showitem4 by remember { mutableStateOf(false) }

    var showButton by remember { mutableStateOf(false) }
    val showAdd by remember { mutableStateOf(false) }
    var payNumber by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }

    var show by remember { mutableStateOf(false) }
    var json by remember { mutableStateOf("") }

    var showDialog2 by remember { mutableStateOf(false) }

    if (showBottomSheet) {

        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState,
            autoShape = false
//            sheetState = sheetState,
            //    shape = sheetState
        ) {
            Column(
            ) {
                HazeBottomSheetTopBar("支付订单确认", isPaddingStatusBar = false)
                val info by remember { mutableStateOf("手机号 $phoneNumber") }
                var int by remember { mutableStateOf(payNumber.toFloat()) }
                if(int > 0) {
                    PayFor(vm,int,info,json, FeeType.SHOWER_XUANCHENG, hazeState)
                } else showToast("输入数值")
            }

        }
    }

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


    var loading by remember { mutableStateOf(false) }

    //布局///////////////////////////////////////////////////////////////////////////
    Column() {
        HazeBottomSheetTopBar("洗浴", isPaddingStatusBar = false) {
            if(pagerState.currentPage == XUANCHENG_TAB) {
                Row {
                    if(showitem4)
                        IconButton(onClick = {phoneNumber = phoneNumber.replaceFirst(".$".toRegex(), "")}) {
                            Icon(painter = painterResource(R.drawable.backspace), contentDescription = "description") }
                    FilledTonalIconButton(onClick = {
                        show = false
                        CoroutineScope(Job()).launch {
                            async {
                                showitem4 = false
                                Handler(Looper.getMainLooper()).post{
                                    vm.electricData.value = "{}"
                                }
                                saveString("PhoneNumber",phoneNumber )
                            }.await()
                            async { vm.getFee("bearer $auth", FeeType.SHOWER_XUANCHENG, phoneNumber = phoneNumber) }.await()
                            async {
                                Handler(Looper.getMainLooper()).post{
                                    vm.showerData.observeForever { result ->
                                        if (result?.contains("success") == true) {
                                            showButton = true
                                            try {
                                                val jsons = Gson().fromJson(result, ShowerFeeResponse::class.java).map.data

                                                studentID = jsons.identifier.toString()
                                                balance = jsons.accountMoney
                                                givenBalance = jsons.accountGivenMoney
                                                val jsonObject = JSONObject(result)
                                                val dataObject = jsonObject.getJSONObject("map").getJSONObject("data")
                                                dataObject.put("myCustomInfo", "undefined：$phoneNumber")
                                                json = dataObject.toString()
                                                show = true
                                            } catch (e:Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }) { Icon(painter = painterResource(R.drawable.search), contentDescription = "description") }

                    FilledTonalButton(
                        onClick = {
                            showDialogWeb = true
                        }
                    ) {
                        Text("官方充值")
                    }
                }
            }
        }



        CustomTabRow(pagerState,titles)
        if(!isInGuagua) {
            if(loading) {
                LoadingUI("正在核对登录")
            } else {
                StyleCardListItem(
                    headlineContent = { Text("进入呱呱物联") },
                    modifier = Modifier.clickable {
                        getInGuaGua(vm) { loading = it }
                    },
                    trailingContent = {
                        Icon(Icons.Default.ArrowForward,null)
                    },
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }

        HorizontalPager(state = pagerState) { page ->
            Column() {
                when(page) {
                    HEFEI_TAB -> {
//                        Spacer(Modifier.height(APP_HORIZONTAL_DP*3))
                        StyleCardListItem(
                            headlineContent = { Text("官方充值查询入口") },
                            modifier = Modifier.clickable {
                                showDialogWeb = true
                            },
                            trailingContent = {
                                Icon(Icons.Default.ArrowForward,null)
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.search),null)
                            },
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                        Spacer(Modifier.height(APP_HORIZONTAL_DP*3))
                    }
                    XUANCHENG_TAB -> {
                        Column {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = APP_HORIZONTAL_DP), horizontalArrangement = Arrangement.Start) {
                                AssistChip(
                                    onClick = { showitem4 = !showitem4 },
                                    label = { Text(text = "手机号 ${phoneNumber}") },
                                )
                            }

                            AnimatedVisibility(
                                visible = showitem4,
                                enter = slideInVertically(
                                    initialOffsetY = { -40 }
                                ) + expandVertically(
                                    expandFrom = Alignment.Top
                                ) + scaleIn(
                                    // Animate scale from 0f to 1f using the top center as the pivot point.
                                    transformOrigin = TransformOrigin(0.5f, 0f)
                                ) + fadeIn(initialAlpha = 0.3f),
                                exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(targetScale = 1.2f)
                            ) {
                                Column {
                                    Spacer(modifier = Modifier.height(7.dp))
                                    Row (modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)){
                                        OutlinedCard{
                                            LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
                                                item {
                                                    Text(
                                                        text = " 选取手机号",
                                                        modifier = Modifier.padding(10.dp)
                                                    )
                                                }
                                                item {
                                                    LazyRow {
                                                        items(5) { items ->
                                                            IconButton(onClick = {
                                                                if (phoneNumber.length < 11)
                                                                    phoneNumber += items.toString()
                                                                else Toast.makeText(
                                                                    MyApplication.context,
                                                                    "11位数",
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
                                                                if (phoneNumber.length < 11)
                                                                    phoneNumber += num
                                                                else Toast.makeText(MyApplication.context, "11位数", Toast.LENGTH_SHORT).show()
                                                            }) { Text(text = num.toString()) }
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(10.dp))
                                        }
                                    }
                                }
                            }


                            DividerTextExpandedWith(text = "查询结果") {
                                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                                    Spacer(modifier = Modifier.height(100.dp))
                                    LoadingLargeCard(
                                        title = if(!show)"￥XX.XX" else {     "￥${tranamt(balance)}"},
                                        loading = !show,
                                        rightTop = {
                                            if(show) {
                                                if(showButton)
                                                    FilledTonalButton(onClick = { if(showAdd && payNumber != "") showBottomSheet = true   else showDialog2 = true  }) { Text(text = if(showAdd && payNumber != "") "提交订单" else "快速充值") }
                                            } else FilledTonalButton(onClick = { null }) { Text(text = "快速充值") }
                                        }
                                    ) {
                                        TransplantListItem(
                                            //headlineContent = { androidx.compose.material3.Text( text = if(!show)"学号 " + " 2000000000" else "学号 $studentID") },
                                            headlineContent = { (if(!show)"手机号 1XXXXXXXXXX" else "手机号 $phoneNumber").let { Text(text = it) } },
                                            leadingContent = { Icon(painter = painterResource(id = R.drawable.info), contentDescription = "") }
                                        )
                                    }
                                }
                                Spacer(Modifier.height(APP_HORIZONTAL_DP*3))
                            }
                        }
                    }
                }
            }
        }
    }
}
//适配
fun tranamt(bills : Int) : Float {
    return try {
        var num =( bills ).toString()
        //优化0.0X元Bug
        if(num.length == 1)
            num = "000$num"
        if(num.length == 2)
            num = "00$num"
        num = num.substring(0, num.length - 3) + "." + num.substring(num.length - 3)
        val big = BigDecimal(num)
        val num_float = big.toFloat()
        num_float
    }catch (_:Exception) {
        0.0f
    }
}
