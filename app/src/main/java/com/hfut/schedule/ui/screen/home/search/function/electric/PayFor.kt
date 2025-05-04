package com.hfut.schedule.ui.screen.home.search.function.electric

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.zjgd.FeeType
import com.hfut.schedule.logic.util.network.SimpleUiState
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.LittleDialog
import com.hfut.schedule.ui.component.StatusUI
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.screen.home.cube.sub.CirclePoint
import com.hfut.schedule.ui.screen.home.cube.sub.KeyBoard
import com.hfut.schedule.ui.screen.home.search.function.loginWeb.getIdentifyID
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayFor(vm : NetWorkViewModel, payNumber : Float, tipInfo : String, json : String, type : FeeType, hazeState: HazeState) {
    var showDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "" // 使用弹簧动画
    )

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

    val psk = SharedPrefs.prefs.getString("pins",null)
    var password by remember { mutableStateOf("") }
    var passwordStatus by remember { mutableStateOf("请输入密码") }
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
            if(!SharedPrefs.prefs.getBoolean("SWITCHPIN",false))
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
    val auth = remember { "Bearer " + SharedPrefs.prefs.getString("auth","") }
    val uiState by vm.payResultData.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.orderIdData.clear()
        vm.uuIdData.clear()
        vm.payResultData.clear()
        // 开始
        vm.payStep1(auth,json, payNumber,type)
        val orderId = (vm.orderIdData.state.value as? SimpleUiState.Success)?.data ?: return@LaunchedEffect
        vm.payStep2(auth,orderId,type)
        val map = (vm.uuIdData.state.value as? SimpleUiState.Success)?.data ?: return@LaunchedEffect
        var uuid = ""
        var passwordKey = ""
        for((key,value) in map) {
            uuid = key
            passwordKey = value
        }
        val pwd = getPsk(passwordKey) ?: return@LaunchedEffect
        vm.payStep3(auth,orderId,pwd,uuid,type)
    }

    CommonNetworkScreen(uiState,isCenter = false, onReload = { showToast("禁止刷新") }) {
        val msg = (uiState as SimpleUiState.Success).data
        StatusUI(iconId = R.drawable.send_money, text = msg)
    }
}

fun getPsk(key : String) : String? {
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
    } catch (e : Exception) {
        null
    }
}
