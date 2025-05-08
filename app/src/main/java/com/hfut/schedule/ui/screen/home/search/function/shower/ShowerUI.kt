package com.hfut.schedule.ui.screen.home.search.function.shower

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.zjgd.FeeType
import com.hfut.schedule.logic.model.zjgd.ShowerFeeResponse
import com.hfut.schedule.logic.util.network.reEmptyLiveDta
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.Starter.loginGuaGua
import com.hfut.schedule.logic.util.sys.Starter.startGuaGua
import com.hfut.schedule.ui.screen.home.search.function.electric.PayFor
import com.hfut.schedule.ui.screen.home.search.function.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.transfer.getCampus
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.BottomTip
import com.hfut.schedule.ui.component.BottomSheetTopBar
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.LoadingLargeCard
import com.hfut.schedule.ui.component.LoadingUI
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.component.ScrollText
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.WebDialog
import com.hfut.schedule.ui.style.CardForListColor
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
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
                vm.guaguaUserInfo.removeObserver(guaguaUserInfoObserver) // 正常移除观察者
                startGuaGua()
            } else if (result.contains("error")) {
                vm.guaguaUserInfo.removeObserver(guaguaUserInfoObserver) // 正常移除观察者
                loginGuaGua()
            }
        }
    }

    CoroutineScope(Job()).launch {
        async { reEmptyLiveDta(vm.guaguaUserInfo) }.await()
        async { onResult(true) }.await()
        async { vm.getGuaGuaUserInfo() }.await()
        launch {
            Handler(Looper.getMainLooper()).post {
                vm.guaguaUserInfo.observeForever(guaguaUserInfoObserver)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowerUI(vm : NetWorkViewModel, isInGuagua : Boolean = false, hazeState: HazeState) {
//    val hazeState = remember { HazeState() }
    val auth = SharedPrefs.prefs.getString("auth","")
    val zjgdUrl = MyApplication.HUIXIN_URL + "charge-app/?name=pays&appsourse=ydfwpt&id=${FeeType.SHOWER.code}&name=pays&paymentUrl=${MyApplication.HUIXIN_URL}plat&token=" + auth
    var showDialogWeb by remember { mutableStateOf(false) }
    WebDialog(showDialogWeb, url = zjgdUrl, title = "慧新易校",showChanged = { showDialogWeb = false }, showTop = false)
    val savedPhoneNumber = prefs.getString("PhoneNumber","")
    var phoneNumber by remember { mutableStateOf(savedPhoneNumber ?: "") }
    var balance by remember { mutableStateOf(0) }
    var givenBalance by remember { mutableStateOf(0) }
    var studentID by remember { mutableStateOf("") }


    var showitem4 by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }


    var showButton by remember { mutableStateOf(false) }
    val showAdd by remember { mutableStateOf(false) }
    var payNumber by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    var show by remember { mutableStateOf(false) }
    var json by remember { mutableStateOf("") }



    val url = MyApplication.HUIXIN_URL + "charge-app/?name=pays&appsourse=ydfwpt&id=223&name=pays&paymentUrl=http://121.251.19.62/plat&token=" + auth
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
                    PayFor(vm,int,info,json, FeeType.SHOWER, hazeState)
                } else showToast("输入数值")
            }

        }
    }

//    WebDialog(showDialog,{ showDialog = false },url,"宣城校区 洗浴缴纳", showTop = false)

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

    //布局///////////////////////////////////////////////////////////////////////////
    Column() {
        HazeBottomSheetTopBar("洗浴" + if(getCampus() != Campus.XUANCHENG) "-宣城校区" else "", isPaddingStatusBar = false) {
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
                            SharedPrefs.saveString("PhoneNumber",phoneNumber )
                        }.await()
                        async { vm.getFee("bearer $auth", FeeType.SHOWER, phoneNumber = phoneNumber) }.await()
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

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = appHorizontalDp(), vertical = 0.dp), horizontalArrangement = Arrangement.Start) {
            AssistChip(
                onClick = { showitem4 = !showitem4 },
                label = { Text(text = "手机号 ${phoneNumber}") },
            )
        }

        Spacer(modifier = Modifier.height(7.dp))
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
            Row (modifier = Modifier.padding(horizontal = appHorizontalDp())){
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

        val scale = animateFloatAsState(
            targetValue = if (!show) 0.97f else 1f, // 按下时为0.9，松开时为1
            //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            animationSpec = tween(MyApplication.ANIMATION_SPEED/2, easing = LinearOutSlowInEasing),
            label = "" // 使用弹簧动画
        )
        var loading by remember { mutableStateOf(false) }

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

            if(!isInGuagua) {
                if(loading) {
                    LoadingUI("正在核对登录")
                } else {
                    Button(
                        onClick = {
                            getInGuaGua(vm) { loading = it }
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = appHorizontalDp(), vertical = 5.dp).scale(scale.value)
                    ) {
                        Text(text = "进入呱呱物联")
                    }
                }
            } else {
                Spacer(Modifier.height(40.dp))
            }
        }

        Spacer(modifier = Modifier.height(appHorizontalDp()))
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
