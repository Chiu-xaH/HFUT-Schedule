package com.hfut.schedule.ui.screen.home.search.function.electric

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import com.hfut.schedule.ui.component.LoadingUI
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.model.zjgd.FeeType
import com.hfut.schedule.logic.model.zjgd.PayStep1Response
import com.hfut.schedule.logic.model.zjgd.PayStep2Response
import com.hfut.schedule.logic.model.zjgd.PayStep3Response
import com.hfut.schedule.logic.util.storage.SharePrefs
import com.hfut.schedule.logic.util.network.reEmptyLiveDta
import com.hfut.schedule.ui.screen.home.cube.sub.CirclePoint
import com.hfut.schedule.ui.screen.home.cube.sub.KeyBoard

import com.hfut.schedule.ui.screen.home.search.function.loginWeb.getIdentifyID
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.BottomSheetTopBar
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.LittleDialog
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.style.bottomSheetRound
import com.hfut.schedule.ui.component.statusUI
import com.hfut.schedule.ui.style.HazeBottomSheet
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayFor(vm : NetWorkViewModel, payNumber : Float, tipInfo : String, json : String, type : FeeType, hazeState: HazeState) {
    var showDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "" // 使用弹簧动画
    )

    LaunchedEffect(Unit) {
        async { reEmptyLiveDta(vm.orderIdData) }.await()
        async { reEmptyLiveDta(vm.uuIdData) }.await()
        async { reEmptyLiveDta(vm.payResultData) }.await()
    }



    if(showDialog) {
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                showDialog = false
                showBottomSheet = true
            },
            dialogTitle = "确认支付吗",
            dialogText = "是否核定好信息无误,交错后或者由于对方接口变动造成的损失请自行处理",
            hazeState = hazeState
        )
    }

    val psk = SharePrefs.prefs.getString("pins",null)
    var password by remember { mutableStateOf("") }
    var passwordStatus by remember { mutableStateOf("请输入密码") }
    val sheetState_pin = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_pin by remember { mutableStateOf(false) }

    if (showBottomSheet_pin) {
        if(password.length != 6) {
            HazeBottomSheet (
                onDismissRequest = {
                    showBottomSheet_pin = false
                },
                showBottomSheet = showBottomSheet_pin,
                hazeState = hazeState,
                autoShape = false
//                sheetState = sheetState_pin,
                // shape = Round(sheetState)
            ) {
                Column {
                    Spacer(Modifier.height(appHorizontalDp()*1.5f))
                    CirclePoint(text = passwordStatus, password = password)
                    Spacer(modifier = Modifier.height(20.dp))
                    KeyBoard(
                        modifier = Modifier.padding(horizontal = appHorizontalDp()),
                        onKeyClick = { num ->
                            if (password.length < 6) {
                                password += num.toString()
                            }
                        },
                        onBackspaceClick = {
                            if (password.isNotEmpty()) {
                                password = password.dropLast(1)
                            }
                        }
                    )
                }
            }
        } else {
            //验证密码
            if(psk == password) {
                showBottomSheet = true
            } else {
                //密码错了
                passwordStatus = "密码错误"
                password = ""
            }
        }
    }

    if (showBottomSheet) {

        HazeBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                               },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState,
            isFullExpand = false
//            sheetState = sheetState,
//            shape = bottomSheetRound(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("支付结果", isPaddingStatusBar = false)
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    PayStatusUI(vm,payNumber,json,type)
                }
            }
        }
    }



    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Icon(
            painter = painterResource(R.drawable.send_money),
            contentDescription = "",
            Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(text = "￥${payNumber}", fontSize = 48.sp,)
    }
    Spacer(modifier = Modifier.height(10.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(text = tipInfo,fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(20.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Button(onClick = {
            if(!SharePrefs.prefs.getBoolean("SWITCHPIN",false))
            showDialog = true
            else {
                showBottomSheet_pin = true
            }
        },
            interactionSource = interactionSource,
            modifier = Modifier
                .weight(1f)
                .scale(scale.value)
                .padding(horizontal = appHorizontalDp())) {
            Text(text = "支付")
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun PayStatusUI(vm : NetWorkViewModel, payNumber : Float, json: String, type : FeeType) {

    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    val auth =   "bearer " + SharePrefs.prefs.getString("auth","")
    var orderid = ""
    var msg  by remember { mutableStateOf("结果") }
    var count = 0;
    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch {
            async{ vm.payStep1(auth,json, payNumber,type) }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.orderIdData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("操作成功")) {
                                try {
                                    orderid = Gson().fromJson(result, PayStep1Response::class.java).data.orderid
                                    vm.payStep2(auth,orderid,type)
                                } catch (e : Exception) {
                                    showToast("服务器错误，终止支付")
                                }
                            }
                        }
                    }
                }
            }.await()

            async {
                Handler(Looper.getMainLooper()).post{
                    vm.uuIdData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("操作成功")) {
                                try {
                                    val map = Gson().fromJson(result, PayStep2Response::class.java).data.passwordMap
                                    var uuid = ""
                                    var passwordKey = ""
                                    for((key,value) in map) {
                                        uuid = key
                                        passwordKey = value
                                    }
                                    //正式支付
                                    if(count == 0) {
                                        vm.payStep3(auth,orderid,getPsk(passwordKey),uuid,type)
                                        count++
                                    }
                                } catch (e : Exception) {
                                    showToast("服务器错误，终止支付")
                                }
                            }
                        }
                    }
                }
            }.await()

            async {
                Handler(Looper.getMainLooper()).post{
                    vm.payResultData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("success")) {
                                msg = try {
                                    Gson().fromJson(result, PayStep3Response::class.java).msg
                                } catch (_:Exception) {
                                    "错误"
                                }
                                refresh = false
                                loading = false
                            }
                        }
                    }
                }
            }
        }
    }


    Box {
        AnimatedVisibility(
            visible = loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                LoadingUI()
            }
        }


        AnimatedVisibility(
            visible = !loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            statusUI(iconId = R.drawable.send_money, text = msg)
        }
    }

}

fun getPsk(key : String) : String {
    var result = ""
    return try {
        val psk = getIdentifyID()
        if (psk != null) {
            for(i in psk.indices) {
                for(j in key.indices) {
                    if(key[j] == psk[i]) {
                        result = result.plus(j)
                    }
                }
            }
        }
        result
    } catch (_ : Exception) {
        "000000"
    }
}
